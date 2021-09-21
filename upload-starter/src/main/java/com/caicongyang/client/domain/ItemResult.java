package com.caicongyang.client.domain;

import java.util.List;

/**
 * Created by pengrongxin on 2017/3/1.
 */
public class ItemResult {


    //生成的文件/图片id
    private Long id;
    private String result;
    private String fileName;
    private String errorInfo;
    private String exception;
    private Long cost_time;
    private String backup_scale;
    private String url;
    /**
     * 如果 有访问多种域名的地址，比如既要求内网地址有要求外网地址，该字段返回内网地址
     * 如果没有这种需求，地址返回和url一样的数据
     */
    private String inner_url;
    private String store_url;
    private String target_url;
    /**
     * 上传图片或者文件的大小(单位是B)
     */
    private Long originalSize;
    /**
     * 处理返回的大小(单位是B)
     */
    private Long returnSize;

    /**
     * 如果上传图片是指定外网地址上传的，
     * 那么该字段保存上传之前的 url,
     * 如果上传图片是指定File上传的，那么就是file.getName
     */
    private String originalFileName;


    /**
     * 当前文件上传使用的bucketName(使用金山云或华为云存储该字段不为空)
     */
    private String bucketName;


    /**
     * @see ItemResult#url
     * @see ItemResult#store_url
     * @see ItemResult#urlPrefix
     * url = urlPrefix + store_url
     */
    private String urlPrefix;

    private List<SerialResult> details;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInner_url() {
        return inner_url;
    }

    public void setInner_url(String inner_url) {
        this.inner_url = inner_url;
    }

    public List<SerialResult> getDetails() {
        return details;
    }

    public void setDetails(List<SerialResult> details) {
        this.details = details;
    }

    public Long getOriginalSize() {
        return originalSize;
    }

    public void setOriginalSize(Long originalSize) {
        this.originalSize = originalSize;
    }

    public Long getReturnSize() {
        return returnSize;
    }

    public void setReturnSize(Long returnSize) {
        this.returnSize = returnSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Long getCost_time() {
        return cost_time;
    }

    public void setCost_time(Long cost_time) {
        this.cost_time = cost_time;
    }

    public String getBackup_scale() {
        return backup_scale;
    }

    public void setBackup_scale(String backup_scale) {
        this.backup_scale = backup_scale;
    }

    public String getTarget_url() {
        return target_url;
    }

    public void setTarget_url(String target_url) {
        this.target_url = target_url;
    }

    public String getStore_url() {
        return store_url;
    }

    public void setStore_url(String store_url) {
        this.store_url = store_url;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }
}
