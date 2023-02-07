package com.caicongyang.sklywalking.annotation;

import com.caicongyang.sklywalking.config.SklywalingExpandAutoConfig;
import com.caicongyang.sklywalking.config.TraceClientConfigurationImportSelector;
import com.caicongyang.sklywalking.db.mybatis.MybatisTraceSelectInterceptor;
import com.caicongyang.sklywalking.db.mybatis.MybatisTraceUpdateInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({TraceClientConfigurationImportSelector.class, MybatisTraceSelectInterceptor.class, MybatisTraceUpdateInterceptor.class, SklywalingExpandAutoConfig.class})
public @interface EnableTraceClient {

    String[] includePatterns() default {"/**/**"};

    String[] excludePatterns() default {"/**/*.js", "/**/*.html", "/**/*.css", "/**/*.html"};

    boolean webTrace() default true;
}
