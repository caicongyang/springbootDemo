package com.caicongyang.sklywalking.db.mybatis;


import com.caicongyang.sklywalking.common.SpanConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;


public class MybatisTraceInterceptor implements Interceptor {
    private Logger logger = LoggerFactory.getLogger(getClass());
    //插件是否生效，默认生效
    public boolean enabled = true;
    //指定哪些statementId不需要此插件处理
    public Set<String> ignoreSet;


    /**
     * 各个参数顺序
     *
     * @see Executor#query(MappedStatement, Object, org.apache.ibatis.session.RowBounds, org.apache.ibatis.session.ResultHandler)
     */
    static int MAPPED_STATEMENT_INDEX = 0;
    static int PARAMETER_INDEX = 1;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object result = null;
        String sql = null;
        String id = null;
 
        try {
            result = invocation.proceed();
        } catch (Exception e) {
            try {
                ActiveSpan.error(ExceptionUtils.getStackTrace(e));
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
            throw e;
        } finally {
            try {
                // 确认 记录日志
                ActiveSpan.tag(SpanConstant.QUERY_SQL, sql);
                ActiveSpan.tag(SpanConstant.QUERY_STATEMENT_ID, id);
                ActiveSpan.tag(SpanConstant.ORM, SpanConstant.ORM_MYBATIS);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        //设置忽略的statementId
        String ignoreIds = properties.getProperty("ignoreIds");
        _setIgnoreIds(ignoreIds);
        String effective = properties.getProperty("enabled");
        if (StringUtils.isNotBlank(effective)) {
            this.enabled = Boolean.valueOf(effective.trim());
        }
    }

    private void _setIgnoreIds(String ignoreIds) {
        if (StringUtils.isBlank(ignoreIds)) {
            return;
        }
        if (ignoreSet == null) {
            ignoreSet = new HashSet<>();
        }
        StringTokenizer tokenizer = new StringTokenizer(ignoreIds, ",", false);
        while (tokenizer.hasMoreTokens()) {
            ignoreSet.add(tokenizer.nextToken().trim());
        }
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setIgnoreIds(String ignoreIds) {
        _setIgnoreIds(ignoreIds);
    }
}