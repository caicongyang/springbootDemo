package com.caicongyang.seq;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis-backed sequence generator using INCR.
 */
public class RedisSeqGenerator implements SeqGenerator {

    private static final String KEY_PREFIX = "seq:";

    private final StringRedisTemplate stringRedisTemplate;

    public RedisSeqGenerator(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public long nextId(String bizKey) {
        Long id = stringRedisTemplate.opsForValue().increment(KEY_PREFIX + bizKey);
        return id != null ? id : 0L;
    }
}
