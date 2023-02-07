package com.caicongyang.cache;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

public class RedisCache implements Cache {

    public RedisTemplate<Object, Object> template;


    public RedisCache(RedisTemplate<Object, Object> redisTemplate) {
        this.template = redisTemplate;
    }


    @Override
    public Object get(String key) {
        return  template.opsForValue().get(key);
    }

    @Override
    public void set(String key, Object value) {
        template.opsForValue().set(key, value);
    }

    @Override
    public void add4Set(String key, Object value) {
        if (template.opsForSet().size(key) > 1024) {
            throw new RuntimeException("set集合大小超过限制！");
        }
        template.opsForSet().add(key, value);
    }

    @Override
    public Set<Object> members4Set(String key) {
        Set<Object> members = template.opsForSet().members(key);
        return members;
    }
}
