package com.caicongyang.sklywalking.http;


import com.caicongyang.sklywalking.common.TraceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.info.GitProperties;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 把skywalking地址 和traceId  放到header
 */
public class TraceInterceptor extends HandlerInterceptorAdapter implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(TraceInterceptor.class);


    public static final String SKYWALKING_TRACE_INFO_KEY = "sTrace-Info";
    public static final String SKYWALKING_TRACE_FULL_INFO_KEY = "sTrace-Full-Info";
    public static final String GIT_BRANCH = "git_branch";
    public static final String GIT_COMMIT_ID = "git_commit_id";


    private TraceProperties traceProperties;

    private GitProperties gitProperties;


    public TraceInterceptor(TraceProperties traceProperties, GitProperties gitProperties) {
        this.traceProperties = traceProperties;
        this.gitProperties = gitProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader(SKYWALKING_TRACE_INFO_KEY, org.apache.skywalking.apm.toolkit.trace.TraceContext.traceId());
        response.setHeader(SKYWALKING_TRACE_FULL_INFO_KEY, traceProperties.getSkywalkingUrl().replace("@{traceId}", org.apache.skywalking.apm.toolkit.trace.TraceContext.traceId()));

        response.setHeader(GIT_BRANCH, gitProperties.getBranch());
        response.setHeader(GIT_COMMIT_ID, gitProperties.getCommitId());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


}