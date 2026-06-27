package com.caicongyang.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisCache implements Cache {

    private static final Logger log = LoggerFactory.getLogger(RedisCache.class);

    /** 大 key 阈值（字节），默认 10KB */
    private static final long BIG_KEY_THRESHOLD = 10 * 1024;

    public RedisTemplate<Object, Object> template;
    public String prefix;

    public RedisCache(RedisTemplate<Object, Object> redisTemplate, Environment environment) {
        this.template = redisTemplate;
        this.prefix = environment.getProperty("spring.application.name", "app") + ":";
    }

    private String fullKey(String key) {
        return prefix + key;
    }

    @Override
    public Object get(String key) {
        String fk = fullKey(key);
        warnBigKey(fk);
        return template.opsForValue().get(fk);
    }

    @Override
    public void set(String key, Object value) {
        template.opsForValue().set(fullKey(key), value);
        checkBigKey(fullKey(key), value);
    }

    @Override
    public void set(String key, Object value, long timeoutSeconds) {
        template.opsForValue().set(fullKey(key), value, timeoutSeconds, TimeUnit.SECONDS);
        checkBigKey(fullKey(key), value);
    }

    @Override
    public void add4Set(String key, Object value) {
        String fk = fullKey(key);
        Long size = template.opsForSet().size(fk);
        if (size != null && size > 1024) {
            throw new RuntimeException("set集合大小超过限制！当前: " + size);
        }
        template.opsForSet().add(fk, value);
    }

    @Override
    public Set<Object> members4Set(String key) {
        String fk = fullKey(key);
        warnBigKey(fk);
        return template.opsForSet().members(fk);
    }

    @Override
    public long size(String key) {
        String fk = fullKey(key);
        Long size = template.opsForValue().size(fk);
        return size != null ? size : 0;
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(template.hasKey(fullKey(key)));
    }

    @Override
    public boolean delete(String key) {
        return Boolean.TRUE.equals(template.delete(fullKey(key)));
    }

    /** 检查写入的值是否为大 key */
    private void checkBigKey(String fullKey, Object value) {
        if (value instanceof String s && s.length() > BIG_KEY_THRESHOLD) {
            log.warn("[BIG-KEY] 写入字符串过大 key={}, size={} bytes", fullKey, s.length());
        }
    }

    /** 读取时检查已存在的 key 是否过大 */
    private void warnBigKey(String fullKey) {
        Long size = template.opsForValue().size(fullKey);
        if (size != null && size > BIG_KEY_THRESHOLD) {
            log.warn("[BIG-KEY] 读取大 key={}, size={} bytes", fullKey, size);
        }
    }
}
