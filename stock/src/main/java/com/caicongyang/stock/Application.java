package com.caicongyang.stock;

import io.micrometer.core.instrument.MeterRegistry;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot web程序主入口
 *
 * @author caicongyang
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
@MapperScan("com.caicongyang.stock.mapper")
public class Application {
    public static void main(String[] args) {
        //第一个简单的应用，   
        SpringApplication.run(Application.class, args);
    }

    @Bean
    MeterRegistryCustomizer meterRegistryCustomizer(MeterRegistry meterRegistry) {
        return meterRegistry1 -> {
            meterRegistry.config()
                    .commonTags("application", "stock");
        };
    }

}
