package com.caicongyang.uploader;

import com.caicongyang.client.Uploader;
import com.caicongyang.config.UploaderProperties;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import java.io.File;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 公开上传
 */
@Component
public class QiniuYunPublicAccessUploader implements Uploader {

    private final Logger logger = LoggerFactory.getLogger(QiniuYunPublicAccessUploader.class);


    private UploaderProperties qiniuYunProperties;


    private UploadManager uploadManager;


    public QiniuYunPublicAccessUploader(UploaderProperties qiniuYunProperties) {
        this.qiniuYunProperties = qiniuYunProperties;
    }

    @PostConstruct
    public void init() {

        ///////////////////////指定上传的Zone的信息//////////////////
        //第一种方式: 指定具体的要上传的zone
        //注：该具体指定的方式和以下自动识别的方式选择其一即可
        //要上传的空间(bucket)的存储区域为华东时
        // Zone z = Zone.zone0();
        //要上传的空间(bucket)的存储区域为华北时
        // Zone z = Zone.zone1();
        //要上传的空间(bucket)的存储区域为华南时
        // Zone z = Zone.zone2();

        //第二种方式: 自动识别要上传的空间(bucket)的存储区域是华东、华北、华南。
        Zone z = Zone.autoZone();
        Configuration c = new Configuration(z);

        //创建上传对象
        uploadManager = new UploadManager(c);

    }


    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        if (StringUtils.isBlank(qiniuYunProperties.getQiniuAccessKey()) || StringUtils
            .isBlank(qiniuYunProperties.getQiniuSecretKey())) {
            throw new RuntimeException("请增加七牛云相关配置");

        }
        //密钥配置
        Auth auth = Auth
            .create(qiniuYunProperties.getQiniuAccessKey(), qiniuYunProperties.getQiniuSecretKey());
        return auth.uploadToken(qiniuYunProperties.getQiniuBucket());
    }

    /**
     * 上传文件
     */
    @Override
    public String upload(byte[] file, String folder, String key) throws IOException {
        try {
            //调用put方法上传
            Response res = uploadManager.put(file, folder + File.separator + key, getUpToken());
            //打印返回的信息
            logger.info("上传结果" + res.bodyString());

            DefaultPutRet putRet = new Gson().fromJson(res.bodyString(), DefaultPutRet.class);
            return qiniuYunProperties.getQiniuBucketUrl() + putRet.key;
        } catch (QiniuException e) {
            throw new RuntimeException("七牛云上传异常", e);

        }
    }

}

