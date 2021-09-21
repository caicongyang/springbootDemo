package com.caicongyang.client.domain;

import java.io.Serializable;


/**
 * 子图描述类，当上传需要生成缩略图，而且需求裁剪时，需要分表
 * 创建一个ImageItem对象，裁剪需要设置isNeedCrop 为true
 * @author pengrongxin
 *
 */
public class ImageItem implements Serializable {

	private static final long serialVersionUID = -8993066880605808939L;

	//生成缩略图的宽度，或者是裁剪的宽度
	private int width;
	
	//生成缩略图的高度，或者是裁剪的宽度
	private int height;
	
	//中间压缩图的宽
	private int resizeWidth;
	
	//中间压缩图的高
	private int resizeHeight;

	// watermarker, like 0,1,2
    private String watermarker = "0";

    //图片类型，默认的为0，如果想指定不通的文件后缀，直接指定后缀格式
    private String format = "0";

	//是否需要切割
	private boolean isNeedCrop = false;

	//切割X轴起点
	private int imgX ;

	//切割Y轴起点
	private int imgY ;

    //套图添加水印的标示，10,20,30,不同的值代表不同的水印
    private int imgWmIndex ;
	
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getResizeWidth() {
        return resizeWidth;
    }

    public void setResizeWidth(int resizeWidth) {
        this.resizeWidth = resizeWidth;
    }

    public int getResizeHeight() {
        return resizeHeight;
    }

    public void setResizeHeight(int resizeHeight) {
        this.resizeHeight = resizeHeight;
    }

    public String getWatermarker() {
        return watermarker;
    }

    public void setWatermarker(String watermarker) {
        this.watermarker = watermarker;
    }


    public boolean isNeedCrop() {
        return isNeedCrop;
    }

    public void setIsNeedCrop(boolean isNeedCrop) {
        this.isNeedCrop = isNeedCrop;
    }

    public int getImgX() {
        return imgX;
    }

    public void setImgX(int imgX) {
        this.imgX = imgX;
    }

    public int getImgY() {
        return imgY;
    }

    public void setImgY(int imgY) {
        this.imgY = imgY;
    }

    public int getImgWmIndex() {
        return imgWmIndex;
    }

    public void setImgWmIndex(int imgWmIndex) {
        this.imgWmIndex = imgWmIndex;
    }
}
