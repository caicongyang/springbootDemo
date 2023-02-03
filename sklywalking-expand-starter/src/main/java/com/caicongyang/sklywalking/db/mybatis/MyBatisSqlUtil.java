package com.caicongyang.sklywalking.db.mybatis;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouChenmin on 2018/3/23.
 */
public class MyBatisSqlUtil extends SqlTraceUtil {


    private static Logger logger = LoggerFactory.getLogger(MyBatisSqlUtil.class);


    /**
     * @see MyBatisSqlUtil#
     * 考虑到效率还是枚举 Type2JdbcType
     */
    @Deprecated
    private static Method getJdbcTypeMethod = null;


    public static List<String> getAllParams(MappedStatement mappedStatement, BoundSql boundSql) {


        ParamResolveHelper helper = new ParamResolveHelper();

        List<String> params = new ArrayList<>();

        Object parameterObject = boundSql.getParameterObject();

        TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();

        Configuration configuration = mappedStatement.getConfiguration();

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (value == null && jdbcType == null) {
                        jdbcType = configuration.getJdbcTypeForNull();
                    }

                    if (jdbcType == null) {
                        jdbcType = lookupJdbcType(typeHandlerRegistry, value);
                    }

                    params.add(dealJdbcType(jdbcType, value, helper));


                }
            }
        }


        return params;


    }

    public static JdbcType lookupJdbcType(TypeHandlerRegistry typeHandlerRegistry, Object value) {


        Integer jdbcType = getJdbcType(value);

        if (jdbcType != null) {
            return JdbcType.forCode(jdbcType);
        }

        if (value instanceof InputStream) {
            return JdbcType.BLOB;
        }
        return null;


    }


    /**
     * TODO 该方法还没有测试过
     *
     * @param typeHandlerRegistry
     * @param value
     * @return
     */
    @Deprecated
    private static JdbcType lookupJdbcTypeByReflect(TypeHandlerRegistry typeHandlerRegistry, Object value) {

        if (value == null || getJdbcTypeMethod == null) {
            return null;
        }

        try {

            Map<JdbcType, TypeHandler<?>> result = (Map) getJdbcTypeMethod.invoke(typeHandlerRegistry, value.getClass());

            if (result != null && !result.isEmpty()) {

                return (JdbcType) result.keySet().toArray()[0];
            }

        } catch (Exception e) {

            logger.error(" getJdbcTypeMethod executed failed", e);
        }

        return null;


    }

    public static String dealJdbcType(JdbcType jdbcType, Object value, ParamResolveHelper helper) {
        if (jdbcType == null) {
            jdbcType = JdbcType.INTEGER;
        }
        return dealJdbcType(jdbcType.TYPE_CODE, value, helper);
    }


}
