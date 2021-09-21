package com.caicongyang.client.handler;

import com.caicongyang.client.UploadException;
import com.caicongyang.client.config.UploadConfig;
import com.caicongyang.client.constant.UploadConstant;
import com.caicongyang.client.data.UploadMetadata;
import com.caicongyang.client.domain.ItemResult;
import com.caicongyang.client.domain.UploadImage;
import com.caicongyang.client.domain.UploadResult;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: FanJiang.
 * @date: Created by ody on 11:08 2018/9/18.
 * @description 支持腾讯云 对象存储
 * @see com.odianyun.architecture.odfs.upload.client.handler.UploadHandler
 * @since 1.0.0
 */
public class TxyunUploadHandler extends UploadHandler {

    private static final Logger logger = LoggerFactory.getLogger(TxyunUploadHandler.class);

    private COSClient cosclient;

    public TxyunUploadHandler() {

        if (cosclient != null) {
            return ;
        }

        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(UploadConfig.FILE_AK, UploadConfig.FILE_SK);
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig中包含了设置region, https(默认http), 超时, 代理等set方法, 使用可参见源码或者接口文档FAQ中说明
        ClientConfig clientConfig = new ClientConfig(new Region(UploadConfig.REGION));
        String endPoint = UploadConfig.END_POINT;
        if(!endPoint.startsWith("\\.")){
            endPoint = "."+endPoint;
        }
        clientConfig.setEndPointSuffix(endPoint);
        // 3 生成cos客户端
        cosclient = new COSClient(cred, clientConfig);

        prepareUrlPrefix();

    }

    @Override
    public UploadResult upload(String fileName, String poolName, InputStream io) throws UploadException {
        UploadMetadata metadata = new UploadMetadata(fileName, io, poolName);
        return finalUpload(metadata, poolName);
    }


    @Override
    public UploadResult upload(String fileName, String poolName, InputStream io, UploadImage uploadImage) throws UploadException {

        return upload(fileName,poolName,io);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, File file) throws UploadException {
        UploadMetadata metadata = new UploadMetadata(uploadName, file, poolName);
        return finalUpload(metadata, poolName);

    }


