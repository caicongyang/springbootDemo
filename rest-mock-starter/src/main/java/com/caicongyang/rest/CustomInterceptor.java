package com.caicongyang.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

public class CustomInterceptor implements ClientHttpRequestInterceptor {



    private RestInterceptorProperties properties;


    public  CustomInterceptor(RestInterceptorProperties properties){
        this.properties = properties;
    }


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {


        if(CollectionUtils.isEmpty(properties.getUrlMap())){
            return execution.execute(request, body);
        }

        // originalUrl
        String originalUrl = request.getURI().toString();
        String domainUrl = originalUrl.substring(0, originalUrl.indexOf("/"));

        Set<String> originalUrlSet= properties.getUrlMap().keySet();
        if(!originalUrlSet.contains(domainUrl)){
            return execution.execute(request, body);
        }

        // 替换 URL
        String replaceUrl = originalUrl.replace(domainUrl,properties.getUrlMap().get(domainUrl));

        HttpRequest modifiedRequest = new ModifiableRequestWrapper(request, replaceUrl);
        // 执行请求
        return execution.execute(modifiedRequest, body);
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
