package com.caicongyang.stock.service;


import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebsocketStreamClientImpl;
import com.binance.connector.client.impl.spot.Market;
import com.caicongyang.core.utils.GsonUtils;
import com.caicongyang.httper.HttpClientProvider;
import com.caicongyang.stock.domain.ExchangeInfo;
import com.caicongyang.stock.domain.OneHourTicker;
import com.caicongyang.stock.domain.QyWeixinTextMsg;
import com.caicongyang.stock.utils.JacksonUtil;
import com.caicongyang.stock.utils.TomDateUtil;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FifteenMinBNService {

    private static final Logger logger = LoggerFactory.getLogger(FifteenMinBNService.class);


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ExecutorService asyncExecutor;

    @Autowired
    HttpClientProvider httpClientProvider;

    @Autowired
    BinanceWebSocketListener listener;


    private static OkHttpClient client = new OkHttpClient();


    @PostConstruct
    public void init() {
        initMarketInfo();
        kline();
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


    public void kline() {
        List<String> bn_exchangeInfo = (List<String>) redisTemplate.opsForList().range("bn_exchangeInfo_list", 0, -1);

        Map<Integer, List<String>> groupListMap = groupListByCount(bn_exchangeInfo, 50);
        for (Integer key : groupListMap.keySet()) {

            String parameter = groupListMap.get(key).stream().map(a -> a.toLowerCase()).map(a -> a + "@kline_15m").collect(Collectors.joining("/"));
            String url = "https://stream.binance.com:9443/stream?streams=" + parameter;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            logger.info(url);
            WebSocket ws = client.newWebSocket(request, listener);

        }
    }


    public static <T> Map<Integer, List<T>> groupListByCount(List<T> list, int groupSize) {
        return IntStream.range(0, (list.size() + groupSize - 1) / groupSize)
                .boxed()
                .collect(Collectors.toMap(
                        i -> i + 1,
                        i -> list.subList(i * groupSize, Math.min((i + 1) * groupSize, list.size()))
                ));
    }


//    private void klineStream(String key) {
//        WebsocketStreamClientImpl client = new WebsocketStreamClientImpl(DefaultUrls.WS_URL);
//        client.klineStream(key, "15m", noopCallback, ((event) -> {
//                    handle15MinKlineEvent(event);
//                }),
//                ((event) -> {
//                    klineStream(key);
//                }),
//                ((event) -> {
//                    klineStream(key);
//                })
//        );
//    }


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

    @PreDestroy
    public void destroy() {
        // Trigger shutdown of the dispatcher's executor so the program can exit
        client.dispatcher().executorService().shutdown();
    }


}
