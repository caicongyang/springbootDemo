package com.caicongyang.lock.config;

import com.caicongyang.lock.RedissionDistributedLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RedisTemplateConfig.class})
public class RedissionDistributedLockAutoConfig {

    @Bean
    @ConditionalOnExpression("#{'true'.equals(environment['conditional.locker'])}")
    //${spring.redis.single.address} == null|| ${spring.redis.host} != null || ${spring.redis.sentinel.nodes} !=null || ${spring.redis.cluster.nodes} !=null || ${spring.redis.url}!=null
    public RedissionDistributedLock redissionDistributedLock() {
        return new RedissionDistributedLock();
    }

}
