package com.caicongyang.core.conf;

import com.caicongyang.core.filter.XSSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caicongyang
 */
@Configuration
public class XssFilterAtuoConfiguration {


    @Bean
    public FilterRegistrationBean xssFiltrRegister() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new XSSFilter());
        registration.addUrlPatterns("/*");
        registration.setName("XSSFilter");
        registration.setOrder(1);
        return registration;
    }


}
