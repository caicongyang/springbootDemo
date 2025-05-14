package com.caicongyang.core.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreventDuplication {

    /**
     * 过期时间
     */
    int expireSeconds() default 10;
}
