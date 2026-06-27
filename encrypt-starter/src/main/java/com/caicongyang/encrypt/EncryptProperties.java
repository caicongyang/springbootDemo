package com.caicongyang.encrypt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the encrypt-starter.
 * Set {@code encrypt.secret-key} in application.properties.
 */
@ConfigurationProperties(prefix = "encrypt")
public class EncryptProperties {

    /**
     * AES secret key. Must be at least 16 characters for AES-128.
     * Default is a built-in demo key; override in production.
     */
    private String secretKey = "caicongyang-2026";

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
