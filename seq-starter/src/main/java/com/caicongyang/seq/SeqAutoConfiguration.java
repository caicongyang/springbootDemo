package com.caicongyang.seq;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableConfigurationProperties(SeqProperties.class)
public class SeqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SeqGenerator.class)
    @ConditionalOnProperty(name = "seq.type", havingValue = "snowflake", matchIfMissing = true)
    public SeqGenerator snowflakeSeqGenerator(SeqProperties properties) {
        return new SnowflakeSeqGenerator(properties.getWorkerId(), properties.getDatacenterId());
    }

    @Bean
    @ConditionalOnMissingBean(SeqGenerator.class)
    @ConditionalOnClass(JdbcTemplate.class)
    @ConditionalOnProperty(name = "seq.type", havingValue = "db")
    public SeqGenerator databaseSeqGenerator(JdbcTemplate jdbcTemplate) {
        return new DatabaseSeqGenerator(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(SeqGenerator.class)
    @ConditionalOnClass(StringRedisTemplate.class)
    @ConditionalOnProperty(name = "seq.type", havingValue = "redis")
    public SeqGenerator redisSeqGenerator(StringRedisTemplate stringRedisTemplate) {
        return new RedisSeqGenerator(stringRedisTemplate);
    }
}
