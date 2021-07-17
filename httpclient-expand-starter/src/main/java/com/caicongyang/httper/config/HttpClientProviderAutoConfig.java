package com.caicongyang.httper.config;

import com.caicongyang.httper.HttpClientProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientProviderAutoConfig {

    @Bean
    public HttpClientProvider httpClientProvider() {
        return new HttpClientProvider();
    }


}
