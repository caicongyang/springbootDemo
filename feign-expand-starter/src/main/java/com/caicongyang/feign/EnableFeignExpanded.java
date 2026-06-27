package com.caicongyang.feign;

import com.caicongyang.feign.config.FeignExpandAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enables FeignExpanded support: auto-scans for &#64;FeignClient interfaces
 * and triggers auto-generation of FeignClient proxies.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FeignExpandAutoConfiguration.class)
public @interface EnableFeignExpanded {
}
