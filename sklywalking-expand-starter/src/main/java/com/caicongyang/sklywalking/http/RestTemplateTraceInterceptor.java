package com.caicongyang.sklywalking.http;

import com.caicongyang.sklywalking.common.SpanConstant;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;

import java.io.*;

/**
 * RestTemplate 拦截器
 */
public class RestTemplateTraceInterceptor implements ClientHttpRequestInterceptor {
    private static Logger logger = LoggerFactory.getLogger(RestTemplateTraceInterceptor.class);

    private static final int RESPONSE_LENGTH_LIMIT = 400000;


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        // 如果返回 code 是 4xx 或者 5xx 记录错误日志
        if (response.getStatusCode() == null
                || response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR
                || response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {

            String codeMsg = "code: " + response.getStatusCode() != null ? response.getStatusCode().toString() : "500"
                    + ", text:" + response.getStatusText();
            byte[] responseBody = getResponseBody(response);

            onError(codeMsg, responseBody);
        } else {
            ByteArrayOutputStream baos = cloneInputStream(response.getBody());
            InputStream inpustStream = new ByteArrayInputStream(baos.toByteArray());
            String responseTxt = new String(FileCopyUtils.copyToByteArray(inpustStream), "UTF-8");
            if (responseTxt.length() > RESPONSE_LENGTH_LIMIT) {
                responseTxt = responseTxt.substring(0, RESPONSE_LENGTH_LIMIT) + "......";
            }
            onResult(responseTxt);

            //重置响应输出流
            InputStream responseStream = new ByteArrayInputStream(baos.toByteArray());
            response = getClientHttpResposne(response, responseStream);
        }
        return response;
    }


    protected byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        } catch (IOException ex) {
            logger.error("获取返合值异常：", ex);
        }
        return new byte[0];
    }


    /**
     * ClientHttpResonse对象复制
     *
     * @param clientHttpResponse
     * @param inputStream
     * @return
     */
    protected ClientHttpResponse getClientHttpResposne(ClientHttpResponse clientHttpResponse, InputStream inputStream) {

        ClientHttpResponse clientHttpResponseCopy = new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return clientHttpResponse.getStatusCode();
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return clientHttpResponse.getRawStatusCode();
            }

            @Override
            public String getStatusText() throws IOException {
                return clientHttpResponse.getStatusText();
            }

            @Override
            public void close() {
                clientHttpResponse.close();
            }

            @Override
            public InputStream getBody() throws IOException {
                return inputStream;
            }

            @Override
            public HttpHeaders getHeaders() {
                return clientHttpResponse.getHeaders();
            }
        };

        return clientHttpResponseCopy;
    }

    /**
     * 流复制
     *
     * @param input
     * @return
     */
    private static ByteArrayOutputStream cloneInputStream(InputStream input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void onError(String code, byte[] error) {
        if (error == null || error.length == 0) {
            return;
        }
        ActiveSpan.tag(SpanConstant.ERROR, code);
        try {
            ActiveSpan.tag(SpanConstant.ERROR_DETAIL, new String(error, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void onResult(String responseContent) {
        ActiveSpan.tag("server.response", responseContent);
    }


    public void beforeLog(HttpRequest request) {
        String service = request.getURI().getRawPath();
        String method = request.getMethod().toString();

        // 记录 rest 相关 tag 及 公共 tag
        ActiveSpan.info(service + "(cloud)");
        ActiveSpan.tag(SpanConstant.HTTP_PATH, service);
        ActiveSpan.tag(SpanConstant.HTTP_METHOD, method);
        ActiveSpan.tag(SpanConstant.CLIENT_HTTP_PATH, request.getURI().toString());
    }


}
