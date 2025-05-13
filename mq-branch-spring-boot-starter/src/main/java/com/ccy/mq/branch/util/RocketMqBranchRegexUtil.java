package com.ccy.mq.branch.util;

public class RocketMqBranchRegexUtil {

    public static String getLegalStr(String originBranch) {
//        String inputStr = "FEATURE-1233WMS-V2.1..";

        // 定义要保留的字符集合的正则表达式
        String regex = "[^%a-zA-Z0-9_\\-]"; // 匹配不属于指定字符集合的字符，需要转义 -

        // 使用replaceAll()方法进行替换
        String cleanedStr = originBranch.replaceAll(regex, "-");

        // 输出清理后的字符串
//        System.out.println(cleanedStr);
        return cleanedStr;
    }

    public static void main(String[] args) {
                String inputStr = "FEATURE-1233WMS-V2.1";

        // 定义要保留的字符集合的正则表达式
        String regex = "[^%a-zA-Z0-9_\\-]"; // 匹配不属于指定字符集合的字符，需要转义 -

        // 使用replaceAll()方法进行替换
        String cleanedStr = inputStr.replaceAll(regex, "-");

        // 输出清理后的字符串
        System.out.println(cleanedStr);

    }
}
