package com.caicongyang.cache;

import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;

public class RedisProxy {

    public RedisCache redisCache;

    public RedisProxy(RedisCache redisCache) {
        this.redisCache = redisCache;
    }


    public Object get(String key){
       Object value =  redisCache.get(key);
        ActiveSpan.tag("cacheKey",key);
        ActiveSpan.tag("cacheValue", JacksonUtils.jsonFromObject(value));
        return  value;
    }



}
