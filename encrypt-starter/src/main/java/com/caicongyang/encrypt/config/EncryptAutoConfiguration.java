package com.caicongyang.encrypt.config;

import com.caicongyang.encrypt.AesUtils;
import com.caicongyang.encrypt.EncryptProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for encrypt-starter.
 */
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
@EnableConfigurationProperties(EncryptProperties.class)
public class EncryptAutoConfiguration {

    public EncryptAutoConfiguration(EncryptProperties encryptProperties) {
        AesUtils.setSecretKey(encryptProperties.getSecretKey());
    }
}
