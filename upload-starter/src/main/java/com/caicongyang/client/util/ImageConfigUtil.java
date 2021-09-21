package com.caicongyang.client.util;


import com.caicongyang.client.domain.ImageItem;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 读取图片配置
 * 
 */
public class ImageConfigUtil {
	
	private static Logger log = LoggerFactory.getLogger(ImageConfigUtil.class);
	
	//图片质量
	private static int quality;
	//
	private static int normalScale;
	//图片压缩最小质量
	private static int minScale;
	//gm命令参数
	private static boolean gmSwitch;
	//服务器url
	private static String postUrl;
	//调用方ip
	private static String ip;
	//传送到fastdfs前,临时文件主图地址
	private static String fatstdfsCacheDir;
	//传送到fastdfs前,临时文件备份图地址
	private static String fatstdfsBackupDir;
	//套图map
	private static Map<String, ImageItem> imageSizeMap = new HashMap<String, ImageItem>();
	//文件后缀判断
	private static Map<String, String> fileFormatMap = new HashMap<String, String>();
	
	static {
		String os = System.getProperties().getProperty("os.name").toLowerCase();
		if(os != null && os.startsWith("win")) { 
			fatstdfsCacheDir = "C:\\tmp\\cache";
			fatstdfsBackupDir = "C:\\tmp\\backup";
		} else {
			fatstdfsCacheDir = "/tmp/cache/";
			fatstdfsBackupDir = "/tmp/backup/";
		}
		
		/*File targetDir = new File(new File(fatstdfsCacheDir).getParent());
		if (!targetDir.exists())
			targetDir.mkdirs();*/
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip= addr.getHostAddress();//获得本机IP
		} catch (UnknownHostException e) {
			log.warn("get host ip error !");
		}
		
		quality = 90;
		normalScale = 90;
		minScale = 70;
		gmSwitch = true;
		String mapPic = "1#380x380;2#200x200;3#115x115;4#90x90;5#60x60;6#40x40;7#80x80;8#160x160;9#450x450";
		String[] sizes = mapPic.trim().split(";");
		if (sizes != null) {
			for (String size : sizes) {
				String[] items = size.split("#");
				if (items.length == 2) {
					String key = items[0];
					String value = items[1];
					String[] values = value.split("x");
					if (values.length == 2) {
						ImageItem img = new ImageItem();
						img.setWidth(Integer.valueOf(values[0]));
						img.setHeight(Integer.valueOf(values[1]));
						imageSizeMap.put(key, img);
					} else
						log.warn("[image.properties] imageSize config error," + value);
				} else
					log.warn("[image.properties] imageSize config error," + size);
			}
		}
		String allowFileFormatString = "SWF,MP3,TXT,PDF,XLS,XLSX,DOC,DOCX,ZIP,RAR,CSV,XML,MP4,3GP,MOV,WAV,AIF,WMA,FLV,TIF,PPT,PPTX,MSG,ODT,LOG,MD5,SQL,MHT,MHTML,HTML,HTM,DHTML,SHTML,XHTML,SHTM,PSD,APK,CSS,JS,PHP,JSON,OGV";
		String[] allowFileFormat = allowFileFormatString.trim().split(",");
		if (allowFileFormat != null) {
			for (String format : allowFileFormat) {
				fileFormatMap.put(format.toUpperCase(), format.toUpperCase());
				//System.out.println(format.toUpperCase());
			}
		}
	}
	
	public static void disableGraphicContext() {
		gmSwitch = false;
	}
	
	public static void enableGraphicContext() {
		gmSwitch = true;
	}

	public static boolean isGmSwitch() {
		return gmSwitch;
	}
	
	
	/**
	 * @param postUrl the postUrl to set
	 */
	public static void setPostUrl(String postUrl) {
		ImageConfigUtil.postUrl = postUrl;
	}

	public static String getPostUrl() {
		return postUrl;
	}

	public static String getFatstdfsCacheDir() {
		return fatstdfsCacheDir;
	}
	
	

	/**
	 * @param fatstdfsCacheDir the fatstdfsCacheDir to set
	 */
	public static void setFatstdfsCacheDir(String fatstdfsCacheDir) {
        ImageConfigUtil.fatstdfsCacheDir = fatstdfsCacheDir;
	}



	public static String getFatstdfsBackupDir() {
		return fatstdfsBackupDir;
	}
	


	/**
	 * @param fatstdfsBackupDir the fatstdfsBackupDir to set
	 */
	public static void setFatstdfsBackupDir(String fatstdfsBackupDir) {
        ImageConfigUtil.fatstdfsBackupDir = fatstdfsBackupDir;
	}

	// 获取图片的压缩质量
	public static int getQuality() {
		return quality;
	}
	
	/**
	 * @param normalScale the normalScale to set
	 */
	public static void setNormalScale(int normalScale) {
		ImageConfigUtil.normalScale = normalScale;
	}


	// 获取图片的普通压缩质量
	public static int getNormalScale() {
		return normalScale;
	}
	
	// 获取图片的最小压缩质量
	public static int getMinScale() {
		return minScale;
	}
	
	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		ImageConfigUtil.ip = ip;
	}

	/**
	 * 获取图片压缩尺寸
	 * 
	 * @return key-value map,1-380x380,2-200x200...
	 */
	public static Map<String, ImageItem> getImageSize() {
		return imageSizeMap;
	}
	
	// 获取设置允许文件的格式
	public static Map<String, String> getFileFormatMap() {
		return fileFormatMap;
	}
}
