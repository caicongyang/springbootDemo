package com.caicongyang.tcc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as a TCC Try operation.
 * The annotated method is the Try phase. On success, confirmMethod is called;
 * on failure, cancelMethod is called.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TccTransaction {

    /** Method name for the Confirm phase. Must exist on the same bean. */
    String confirmMethod() default "";

    /** Method name for the Cancel phase. Must exist on the same bean. */
    String cancelMethod() default "";
}
