package com.caicongyang.client.handler;

import com.caicongyang.client.UploadException;
import com.caicongyang.client.config.UploadConfig;
import com.caicongyang.client.constant.UploadConstant;
import com.caicongyang.client.data.KsyunUploadMetadata;
import com.caicongyang.client.data.UploadMetadata;
import com.caicongyang.client.domain.ItemResult;
import com.caicongyang.client.domain.UploadImage;
import com.caicongyang.client.domain.UploadResult;
import com.caicongyang.client.util.UploadFileUtil;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微软云存储上传核心类
 * Created by ZhouChenmin on 2017/5/2.
 */
public class MsyunUploadHandler extends UploadHandler {

    private static Logger logger = LoggerFactory.getLogger(MsyunUploadHandler.class);

    private static final int MAX_RETRY_TIME = 3;

    /**
     * true表示以   endpoint/{bucket}/{key}的方式访问</br>
     * false表示以  {bucket}.endpoint/{key}的方式访问
     */
    public static final boolean KSYUN_URL_STYLE = false;
//    public static String KSYUN_URL_PREFIX = null;

    CloudBlobClient blobClient ;
    CloudBlobContainer container ;


    /**
     * 构造方法里面初始化静态变量不是很好，这里因为没有任何并发，就不关心了
     * 也可以把 ks3Clint 的 static 去掉
     */
    public MsyunUploadHandler(){
        if ( container != null){
            return ;
        }
        //String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=aceta018sa01;AccountKey=o6YiYeITRzLjco2RO4QFeG0qma3KfpYfuyIblT1MIFqJ5OtfYQFAc5GMbpUwk/1dCuuh0/puIEsn5bDVy21VMA==;EndpointSuffix=core.chinacloudapi.cn";
        String storageConnectionString =
                "DefaultEndpointsProtocol=https;" +
                        "AccountName=" + UploadConfig.FILE_AK +";"+
                        "AccountKey=" + UploadConfig.FILE_SK+";" +
                        "EndpointSuffix=" + UploadConfig.END_POINT ;

        try {
            CloudStorageAccount storageAccount = null;
            // 解析 配置
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            // 创建 client
            blobClient = storageAccount.createCloudBlobClient();
            // 拿到 bucket引用
            container = blobClient.getContainerReference( UploadConfig.BUCKET_NAME );
            // 确保bucket存在
            container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());


            //container.setMetadata( metadata);

            //container.uploadMetadata();

        } catch (Exception e) {
            logger.error(" create client or bucket exception" ,e );
            throw new RuntimeException(e);
        }

