package com.caicongyang.sklywalking.config;


import com.caicongyang.sklywalking.common.TraceProperties;
import com.caicongyang.sklywalking.http.TraceInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TraceProperties.class)
public class ExpandAutoConfig {


    @Bean
    public TraceInterceptor traceInterceptor(TraceProperties traceProperties, GitProperties gitProperties) {
        return new TraceInterceptor(traceProperties, gitProperties);
    }

}
