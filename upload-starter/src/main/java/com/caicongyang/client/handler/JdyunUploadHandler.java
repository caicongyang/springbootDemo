package com.caicongyang.client.handler;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.caicongyang.client.UploadException;
import com.caicongyang.client.config.UploadConfig;
import com.caicongyang.client.data.AwsyunUploadMetadata;
import com.caicongyang.client.domain.ItemResult;
import com.caicongyang.client.domain.UploadImage;
import com.caicongyang.client.domain.UploadResult;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author FanJiang 2019/4/15.
 * @since TODO
 */
public class JdyunUploadHandler extends UploadHandler {

    private static Logger logger = LoggerFactory.getLogger(JdyunUploadHandler.class);

    private static AmazonS3 jdClient = null;

    /**
     * 构造方法里面初始化静态变量不是很好，这里因为没有任何并发，就不关心了
     * 也可以把 jdClint 的 static 去掉
     */
    public JdyunUploadHandler(){


        if ( jdClient != null){
            return ;
        }

        AmazonS3ClientBuilder amazonS3ClientBuilder = AmazonS3ClientBuilder.standard();

        amazonS3ClientBuilder.setCredentials( new AWSStaticCredentialsProvider( new BasicAWSCredentials(
            UploadConfig.FILE_AK,UploadConfig.FILE_SK)));
        amazonS3ClientBuilder.setEndpointConfiguration( new AwsClientBuilder.EndpointConfiguration(UploadConfig.END_POINT, "cn-east-2"));

        jdClient = amazonS3ClientBuilder.build();

        prepareUrlPrefix();
    }


    @Override
    public UploadResult upload(String fileName, String poolName, InputStream io) throws UploadException {

        AwsyunUploadMetadata metadata = new AwsyunUploadMetadata(fileName,io,poolName);
        return finalUpload(metadata,poolName);
    }


