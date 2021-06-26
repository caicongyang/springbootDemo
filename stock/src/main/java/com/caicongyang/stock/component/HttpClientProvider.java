package com.caicongyang.stock.component;

import com.caicongyang.stock.utils.jacksonUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HttpClientProvider {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientProvider.class);

    private static final String APPLICATION_JSON = "application/json";

    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";


    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();

    private static HttpClientBuilder httpClientBuilder = HttpClients.custom();

    static {
        int timeout = 5 * 1000;
        poolingHttpClientConnectionManager.setMaxTotal(300);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(30);
        //多久校验一次链接的有效性
        poolingHttpClientConnectionManager.setValidateAfterInactivity(5000);
        // 关闭过期链接
        poolingHttpClientConnectionManager.closeExpiredConnections();
        //3分钟关闭空闲链接
        poolingHttpClientConnectionManager.closeIdleConnections(3, TimeUnit.MINUTES);


        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout)
                .setSocketTimeout(timeout).setConnectionRequestTimeout(timeout).build();
        httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
        httpClientBuilder.setDefaultRequestConfig(config);
        httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
    }

    public CloseableHttpClient getHttpClient() {
        return httpClientBuilder.build();
    }

    public CloseableHttpResponse doRequest(HttpRequest httpRequest) throws IOException {
        return getHttpClient().execute((HttpUriRequest) httpRequest);
    }


    public String doPostWithApplicationJson(String url, Map<String, String> map) throws IOException {

        logger.info("doPostWithApplicationJson; url ={}, args = {}", url,
            jacksonUtils.jsonFromObject(map));
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            //关闭长连接
            httpPost.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
            httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);

            //解决中文乱码问题
            StringEntity entity = new StringEntity(jacksonUtils.jsonFromObject(map), "UTF-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType(APPLICATION_JSON);
            httpPost.setEntity(entity);
            closeableHttpResponse = doRequest(httpPost);

            if (200 != closeableHttpResponse.getStatusLine().getStatusCode()) {
                throw new HttpResponseException(closeableHttpResponse.getStatusLine().getStatusCode(), url);
            }
            return EntityUtils.toString(closeableHttpResponse.getEntity());

        } finally {
            if (null != closeableHttpResponse) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException ex) {
                    logger.error("close httpclient response ex", ex);
                }
            }
        }
    }


    public String doPostWithFormsubmit(String url, Map<String, String> map) throws IOException {
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            StringBuilder urlBuf = new StringBuilder(url).append("?");
            int index = 0;
            for (String key : map.keySet()) {
                if (index++ > 0) {
                    urlBuf.append("&");
                }
                urlBuf.append(key).append("=").append(URLEncoder.encode(map.get(key), "UTF-8"));
            }
            HttpPost httpPost = new HttpPost(urlBuf.toString());
            httpPost.addHeader(HTTP.CONTENT_TYPE, FORM_URLENCODED);

            logger.info("http  doRequest:{}", urlBuf.toString());
            closeableHttpResponse = doRequest(httpPost);

            if (200 != closeableHttpResponse.getStatusLine().getStatusCode()) {
                throw new HttpResponseException(closeableHttpResponse.getStatusLine().getStatusCode(), url);
            }
            return EntityUtils.toString(closeableHttpResponse.getEntity());
        } finally {

            if (null != closeableHttpResponse) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException ex) {
                    logger.error("close httpclient response ex", ex);
                }
            }
        }
    }
}
