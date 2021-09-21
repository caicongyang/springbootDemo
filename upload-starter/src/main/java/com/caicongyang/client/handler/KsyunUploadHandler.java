package com.caicongyang.client.handler;

import com.caicongyang.client.UploadException;
import com.caicongyang.client.config.UploadConfig;
import com.caicongyang.client.data.KsyunUploadMetadata;
import com.caicongyang.client.domain.ItemResult;
import com.caicongyang.client.domain.UploadImage;
import com.caicongyang.client.domain.UploadResult;
import com.ksyun.ks3.dto.CannedAccessControlList;
import com.ksyun.ks3.dto.GetObjectResult;
import com.ksyun.ks3.dto.PutObjectResult;
import com.ksyun.ks3.exception.Ks3ServiceException;
import com.ksyun.ks3.http.HttpClientConfig;
import com.ksyun.ks3.service.Ks3;
import com.ksyun.ks3.service.Ks3Client;
import com.ksyun.ks3.service.Ks3ClientConfig;
import com.ksyun.ks3.service.request.DeleteObjectRequest;
import com.ksyun.ks3.service.request.GetObjectRequest;
import com.ksyun.ks3.service.request.PutObjectRequest;
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
 * 金山云存储上传核心类
 * Created by ZhouChenmin on 2017/5/2.
 */
public class KsyunUploadHandler extends UploadHandler {

    private static Logger logger = LoggerFactory.getLogger(KsyunUploadHandler.class);

    private static final int MAX_RETRY_TIME = 3;

    /**
     * true表示以   endpoint/{bucket}/{key}的方式访问</br>
     * false表示以  {bucket}.endpoint/{key}的方式访问
     */
    public static final boolean KSYUN_URL_STYLE = false;
//    public static String KSYUN_URL_PREFIX = null;

    private static Ks3 ks3Client = null;

    /**
     * 构造方法里面初始化静态变量不是很好，这里因为没有任何并发，就不关心了
     * 也可以把 ks3Clint 的 static 去掉
     */
    public KsyunUploadHandler(){


        if ( ks3Client != null){
            return ;
        }


        Ks3ClientConfig config = new Ks3ClientConfig();
        /**
         * 设置服务地址</br>
         * 中国（北京）| ks3-cn-beijing.ksyun.com
         * 中国（上海）| ks3-cn-shanghai.ksyun.com
         * 中国（香港）| ks3-cn-hk-1.ksyun.com
         */
        config.setEndpoint(UploadConfig.END_POINT);   //此处以北京region为例
        config.setProtocol(Ks3ClientConfig.PROTOCOL.http);
        /**
         *true表示以   endpoint/{bucket}/{key}的方式访问</br>
         *false表示以  {bucket}.endpoint/{key}的方式访问
         */
        config.setPathStyleAccess(KSYUN_URL_STYLE);

        HttpClientConfig hconfig = new HttpClientConfig();
        //在HttpClientConfig中可以设置httpclient的相关属性，比如代理，超时，重试等。
        hconfig.setMaxRetry(MAX_RETRY_TIME);
        config.setHttpClientConfig(hconfig);
        ks3Client = new Ks3Client(UploadConfig.FILE_AK,UploadConfig.FILE_SK,config);

        prepareUrlPrefix(Boolean.toString(KsyunUploadHandler.KSYUN_URL_STYLE));
    }


    @Override
    public UploadResult upload(String fileName, String poolName, InputStream io) throws UploadException {

        KsyunUploadMetadata metadata = new KsyunUploadMetadata(fileName,io,poolName);
        return finalUpload(metadata,poolName);

    }


    @Override
    public UploadResult upload(String fileName, String poolName, InputStream io, UploadImage uploadImage) throws UploadException {

        return upload(fileName,poolName,io);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, File file) throws UploadException {

        KsyunUploadMetadata metadata = new KsyunUploadMetadata(uploadName,file,poolName);
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
        KsyunUploadMetadata metadata = new KsyunUploadMetadata(uploadName, dataInputStream,poolName,true);
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

        List<KsyunUploadMetadata> metadataList = new ArrayList<KsyunUploadMetadata>();
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

        List<KsyunUploadMetadata> metadataList = new ArrayList<KsyunUploadMetadata>();
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
                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(UploadConfig.BUCKET_NAME,storePathInKsyun);
                ks3Client.deleteObject(deleteObjectRequest);
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

    public UploadResult finalUpload(KsyunUploadMetadata metadata ,String poolName){

        List<KsyunUploadMetadata> metadataList = new ArrayList<KsyunUploadMetadata>();
        metadataList.add(metadata);
        return finalUpload(metadataList,poolName);

    }


    public UploadResult finalUpload(List<KsyunUploadMetadata> fileList ,String poolName){

        logger.info("use ksyun upload file {}" ,fileList);

        long timeBegin = System.currentTimeMillis();
        List<ItemResult> resultDetail = new ArrayList<ItemResult>();
        ItemResult itemResult = null;
        UploadResult uploadResult = new UploadResult();
        uploadResult.setTotalCount(fileList.size());
        int successCount = 0;
        int failCount = 0;

        for (KsyunUploadMetadata metadata : fileList){

            long itemTimeBegin = System.currentTimeMillis();
            try {

                itemResult = new ItemResult();
                PutObjectRequest putObjectRequest = metadata.convertToUploadReq();
                putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                PutObjectResult putObjectResult = ks3Client.putObject(putObjectRequest);

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
    public void processException(ItemResult itemResult,Exception e){

        logger.error("ksyun operation exception" ,e);
        itemResult.setResult(DEFAULT_FAIL_RETURN);
        if (e instanceof Ks3ServiceException){
            itemResult.setException( ((Ks3ServiceException) e).getErrorMessage() );
        }else{
            itemResult.setException(e.getMessage());
        }

    }


    @Override
    public InputStream download(String url) {

        try {

            if (StringUtils.isBlank(url)){
                throw new IllegalArgumentException("download url can not be null");
            }

            String fileStorePathInKsyun = getStorePathInYun(url,KSYUN_URL_STYLE);

            // FIXME 下载的bucketName需要从url中拿，但是URl又没有一个统一的格式，不一定能截到正确的bucket
            GetObjectRequest request = new GetObjectRequest(UploadConfig.BUCKET_NAME, fileStorePathInKsyun);

            //只接受数据的0-10字节。通过控制该项可以实现分块下载
            //request.setRange(0,10);
            GetObjectResult result  = ks3Client.getObject(request);

            return result.getObject().getObjectContent();

        }catch (Exception e){

            logger.error("download url error {}",url);
            throw new RuntimeException( " download url error " + url ,e);
        }

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
