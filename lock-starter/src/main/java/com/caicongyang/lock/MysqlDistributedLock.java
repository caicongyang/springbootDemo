package com.caicongyang.lock;

import com.caicongyang.lock.mapper.LockerMapper;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 基于mysql的锁实现 /需要手动unlock
 */
@Component
public class MysqlDistributedLock {


    private static final Logger logger = LoggerFactory.getLogger(MysqlDistributedLock.class);

    private static ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);


    private static final String LOCK_NAME_PREFIX = "lock.mysql.";


    @Resource
    LockerMapper lockerMapper;


    public Boolean getLock(String lockName, String lockDesc) {
        try {
            LockerResource resource = new LockerResource();
            resource.setResource(LOCK_NAME_PREFIX + lockName);
            resource
                .setDescription(StringUtils.isEmpty(lockDesc) ? lockName + new Date() : lockDesc);
            return lockerMapper.addResource(resource) == 1 ? true : false;
        } catch (DuplicateKeyException e) {
            logger.warn("获取{}数据库锁失败", lockName, e);
            return false;
        }
    }


    public Boolean unLock(String lockName) {
        LockerResource resource = new LockerResource();
        resource.setResource(LOCK_NAME_PREFIX + lockName);
        lockerMapper.deleteResource(resource);
        return true;
    }


    /**
     * 删除20分钟前无效的key
     */
    @PostConstruct
    public void init() {
        service.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //10分钟前的时间
                Date expireDate = new Date(System.currentTimeMillis() - 60 * 10 * 1000);
                lockerMapper.deleteUnuserResource(expireDate);
            }
        }, 10, 10, TimeUnit.MINUTES);

    }


    @PreDestroy
    public void preDestroy() throws InterruptedException {
        service.shutdownNow();
        service.awaitTermination(1, TimeUnit.MINUTES);
    }


}
