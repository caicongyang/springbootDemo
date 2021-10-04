package com.caicongyang.upload.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "uploader")
@PropertySource("classpath:config/uploader.properties")
public class UploaderProperties {


    private String type;
    private String qiniuAccessKey;
    private String qiniuSecretKey;
    private String qiniuBucket;
    private String qiniuBucketUrl;


    public String getQiniuAccessKey() {
        return qiniuAccessKey;
    }

    public void setQiniuAccessKey(String qiniuAccessKey) {
        this.qiniuAccessKey = qiniuAccessKey;
    }

    public String getQiniuSecretKey() {
        return qiniuSecretKey;
    }

    public void setQiniuSecretKey(String qiniuSecretKey) {
        this.qiniuSecretKey = qiniuSecretKey;
    }

    public String getQiniuBucket() {
        return qiniuBucket;
    }

    public void setQiniuBucket(String qiniuBucket) {
        this.qiniuBucket = qiniuBucket;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQiniuBucketUrl() {
        return qiniuBucketUrl;
    }

    public void setQiniuBucketUrl(String qiniuBucketUrl) {
        this.qiniuBucketUrl = qiniuBucketUrl;
    }
}
