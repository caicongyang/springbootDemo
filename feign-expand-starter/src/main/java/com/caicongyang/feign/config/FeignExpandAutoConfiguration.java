package com.caicongyang.feign.config;

import com.caicongyang.feign.FeignClientFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration for feign-expand-starter.
 * <p>
 * Enables FeignClients scanning and registers the FeignClientFactory bean
 * for programmatic proxy creation.
 */
@AutoConfiguration
@Import(FeignAutoConfiguration.class)
@EnableFeignClients
public class FeignExpandAutoConfiguration {

    @Bean
    public FeignClientFactory feignClientFactory() {
        return new FeignClientFactory();
    }
}
