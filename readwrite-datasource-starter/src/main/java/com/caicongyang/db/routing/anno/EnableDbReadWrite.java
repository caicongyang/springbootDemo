package com.caicongyang.db.routing.anno;

import com.caicongyang.db.routing.DbReadWriteRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(DbReadWriteRegistrar.class)
public @interface EnableDbReadWrite {
    /**
     * 指定需要拦截的包
     * @return
     */
    String[] routingInterceptionPackage();

    /**
     * 读数据源指定的包
     * @return
     */
    String[] readPackage() default {};

    /**
     * 写数据源指定的包
     * @return
     */
    String[] writePackage() default {};

}
