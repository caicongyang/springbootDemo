package com.caicongyang.client.util;

import java.util.Random;

public final class RandomUtil {

	// 随机字符串
	private static char[] NUM_AND_LETTER = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	private static int NUM_AND_LETTER_LEN = 62;

	public static String getRandomStr(int len){
		if (len <= 0) {
			return "";
		}
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append(NUM_AND_LETTER[r.nextInt(NUM_AND_LETTER_LEN - 1)]);
		}
		return sb.toString();
	}
}
