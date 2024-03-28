package com.caicongyang.rest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateInterceptorBeanPostProcessor implements BeanPostProcessor {


    private RestInterceptorProperties properties;

    public RestTemplateInterceptorBeanPostProcessor(RestInterceptorProperties properties) {
        this.properties = properties;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            RestTemplate restTemplate = (RestTemplate) bean;
            restTemplate.getInterceptors().add(new CustomInterceptor(properties));
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


}
