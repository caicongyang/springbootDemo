package com.caicongyang.client.handler;


import com.alibaba.fastjson.JSON;
import com.aliyun.oss.model.GetObjectRequest;
import com.caicongyang.client.UploadException;
import com.caicongyang.client.config.UploadConfig;
import com.caicongyang.client.constant.UploadConstant;
import com.caicongyang.client.data.UploadMetadata;
import com.caicongyang.client.domain.ItemResult;
import com.caicongyang.client.domain.UploadImage;
import com.caicongyang.client.domain.UploadResult;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 阿里云存储上传核心类 Created by ZhouChenmin on 2018/4/10.
 */
public class QnyunUploadHandler extends UploadHandler {


    private static Logger logger = LoggerFactory.getLogger(QnyunUploadHandler.class);

//    public static String QNYUN_URL_PREFIX = null;


    Configuration cfg;

    UploadManager uploadManager;


    public QnyunUploadHandler() {

        if (uploadManager != null && cfg != null) {
            return;
        }

        cfg = new Configuration(getZone());

        cfg.connectTimeout = 10000;
        cfg.readTimeout = 30000;
        //...其他参数参考类注释
        uploadManager = new UploadManager(cfg);

        prepareUrlPrefix(UploadConstant.ONLY_SLASH);
    }

    @Override
    public UploadResult upload(String fileName, String poolName, InputStream io)
        throws UploadException {
        UploadMetadata metadata = new UploadMetadata(fileName, io, poolName);
        return finalUpload(metadata, poolName);
    }

    @Override
    public UploadResult upload(String fileName, String poolName, InputStream io,
        UploadImage uploadImage) throws UploadException {

        return upload(fileName, poolName, io);
    }

    @Override
    public UploadResult upload(String uploadName, String poolName, File file)
        throws UploadException {

        UploadMetadata metadata = new UploadMetadata(uploadName, file, poolName);
        return finalUpload(metadata, poolName);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, File file,
        UploadImage uploadImage) throws UploadException {

        return upload(file.getName(), poolName, file);
    }

