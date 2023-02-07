package com.caicongyang.cache.config;

import com.caicongyang.cache.RedisCache;
import com.caicongyang.cache.RedisProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class CacheAutoConfiguration {

    @Bean
    public RedisCache redisCache(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisCache(redisTemplate);
    }


    @Bean
    public RedisProxy redisProxy(RedisCache redisCache) {
        return new RedisProxy(redisCache);
    }

}
