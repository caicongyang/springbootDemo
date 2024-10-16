//package com.caicongyang.core.Interceptor;
//
//
//import org.apache.ibatis.executor.Executor;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.mapping.SqlCommandType;
//import org.apache.ibatis.plugin.*;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Field;
//import java.util.Date;
//import java.util.Properties;
//
//@Intercepts({@Signature(
//        type = Executor.class,
//        method = "update",
//        args = {MappedStatement.class, Object.class})})
//@Component
//public class AutoComplateInterceptor implements Interceptor {
//
//
//    /**
//     * 各个参数顺序
//     *
//     * @see Executor#update(MappedStatement, Object)
//     */
//    static int MAPPED_STATEMENT_INDEX = 0;
//    static int PARAMETER_INDEX = 1;
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//        final Object[] args = invocation.getArgs();
//        final MappedStatement ms = (MappedStatement) args[MAPPED_STATEMENT_INDEX];
//        final Object parameter = args[PARAMETER_INDEX];
//        Field[] fields = parameter.getClass().getDeclaredFields();
//
//
//        SqlCommandType sqlCommandType = ms.getSqlCommandType();
//        for (Field field : fields) {
//            //注入对应的属性值
//            if (field.getName().equals("createTime") && (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType))) {
//                field.setAccessible(true);
//                field.set(parameter, new Date());
//            }
//            if (field.getName().equals("modifyTime") && SqlCommandType.UPDATE.equals(sqlCommandType)) {
//                field.setAccessible(true);
//                field.set(parameter, new Date());
//            }
//
//            if (field.getName().equals("createUserId") && SqlCommandType.INSERT.equals(sqlCommandType)) {
//                field.setAccessible(true);
//                //todo
//                field.set(parameter, "");
//            }
//            if (field.getName().equals("createUsername") && SqlCommandType.INSERT.equals(sqlCommandType)) {
//                field.setAccessible(true);
//                // todo
//                field.set(parameter, "");
//            }
//
//            if (field.getName().equals("modifyUsername") && SqlCommandType.UPDATE.equals(sqlCommandType)) {
//                field.setAccessible(true);
//                // todo
//                field.set(parameter, "");
//            }
//            if (field.getName().equals("modifyUserId") && SqlCommandType.UPDATE.equals(sqlCommandType)) {
//                field.setAccessible(true);
//                //todo
//                field.set(parameter, "");
//            }
//        }
//
//        return invocation.proceed();
//    }
//
//    @Override
//    public Object plugin(Object target) {
//        return Plugin.wrap(target, this);
//    }
//
//    @Override
//    public void setProperties(Properties properties) {
//    }
//}
