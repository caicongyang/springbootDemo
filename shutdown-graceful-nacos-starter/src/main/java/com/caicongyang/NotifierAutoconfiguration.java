package com.caicongyang;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "ty.nacos.smooth.enable", havingValue = "true")
public class NotifierAutoconfiguration {

    @Bean
    @ConditionalOnProperty(name = "ty.nacos.smooth.cache.type", havingValue = "loadBalancer")
    public LoadBalancerServiceChangeNotifier loadBalancerServiceChangeNotifier() {
        return new LoadBalancerServiceChangeNotifier();
    }

    @Bean
    @ConditionalOnProperty(name = "ty.nacos.smooth.cache.type", havingValue = "ribbon")
    public RibbonServiceChangeNotifier ribbonServiceChangeNotifier() {
        return new RibbonServiceChangeNotifier();
    }
}
