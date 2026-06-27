package com.caicongyang.lock;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RedissionDistributedLockTest {

    @Test
    void lockNameShouldBeSet() {
        assertNotNull(RedissionDistributedLock.class);
    }
}
