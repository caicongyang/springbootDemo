package com.caicongyang.sklywalking.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.*;

public class RestTemplateTraceInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateTraceInterceptor.class);
    private static final int RESPONSE_LENGTH_LIMIT = 400000;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        beforeLog(request);
        ClientHttpResponse response = execution.execute(request, body);

        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode == null || statusCode.is5xxServerError() || statusCode.is4xxClientError()) {
            byte[] responseBody = getResponseBody(response);
            onError(statusCode != null ? String.valueOf(statusCode.value()) : "500", responseBody);
        } else {
            ByteArrayOutputStream baos = cloneInputStream(response.getBody());
            String responseTxt = baos.toString("UTF-8");
            if (responseTxt.length() > RESPONSE_LENGTH_LIMIT) {
                responseTxt = responseTxt.substring(0, RESPONSE_LENGTH_LIMIT) + "......";
            }
            onResult(responseTxt);
            response = wrapResponse(response, new ByteArrayInputStream(baos.toByteArray()));
        }
        return response;
    }

    private byte[] getResponseBody(ClientHttpResponse response) {
        try { return response.getBody().readAllBytes(); }
        catch (IOException ex) { logger.error("获取返回值异常：", ex); }
        return new byte[0];
    }

    private ClientHttpResponse wrapResponse(ClientHttpResponse original, InputStream body) {
        return new ClientHttpResponse() {
            @Override public HttpStatusCode getStatusCode() throws IOException { return original.getStatusCode(); }
            @Override public int getRawStatusCode() throws IOException { return original.getRawStatusCode(); }
            @Override public String getStatusText() throws IOException { return original.getStatusText(); }
            @Override public void close() { original.close(); }
            @Override public InputStream getBody() { return body; }
            @Override public HttpHeaders getHeaders() { return original.getHeaders(); }
        };
    }

    private ByteArrayOutputStream cloneInputStream(InputStream input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) baos.write(buffer, 0, len);
            baos.flush();
            return baos;
        } catch (IOException e) { return new ByteArrayOutputStream(); }
    }

    private void onError(String code, byte[] error) { logger.warn("RestTemplate error: code={}", code); }
    private void onResult(String responseContent) { logger.debug("RestTemplate response: {} chars", responseContent != null ? responseContent.length() : 0); }
    private void beforeLog(HttpRequest request) { logger.debug("RestTemplate request: {} {}", request.getMethod(), request.getURI()); }
}