    @Override
    public UploadResult upload(String uploadName, String poolName, byte[] content)
        throws UploadException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        UploadMetadata metadata = new UploadMetadata(uploadName, dataInputStream, poolName, true);
        return finalUpload(metadata, poolName);
    }


    @Override
    public UploadResult upload(String uploadName, String poolName, byte[] content,
        UploadImage uploadImage) throws UploadException {

        return upload(uploadName, poolName, content);
    }


    @Override
    public UploadResult upload(String filePath, String poolName) throws UploadException {

        File file = new File(filePath);
        UploadMetadata metadata = new UploadMetadata(filePath, file, poolName);
        return finalUpload(metadata, poolName);
    }

    @Override
    public UploadResult upload(String filePath, String poolName, UploadImage uploadImage)
        throws UploadException {

        return upload(filePath, poolName);
    }


    @Override
    public UploadResult batchUpload(List<String> fileList, String poolName, UploadImage uploadImage)
        throws UploadException {

        List<UploadMetadata> metadataList = new ArrayList<>();
        for (String filePath : fileList) {
            File file = new File(filePath);
            metadataList.add(new UploadMetadata(file, poolName));
        }
        return finalUpload(metadataList, poolName);
    }

    @Override
    public UploadResult uploadByUrl(String fileUrl, String poolName) throws UploadException {

        return batchUploadByUrl(Arrays.asList(fileUrl), poolName);
    }

    @Override
    public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName)
        throws UploadException {

        return batchUploadByUrl(fileUrlList, poolName, null);
    }

    @Override
    public UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName,
        UploadImage uploadImage) throws UploadException {

        List<UploadMetadata> metadataList = new ArrayList<>();
        for (String fileUrlPath : fileUrlList) {
            metadataList.add(new UploadMetadata(fileUrlPath, poolName));
        }
        return finalUpload(metadataList, poolName);
    }

    @Override
    public UploadResult upload(File file, String poolName, UploadImage uploadImage)
        throws UploadException {

        return upload(file.getName(), poolName, file);
    }

    @Override
    public UploadResult delete(String fileUrl, String poolName) throws UploadException {

        return delete(Arrays.asList(fileUrl), poolName, true);
    }

    @Override
    public UploadResult delete(List<String> fileList, String poolName, boolean deleteSeriesImage)
        throws UploadException {

        long timeBegin = System.currentTimeMillis();
        List<ItemResult> resultDetail = new ArrayList<ItemResult>();
        ItemResult itemResult = null;
        UploadResult uploadResult = new UploadResult();
        uploadResult.setTotalCount(fileList.size());
        int successCount = 0;
        int failCount = 0;

        for (String fileUrl : fileList) {

            long itemTimeBegin = System.currentTimeMillis();
            try {
                itemResult = new ItemResult();
                String storePathInKsyun = getStorePathInYun(fileUrl, false);
                // FIXME 删除的bucketName需要从url中拿，但是URl又没有一个统一的格式，不一定能截到正确的bucket

                Auth auth = Auth.create(UploadConfig.FILE_AK, UploadConfig.FILE_SK);
                BucketManager bucketManager = new BucketManager(auth, cfg);
                bucketManager.delete(UploadConfig.BUCKET_NAME, storePathInKsyun);
                successCount++;
                itemResult.setResult(DEFAULT_SUCCESS_RETURN);
            } catch (Exception e) {
                failCount++;
                processException(itemResult, e);
            } finally {
                itemResult.setCost_time(System.currentTimeMillis() - itemTimeBegin);
                resultDetail.add(itemResult);
            }

        }
        uploadResult.setResultDetail(resultDetail);
        uploadResult.setSuccessCount(successCount);
        uploadResult.setFailCount(failCount);
        uploadResult.setTotalCostTime((int) (System.currentTimeMillis() - timeBegin));

        return uploadResult;

    }


    public UploadResult finalUpload(UploadMetadata metadata, String poolName) {

        List<UploadMetadata> metadataList = new ArrayList<>();
        metadataList.add(metadata);
        return finalUpload(metadataList, poolName);

    }


    public UploadResult finalUpload(List<UploadMetadata> fileList, String poolName) {

        logger.info("use qnyun upload file {}", fileList);

        long timeBegin = System.currentTimeMillis();
        List<ItemResult> resultDetail = new ArrayList<ItemResult>();
        ItemResult itemResult = null;
        UploadResult uploadResult = new UploadResult();
        uploadResult.setTotalCount(fileList.size());
        int successCount = 0;
        int failCount = 0;

        for (UploadMetadata metadata : fileList) {

            long itemTimeBegin = System.currentTimeMillis();
            try {

                itemResult = new ItemResult();

                Response response = doUpload(metadata);

                DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);

                if (logger.isInfoEnabled()) {
                    logger.info(" qnyun upload response return {}", JSON.toJSONString(putRet));
                }

                successCount++;
                itemResult.setBucketName(UploadConfig.BUCKET_NAME);
                itemResult.setUrlPrefix(COMMON_URL_PREFIX);
                itemResult.setUrl(finalUrl(metadata));
                itemResult.setInner_url(finalInnerUrl(metadata));
                itemResult.setStore_url(metadata.getStorePathInYun());
                itemResult.setResult(DEFAULT_SUCCESS_RETURN);
                itemResult.setOriginalFileName(metadata.getOriginFileName());

            } catch (Exception e) {
                failCount++;
                processException(itemResult, e);
            } finally {
                metadata.close();
                itemResult.setCost_time(System.currentTimeMillis() - itemTimeBegin);
                resultDetail.add(itemResult);
            }

        }
        uploadResult.setResultDetail(resultDetail);
        uploadResult.setSuccessCount(successCount);
        uploadResult.setFailCount(failCount);
        uploadResult.setTotalCostTime((int) (System.currentTimeMillis() - timeBegin));

        return uploadResult;

    }


    private String getUrl(String key) {
        // domain   下载 domain, eg: qiniu.com【必须】
// useHttps 是否使用 https【必须】
// key      下载资源在七牛云存储的 key【必须】
        DownloadUrl url = new DownloadUrl(domain, false, key);
        url.setAttname(attname) // 配置 attname
            .setFop(fop) // 配置 fop
            .setStyle(style, styleSeparator, styleParam) // 配置 style
        // 带有效期
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        Auth auth = Auth.create("your access key", "your secret key");
        String urlString = url.buildURL(auth, deadline);

    }


    private Response doUpload(UploadMetadata metadata) throws QiniuException {

        Response response = null;

        Auth auth = Auth.create(UploadConfig.FILE_AK, UploadConfig.FILE_SK);
        String upToken = auth.uploadToken(UploadConfig.BUCKET_NAME);

        String key = metadata.getStorePathInYun();

        if (UploadConstant.FileUploadDataType.FILE == metadata.getDataType()) {

            response = uploadManager.put(metadata.getFile(), key, upToken);

        } else if (UploadConstant.FileUploadDataType.INPUT_STREAM == metadata.getDataType()
            || UploadConstant.FileUploadDataType.CLOSEABLE_INPUT_STREAM == metadata.getDataType()) {

            response = uploadManager.put(metadata.getInputStream(), key, upToken, null, null);

        } else {
            throw new IllegalStateException(" upload metadata must be file or inputStream");
        }

        return response;

    }


    @Override
    public InputStream download(String url) {

        try {
            URL downloadUrl = new URL(url);
            return downloadUrl.openConnection().getInputStream();
        } catch (Exception e) {

        }

        try {

            if (StringUtils.isBlank(url)) {
                throw new IllegalArgumentException("download url can not be null");
            }

            String fileStorePathInKsyun = getStorePathInYun(url, false);
            // FIXME 下载的bucketName需要从url中拿，但是URl又没有一个统一的格式，不一定能截到正确的bucket
            GetObjectRequest request = new GetObjectRequest(UploadConfig.BUCKET_NAME,
                fileStorePathInKsyun);

            //只接受数据的0-10字节。通过控制该项可以实现分块下载
            //request.setRange(0,10);
            //OSSObject result  = ossClient.getObject(request);

            //return result.getObjectContent();

            return null;
        } catch (Exception e) {

            logger.error("download url error {}", url);
            throw new RuntimeException(" download url error " + url, e);
        }
    }


    /**
     * 机房 	Zone对象 华东 	Zone.zone0() 华北 	Zone.zone1() 华南 	Zone.zone2() 北美 	Zone.zoneNa0()
     *
     *
     * 因为就这个几个 机房，并且只会传到一个机房里面，没有必要用map存起来，也没有必须要用反射执行
     */
    public Zone getZone() {

        String zoneKey = UploadConfig.getQnYunUploadZone();

        if ("0".equals(zoneKey)) {

            return Zone.zone0();
        } else if ("1".equals(zoneKey)) {

            return Zone.zone1();
        } else if ("2".equals(zoneKey)) {

            return Zone.zone2();
        } else if ("Na0".equals(zoneKey)) {

            return Zone.zoneNa0();
        } else {

            throw new IllegalArgumentException(" 七牛云zone只有 zone0,zone1,zone2,zoneNa0");
        }

    }
}
