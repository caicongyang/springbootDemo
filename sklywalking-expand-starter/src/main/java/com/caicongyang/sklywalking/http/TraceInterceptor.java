package com.caicongyang.sklywalking.http;

import com.caicongyang.sklywalking.common.TraceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.info.GitProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 把skywalking地址 和traceId  放到header
 */
public class TraceInterceptor implements HandlerInterceptor, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(TraceInterceptor.class);

    public static final String SKYWALKING_TRACE_INFO_KEY = "sTrace-Info";
    public static final String SKYWALKING_TRACE_FULL_INFO_KEY = "sTrace-Full-Info";
    public static final String GIT_BRANCH = "git_branch";
    public static final String GIT_COMMIT_ID = "git_commit_id";

    private final TraceProperties traceProperties;
    private final GitProperties gitProperties;

    public TraceInterceptor(TraceProperties traceProperties, GitProperties gitProperties) {
        this.traceProperties = traceProperties;
        this.gitProperties = gitProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // init
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String traceId = org.apache.skywalking.apm.toolkit.trace.TraceContext.traceId();
            response.setHeader(SKYWALKING_TRACE_INFO_KEY, traceId);
            if (traceProperties != null && traceProperties.getSkywalkingUrl() != null) {
                response.setHeader(SKYWALKING_TRACE_FULL_INFO_KEY,
                    traceProperties.getSkywalkingUrl().replace("@{traceId}", traceId));
            }
            if (gitProperties != null) {
                response.setHeader(GIT_BRANCH, gitProperties.getBranch());
                response.setHeader(GIT_COMMIT_ID, gitProperties.getCommitId());
            }
        } catch (Exception e) {
            logger.warn("TraceInterceptor error", e);
        }
        return true;
    }
}
