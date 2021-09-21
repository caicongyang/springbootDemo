package com.caicongyang.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2021/6/24 17:56
 */
@ConfigurationProperties(prefix = "spring.odfs.filter")
public class OdfsFilterProperties {
    
    private static OdfsFilterProperties instance;
    
    public static OdfsFilterProperties getInstance() {
        return instance;
    }
    
    private OdfsFilterProperties() {
        OdfsFilterProperties.instance = this;
    }
    
    private Boolean watermark;
    private String uploadImageWatermark;
    private Integer imageQualityWifi = 80;
    private String imgUrlPattern = "^(http://|https://|//).*\\.(?i)(png|bmp|jpg|jpeg|tiff|gif|pcx|tga|exif|fpx|svg|psd|cdr|pcd|dxf|ufo|eps|ai|raw)?.*$";
    private String imagePattern = ".*\\.(?i)(png|bmp|jpg|jpeg|tiff|gif|pcx|tga|exif|fpx|svg|psd|cdr|pcd|dxf|ufo|eps|ai|raw)$";
    private String[] wddDomain = new String[]{"file.wddcn.com"};
    private String[] ksyunDomain = new String[]{".+.ks-cdn.com", ".*.ksyun.com,.*cdn.oudianyun.com"};
    private String[] lyfDomain = new String[]{"images.laiyifen.com"};
    private String[] wddPath = new String[]{"/wddwechatshop/picture/goods/.*"};
    private String docPattern = ".*\\.(?i)(txt|pdf|xls|xlsx|doc|docx|ppt|pptx|zip|rar|7z|wps|md|html|csv)$";
    private String mediaPattern;
    private Boolean rotateIfNeed;
    private Integer watermarkDissolve = 40;
    private Boolean allowUpdate = true;
    
    public Boolean getWatermark() {
        return watermark;
    }
    
    public void setWatermark(Boolean watermark) {
        this.watermark = watermark;
    }
    
    public String getUploadImageWatermark() {
        return uploadImageWatermark;
    }
    
    public void setUploadImageWatermark(String uploadImageWatermark) {
        this.uploadImageWatermark = uploadImageWatermark;
    }
    
    public Integer getImageQualityWifi() {
        return imageQualityWifi;
    }
    
    public void setImageQualityWifi(Integer imageQualityWifi) {
        this.imageQualityWifi = imageQualityWifi;
    }
    
    public String getImgUrlPattern() {
        return imgUrlPattern;
    }
    
    public void setImgUrlPattern(String imgUrlPattern) {
        this.imgUrlPattern = imgUrlPattern;
    }
    
    public String getImagePattern() {
        return imagePattern;
    }
    
    public void setImagePattern(String imagePattern) {
        this.imagePattern = imagePattern;
    }
    
    public String[] getWddDomain() {
        return wddDomain;
    }
    
    public void setWddDomain(String[] wddDomain) {
        this.wddDomain = wddDomain;
    }
    
    public String[] getKsyunDomain() {
        return ksyunDomain;
    }
    
    public void setKsyunDomain(String[] ksyunDomain) {
        this.ksyunDomain = ksyunDomain;
    }
    
    public String[] getLyfDomain() {
        return lyfDomain;
    }
    
    public void setLyfDomain(String[] lyfDomain) {
        this.lyfDomain = lyfDomain;
    }
    
    public String[] getWddPath() {
        return wddPath;
    }
    
    public void setWddPath(String[] wddPath) {
        this.wddPath = wddPath;
    }
    
    public String getDocPattern() {
        return docPattern;
    }
    
    public void setDocPattern(String docPattern) {
        this.docPattern = docPattern;
    }
    
    public String getMediaPattern() {
        return mediaPattern;
    }
    
    public void setMediaPattern(String mediaPattern) {
        this.mediaPattern = mediaPattern;
    }
    
    public Boolean getRotateIfNeed() {
        return rotateIfNeed;
    }
    
    public void setRotateIfNeed(Boolean rotateIfNeed) {
        this.rotateIfNeed = rotateIfNeed;
    }
    
    public Integer getWatermarkDissolve() {
        return watermarkDissolve;
    }
    
    public void setWatermarkDissolve(Integer watermarkDissolve) {
        this.watermarkDissolve = watermarkDissolve;
    }
    
    public Boolean getAllowUpdate() {
        return allowUpdate;
    }
    
    public void setAllowUpdate(Boolean allowUpdate) {
        this.allowUpdate = allowUpdate;
    }
}
