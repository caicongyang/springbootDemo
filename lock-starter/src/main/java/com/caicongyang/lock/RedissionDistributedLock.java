package com.caicongyang.lock;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedissionDistributedLock {

    private static final String LOCK_NAME_PREFIX = "lock.";


    @Autowired
    RedissonClient redissonClient;


    /**
     * 使用分布式锁运行callable代码。
     *
     * @param lockName 锁的名称。以名称区分锁。
     * @param maxWaitSeconds 等待获得锁的最大时间，单位：秒
     * @param lockExpiredSeconds 锁的超时时间。单位：秒
     * @param callable 要运行的代码。
     * @param <T> 返回值的类型
     * @return 返回callable返回的值。
     */
    public <T> T runWithLock(String lockName, int maxWaitSeconds, int lockExpiredSeconds,
        Callable<T> callable) throws InterruptedException, Exception {
        RLock lock = redissonClient.getLock(getFullLockName(lockName));

        boolean success = false;

        success = lock.tryLock(maxWaitSeconds, lockExpiredSeconds, TimeUnit.SECONDS);
        if (success) {
            try {
                return callable.call();
            } finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
            }
            }
        } else {
            throw new RuntimeException(
                "Failed to get distributed lock:" + lockName + " in seconds:" + maxWaitSeconds);
        }
    }


    private String getFullLockName(String lockName) {
        return LOCK_NAME_PREFIX + lockName;
    }

}
