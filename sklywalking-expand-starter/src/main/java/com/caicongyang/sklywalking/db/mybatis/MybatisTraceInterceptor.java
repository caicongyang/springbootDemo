package com.caicongyang.sklywalking.db.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public class MybatisTraceInterceptor implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public boolean enabled = true;
    public Set<String> ignoreSet;

    static int MAPPED_STATEMENT_INDEX = 0;
    static int PARAMETER_INDEX = 1;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result;
        try {
            result = invocation.proceed();
            Object[] args = invocation.getArgs();
            if (args.length > MAPPED_STATEMENT_INDEX && args[MAPPED_STATEMENT_INDEX] instanceof MappedStatement) {
                MappedStatement ms = (MappedStatement) args[MAPPED_STATEMENT_INDEX];
                logger.debug("MyBatis SQL executed: statementId={}", ms.getId());
            }
        } catch (Exception e) {
            logger.error("MyBatis error: {}", e.getMessage(), e);
            throw e;
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String ignoreIds = properties.getProperty("ignoreIds");
        _setIgnoreIds(ignoreIds);
        String effective = properties.getProperty("enabled");
        if (StringUtils.isNotBlank(effective)) {
            this.enabled = Boolean.parseBoolean(effective.trim());
        }
    }

    private void _setIgnoreIds(String ignoreIds) {
        if (StringUtils.isBlank(ignoreIds)) return;
        if (ignoreSet == null) ignoreSet = new HashSet<>();
        StringTokenizer tokenizer = new StringTokenizer(ignoreIds, ",", false);
        while (tokenizer.hasMoreTokens()) {
            ignoreSet.add(tokenizer.nextToken().trim());
        }
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setIgnoreIds(String ignoreIds) { _setIgnoreIds(ignoreIds); }
}
