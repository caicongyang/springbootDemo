package com.caicongyang.rest;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;

public class CustomInterceptor implements ClientHttpRequestInterceptor {

    private static Logger logger = LoggerFactory.getLogger(CustomInterceptor.class);


    private RestInterceptorProperties properties;


    public CustomInterceptor(RestInterceptorProperties properties) {
        this.properties = properties;
    }


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {


        if (CollectionUtils.isEmpty(properties.getUrlMap())) {
            return execution.execute(request, body);
        }

        // originalUrl
        String originalUrl = request.getURI().toString();
        String domainUrl = extractDomain(originalUrl);

        Set<String> originalUrlSet = properties.getUrlMap().keySet();
        if (!originalUrlSet.contains(domainUrl)) {
            return execution.execute(request, body);
        }

        // 替换 URL
        String replaceUrl = originalUrl.replace(domainUrl, properties.getUrlMap().get(domainUrl));

        HttpRequest modifiedRequest = new ModifiableRequestWrapper(request, replaceUrl);
        // 执行请求
        return execution.execute(modifiedRequest, body);
    }

    public static String extractDomain(String urlString) {
        try {
            URL url = new URL(urlString);
            return url.getHost();
        } catch (MalformedURLException e) {
            logger.error("get extractDomain err:", e);
            return null;
        }
    }


    private class ModifiableRequestWrapper implements HttpRequest {
        private final HttpRequest request;
        private final String uri;

        public ModifiableRequestWrapper(HttpRequest request, String uri) {
            this.request = request;
            this.uri = uri;
        }

        @Override
        public HttpHeaders getHeaders() {
            return request.getHeaders();
        }

        @Override
        public String getMethodValue() {
            return request.getMethodValue();
        }

        @Override
        public URI getURI() {
            return URI.create(uri);
        }
    }


}
