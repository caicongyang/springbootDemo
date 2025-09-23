package com.caicongyang.core.annotation;

import com.caicongyang.core.aspect.PreventDuplicationAspect;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(PreventDuplicationAspect.class)
public @interface EnablePreventDuplication {
}
