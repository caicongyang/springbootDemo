package com.caicongyang.core.conf;

import com.caicongyang.core.advice.ExceptionControllerAdvice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caicongyang
 */
@Configuration
public class ExceptionAutoConfiguration {

    @Bean
    public ExceptionControllerAdvice exceptionControllerAdvice() {
        return new ExceptionControllerAdvice();
    }

}
