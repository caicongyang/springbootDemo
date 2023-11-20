package com.caicongyang.stock.service;


import com.caicongyang.httper.HttpClientProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

@Service
public class BNMarketService {

    private static final Logger logger = LoggerFactory.getLogger(BNMarketService.class);


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ExecutorService asyncExecutor;

    @Autowired
    HttpClientProvider httpClientProvider;

    @PostConstruct
    public void init() {

    }



}
