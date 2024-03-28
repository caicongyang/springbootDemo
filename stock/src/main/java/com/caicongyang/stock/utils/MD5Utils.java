package com.caicongyang.stock.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 编码工具类
 * @author FanJiang 2019/1/24.
 * @since 3.0
 */
public class MD5Utils {

    public static String md5Hex(String data) {
        return DigestUtils.md5Hex(data);
    }

    public static String md5Hex(byte[] data) {
        return DigestUtils.md5Hex(data);
    }
}
