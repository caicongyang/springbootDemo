package com.caicongyang.client.data;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.caicongyang.client.config.UploadConfig;
import com.caicongyang.client.constant.UploadConstant;
import java.io.File;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ZhouChenmin on 2018/4/10.
 */
public class AwsyunUploadMetadata extends UploadMetadata {

    private static Logger logger = LoggerFactory.getLogger(AwsyunUploadMetadata.class);

    public AwsyunUploadMetadata(File file, String poolName) {
        super(file, poolName);
    }

    public AwsyunUploadMetadata(String fileName, File file, String poolName) {
        super(fileName, file, poolName);
    }

    public AwsyunUploadMetadata(String fileName, InputStream inputStream, String poolName) {
        super(fileName, inputStream, poolName);
    }

    public AwsyunUploadMetadata(String fileName, InputStream inputStream, String poolName, boolean needCloseInputStream) {
        super(fileName, inputStream, poolName, needCloseInputStream);
    }

    public AwsyunUploadMetadata(String fileUrl, String poolName) {
        super(fileUrl, poolName);
    }


    public PutObjectRequest convertToUploadReq(){

        PutObjectRequest putObjectRequest = null;
        if (UploadConstant.FileUploadDataType.FILE == dataType){

            putObjectRequest = new PutObjectRequest(UploadConfig.BUCKET_NAME,getStorePathInYun(),file);
        }else if(UploadConstant.FileUploadDataType.INPUT_STREAM == dataType || UploadConstant.FileUploadDataType.CLOSEABLE_INPUT_STREAM == dataType){

            putObjectRequest = new PutObjectRequest(UploadConfig.BUCKET_NAME,getStorePathInYun(),inputStream,null);
        }else {
            logger.error("metadata type should not be bytes");
        }

        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);

        return putObjectRequest;

    }

}
