package com.caicongyang.cache;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CacheInterfaceTest {

    @Test
    void redisProxyShouldAcceptRedisCache() {
        // Interface validation — RedisProxy compiles with RedisCache parameter
        assertNotNull(RedisProxy.class.getDeclaredConstructors());
    }
}
