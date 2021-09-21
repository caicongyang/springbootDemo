package com.caicongyang.client.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;

public class ImageUtil {
	/**
	 * inputStream转化为Byte[]
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] InputStreamToByte(InputStream is) throws IOException {
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		int ch;
		while ((ch = is.read()) != -1) {
			bytestream.write(ch);
		}
		byte imgdata[] = bytestream.toByteArray();
		return imgdata;
	}

	/**
	 * 校验文件大小
	 * 
	 * @param imageByte
	 * @return
	 * @throws IOException
	 *             该图片大小在设定范围内,返回true ,反之返回false
	 */
	public static float checkImageSize(byte[] imageByte) {
		return (float) imageByte.length / (1024 * 1024);
	}

	public static float getRatio(int width, int height, int maxWidth, int maxHeight) {
		float Ratio = 1.0f;
		float widthRatio;
		float heightRatio;
		widthRatio = (float) maxWidth / width;
		heightRatio = (float) maxHeight / height;
		if (widthRatio < 1.0 || heightRatio < 1.0) {
			Ratio = widthRatio <= heightRatio ? widthRatio : heightRatio;
		}
		return Ratio;
	}
	/**
	 * 获取保存图片的中间路径
	 * 
	 * @return
	 */
	public static String obtainSavePicDirPath(Integer pic_id) {
		String picPath = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MMdd");
		picPath = simpleDateFormat.format(new Date()) + "/" + (pic_id % 512)
				+ "/" + ((pic_id / 512) % 512);
		return picPath;
	}

	/**
	 * 删除图片
	 * 
	 * @param src
	 * @return
	 */
	public static boolean removePic(String src) {
		File file = new File(src);
		if (file.isFile()) {
			return file.delete();
		} else {
			return false;
		}
	}

	/**
	 * 备份图片的重命名
	 * 
	 * @param originalName
	 * @return String
	 * @throws
	 */
	public static String getBuckupName(String originalName) {
		String rePicName = originalName;
		// 处理原始图片的名称 ,防止重名
		int dot_index = originalName.lastIndexOf(".");
		rePicName = System.currentTimeMillis() + "_" + RandomUtil.getRandomStr(8) + originalName.substring(dot_index, originalName.length());
		return rePicName;
	}

	/**
	 * 复制图片
	 * 
	 * @param src
	 * @param dest
	 * @return
	 * @throws IOException
	 */
	public static boolean copyPic(String src, String dest) throws IOException {

		if (src != null && dest != null && !src.equalsIgnoreCase(dest)) {
			File srcFile = new File(src);
			File destFile = new File(dest);
			FileUtils.copyFile(srcFile, destFile);
		}
		return true;

	}

}