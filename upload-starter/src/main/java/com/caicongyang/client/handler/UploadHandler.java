package com.caicongyang.client.handler;

import com.caicongyang.client.UploadException;
import com.caicongyang.client.config.UploadConfig;
import com.caicongyang.client.constant.UploadConstant;
import com.caicongyang.client.data.UploadMetadata;
import com.caicongyang.client.domain.ItemResult;
import com.caicongyang.client.domain.UploadImage;
import com.caicongyang.client.domain.UploadResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 上传的抽象类，先设计成了抽象类，如果没有其他需求，后续会改成接口z
 * Created by ZhouChenmin on 2017/5/2.
 */
public abstract class UploadHandler {

    private static Logger logger = LoggerFactory.getLogger(UploadHandler.class);

    protected static final String DEFAULT_SUCCESS_RETURN = "success";
    protected static final String DEFAULT_FAIL_RETURN = "fail";

    public String  COMMON_URL_PREFIX = null;


    public static String getFileName(String url){

        if (StringUtils.isBlank(url)){
            throw new IllegalArgumentException("download url can not be null");
        }

        String fileName  ="";

        if (url.indexOf("/") > -1){

            fileName = url.substring(url.lastIndexOf("/") + 1 ,url.length());
        }
        return fileName;
    }



    public abstract UploadResult upload(final String fileName, String poolName, final InputStream io) throws UploadException;

    public abstract UploadResult upload(final String fileName, String poolName, final InputStream io, UploadImage uploadImage) throws UploadException;

    public abstract UploadResult upload(String uploadName, String poolName, File file) throws UploadException;

    public abstract UploadResult upload(String uploadName, String poolName, File file, UploadImage uploadImage) throws UploadException;

    public abstract UploadResult upload(String uploadName, String poolName, byte[] content) throws UploadException;

    public abstract UploadResult upload(String uploadName, String poolName, byte[] content, UploadImage uploadImage) throws UploadException;

    public abstract UploadResult upload(String filePath, String poolName) throws UploadException;

    public abstract UploadResult upload(String filePath, String poolName, UploadImage uploadImage) throws UploadException;

    public abstract UploadResult batchUpload(List<String> fileList, String poolName, UploadImage uploadImage) throws UploadException;

    public abstract UploadResult uploadByUrl(String fileUrl, String poolName) throws UploadException;

    public abstract UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName) throws UploadException;

    public abstract UploadResult batchUploadByUrl(List<String> fileUrlList, String poolName, UploadImage uploadImage) throws UploadException;

    public abstract UploadResult upload(File file, String poolName, UploadImage uploadImage) throws UploadException;

    //public abstract UploadResult uploadMulitFile(List<File> fileList, String poolName, UploadImage uploadImage) throws UploadException;

    public abstract UploadResult delete(String fileUrl, String poolName ) throws UploadException ;

    public abstract UploadResult delete(List<String> fileList, String poolName,boolean deleteSeriesImage) throws UploadException ;

    public abstract InputStream download(String url);

    public File download(String url, String storeDirectory){

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

    }


    public void processException(ItemResult itemResult, Exception e){


        logger.error(" odfs operation exception" ,e);
        itemResult.setResult(DEFAULT_FAIL_RETURN);
        itemResult.setException(e.getMessage());

    }


    protected void prepareUrlPrefix(){
        prepareUrlPrefix(UploadConstant.USE_POINT);
    }

    protected void prepareUrlPrefix(String style) {
        StringBuilder sb = new StringBuilder(UploadConfig.getUploadProtocolPrefix());

        if (StringUtils.isNotBlank(UploadConfig.FILE_DOMAIN)){
            if (UploadConfig.FILE_DOMAIN.startsWith(UploadConstant.HTTP_PROTOCOL) || UploadConfig.FILE_DOMAIN.startsWith(UploadConstant.HTTPS_PROTOCOL)) {
                sb = new StringBuilder();
            }
            sb.append(UploadConfig.FILE_DOMAIN).append("/");

        }else {

            if (Boolean.TRUE.toString().equals(style)) {
                sb.append(UploadConfig.END_POINT)
                        .append("/")
                        .append(UploadConfig.BUCKET_NAME)
                        .append("/");
            } else if (Boolean.FALSE.toString().equals(style)) {
                sb.append(UploadConfig.BUCKET_NAME)
                        .append(".")
                        .append(UploadConfig.END_POINT)
                        .append("/");
            } else {
                sb.append(UploadConfig.END_POINT)
                        .append("/");
            }

        }
        COMMON_URL_PREFIX = sb.toString();
    }


    protected String getStorePathInYun(String targetUrl ){
        return getStorePathInYun(targetUrl,false);
    }

    /**
     * 删除的时候只需要存放在yun上的存储路径，该方法把完整的url还原成在云的保存路径
     * 建议不要使用replace 方法去替换endPointer和bucketName，因为可能会出现拿由老的bucket拼成的url来删除
     *
     * @param targetUrl
     * @return
     */
    protected String getStorePathInYun(String targetUrl ,boolean urlStyle){

        String fileNameInYun =  null;

        if( !fullUrl(targetUrl) ){


            if (targetUrl.startsWith("/")){
                return targetUrl.substring(1);
            }else {
                return targetUrl;
            }

        }

        targetUrl = targetUrl.replace(UploadConstant.HTTP_PROTOCOL,"");
        targetUrl = targetUrl.replace(UploadConstant.HTTPS_PROTOCOL,"");

        /**
         *true表示以   endpoint/{bucket}/{key}的方式访问</br>
         *false表示以  {bucket}.endpoint/{key}的方式访问
         */
        if (!urlStyle){
            fileNameInYun = targetUrl.substring(targetUrl.indexOf("/") + 1);
        }else {
            String tempString = targetUrl.substring(targetUrl.indexOf("/") + 1);
            fileNameInYun = tempString.substring(tempString.indexOf("/") + 1);
        }

        return fileNameInYun;

    }

    public static boolean fullUrl( String targetUrl ) {


        if ( targetUrl.startsWith(UploadConstant.HTTP_PROTOCOL) || targetUrl.startsWith(UploadConstant.HTTPS_PROTOCOL) ){

            return true;
        }

        return false;

    }

    public static String getBucketNameFromUrl (String targetUrl ,boolean urlStyle){

        if ( fullUrl(targetUrl) ){



        }
        return "";

    }



    private String finalUrl(String storePathInYun){

        return COMMON_URL_PREFIX + storePathInYun;
    }

    public String finalUrl(UploadMetadata metadata){

        return finalUrl(metadata.getStorePathInYun());
    }

    /**
     * 内网访问地址,暂无区别,
     * @param metadata
     * @return
     */
    public String finalInnerUrl(UploadMetadata metadata){

        return finalUrl(metadata.getStorePathInYun());
    }


    public static ByteArrayOutputStream cloneInputStream(InputStream input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}