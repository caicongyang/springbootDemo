package com.caicongyang.stock.service;


import com.binance.connector.client.enums.DefaultUrls;
import com.binance.connector.client.impl.WebsocketStreamClientImpl;
import com.caicongyang.core.utils.GsonUtils;
import com.caicongyang.httper.HttpClientProvider;
import com.caicongyang.stock.domain.OneHourTicker;
import com.caicongyang.stock.domain.QyWeixinMsg;
import com.caicongyang.stock.domain.QyWeixinTextMsg;
import com.caicongyang.stock.utils.TomDateUtils;
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

@Service
public class BNService {

    private static final Logger logger = LoggerFactory.getLogger(BNService.class);


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ExecutorService asyncExecutor;

    @Autowired
    HttpClientProvider httpClientProvider;

    @PostConstruct
    public void init() {
        listen();
    }

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
    private void processEvent(List<OneHourTicker> oneHourTickers) {
        oneHourTickers.stream().forEach(
                a -> {
                    String date = TomDateUtils.getDateTimePattern(new Date(a.getOpenTime()));
                    String key = "1hTicker" + "_" + a.getSymbol() + "_" + TomDateUtils.getDayPatternCurrentDay();
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
    private void alert(List<OneHourTicker> oneHourTickers) {

        String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=e1defa64-8131-4ba9-87e0-b7cf970be2b7";
        oneHourTickers.stream().forEach(
                a -> {
                    try {
                        String key = "1hTicker" + "_" + a.getSymbol() + "_" + TomDateUtils.getDayPatternCurrentDay();
                        if(!a.getSymbol().endsWith("USDT")){
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
                            context.setContent("触发异动事件：" + a.getSymbol() + "---> 比率：" + radio +"---> 价格浮动："+Double.valueOf(a.getPriceChangePercent()) );
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
