package com.caicongyang.encrypt;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * Marks a String field for masking during JSON serialization.
 * <ul>
 *   <li>phone: 138****1234</li>
 *   <li>email: t***@example.com</li>
 *   <li>idCard: 310***********1234</li>
 * </ul>
 * The mask type is auto-detected from the field value shape.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = com.caicongyang.encrypt.jackson.SensitiveSerializer.class)
public @interface Sensitive {

    /**
     * Explicit mask type. Default AUTO lets the serializer detect.
     */
    Type type() default Type.AUTO;

    enum Type {
        AUTO, PHONE, EMAIL, ID_CARD
    }
}
