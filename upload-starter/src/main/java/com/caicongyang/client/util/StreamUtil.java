package com.caicongyang.client.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ZhouChenmin on 2017/6/14.
 */
public class StreamUtil {


    /**
     * 从文件读取byte[]
     * @param file
     * @return
     */
    public static byte[] readByteFromFile(File file){

        if (file.isDirectory()){
            throw new IllegalArgumentException("file can not be directory " + file.getAbsolutePath());
        }else if (!file.exists()){
            throw new IllegalArgumentException("file not exists " + file.getAbsolutePath());
        }

        FileInputStream fileInputStream = null;
        int fileLength = (int)file.length();
        byte[] content = new byte[fileLength];
        try {
            // 一次性读出来不会占太多内存，和mappedBuffer,directBuffer 差不多
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(content);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return content;
    }

    /**
     * 从inputStream读取byte[]
     * @param inputStream
     * @return
     */
    public static byte[] readByteFromInputStream(InputStream inputStream){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int length = 0;
        try {
            while ((length = inputStream.read(b)) != -1){
                byteArrayOutputStream.write(b,0,length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (byteArrayOutputStream != null){
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return byteArrayOutputStream.toByteArray();

    }


}
