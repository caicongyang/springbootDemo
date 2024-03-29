package com.caicongyang.dubbo;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DubboMockProperties.class)
public class DubboMockConfiguration {
}