        prepareUrlPrefix(Boolean.toString(MsyunUploadHandler.KSYUN_URL_STYLE));
    }


    /**
     * 微软云 的 url 风格 是     https://fileAk.blob.endpoint/bucketname/storePath
     * @param style
     */
    @Override
    protected void prepareUrlPrefix(String style) {
        if (StringUtils.isNotBlank(UploadConfig.FILE_DOMAIN)){
            super.prepareUrlPrefix(style);
        }else {

            StringBuilder sb = new StringBuilder(UploadConfig.getUploadProtocolPrefix());
            // 和其他云存储不一样的地方是  其他的云存储 使用的 BucketName加在url的域名前面,微软云是 FileAk也就是 账号信息
            // 加载域名前面,bucketName放在域名后
            sb.append(UploadConfig.FILE_AK)
                    .append(".")
                    /**
                     * {@link https://docs.azure.cn/zh-cn/storage/blobs/storage-blobs-introduction}
                     * 默认就是要加一个 blob
                     */
                    .append("blob.")
                    .append(UploadConfig.END_POINT)
                    .append("/").append(UploadConfig.BUCKET_NAME).append("/");
            COMMON_URL_PREFIX = sb.toString();
        }

    }


    @Override
    protected String getStorePathInYun(String targetUrl, boolean urlStyle) {


        if ( !fullUrl(targetUrl) ){
            return super.getStorePathInYun( targetUrl, urlStyle);
        }

        /**
         * targetUrl 是
         * https://aceta018sa01.blob.core.chinacloudapi.cn/aldiqas/branch/osoa/1553223872941_5.7981772580506075_ec7c0c08-f2f6-486c-825c-12d57db804ff.jpg
         * https://fileAk      .blob.endpoint             /bucketName/storePath
         *
         *  比金山云的地址多了一个 bucketName的层级
         */

        // tempStorePath 地址为 bucketName/storePath
        String tempStorePath = super.getStorePathInYun(targetUrl, urlStyle);
        // 需要把 bucketName 也截掉
        return tempStorePath.substring(tempStorePath.indexOf("/") + 1);

    }

    @Override
    public UploadResult upload(String fileName, String poolName, InputStream io) throws UploadException {

        UploadMetadata metadata = new UploadMetadata(fileName,io,poolName);
        return finalUpload(metadata,poolName);

    }


    @Override
    public UploadResult upload(String fileName, String poolName, InputStream io, UploadImage uploadImage) throws UploadException {

        return upload(fileName,poolName,io);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, File file) throws UploadException {

        UploadMetadata metadata = new UploadMetadata(uploadName,file,poolName);
        return finalUpload(metadata,poolName);

    }


    @Override
    public UploadResult upload(File file, String poolName, UploadImage uploadImage) throws UploadException {
        return upload(file.getName(),poolName,file);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, File file, UploadImage uploadImage) throws UploadException {
        return upload(uploadName,poolName,file);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, byte[] content) throws UploadException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        UploadMetadata metadata = new UploadMetadata(uploadName, dataInputStream,poolName,true);
        return finalUpload(metadata,poolName);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, byte[] content, UploadImage uploadImage) throws UploadException {

        return upload(uploadName,poolName,content);

    }


    @Override
    public UploadResult upload(String fileName, String poolName) throws UploadException {

        File file = new File(fileName);
        KsyunUploadMetadata metadata = new KsyunUploadMetadata(fileName,file,poolName);
        return finalUpload(metadata,poolName);
    }


    @Override
    public UploadResult upload(String fileName, String poolName, UploadImage uploadImage) throws UploadException {
        return upload(fileName,poolName);
    }


    @Override
    public UploadResult batchUpload(List<String> fileList, String poolName, UploadImage uploadImage) throws UploadException {

        List<UploadMetadata> metadataList = new ArrayList<UploadMetadata>();
        for (String filePath : fileList){
            File file = new File(filePath);
            metadataList.add(new KsyunUploadMetadata(file,poolName));
        }
        return finalUpload(metadataList,poolName);
    }

    @Override
    public UploadResult uploadByUrl(String fileUrl, String poolName) {

        return batchUploadByUrl(Arrays.asList(fileUrl),poolName);
    }

    @Override
    public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName) {

        return batchUploadByUrl(fileUrlList,poolName,null);
    }

    @Override
    public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName, UploadImage uploadImage) {

        List<UploadMetadata> metadataList = new ArrayList<>();
        for (String fileUrlPath : fileUrlList){
            metadataList.add(new KsyunUploadMetadata(fileUrlPath,poolName));
        }
        return finalUpload(metadataList,poolName);


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
                String storePathInKsyun = getStorePathInYun(fileUrl,KSYUN_URL_STYLE);
                // FIXME 删除的bucketName需要从url中拿，但是URl又没有一个统一的格式，不一定能截到正确的bucket
                //DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(UploadConfig.BUCKET_NAME,storePathInKsyun);
                //ks3Client.deleteObject(deleteObjectRequest);
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

    public UploadResult finalUpload(UploadMetadata metadata ,String poolName){

        List<UploadMetadata> metadataList = new ArrayList<UploadMetadata>();
        metadataList.add(metadata);
        return finalUpload(metadataList,poolName);

    }


    public UploadResult finalUpload(List<UploadMetadata> fileList ,String poolName){

        logger.info("use msyun upload file {}" ,fileList);

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


                CloudBlockBlob blob = container.getBlockBlobReference(metadata.getStorePathInYun());


                blob.getProperties().setCacheControl("max-age=600");
                String contentType = UploadFileUtil.getContentTypeByFilename( metadata.getOriginFileName());
                if ( StringUtils.isNotBlank( contentType )){
                    blob.getProperties().setContentType(contentType);
                }


                if (UploadConstant.FileUploadDataType.FILE == metadata.getDataType()){
                    blob.uploadFromFile( metadata.getFile().getAbsolutePath());
                }else if ( UploadConstant.FileUploadDataType.INPUT_STREAM == metadata.getDataType() || UploadConstant.FileUploadDataType.CLOSEABLE_INPUT_STREAM == metadata.getDataType()){
                    ByteArrayOutputStream byteArrayOutputStream = cloneInputStream(metadata.getInputStream());
                    int contentLength = byteArrayOutputStream.size();
                    ByteArrayInputStream requestInput = null;
                    try {
                         requestInput = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                         blob.upload( requestInput, contentLength);
                    } finally {
                        IOUtils.closeQuietly(requestInput);
                        IOUtils.closeQuietly(byteArrayOutputStream);
                    }
                }

                successCount++;
                itemResult.setUrl(finalUrl(metadata));
                itemResult.setInner_url(finalInnerUrl(metadata));
                itemResult.setBucketName(UploadConfig.BUCKET_NAME);
                itemResult.setUrlPrefix(COMMON_URL_PREFIX);
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
            URL downloadUrl = new URL(url);
            return downloadUrl.openConnection().getInputStream();
        } catch (Exception e) {

        }
        String storePath = getStorePathInYun( url  ,false);
        try {

            CloudBlockBlob blob = container.getBlockBlobReference(storePath);
            return blob.openInputStream();

        } catch (Exception e) {
            logger.error("down load file error ,url is :" , url ,e);
        }
        return null;

    }

    /*@Override
    public File download(String url, String storeDirectory) {

        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;

        File file = null;
        try {

            File fileDir = new File(storeDirectory);
            if (!fileDir.isDirectory()){
                throw new IllegalArgumentException( storeDirectory +" is not directory ");
            }
            if (!fileDir.exists()){
                fileDir.mkdirs();
            }

            file = new File(storeDirectory + "/" + getFileName(url));

            if ( !file.exists()) {

                inputStream = download(url);

                fileOutputStream = new FileOutputStream(file);

                byte[] b = new byte[1024];
                int length = 0;
                while ( (length = inputStream.read(b)) != -1){
                    fileOutputStream.write(b,0,length);
                }

            }

        }catch (Exception e){
            logger.error("download file exception {}" , url ,e);
            throw new RuntimeException("download file exception :"+url ,e);
        }finally {

            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(fileOutputStream);


        }

        return file;
    }*/
}
