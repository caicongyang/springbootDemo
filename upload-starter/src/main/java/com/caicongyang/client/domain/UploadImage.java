package com.caicongyang.client.domain;


import com.caicongyang.client.constant.UploadConstant;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传图片，打水印，是否生成缩略图，裁剪等配置类
 *
 * @author pengrongxin
 */
public class UploadImage {

    private static final long serialVersionUID = 3381254011391090189L;

    private String format;

    //主图添加水印的标示，10,20,30,不同的值代表不同的水印
    //如果没有指定wateImgUrl 则取该值对应的水印图片
    private int imgWmIndex;

    //套图及水印列表
    private List<ImageItem> imgSeries = new ArrayList<>();

    //水印添加的位置，可选值为:
    // NorthWest
    // North
    // NorthEast
    // West
    // Center
    // East
    // SouthWest
    // South
    // SouthEast
    private UploadConstant.WaterMarkGravity waterMarkGravity = UploadConstant.WaterMarkGravity.SOUTH_EAST;

    /**
     * 水印特效
     */
    private UploadConstant.WaterMarkStyle waterMarkStyle = UploadConstant.WaterMarkStyle.NORMAL;

    //水印图片地址，需要是可以访问的图片地址。
    //如果指定了该值，则忽略imgWmIndex对应的水印图
    private String wateImgUrl;


    //水印透明度，起值为1到100，越小越透明
    private Integer waterMarkDissolve;

    //原图的宽
    private int backupWidth;

    //原图的高
    private int backupHeight;

    //主图是否做无损压缩，默认做无损压缩，减少图片的大小，如果有套图，回自动做无损压缩
    private boolean strip = true;


    public UploadConstant.WaterMarkGravity getWaterMarkGravity() {
        return waterMarkGravity;
    }

    public void setWaterMarkGravity(UploadConstant.WaterMarkGravity waterMarkGravity) {
        this.waterMarkGravity = waterMarkGravity;
    }

    public UploadConstant.WaterMarkStyle getWaterMarkStyle() {
        return waterMarkStyle;
    }

    public void setWaterMarkStyle(UploadConstant.WaterMarkStyle waterMarkStyle) {
        this.waterMarkStyle = waterMarkStyle;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<ImageItem> getImgSeries() {
        return imgSeries;
    }

    public void setImgSeries(List<ImageItem> imgSeries) {
        this.imgSeries = imgSeries;
    }

    public String getWateImgUrl() {
        return wateImgUrl;
    }

    public void setWateImgUrl(String wateImgUrl) {
        this.wateImgUrl = wateImgUrl;
    }

    public int getBackupWidth() {
        return backupWidth;
    }

    public void setBackupWidth(int backupWidth) {
        this.backupWidth = backupWidth;
    }

    public int getBackupHeight() {
        return backupHeight;
    }

    public void setBackupHeight(int backupHeight) {
        this.backupHeight = backupHeight;
    }

    public void addImageItem(ImageItem imageItem) {
        this.imgSeries.add(imageItem);
    }


    public Integer getWaterMarkDissolve() {
        return waterMarkDissolve;
    }

    public void setWaterMarkDissolve(Integer waterMarkDissolve) {
        this.waterMarkDissolve = waterMarkDissolve;
    }

    public int getImgWmIndex() {
        return imgWmIndex;
    }

    public void setImgWmIndex(int imgWmIndex) {
        this.imgWmIndex = imgWmIndex;
    }

    public boolean isStrip() {
        return strip;
    }

    public void setStrip(boolean strip) {
        this.strip = strip;
    }
}
