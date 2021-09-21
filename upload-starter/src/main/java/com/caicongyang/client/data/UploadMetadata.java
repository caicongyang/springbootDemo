package com.caicongyang.client.data;

import com.caicongyang.client.config.UploadConfig;
import com.caicongyang.client.constant.UploadConstant;
import com.caicongyang.client.handler.UploadHandler;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ZhouChenmin on 2017/5/2.
 */
public  class UploadMetadata implements Closeable{

    private static Logger logger = LoggerFactory.getLogger(UploadMetadata.class);

    protected String fileName;

//    protected String remoteFileName;

    /**
     * 存放在云服务器端里面的相对路径
     * 如果是存放在金山云(如果访问全路径是)
     *
     * http://ody.ks3-cn-shanghai.ksyun.com/product/1493704373680_32.96051688888683_ef3d3772-917f-4dd9-a642-052ca72480dd.jpg
     * 那么该变量的值为  product/1493704373680_32.96051688888683_ef3d3772-917f-4dd9-a642-052ca72480dd.jpg
     *
     * 如果存放在华为云(如果访问全路径是)
     * https://obs-c4b9.obs.cn-east-2.myhwclouds.com/product2/1234.png
     * 那么该变量的值为  product2/1234.png
     *
     */
    protected String storePathInYun ;

    protected File file;

    protected InputStream inputStream;

    protected String poolName;

    protected byte[] bytes;

    protected UploadConstant.FileUploadDataType dataType;

    protected String originFileName;


    protected UploadConstant.UploadType uploadType;



    /**
     * 拼装成在云存储端的url
     * @return
     */
//    public String convertToUrl(){
//
//        return UploadHandler.COMMON_URL_PREFIX + storePathInYun;
//
//    }
    /**
     * 拼装成在云存储端的url
     * @return
     */
//    public abstract String convertToUrl();











    /**
     * 如果fileName 中没有 . 那么就会空指针异常
     * @return
     */
    protected String generateFileNameInYun(){

        //String fileSubFix = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));  //只要后缀名
        String fileSubFix = UUID.randomUUID().toString()  + fileName.substring(fileName.lastIndexOf("."));  //只要后缀名
        String filePre = System.currentTimeMillis() + "_" + Math.random() * 100 + "_";
        //String filePre = "";
        return  filePre + fileSubFix;

    }

    private void generateStorePathInYun(){
        String uploadEnvPrefix = "";
        if (StringUtils.isNotBlank(UploadConfig.UPLOAD_ENV)){
            // 如果需要依据 uploadEnv来区分不同项目
            uploadEnvPrefix = UploadConfig.UPLOAD_ENV + "/";
        }
        storePathInYun = uploadEnvPrefix + poolName + "/" + generateFileNameInYun();
    }


    public UploadMetadata(File file, String poolName){
        this.poolName = poolName;
        this.file = file;
        this.fileName = file.getName();
        dataType = UploadConstant.FileUploadDataType.FILE;
        generateStorePathInYun();
    }


    public UploadMetadata(String fileName, File file, String poolName){
        this.poolName = poolName;
        this.file = file;
        this.fileName = fileName;
        dataType = UploadConstant.FileUploadDataType.FILE;
        generateStorePathInYun();
    }


    public UploadMetadata(String fileName , InputStream inputStream, String poolName){
        this(fileName,inputStream,poolName,false);
    }

    public UploadMetadata(String fileName , InputStream inputStream, String poolName,boolean needCloseInputStream){
        this.poolName = poolName;
        this.fileName = fileName;
        this.inputStream = inputStream;
        if ( needCloseInputStream ){
            dataType = UploadConstant.FileUploadDataType.CLOSEABLE_INPUT_STREAM;
        }else {
            dataType = UploadConstant.FileUploadDataType.INPUT_STREAM;
        }

        generateStorePathInYun();
    }


    /*public UploadMetadata(String fileName , byte[] bytes, String poolName){
        this.poolName = poolName;
        this.fileName = fileName;
        this.bytes = bytes;
        bytes = null;
        dataType = UploadConstant.FileUploadDataType.BYTE;
        generateStorePathInYun();
    }*/

    /**
     * 文件的url地址
     * @param fileUrl
     * @param poolName
     */
    public UploadMetadata(String fileUrl , String poolName){
        this.originFileName = fileUrl;
        this.poolName = poolName;
        this.fileName = UploadHandler.getFileName(fileUrl);
        try {
            this.inputStream = new URL(fileUrl).openStream();
        } catch (Exception e) {
            throw new RuntimeException("can not open url: " + fileUrl , e);
        }
        // 该流需要关闭
        dataType = UploadConstant.FileUploadDataType.CLOSEABLE_INPUT_STREAM;
        generateStorePathInYun();
    }

    public String getFileSuffix(){
        return fileName.substring(fileName.lastIndexOf("."));
    }



    @Override
    public void close() {

        if (UploadConstant.FileUploadDataType.CLOSEABLE_INPUT_STREAM == dataType){

            IOUtils.closeQuietly(this.inputStream);

        }
    }

    @Override
    public String toString() {
        return "UploadMetadata{" +
                "fileName='" + fileName + '\'' +
                ", storePathInKsyun='" + storePathInYun + '\'' +
                ", file=" + file +
                ", poolName='" + poolName + '\'' +
                '}';
    }

    public UploadConstant.FileUploadDataType getDataType() {
        return dataType;
    }

    public void setDataType(UploadConstant.FileUploadDataType dataType) {
        this.dataType = dataType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getStorePathInYun() {
        return storePathInYun;
    }

    public void setStorePathInYun(String storePathInYun) {
        this.storePathInYun = storePathInYun;
    }

    public String getOriginFileName() {
        if (StringUtils.isNotBlank(originFileName)){
            return originFileName;
        }else {
            return fileName;
        }
    }

    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }

}
