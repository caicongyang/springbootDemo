package com.caicongyang;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "nacos.shutdown.gracefule.enable", havingValue = "true")
public class NotifierAutoconfiguration {

    @Bean
    @ConditionalOnProperty(name = "nacos.shutdown.gracefule.type", havingValue = "loadBalancer")
    public LoadBalancerServiceChangeNotifier loadBalancerServiceChangeNotifier() {
        return new LoadBalancerServiceChangeNotifier();
    }

    @Bean
    @ConditionalOnProperty(name = "nacos.shutdown.gracefule.type", havingValue = "ribbon")
    public RibbonServiceChangeNotifier ribbonServiceChangeNotifier() {
        return new RibbonServiceChangeNotifier();
    }
}
