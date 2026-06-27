package com.caicongyang.httper;

import com.caicongyang.core.exception.BusinessException;
import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class HttpClientProvider {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientProvider.class);
    private static final String APPLICATION_JSON = "application/json";
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final CloseableHttpClient httpClient;

    public HttpClientProvider() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(300);
        cm.setDefaultMaxPerRoute(30);
        cm.closeExpired();

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5, TimeUnit.SECONDS)
                .setResponseTimeout(5, TimeUnit.SECONDS)
                .build();

        this.httpClient = HttpClientBuilder.create()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(config)
                .build();
    }

    public String doPostWithApplicationJson(String url, Map<String, String> map) throws IOException {
        Gson gson = new Gson();
        logger.info("doPostWithApplicationJson; url={}, args={}", url, gson.toJson(map));

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", APPLICATION_JSON);
        httpPost.setEntity(new StringEntity(gson.toJson(map), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getCode() != 200) {
                throw new BusinessException(-1, "HTTP " + response.getCode() + " from " + url);
            }
            try {
                return EntityUtils.toString(response.getEntity());
            } catch (ParseException e) {
                throw new IOException("Failed to parse response entity", e);
            }
        }
    }

    public String doPost(String url, Object ob) throws IOException {
        Gson gson = new Gson();
        logger.info("doPost; url={}, args={}", url, gson.toJson(ob));

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", APPLICATION_JSON);
        httpPost.setEntity(new StringEntity(gson.toJson(ob), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getCode() != 200) {
                throw new BusinessException(-1, "HTTP " + response.getCode() + " from " + url);
            }
            try {
                return EntityUtils.toString(response.getEntity());
            } catch (ParseException e) {
                throw new IOException("Failed to parse response entity", e);
            }
        }
    }

    public String doPostWithFormSubmit(String url, Map<String, String> map) throws IOException {
        StringBuilder urlBuf = new StringBuilder(url).append("?");
        int index = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (index++ > 0) urlBuf.append("&");
            urlBuf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        HttpPost httpPost = new HttpPost(urlBuf.toString());
        httpPost.setHeader("Content-Type", FORM_URLENCODED);

        logger.info("http doRequest: {}", urlBuf);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getCode() != 200) {
                throw new BusinessException(-1, "HTTP " + response.getCode() + " from " + url);
            }
            try {
                return EntityUtils.toString(response.getEntity());
            } catch (ParseException e) {
                throw new IOException("Failed to parse response entity", e);
            }
        }
    }
}
