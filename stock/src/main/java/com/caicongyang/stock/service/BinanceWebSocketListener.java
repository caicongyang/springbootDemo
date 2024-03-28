package com.caicongyang.stock.service;

import com.caicongyang.core.utils.GsonUtils;
import com.caicongyang.httper.HttpClientProvider;
import com.caicongyang.stock.domain.KlineTicker;
import com.caicongyang.stock.domain.KlineTickerEvent;
import com.caicongyang.stock.domain.MuiltStremKlineTickerEvent;
import com.caicongyang.stock.domain.QyWeixinTextMsg;
import com.caicongyang.stock.utils.JacksonUtil;
import com.caicongyang.stock.utils.TomDateUtil;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class BinanceWebSocketListener extends WebSocketListener {

    private static final Logger logger = LoggerFactory.getLogger(BinanceWebSocketListener.class);


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ExecutorService asyncExecutor;

    @Autowired
    HttpClientProvider httpClientProvider;


    @Autowired
    FifteenMinBNService fifteenMinBNService;


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {

        asyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                MuiltStremKlineTickerEvent event = GsonUtils.toJavaObject(text, MuiltStremKlineTickerEvent.class);
                // handle msg
                try {
                    handle15MinKlineEvent(event.getData());
                } catch (Exception e) {
                    logger.error("处理消息错误",e);
                }
            }
        });

    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {


    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        logger.error("CLOSE: " + code + " " + reason);
        fifteenMinBNService.kline();
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        logger.error("Failure: " + t.getMessage());
    }


    public void handle15MinKlineEvent(KlineTickerEvent klineTickerEvent) {

        KlineTicker klineTicker = klineTickerEvent.getKlineTickers();

        if (!klineTicker.getIsklineClosed().equalsIgnoreCase("true")) {
            return;
        }
        // 打印k线数据
        logger.info(JacksonUtil.jsonFromObject(klineTickerEvent));
        //redis数据存储格式
        // 当前k线的收盘时间
        String date = TomDateUtil.getDateTimePattern(new Date(Long.valueOf(klineTicker.getCloseTime()) + 1));
        String redisKey = "15min" + "_" + klineTicker.getSymbol() + "_" + TomDateUtil.getDayPatternCurrentDay();

        //此消息已经处理
        if(Objects.nonNull(redisTemplate.opsForHash().get(redisKey, date))){
            return;
        }

        // 当前的成交量放进去
        redisTemplate.opsForHash().put(redisKey, date, klineTicker.getBaseAssetVolume());
        redisTemplate.expire(redisKey, 48, TimeUnit.HOURS);

        // 前15分钟的时间
        String before15MinutesKey = TomDateUtil.getDateTimePattern(TomDateUtil.getTimeBefore15Minutes(TomDateUtil.timestamp2LocalDateTime(Long.valueOf(klineTicker.getCloseTime()) + 1)));

        Object before15Minutes = redisTemplate.opsForHash().get(redisKey, before15MinutesKey);
        if (before15Minutes != null) {
            String before15mBaseAssetVolume = (String) before15Minutes;
            compare(redisKey, klineTicker.getSymbol(), date, before15mBaseAssetVolume, klineTicker.getBaseAssetVolume(), klineTicker.getQuoteAssetVolume());
        } else {
            Set<String> keys = redisTemplate.opsForHash().keys(redisKey);
            Optional<String> first = keys.stream().sorted(Comparator.reverseOrder()).findFirst();
            if (first.isPresent()) {
                String before15mBaseAssetVolume = (String) redisTemplate.opsForHash().get(redisKey, first.get());
                if(Objects.nonNull(before15mBaseAssetVolume)) {
                    compare(redisKey, klineTicker.getSymbol(), date, before15mBaseAssetVolume, klineTicker.getBaseAssetVolume(), klineTicker.getQuoteAssetVolume());
                }else {
                    logger.error("没有获取到当日最近的成交量："+first.get());
                }
            }
        }
    }


    public void compare(String redisKey, String symbol, String date, String before15mBaseAssetVolume, String baseAssetVolume, String quoteAssetVolume) {
        String alertKey = redisKey + "_alert";
        String preDayKey = "15min" + "_" + symbol + "_" + TomDateUtil.getBeforeDayPatternCurrentDay() + "_alert";
        Double radio = Double.valueOf(baseAssetVolume) / Double.valueOf(before15mBaseAssetVolume);


        if (radio > 3 & Double.valueOf(quoteAssetVolume) >= 1000000) {
            // 当前的成交量放进去
            redisTemplate.opsForHash().put(alertKey, date, String.valueOf(radio));
            redisTemplate.expire(redisKey, 48, TimeUnit.HOURS);


            Map entries = redisTemplate.opsForHash().entries(alertKey);
            Map preEntries = redisTemplate.opsForHash().entries(preDayKey);
            entries.putAll(preEntries);


            Map<String, String> map = new HashMap<>();
            map.put(date,  new BigDecimal(radio).setScale(2, RoundingMode.UP).toString());
            alert(symbol + " [48小时异动次数:" + entries.size() + "]", map);
        }
    }


    public void alert(String alertKey, Map<String, String> map) {
        try {
            QyWeixinTextMsg.QyWeixinTextMsgContext context = new QyWeixinTextMsg.QyWeixinTextMsgContext();
            StringBuilder sBuilder = new StringBuilder(alertKey + "-->");
            for (String key : map.keySet()) {
                sBuilder.append("最新触发时间：" + key + " 成交量比率：" + map.get(key));
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

    @PreDestroy
    public void destroy() {
        asyncExecutor.shutdown();
        try {
            // 等待线程池终止
            if (!asyncExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                // 如果在超时时间内线程池未终止，可以选择调用 shutdownNow()
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("线程池关闭异常：", e);
        }
    }


}