    @Override
    public UploadResult upload(String fileName, String poolName, InputStream io, UploadImage uploadImage) throws UploadException {

        return upload(fileName,poolName,io);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, File file) throws UploadException {

        AwsyunUploadMetadata metadata = new AwsyunUploadMetadata(uploadName,file,poolName);
        return finalUpload(metadata,poolName);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, File file, UploadImage uploadImage) throws UploadException {
        return upload(file.getName(),poolName,file);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, byte[] content) throws UploadException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        AwsyunUploadMetadata metadata = new AwsyunUploadMetadata(uploadName, dataInputStream, poolName,true);
        return finalUpload(metadata, poolName);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, byte[] content, UploadImage uploadImage) throws UploadException {

        return upload(uploadName, poolName, content);
    }


    @Override
    public UploadResult upload(String filePath, String poolName) throws UploadException {

        File file = new File(filePath);
        AwsyunUploadMetadata metadata = new AwsyunUploadMetadata(filePath, file, poolName);
        return finalUpload(metadata, poolName);
    }


    @Override
    public UploadResult upload(String filePath, String poolName, UploadImage uploadImage) throws UploadException {

        return upload(filePath,poolName);
    }


    @Override
    public UploadResult batchUpload(List<String> fileList, String poolName, UploadImage uploadImage) throws UploadException {

        List<AwsyunUploadMetadata> metadataList = new ArrayList<AwsyunUploadMetadata>();
        for (String filePath : fileList){
            File file = new File(filePath);
            metadataList.add(new AwsyunUploadMetadata(file,poolName));
        }
        return finalUpload(metadataList,poolName);
    }

    @Override
    public UploadResult uploadByUrl(String fileUrl, String poolName) throws UploadException {

        return batchUploadByUrl(Arrays.asList(fileUrl),poolName);
    }

    @Override
    public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName) throws UploadException {

        return batchUploadByUrl(fileUrlList,poolName,null);
    }

    @Override
    public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName, UploadImage uploadImage) throws UploadException {

        List<AwsyunUploadMetadata> metadataList = new ArrayList<AwsyunUploadMetadata>();
        for (String fileUrlPath : fileUrlList){
            metadataList.add(new AwsyunUploadMetadata(fileUrlPath,poolName));
        }
        return finalUpload(metadataList,poolName);
    }

    @Override
    public UploadResult upload(File file, String poolName, UploadImage uploadImage) throws UploadException {

        return upload(file.getName(),poolName,file);
    }


    @Override
    public UploadResult delete(String fileUrl, String poolName) throws UploadException {

        return delete(Arrays.asList(fileUrl),poolName,true);
    }

    @Override
    public UploadResult delete(List<String> fileList, String poolName, boolean deleteSeriesImage) throws UploadException {


        long timeBegin = System.currentTimeMillis();
        List<ItemResult> resultDetail = new ArrayList<ItemResult>();
        ItemResult itemResult = null;
        UploadResult uploadResult = new UploadResult();
        uploadResult.setTotalCount(fileList.size());
        int successCount = 0;
        int failCount = 0;

        for (String fileUrl : fileList){

            long itemTimeBegin = System.currentTimeMillis();
            try {
                itemResult = new ItemResult();
                String storePathInKsyun = getStorePathInYun(fileUrl,false);
                // FIXME 删除的bucketName需要从url中拿，但是URl又没有一个统一的格式，不一定能截到正确的bucket
                jdClient.deleteObject(UploadConfig.BUCKET_NAME,storePathInKsyun);
                successCount++;
                itemResult.setResult(DEFAULT_SUCCESS_RETURN);
            }catch (Exception e){
                failCount++;
                processException(itemResult, e);
            }finally {
                itemResult.setCost_time(System.currentTimeMillis() - itemTimeBegin);
                resultDetail.add(itemResult);
            }

        }
        uploadResult.setResultDetail(resultDetail);
        uploadResult.setSuccessCount(successCount);
        uploadResult.setFailCount(failCount);
        uploadResult.setTotalCostTime((int)(System.currentTimeMillis() - timeBegin));

        return uploadResult;

    }


    public UploadResult finalUpload(AwsyunUploadMetadata metadata , String poolName){

        List<AwsyunUploadMetadata> metadataList = new ArrayList<AwsyunUploadMetadata>();
        metadataList.add(metadata);
        return finalUpload(metadataList,poolName);

    }


    public UploadResult finalUpload(List<AwsyunUploadMetadata> fileList , String poolName){

        logger.info("use awsyun upload file {}" ,fileList);

        long timeBegin = System.currentTimeMillis();
        List<ItemResult> resultDetail = new ArrayList<ItemResult>();
        ItemResult itemResult = null;
        UploadResult uploadResult = new UploadResult();
        uploadResult.setTotalCount(fileList.size());
        int successCount = 0;
        int failCount = 0;

        for (AwsyunUploadMetadata metadata : fileList){

            long itemTimeBegin = System.currentTimeMillis();
            try {

                itemResult = new ItemResult();
                PutObjectRequest putObjectRequest = metadata.convertToUploadReq();
                PutObjectResult putObjectResult = jdClient.putObject(putObjectRequest);

                successCount++;
                itemResult.setBucketName(UploadConfig.BUCKET_NAME);
                itemResult.setUrlPrefix(COMMON_URL_PREFIX);
                itemResult.setUrl(finalUrl(metadata));
                itemResult.setInner_url(finalInnerUrl(metadata));
                itemResult.setStore_url(metadata.getStorePathInYun());
                itemResult.setResult(DEFAULT_SUCCESS_RETURN);
                itemResult.setOriginalFileName(metadata.getOriginFileName());

            }catch (Exception e){
                failCount++;
                processException(itemResult,e);
            }finally {
                metadata.close();
                itemResult.setCost_time(System.currentTimeMillis() - itemTimeBegin);
                resultDetail.add(itemResult);
            }

        }
        uploadResult.setResultDetail(resultDetail);
        uploadResult.setSuccessCount(successCount);
        uploadResult.setFailCount(failCount);
        uploadResult.setTotalCostTime((int)(System.currentTimeMillis() - timeBegin));

        return uploadResult;

    }




    @Override
    public InputStream download(String url) {


        try {

            if (StringUtils.isBlank(url)){
                throw new IllegalArgumentException("download url can not be null");
            }

            String fileStorePathInKsyun = getStorePathInYun(url,false);
            // FIXME 下载的bucketName需要从url中拿，但是URl又没有一个统一的格式，不一定能截到正确的bucket
            GetObjectRequest request = new GetObjectRequest(UploadConfig.BUCKET_NAME, fileStorePathInKsyun);

            //只接受数据的0-10字节。通过控制该项可以实现分块下载
            //request.setRange(0,10);
            S3Object result  = jdClient.getObject(request);

            return result.getObjectContent();

        }catch (Exception e){

            logger.error("download url error {}",url);
            throw new RuntimeException( " download url error " + url ,e);
        }
    }
}
