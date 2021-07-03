package com.caicongyang.lock.config;

import com.caicongyang.lock.RedissionDistributedLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RedisTemplateConfig.class})
public class RedissionDistributedLockAutoConfig {

    @Bean
    public RedissionDistributedLock redissionDistributedLock() {
        return new RedissionDistributedLock();
    }

}
