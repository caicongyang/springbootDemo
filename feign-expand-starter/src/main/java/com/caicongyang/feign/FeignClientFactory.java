package com.caicongyang.feign;

import feign.Feign;
import feign.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory that creates FeignClient proxies at runtime from an interface class.
 * Supports both declarative (via &#64;FeignClient) and programmatic creation.
 */
@Component
public class FeignClientFactory {

    @Autowired
    private Feign.Builder feignBuilder;

    /**
     * Create a Feign client proxy for the given interface, targeting the provided URL.
     *
     * @param apiType the client interface class
     * @param url     the base URL for the target service
     * @param <T>     the interface type
     * @return a dynamic proxy implementing the interface and dispatching to the URL
     */
    public <T> T createClient(Class<T> apiType, String url) {
        Target<T> target = new Target.HardCodedTarget<>(apiType, url);
        return feignBuilder.target(target);
    }
}
