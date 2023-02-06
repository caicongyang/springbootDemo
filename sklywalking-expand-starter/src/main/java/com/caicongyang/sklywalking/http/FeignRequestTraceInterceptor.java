package com.caicongyang.sklywalking.http;

import com.caicongyang.sklywalking.common.JacksonUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class FeignRequestTraceInterceptor implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate requestTemplate) {

        HttpServletRequest httpServletRequest = getHttpServletRequest();
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        ActiveSpan.tag("url",httpServletRequest.getRequestURI());
        ActiveSpan.tag("server.response", JacksonUtils.jsonFromObject(parameterMap));

    }


    /**
     * RequestContextHolder 中获取 HttpServletRequest对象
     *
     * @return
     */
    private HttpServletRequest getHttpServletRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