    @Override
    public UploadResult upload(String uploadName, String poolName, File file, UploadImage uploadImage) throws UploadException {


        return upload(file.getName(),poolName,file);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, byte[] content) throws UploadException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        UploadMetadata metadata = new UploadMetadata(uploadName,dataInputStream,poolName);
        return finalUpload(metadata,poolName);

    }


    @Override
    public UploadResult upload(String uploadName, String poolName, byte[] content, UploadImage uploadImage) throws UploadException {

        return upload(uploadName,poolName,content);
    }



    @Override
    public UploadResult upload(String filePath, String poolName) throws UploadException {
        return upload(filePath,poolName, new UploadImage());

    }


    @Override
    public UploadResult upload(String filePath, String poolName, UploadImage uploadImage) throws UploadException {
        File file = new File(filePath);
        UploadMetadata metadata = new UploadMetadata(filePath,file,poolName);
        return finalUpload(metadata,poolName);
    }

    @Override
    public UploadResult batchUpload(List<String> fileList, String poolName, UploadImage uploadImage) throws UploadException {

        List<UploadMetadata> metadataList = new ArrayList<>();
        for (String filePath : fileList){
            File file = new File(filePath);
            metadataList.add(new UploadMetadata(file,poolName));
        }
        return finalUpload(metadataList,poolName);
    }

    @Override
    public UploadResult uploadByUrl(String fileUrl, String poolName) throws UploadException {

        return batchUploadByUrl(Collections.singletonList(fileUrl),poolName);
    }

    @Override
    public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName) throws UploadException {

        return batchUploadByUrl(fileUrlList,poolName,null);
    }

    @Override
    public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName, UploadImage uploadImage) throws UploadException {

        List<UploadMetadata> metadataList = new ArrayList<>();
        for (String fileUrlPath : fileUrlList){
            metadataList.add(new UploadMetadata(fileUrlPath,poolName));
        }
        return finalUpload(metadataList,poolName);
    }

    @Override
    public UploadResult upload(File file, String poolName, UploadImage uploadImage) throws UploadException {

        return upload(file.getName(),poolName,file);
    }

    @Override
    public UploadResult delete(String fileUrl, String poolName) throws UploadException {

        return delete(Collections.singletonList(fileUrl),poolName,true);
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

                cosclient.deleteObject( UploadConfig.BUCKET_NAME, storePathInKsyun);
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


    public UploadResult finalUpload(UploadMetadata metadata , String poolName){

        List<UploadMetadata> metadataList = new ArrayList<>();
        metadataList.add(metadata);
        return finalUpload(metadataList,poolName);

    }


    public UploadResult finalUpload(List<UploadMetadata> fileList , String poolName){

        logger.info("use TxYun upload file {}" ,fileList);

        long timeBegin = System.currentTimeMillis();
        List<ItemResult> resultDetail = new ArrayList<ItemResult>();
        ItemResult itemResult = null;
        UploadResult uploadResult = new UploadResult();
        uploadResult.setTotalCount(fileList.size());
        int successCount = 0;
        int failCount = 0;

        for (UploadMetadata metadata : fileList){

            long itemTimeBegin = System.currentTimeMillis();
            try {

                itemResult = new ItemResult();


                successCount++;
                itemResult.setBucketName(UploadConfig.BUCKET_NAME);
                itemResult.setUrlPrefix(COMMON_URL_PREFIX);
                itemResult.setUrl(getUrl(cosclient, UploadConfig.BUCKET_NAME, metadata.getStorePathInYun()));
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


    /**
     * 请求失效时间
     */
    static final long expiration = 50L * 365L * 24L * 60L * 60L * 1000L;

    /**
     * 获取OSS文件公网URL
     *
     * @param client     桶客户端
     * @param bucketName 桶名称
     * @param key        COS文件路径
     * @return OSS文件公网URL
     */
    private String getUrl(COSClient client, String bucketName, String key) throws Exception {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
        Date expirationTime = new Date(System.currentTimeMillis() + expiration);
        logger.debug("getUrl expireTime = " + expirationTime.toString());
        request.setExpiration(expirationTime);
        URL url = client.generatePresignedUrl(request);
        if (url == null) {
            throw new Exception("Can't get the url of COS file!");
        }
        //使用上诉方法生成的url不会使用txyun.domain这个变量而是使用，txyun.bucketName+txyun.endpoint组装而成，需要替换
        String file = url.getFile();
        if(file.startsWith("/")){
            file = file.replaceFirst("/","");
        }
        return COMMON_URL_PREFIX+file;
    }


    private PutObjectResult doUpload(UploadMetadata metadata) throws IOException {

        PutObjectResult result = null;

        String key = metadata.getStorePathInYun();

        cosclient.setBucketAcl(UploadConfig.BUCKET_NAME, CannedAccessControlList.Private);

        if (UploadConstant.FileUploadDataType.FILE == metadata.getDataType()){

            result = cosclient.putObject(UploadConfig.BUCKET_NAME, key, metadata.getFile());

        }else if(UploadConstant.FileUploadDataType.INPUT_STREAM == metadata.getDataType() || UploadConstant.FileUploadDataType.CLOSEABLE_INPUT_STREAM == metadata.getDataType()){

            ObjectMetadata objectMetadata = new ObjectMetadata();
            ByteArrayOutputStream byteArrayOutputStream = cloneInputStream(metadata.getInputStream());
            int contentLength = byteArrayOutputStream.size();
            objectMetadata.setContentLength(contentLength);
            ByteArrayInputStream requestInput = null;
            try {
                requestInput = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                result = cosclient.putObject(UploadConfig.BUCKET_NAME, key, requestInput, objectMetadata);
            } finally {
                IOUtils.closeQuietly(requestInput);
                IOUtils.closeQuietly(byteArrayOutputStream);
            }



        }else {
            throw new IllegalStateException( " upload metadata must be file or inputStream");
        }

        return result;

    }



    @Override
    public InputStream download(String url) {

        try {

            if (StringUtils.isBlank(url)){
                throw new IllegalArgumentException("download url can not be null");
            }

            String fileStorePathInQcloudyun = getStorePathInYun(url,false);
            // FIXME 下载的bucketName需要从url中拿，但是URl又没有一个统一的格式，不一定能截到正确的bucket
            //return result.getObjectContent();

            return cosclient.getObject(UploadConfig.BUCKET_NAME, fileStorePathInQcloudyun).getObjectContent();
        }catch (Exception e){

            logger.error("download url error {}",url);
            throw new RuntimeException( " download url error " + url ,e);
        }
    }
}
