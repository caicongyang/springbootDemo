package com.caicongyang.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties("rest.interceptor")
public class RestInterceptorProperties {

    /**
     * key:  originalUrl
     * value: replaceUrl
     */
    private Map<String, String> urlMap;


    public Map<String, String> getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(Map<String, String> urlMap) {
        this.urlMap = urlMap;
    }
}
