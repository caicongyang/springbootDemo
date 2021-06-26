package com.caicongyang.core.conf;

import com.caicongyang.core.aspect.RequestLogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author caicongyang
 */
@Configuration
@EnableAspectJAutoProxy
public class RequestLogAutoConfiguration {


    @Bean
    public RequestLogAspect requestLogAspect() {
        return new RequestLogAspect();
    }
}


