package com.caicongyang.sklywalking.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableTraceClient {

    String[] includePatterns() default {"/**/**"};

    String[] excludePatterns() default {"/**/*.js", "/**/*.html", "/**/*.css", "/**/*.html"};

    boolean webTrace() default true;
}
