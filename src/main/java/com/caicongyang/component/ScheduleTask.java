package com.caicongyang.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static org.aspectj.bridge.Version.getTime;

@Component
public class ScheduleTask {


    private static final Logger logger = LoggerFactory.getLogger(ScheduleTask.class);

    /**
     * 从第0秒开始，每隔5秒执行一次
     */
    @Scheduled(cron = "0/5 * * * * *")
    public void test() {
        logger.info(getTime() + " cron");
    }

}
