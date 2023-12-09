package com.caicongyang.stock.service;


import com.binance.connector.client.enums.DefaultUrls;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebsocketStreamClientImpl;
import com.binance.connector.client.impl.spot.Market;
import com.binance.connector.client.utils.WebSocketCallback;
import com.caicongyang.core.utils.GsonUtils;
import com.caicongyang.httper.HttpClientProvider;
import com.caicongyang.stock.domain.*;
import com.caicongyang.stock.utils.JacksonUtil;
import com.caicongyang.stock.utils.TomDateUtil;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FifteenMinBNService {

    private static final Logger logger = LoggerFactory.getLogger(FifteenMinBNService.class);


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ExecutorService asyncExecutor;

    @Autowired
    HttpClientProvider httpClientProvider;


    private final WebSocketCallback noopCallback = msg -> {
    };

    @PostConstruct
    public void init() {
        initMarketInfo();


        kline();

        //listen();
    }

    private void initMarketInfo() {


        SpotClientImpl client = new SpotClientImpl();
        Market market = client.createMarket();
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("SPOT");
        permissions.add("MARGIN");
        parameters.put("permissions", permissions);
        String result = market.exchangeInfo(parameters);
        ExchangeInfo exchangeInfo = JacksonUtil.objectFromJson(result, ExchangeInfo.class);
        List<String> allTradingSymbolList = exchangeInfo.getSymbols().stream().filter(a -> a.getStatus().equalsIgnoreCase("TRADING")).map(a -> a.getSymbol()).filter(a -> a.endsWith("USDT")).collect(Collectors.toList());
        logger.info("交易中的交易对：" + allTradingSymbolList.size() + "----> " + String.join(",", allTradingSymbolList));
        redisTemplate.opsForList().leftPushAll("bn_exchangeInfo_list", allTradingSymbolList);


    }


    private void kline() {
        List<String> bn_exchangeInfo = (List<String>) redisTemplate.opsForList().range("bn_exchangeInfo_list", 0, -1);
        for (String key : bn_exchangeInfo) {
            klineStream(key);
        }
    }


    private void klineStream(String key) {
        WebsocketStreamClientImpl client = new WebsocketStreamClientImpl(DefaultUrls.WS_URL);
        client.klineStream(key, "15m", noopCallback, ((event) -> {
                    KlineTickerEvent klineTickerEvent = GsonUtils.toJavaObject(event, KlineTickerEvent.class);
                    // 打印k线数据
                    logger.info(JacksonUtil.jsonFromObject(klineTickerEvent));
                    KlineTicker klineTicker = klineTickerEvent.getKlineTickers();

                    if (!klineTicker.getIsklineClosed().equalsIgnoreCase("true")) {
                        return;
                    }
                    //redis数据存储格式

                    // 当前的收盘时间
                    String date = TomDateUtil.getDateTimePattern(new Date(Long.valueOf(klineTicker.getCloseTime()) + 1));

                    String redisKey = "15min" + "_" + klineTicker.getSymbol() + "_" + TomDateUtil.getDayPatternCurrentDay();

                    String before15MinutesKey = TomDateUtil.getDateTimePattern(TomDateUtil.getTimeBefore15Minutes(TomDateUtil.timestamp2LocalDateTime(Long.valueOf(klineTicker.getCloseTime()) + 1)));

                    Object before15Minutes = redisTemplate.opsForHash().get(redisKey, before15MinutesKey);
                    if (before15Minutes != null) {
                        String before15mBaseAssetVolume = (String) before15Minutes;
                        compare(redisKey,klineTicker.getSymbol() , date, before15mBaseAssetVolume, klineTicker.getBaseAssetVolume(), klineTicker.getQuoteAssetVolume());

                    } else {
                        Set<String> keys = redisTemplate.opsForHash().keys(redisKey);

                        Optional<String> first = keys.stream().sorted(Comparator.reverseOrder()).findFirst();
                        if (first.isPresent()) {
                            String before15mBaseAssetVolume = (String) redisTemplate.opsForHash().get(redisKey, before15MinutesKey);
                            compare(redisKey,klineTicker.getSymbol() , date, before15mBaseAssetVolume, klineTicker.getBaseAssetVolume(), klineTicker.getQuoteAssetVolume());
                        }
                    }
                    // 当前的成交量放进去
                    redisTemplate.opsForHash().put(redisKey, date, klineTicker.getBaseAssetVolume());
                    redisTemplate.expire(redisKey, 48, TimeUnit.HOURS);
                }),
                ((event) -> {
                    klineStream(key);
                }),
                ((event) -> {
                    klineStream(key);
                })
        );
    }


    public void compare(String redisKey, String symbol, String date, String before15mBaseAssetVolume, String baseAssetVolume, String quoteAssetVolume) {
        String alertKey = redisKey + "_alert";
        String preDayKey = "1h" + "_" + symbol + "_" + TomDateUtil.getBeforeDayPatternCurrentDay() + "_alert";
        Double radio = Double.valueOf(baseAssetVolume) / Double.valueOf(before15mBaseAssetVolume);


        if (radio > 3 & Double.valueOf(quoteAssetVolume) >= 1000000) {
            // 当前的成交量放进去
            redisTemplate.opsForHash().put(alertKey, date, String.valueOf(radio));
            redisTemplate.expire(redisKey, 48, TimeUnit.HOURS);


            Map entries = redisTemplate.opsForHash().entries(alertKey);
            Map preEntries = redisTemplate.opsForHash().entries(preDayKey);
            entries.putAll(preEntries);


            Map<String, String> map = new HashMap<>();
            map.put(date, String.valueOf(radio));
            alert(symbol + "[" + entries.size() + "]", map);
        }
    }


    public void alert(String alertKey, Map<String, String> map) {
        try {


            QyWeixinTextMsg.QyWeixinTextMsgContext context = new QyWeixinTextMsg.QyWeixinTextMsgContext();
            StringBuilder sBuilder = new StringBuilder("触发异动事件：" + alertKey + ":");
            for (String key : map.keySet()) {
                sBuilder.append("时间：" + key + " 成交量比率：" + map.get(key));
            }

            context.setContent(sBuilder.toString());
            QyWeixinTextMsg msg = new QyWeixinTextMsg();
            msg.setMsgtype("text");
            msg.setText(context);
            String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=e1defa64-8131-4ba9-87e0-b7cf970be2b7";
            httpClientProvider.doPost(url, msg);

        } catch (Exception e) {
            logger.error("处理告警时间失败", e);
        }
    }


    @Deprecated
    private void listen() {
        WebsocketStreamClientImpl client = new WebsocketStreamClientImpl();
        client.allRollingWindowTicker("1h", ((event) -> {
            List<OneHourTicker> oneHourTickers = GsonUtils.toJavaObject(event, new TypeToken<ArrayList<OneHourTicker>>() {
            });
            asyncExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    processEvent(oneHourTickers);
                    alert(oneHourTickers);
                }
            });
        }), ((event) -> {
            listen();
        }), ((event) -> {
            listen();
        }));
    }


    /**
     * 1、把时间数据存储到redis
     *
     * @param oneHourTickers
     */
    @Deprecated
    private void processEvent(List<OneHourTicker> oneHourTickers) {
        oneHourTickers.stream().forEach(
                a -> {
                    String date = TomDateUtil.getDateTimePattern(new Date(a.getOpenTime()));
                    String key = "1hTicker" + "_" + a.getSymbol() + "_" + TomDateUtil.getDayPatternCurrentDay();
                    redisTemplate.opsForHash().put(key, date, a.getBaseAssetVolume());
                    redisTemplate.expire(key, 48, TimeUnit.HOURS);
                }
        );
    }

    /**
     * 1.异常事件通知
     *
     * @param oneHourTickers
     */
    @Deprecated
    private void alert(List<OneHourTicker> oneHourTickers) {

        String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=e1defa64-8131-4ba9-87e0-b7cf970be2b7";
        oneHourTickers.stream().forEach(
                a -> {
                    try {
                        String key = "1hTicker" + "_" + a.getSymbol() + "_" + TomDateUtil.getDayPatternCurrentDay();
                        if (!a.getSymbol().endsWith("USDT")) {
                            return;
                        }
                        Map entries = redisTemplate.opsForHash().entries(key);
                        Collection values = entries.values();
                        String max = (String) values.stream().max(Comparator.reverseOrder()).orElse(null);
                        if (null == max) {
                            return;
                        }
                        Double radio = Double.valueOf(a.getBaseAssetVolume()) / Double.valueOf(max);
                        if (radio > 10 && Double.valueOf(a.getBaseAssetVolume()) > 108000 && Math.abs(Double.valueOf(a.getPriceChangePercent())) > 3) {
                            QyWeixinTextMsg.QyWeixinTextMsgContext context = new QyWeixinTextMsg.QyWeixinTextMsgContext();
                            context.setContent("触发异动事件：" + a.getSymbol() + "---> 比率：" + radio + "---> 价格浮动：" + Double.valueOf(a.getPriceChangePercent()));
                            QyWeixinTextMsg msg = new QyWeixinTextMsg();
                            msg.setMsgtype("text");
                            msg.setText(context);

                            httpClientProvider.doPost(url, msg);
                        }
                    } catch (Exception e) {
                        logger.error("处理告警时间失败", e);
                    }
                }
        );
    }


}
