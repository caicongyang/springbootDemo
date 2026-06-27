package com.caicongyang.encrypt.jackson;

import com.caicongyang.encrypt.AesUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Jackson serializer for {@link com.caicongyang.encrypt.EncryptField}.
 * Encrypts the String value with AES before writing to JSON.
 */
public class EncryptFieldSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(AesUtils.encrypt(value, AesUtils.getSecretKey()));
    }
}
