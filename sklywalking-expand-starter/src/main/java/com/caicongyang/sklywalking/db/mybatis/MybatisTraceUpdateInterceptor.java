package com.caicongyang.sklywalking.db.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Signature;

/**
 * Created by ZhouChenmin on 2018/3/21.
 */

@Intercepts({@Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class})})
public class MybatisTraceUpdateInterceptor extends MybatisTraceInterceptor {



}
