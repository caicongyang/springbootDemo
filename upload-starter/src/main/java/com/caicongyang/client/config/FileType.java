package com.caicongyang.client.config;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class FileType {


    // TODO 需不需要 从配置文件获取
    public static final List<String> IMAGE_TYPES = Arrays.asList("JPG","BMP","GIF","JPEG","PNG","TIFF","WMF");

    public static boolean imageType(String suffix){
        if (StringUtils.isBlank( suffix )){
            return false;
        }
        if ( suffix.startsWith(".")){
            suffix = suffix.substring(1);
        }
        for ( String imageType : IMAGE_TYPES ){
            if ( imageType.equalsIgnoreCase( suffix )){
                return true ;
            }
        }
        return false;
    }


    /**
     * 根据文件后缀返回 ContentType, 目前只有 图片需要
     * @param suffix
     * @return
     */
    public static String getContentType(String suffix){

        if ( imageType( suffix )){
            if ( suffix.startsWith(".")){
                suffix = suffix.substring(1);
            }
            return "image/" + suffix;
        } else {
            return null;
        }

    }
}
