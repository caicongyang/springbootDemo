package com.caicongyang.client.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author 王明
 * @since 2015-05-06
 */
public class UploadFileUtil {

	private static final Logger log = LoggerFactory.getLogger(UploadFileUtil.class);

	public static BufferedInputStream toBufferedStream(InputStream is) throws IOException {
		byte[] buf = new byte[4096];
		int len = -1;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = is.read(buf)) != -1) {
			bos.write(buf, 0, len);
		}
		is.close();
		
		return new BufferedInputStream(new ByteArrayInputStream(bos.toByteArray()));
	}
	
	public static void saveFile(InputStream is, String filePath) throws IOException {
		File file = new File(filePath);
		File dir = file.getParentFile();
		if(!dir.exists())
			dir.mkdirs();
		OutputStream fos = new FileOutputStream(file);
		int limit = is.available();
		is.mark(limit + 1);
		byte[] buf = new byte[8192];
		int len = -1;
		while ((len = is.read(buf)) != -1) {
			fos.write(buf, 0, len);
		}
		fos.close();
		is.reset();
	}

	// 复制文件
    public static void copyFile(String sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(new File(sourceFile)));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[4096];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

	/**
	 * 按文件名后缀探测ContentType。
	 * 不抛出异常。
	 * @param filename
	 * @return
	 */
	public static String getContentTypeByFilename(String filename){
		try {
			return Files.probeContentType(Paths.get(filename));
		} catch (Exception e) {
			log.warn("getContentTypeByFilename error", e);
		}
		return null;
	}
}

