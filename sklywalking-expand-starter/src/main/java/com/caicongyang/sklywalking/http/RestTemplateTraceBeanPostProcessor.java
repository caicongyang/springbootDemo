package com.caicongyang.sklywalking.http;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 注入RestTemplate 拦截器
 */
public class RestTemplateTraceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            RestTemplate rt = (RestTemplate) bean;
            List<ClientHttpRequestInterceptor> interceptors = rt.getInterceptors();
            interceptors.add(new RestTemplateTraceInterceptor());
            rt.setInterceptors(interceptors);
        }
        return bean;
    }
}
