package com.caicongyang.dubbo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;


@ConfigurationProperties("dubbo.mock")
public class DubboMockProperties {

    /**
     * key:  interface
     * value: replaceUrl
     *
     * ex ： dubbo.mock.com.1233s2b.com.add=dubbo://127.0.0.1:2880
     *
     *
     *
     */
    private Map<String, String> urlMap;


    public Map<String, String> getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(Map<String, String> urlMap) {
        this.urlMap = urlMap;
    }
}
