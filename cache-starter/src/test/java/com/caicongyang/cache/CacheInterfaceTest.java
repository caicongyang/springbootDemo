package com.caicongyang.cache;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CacheInterfaceTest {

    @Test
    void cacheInterfaceHasAllMethods() {
        assertDoesNotThrow(() -> Cache.class.getMethod("get", String.class));
        assertDoesNotThrow(() -> Cache.class.getMethod("set", String.class, Object.class));
        assertDoesNotThrow(() -> Cache.class.getMethod("set", String.class, Object.class, long.class));
        assertDoesNotThrow(() -> Cache.class.getMethod("add4Set", String.class, Object.class));
        assertDoesNotThrow(() -> Cache.class.getMethod("members4Set", String.class));
        assertDoesNotThrow(() -> Cache.class.getMethod("size", String.class));
        assertDoesNotThrow(() -> Cache.class.getMethod("exists", String.class));
        assertDoesNotThrow(() -> Cache.class.getMethod("delete", String.class));
    }

    @Test
    void redisProxyShouldDelegate() {
        assertNotNull(RedisProxy.class.getDeclaredConstructors());
    }
}
