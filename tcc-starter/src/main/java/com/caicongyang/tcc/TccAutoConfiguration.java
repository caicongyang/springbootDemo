package com.caicongyang.tcc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TccProperties.class)
@ConditionalOnProperty(prefix = "tcc", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TccAutoConfiguration {

    @Bean
    public TccCoordinator tccCoordinator() {
        return new TccCoordinator();
    }

    @Bean
    public TccAspect tccAspect(TccCoordinator tccCoordinator) {
        return new TccAspect(tccCoordinator);
    }
}
