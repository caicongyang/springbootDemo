package com.caicongyang.client.domain;

import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;

/*
 * 上传文件类，对于每一个上传文件首先要转换为此类，再进行后续处理
 */
public class UploadFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4629886015257894492L;

	// 创建人id
	private String creatorId;

	// 资源类型，图片类型
	private String resourceType;


	// 上传文件名
	private String name;
	
	// pool名称
	private String poolName;

	// 上传文件是否图片
	private String type;
	
	// 操作类型
	private String action;

//	// 原文件地址
//	private String localPath;

	// 原文件备份fastdfs地址
	private String backupUrl;

//	// 原文件备份图本地地址
//	private String backupLocalPath;
	
	// 原文件备份地址
	private String backupName;

	// 原文件大小
	private Long backupSize;

	//文件在fastdfs路径
	private String backUpDfsPath;
	
	//文件在fastdfs所在组
	private String backUpDfsGroup;

	// 原始上传文件大小
	private Long originalSize;
	
	//图片或文件或私有文件 file为V00,image或者不填为N00;N01;N02;N03;N04;N05,secret为A00
	private String fileOrImage; 
	
	//主图是否压缩,默认不压缩
	private Boolean isMajorCompress = Boolean.FALSE;
	//主图压缩大小
	private String majorScale;
	
	//主图压缩后文件大小上限
	private Long majorSize;
	//水印url
	private String watermarkUrl;
	//需要打水印图片url
	private String originUrl;
//	//需要打水印图片临时存放地址
//	private String originFile;
	//打水印X轴偏移量
	private Integer watermarkX ;

	//打水印Y轴偏移量
	private Integer watermarkY ;
	//水印位置  NorthWest, North, NorthEast, West, Center, East, SouthWest, South, SouthEast
	private String watermarkGravity;
	//水印透明度
	private Integer watermarkDissolve;
	//水印顺序
	private String watermarkSequence;	
	//水印旋转
	private double watermarkRotate;
	//是否文字水印
	private Boolean isWatermarkWord  = Boolean.FALSE;
	//水印文字
	private String watermarkText;
	//水印文字字体
	private String watermarkFont;
	//水印文字字体大小
	private Integer watermarkFontSize;
	//水印文字颜色
	private String watermarkColor;
	
	private JSONObject FAL;
	
	private JSONObject FAM;
	
	
	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

//	public String getLocalPath() {
//		return localPath;
//	}
//
//	public void setLocalPath(String localPath) {
//		this.localPath = localPath;
//	}

//	public String getBackupLocalPath() {
//		return backupLocalPath;
//	}
//
//	public void setBackupLocalPath(String backupLocalPath) {
//		this.backupLocalPath = backupLocalPath;
//	}

	public Boolean IsWatermarkWord() {
		return isWatermarkWord;
	}

	public void setIsWatermarkWord(Boolean isWatermarkWord) {
		this.isWatermarkWord = isWatermarkWord;
	}
	
	public double getWatermarkRotate() {
		return watermarkRotate;
	}

	public void setWatermarkRotate(double watermarkRotate) {
		this.watermarkRotate = watermarkRotate;
	}

	public String getWatermarkText() {
		return watermarkText;
	}

	public void setWatermarkText(String watermarkText) {
		this.watermarkText = watermarkText;
	}

	public String getWatermarkFont() {
		return watermarkFont;
	}

	public void setWatermarkFont(String watermarkFont) {
		this.watermarkFont = watermarkFont;
	}

	public Integer getWatermarkFontSize() {
		return watermarkFontSize;
	}

	public void setWatermarkFontSize(Integer watermarkFontSize) {
		this.watermarkFontSize = watermarkFontSize;
	}

	public String getWatermarkColor() {
		return watermarkColor;
	}

	public void setWatermarkColor(String watermarkColor) {
		this.watermarkColor = watermarkColor;
	}

//	public String getOriginFile() {
//		return originFile;
//	}
//
//	public void setOriginFile(String originFile) {
//		this.originFile = originFile;
//	}

	public String getWatermarkGravity() {
		return watermarkGravity;
	}

	public void setWatermarkGravity(String watermarkGravity) {
		this.watermarkGravity = watermarkGravity;
	}

	public Integer getWatermarkDissolve() {
		return watermarkDissolve;
	}

	public void setWatermarkDissolve(Integer watermarkDissolve) {
		this.watermarkDissolve = watermarkDissolve;
	}

	public String getWatermarkSequence() {
		return watermarkSequence;
	}

	public void setWatermarkSequence(String watermarkSequence) {
		this.watermarkSequence = watermarkSequence;
	}

	public Integer getWatermarkX() {
		return watermarkX;
	}

	public void setWatermarkX(Integer watermarkX) {
		this.watermarkX = watermarkX;
	}

	public Integer getWatermarkY() {
		return watermarkY;
	}

	public void setWatermarkY(Integer watermarkY) {
		this.watermarkY = watermarkY;
	}

	public String getWatermarkUrl() {
		return watermarkUrl;
	}

	public void setWatermarkUrl(String watermarkUrl) {
		this.watermarkUrl = watermarkUrl;
	}

	public String getOriginUrl() {
		return originUrl;
	}

	public void setOriginUrl(String originUrl) {
		this.originUrl = originUrl;
	}

	public Long getMajorSize() {
		return majorSize;
	}

	public void setMajorSize(Long majorSize) {
		this.majorSize = majorSize;
	}

	public Boolean IsMajorCompress() {
		return isMajorCompress;
	}

	public void setMajorCompress(Boolean isMajorCompress) {
		this.isMajorCompress = isMajorCompress;
	}

	public String getMajorScale() {
		return majorScale;
	}

	public void setMajorScale(String majorScale) {
		this.majorScale = majorScale;
	}

	public String getFileOrImage() {
		return fileOrImage;
	}

	public void setFileOrImage(String fileOrImage) {
		this.fileOrImage = fileOrImage;
	}

	public String getBackUpDfsPath() {
		return backUpDfsPath;
	}

	public void setBackUpDfsPath(String backUpDfsPath) {
		this.backUpDfsPath = backUpDfsPath;
	}

	public String getBackUpDfsGroup() {
		return backUpDfsGroup;
	}

	public void setBackUpDfsGroup(String backUpDfsGroup) {
		this.backUpDfsGroup = backUpDfsGroup;
	}


	public Long getOriginalSize() {
		return originalSize;
	}

	public void setOriginalSize(Long originalSize) {
		this.originalSize = originalSize;
	}

	public Long getBackupSize() {
		return backupSize;
	}

	public void setBackupSize(Long backupSize) {
		this.backupSize = backupSize;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getBackupUrl() {
		return backupUrl;
	}

	public void setBackupUrl(String backupUrl) {
		this.backupUrl = backupUrl;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBackupName() {
		return backupName;
	}

	public void setBackupName(String backupName) {
		this.backupName = backupName;
	}

	/**
	 * @return the fAL
	 */
	public JSONObject getFAL() {
		return FAL;
	}

	/**
	 * @param fAL the fAL to set
	 */
	public void setFAL(JSONObject fAL) {
		FAL = fAL;
	}

	/**
	 * @return the fAM
	 */
	public JSONObject getFAM() {
		return FAM;
	}

	/**
	 * @param fAM the fAM to set
	 */
	public void setFAM(JSONObject fAM) {
		FAM = fAM;
	}
}
