package com.caicongyang.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 金额转中文
 * @Author 蔡从洋
 * @Date 2021/7/2 15:48
 */
public class AmountCovertUtil {

    // 不考虑分隔符的正确性
    private static final Pattern AMOUNT_PATTERN = Pattern
        .compile("^(0|[1-9]\\d{0,11})\\.(\\d\\d)$");
    private static final char[] RMB_NUMS = "零壹贰叁肆伍陆柒捌玖".toCharArray();
    private static final String[] UNITS = {"元", "角", "分", "整"};
    private static final String[] U1 = {"", "拾", "佰", "仟"};
    private static final String[] U2 = {"", "万", "亿"};

    /**
     * 将金额（整数部分等于或少于 12 位，小数部分 2 位）转换为中文大写形式.
     *
     * @param amount 金额数字
     * @return 中文大写
     */
    public static String convert(String amount) throws IllegalArgumentException {
        // 去掉分隔符
        amount = amount.replace(",", "");

        // 验证金额正确性
        if (amount.equals("0.00")) {
            return "零";
        }
        Matcher matcher = AMOUNT_PATTERN.matcher(amount);
        if (!matcher.find()) {
            throw new IllegalArgumentException("输入金额有误.");
        }

        // 整数部分
        String integer = matcher.group(1);
        // 小数部分
        String fraction = matcher.group(2);

        String result = "";
        if (!integer.equals("0")) {
            // 整数部分
            result += integer2rmb(integer) + UNITS[0];
        }
        if (fraction.equals("00")) {
            // 添加[整]
            result += UNITS[3];
        } else if (fraction.startsWith("0") && integer.equals("0")) {
            // 去掉分前面的[零]
            result += fraction2rmb(fraction).substring(1);
        } else {
            // 小数部分
            result += fraction2rmb(fraction);
        }

        return result;
    }

    // 将金额小数部分转换为中文大写
    private static String fraction2rmb(String fraction) {
        // 角
        char jiao = fraction.charAt(0);
        // 分
        char fen = fraction.charAt(1);
        return (RMB_NUMS[jiao - '0'] + (jiao > '0' ? UNITS[1] : ""))
            + (fen > '0' ? RMB_NUMS[fen - '0'] + UNITS[2] : "");
    }

    // 将金额整数部分转换为中文大写
    private static String integer2rmb(String integer) {
        StringBuilder buffer = new StringBuilder();
        // 从个位数开始转换
        int i, j;
        for (i = integer.length() - 1, j = 0; i >= 0; i--, j++) {
            char n = integer.charAt(i);
            if (n == '0') {
                // 当 n 是 0 且 n 的右边一位不是 0 时，插入[零]
                if (i < integer.length() - 1 && integer.charAt(i + 1) != '0') {
                    buffer.append(RMB_NUMS[0]);
                }
                // 插入[万]或者[亿]
                if (j % 4 == 0) {
                    if (i > 0 && integer.charAt(i - 1) != '0'
                        || i > 1 && integer.charAt(i - 2) != '0'
                        || i > 2 && integer.charAt(i - 3) != '0') {
                        buffer.append(U2[j / 4]);
                    }
                }
            } else {
                if (j % 4 == 0) {
                    // 插入[万]或者[亿]
                    buffer.append(U2[j / 4]);
                }
                // 插入[拾]、[佰]或[仟]
                buffer.append(U1[j % 4]);
                // 插入数字
                buffer.append(RMB_NUMS[n - '0']);
            }
        }
        return buffer.reverse().toString();
    }


    public static void main(String[] args) throws Exception {
        System.out.println(convert("1432 4032 9301.01"));

    }
}
