package com.caicongyang.config;

import com.caicongyang.client.Uploader;
import com.caicongyang.uploader.QiniuYunPublicAccessUploader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UploaderAutoConfig {


    @Bean
    @ConditionalOnMissingBean
    UploaderProperties getUploaderProperties() {
        return new UploaderProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public Uploader getUploader(UploaderProperties uploaderProperties) {
        return getQiniuYunPublicAccessUploader(
            uploaderProperties);
    }


    @Bean
    @ConditionalOnMissingBean
    public QiniuYunPublicAccessUploader getQiniuYunPublicAccessUploader(
        UploaderProperties uploaderProperties) {
        return new QiniuYunPublicAccessUploader(uploaderProperties);

    }


}
