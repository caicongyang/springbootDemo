package com.caicongyang.cache;

public class RedisProxy {

    public RedisCache redisCache;

    public RedisProxy(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    public Object get(String key) {
        return redisCache.get(key);
    }

    public void set(String key, Object value) {
        redisCache.set(key, value);
    }

    public boolean exists(String key) {
        return redisCache.exists(key);
    }

    public boolean delete(String key) {
        return redisCache.delete(key);
    }
}
