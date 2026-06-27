package com.caicongyang.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class CustomInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(CustomInterceptor.class);
    private final RestInterceptorProperties properties;

    public CustomInterceptor(RestInterceptorProperties properties) {
        this.properties = properties;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (CollectionUtils.isEmpty(properties.getUrlMap())) {
            return execution.execute(request, body);
        }

        String originalUrl = request.getURI().toString();
        String domainUrl = extractDomain(originalUrl);

        Set<String> configUrlMap = properties.getUrlMap().keySet();
        if (!configUrlMap.contains(domainUrl)) {
            return execution.execute(request, body);
        }

        String replaceUrl = originalUrl.replace(domainUrl, properties.getUrlMap().get(domainUrl));
        HttpRequest modifiedRequest = new ModifiableRequestWrapper(request, replaceUrl);
        return execution.execute(modifiedRequest, body);
    }

    public String extractDomain(String urlString) {
        try {
            URL url = new URL(urlString);
            return url.getHost();
        } catch (MalformedURLException e) {
            logger.error("get extractDomain err:", e);
            return null;
        }
    }

    private static class ModifiableRequestWrapper implements HttpRequest {
        private final HttpRequest request;
        private final String uri;

        ModifiableRequestWrapper(HttpRequest request, String uri) {
            this.request = request;
            this.uri = uri;
        }

        @Override
        public HttpHeaders getHeaders() {
            return request.getHeaders();
        }

        @Override
        public HttpMethod getMethod() {
            return request.getMethod();
        }

        @Override
        public URI getURI() {
            return URI.create(uri);
        }

        @Override
        public Map<String, Object> getAttributes() {
            return request.getAttributes();
        }
    }
}
