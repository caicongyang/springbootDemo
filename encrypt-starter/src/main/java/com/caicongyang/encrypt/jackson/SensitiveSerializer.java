package com.caicongyang.encrypt.jackson;

import com.caicongyang.encrypt.Sensitive;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;

/**
 * Jackson serializer for {@link Sensitive}.
 * Masks the String value based on detected or explicit type.
 */
public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private final Sensitive.Type type;

    public SensitiveSerializer() {
        this.type = Sensitive.Type.AUTO;
    }

    public SensitiveSerializer(Sensitive.Type type) {
        this.type = type;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(mask(value));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (property != null) {
            Sensitive ann = property.getAnnotation(Sensitive.class);
            if (ann != null && ann.type() != Sensitive.Type.AUTO) {
                return new SensitiveSerializer(ann.type());
            }
        }
        return this;
    }

    private String mask(String value) {
        if (value.isEmpty()) {
            return value;
        }

        Sensitive.Type resolved = resolveType(value);

        switch (resolved) {
            case PHONE:
                return maskPhone(value);
            case EMAIL:
                return maskEmail(value);
            case ID_CARD:
                return maskIdCard(value);
            default:
                // full mask as fallback
                return "*".repeat(value.length());
        }
    }

    private Sensitive.Type resolveType(String value) {
        if (type != Sensitive.Type.AUTO) {
            return type;
        }
        // phone: starts with 1, 11 digits
        if (value.matches("^1\\d{10}$")) {
            return Sensitive.Type.PHONE;
        }
        // idCard: 18 digits (allow X at end), or 15 digits
        if (value.matches("^\\d{17}[\\dXx]$") || value.matches("^\\d{15}$")) {
            return Sensitive.Type.ID_CARD;
        }
        // email: contains @ and .
        if (value.contains("@") && value.contains(".")) {
            return Sensitive.Type.EMAIL;
        }
        // fallback: treat as phone if it looks numeric-ish, else full mask
        if (value.matches("^\\d+$")) {
            return Sensitive.Type.PHONE;
        }
        return Sensitive.Type.EMAIL;
    }

    private String maskPhone(String value) {
        if (value.length() < 7) {
            return "*".repeat(value.length());
        }
        // Show first 3, mask middle, show last 4: 138****1234
        return value.substring(0, 3) + "****" + value.substring(value.length() - 4);
    }

    private String maskEmail(String value) {
        int atIndex = value.indexOf('@');
        if (atIndex <= 1) {
            return "*".repeat(value.length());
        }
        // Show first char, mask rest of local part, keep domain: t***@example.com
        return value.charAt(0) + "***" + value.substring(atIndex);
    }

    private String maskIdCard(String value) {
        if (value.length() < 8) {
            return "*".repeat(value.length());
        }
        // Show first 3, mask middle, show last 4: 310***********1234
        return value.substring(0, 3) + "*".repeat(value.length() - 7) + value.substring(value.length() - 4);
    }
}
