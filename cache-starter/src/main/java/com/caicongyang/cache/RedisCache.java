package com.caicongyang.cache;

import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

public class RedisCache implements Cache {

    public RedisTemplate<Object, Object> template;


    public String prefix;

    public RedisCache(RedisTemplate<Object, Object> redisTemplate,Environment environment) {
        this.template = redisTemplate;
        // 需要在配置文件配置
        this.prefix = environment.getProperty("spring.application.name")+":";
    }


    @Override
    public Object get(String key) {
        return template.opsForValue().get(prefix+key);
    }

    @Override
    public void set(String key, Object value) {
        template.opsForValue().set(prefix+key, value);
    }

    @Override
    public void add4Set(String key, Object value) {
        if (template.opsForSet().size(prefix+key) > 1024) {
            throw new RuntimeException("set集合大小超过限制！");
        }
        template.opsForSet().add(prefix+key, value);
    }

    @Override
    public Set<Object> members4Set(String key) {
        Set<Object> members = template.opsForSet().members(prefix+key);
        return members;
    }
}
