package com.caicongyang.rest;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Import(RestTemplateInterceptorBeanPostProcessor.class)
@EnableConfigurationProperties(RestInterceptorProperties.class)
public class RestMockConfiguration {


}
