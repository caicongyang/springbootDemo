package com.caicongyang.encrypt;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * Marks a String field for AES encryption during JSON serialization
 * and decryption during deserialization.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = com.caicongyang.encrypt.jackson.EncryptFieldSerializer.class)
public @interface EncryptField {
}
