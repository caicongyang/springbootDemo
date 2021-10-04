package com.caicongyang.config;

import com.caicongyang.client.Uploader;
import com.caicongyang.uploader.QiniuYunPublicAccessUploader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({UploaderProperties.class})
public class UploaderAutoConfig {


    @Bean
    @ConditionalOnMissingBean
    public Uploader uploader(UploaderProperties uploaderProperties) {
        if (uploaderProperties.getType().equalsIgnoreCase("qiniu")) {
            return getQiniuYunPublicAccessUploader(
                uploaderProperties);
        } else {
            return null;
        }

    }


    public QiniuYunPublicAccessUploader getQiniuYunPublicAccessUploader(
        UploaderProperties uploaderProperties) {
        return new QiniuYunPublicAccessUploader(uploaderProperties);

    }


}
