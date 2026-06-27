package com.caicongyang.cache;

public class RedisProxy {

    public RedisCache redisCache;

    public RedisProxy(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    public Object get(String key) {
        return redisCache.get(key);
    }
}
