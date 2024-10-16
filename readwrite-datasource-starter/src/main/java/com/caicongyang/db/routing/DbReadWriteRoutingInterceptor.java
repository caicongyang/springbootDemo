package com.caicongyang.db.routing;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Collections;
import java.util.Map;

/**
 * 基于包名和注解的数据库路由拦截器
 */
public class DbReadWriteRoutingInterceptor implements MethodInterceptor {

    private Logger logger = LoggerFactory.getLogger(DbReadWriteRoutingInterceptor.class);


    private Map<String, String> packageDataSourceMapping = Collections.EMPTY_MAP;

    private PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        String methodName = mi.getMethod().getName();
        boolean matched = false;
        if (!matched && !packageDataSourceMapping.isEmpty()) {
            String name = mi.getThis().getClass().getName() + "." + methodName;
            for (String pattern : packageDataSourceMapping.keySet()) {
                if (pathMatcher.match(pattern, name)) {
                    logger.debug("Set DataSource key by package matcher: " + pattern);
                    DataSourceContextHolder.setDataSourceType(packageDataSourceMapping.get(pattern));
                    matched = true;
                    break;
                }
            }
        }

        try {

            Object returned = mi.proceed();
            return returned;
        } finally {
            if (!matched && !packageDataSourceMapping.isEmpty()) {
                DataSourceContextHolder.clearDataSourceType();
            }
        }


    }
}
