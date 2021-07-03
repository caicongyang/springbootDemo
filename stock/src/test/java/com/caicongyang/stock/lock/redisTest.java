package com.caicongyang.stock.lock;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.caicongyang.lock.RedissionDistributedLock;
import com.caicongyang.stock.BaseApplicationTest;
import java.util.concurrent.CountDownLatch;
import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;

public class redisTest extends BaseApplicationTest {


    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    RedissionDistributedLock distributedLock;


    @Test
    public void redisStringTest() {
        String putV = "niubi";
        String putK = "caicongyang1";
        redisTemplate.opsForValue().set(putK, putV);

        String value = redisTemplate.opsForValue().get(putK);
        assertEquals(putV, value);

    }


    public void backLockTest(Boolean isSameLock, Integer sleepMilliseconds) throws Exception {
        long time = System.currentTimeMillis();

        String[] lockNames = {"lock1", isSameLock ? "lock1" : "lock2"};
        final CountDownLatch endLatch = new CountDownLatch(lockNames.length);
        for (String lockName : lockNames) {
            new Thread() {
                public void run() {
                    try {
                        distributedLock.runWithLock(lockName, 10, 10, () -> {
                            System.out.println(lockName);
                            Thread.sleep(sleepMilliseconds);
                            endLatch.countDown();
                            return null;
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        endLatch.await();
        long cost = System.currentTimeMillis() - time;
        System.out.println("cost:" + cost);

        if (isSameLock) {
            Assertions.assertTrue(cost >= sleepMilliseconds * lockNames.length);
        } else {
            Assertions.assertTrue(cost < sleepMilliseconds * lockNames.length);
        }



    }


    @Test
    public void lockTest1() throws Exception {

        backLockTest(true, 1000 * 9);
    }


    @Test
    public void lockTest2() throws Exception {
        backLockTest(true, 1000 * 11);


    }





}
