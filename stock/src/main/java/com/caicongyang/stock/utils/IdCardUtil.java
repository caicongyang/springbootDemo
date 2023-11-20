package com.caicongyang.stock.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caicongyang
 * @version $Id: JsonUtils.java, v 0.1 2015年7月17日 上午11:19:30 caicongyang Exp $
 */
public class IdCardUtil {
    public static final Logger           LOGGER              = LoggerFactory.getLogger(IdCardUtil.class);

    /** 中国公民身份证号码最小长度。 */
    private static final int              CHINA_ID_MIN_LENGTH = 15;

    /** 每位加权因子 */
    private static final int           power[]             = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

    private static Map<String, String> cityCodes           = new HashMap<>();

    static {
        cityCodes.put("11", "北京");
        cityCodes.put("12", "天津");
        cityCodes.put("13", "河北");
        cityCodes.put("14", "山西");
        cityCodes.put("15", "内蒙古");
        cityCodes.put("21", "辽宁");
        cityCodes.put("22", "吉林");
        cityCodes.put("23", "黑龙江");
        cityCodes.put("31", "上海");
        cityCodes.put("32", "江苏");
        cityCodes.put("33", "浙江");
        cityCodes.put("34", "安徽");
        cityCodes.put("35", "福建");
        cityCodes.put("36", "江西");
        cityCodes.put("37", "山东");
        cityCodes.put("41", "河南");
        cityCodes.put("42", "湖北");
        cityCodes.put("43", "湖南");
        cityCodes.put("44", "广东");
        cityCodes.put("45", "广西");
        cityCodes.put("46", "海南");
        cityCodes.put("50", "重庆");
        cityCodes.put("51", "四川");
        cityCodes.put("52", "贵州");
        cityCodes.put("53", "云南");
        cityCodes.put("54", "西藏");
        cityCodes.put("61", "陕西");
        cityCodes.put("62", "甘肃");
        cityCodes.put("63", "青海");
        cityCodes.put("64", "宁夏");
        cityCodes.put("65", "新疆");
        cityCodes.put("71", "台湾");
        cityCodes.put("81", "香港");
        cityCodes.put("82", "澳门");
        cityCodes.put("91", "国外");
    }

    /**
     * 将15位身份证号码转换为18位
     *
     * @param idCard
     *            15位身份编码
     * @return 18位身份编码
     */
    private static String convert15CardTo18(String idCard) {
        if (StringUtils.isBlank(idCard) || idCard.length() != CHINA_ID_MIN_LENGTH || !StringUtils.isNumeric(idCard)) {
            return null;
        }

        // 获取出生年月日
        String birthday = idCard.substring(6, 12);
        Date birthDate = null;
        try {
            birthDate = new SimpleDateFormat("yyMMdd").parse(birthday);
        } catch (ParseException e) {
            LOGGER.error("ParseException", e);
        }
        Calendar cal = Calendar.getInstance();
        if (birthDate != null)
            cal.setTime(birthDate);
        // 获取出生年(完全表现形式,如：2010)
        String sYear = String.valueOf(cal.get(Calendar.YEAR));
        String idCard18 = idCard.substring(0, 6) + sYear + idCard.substring(8);
        // 转换字符数组
        char[] cArr = idCard18.toCharArray();
        int[] iCard = convertCharToInt(cArr);
        int iSum17 = getPowerSum(iCard);
        // 获取校验位
        String sVal = getCheckCode18(iSum17);
        if (sVal.length() > 0) {
            idCard18 += sVal;
        } else {
            return null;
        }
        return idCard18;

    }

    /**
     * 将字符数组转换成数字数组
     *
     * @param ca
     *            字符数组
     * @return 数字数组
     */
    private static int[] convertCharToInt(char[] ca) {
        int len = ca.length;
        int[] iArr = new int[len];
        try {
            for (int i = 0; i < len; i++) {
                iArr[i] = Integer.parseInt(String.valueOf(ca[i]));
            }
        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException", e);
        }
        return iArr;
    }

    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     *
     * @param iArr
     * @return 身份证编码。
     */
    private static int getPowerSum(int[] iArr) {
        int iSum = 0;
        if (power.length == iArr.length) {
            for (int i = 0; i < iArr.length; i++) {
                for (int j = 0; j < power.length; j++) {
                    if (i == j) {
                        iSum = iSum + iArr[i] * power[j];
                    }
                }
            }
        }
        return iSum;
    }

    /**
     * 将power和值与11取模获得余数进行校验码判断
     *
     * @param iSum
     * @return 校验位
     */
    private static String getCheckCode18(int iSum) {
        String sCode = "";
        switch (iSum % 11) {
            case 10:
                sCode = "2";
                break;
            case 9:
                sCode = "3";
                break;
            case 8:
                sCode = "4";
                break;
            case 7:
                sCode = "5";
                break;
            case 6:
                sCode = "6";
                break;
            case 5:
                sCode = "7";
                break;
            case 4:
                sCode = "8";
                break;
            case 3:
                sCode = "9";
                break;
            case 2:
                sCode = "x";
                break;
            case 1:
                sCode = "0";
                break;
            case 0:
                sCode = "1";
                break;
        }
        return sCode;
    }

    /**
     * 根据身份编号获取年龄
     *
     * @param idCard
     *            身份编号
     * @return 年龄
     */
    public static int getAgeByIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return 0;
        }


        String idCard18;
        if (idCard.length() == CHINA_ID_MIN_LENGTH) {
            idCard18 = convert15CardTo18(idCard);
        } else {
            idCard18 = idCard;
        }
        if(StringUtils.isBlank(idCard18)){
            return 0;
        }

        int iYear = Integer.parseInt(idCard18.substring(6, 10));
        int iMonth = Integer.parseInt(idCard18.substring(10, 12)) - 1;//月份是从0开始的
        int iDay = Integer.parseInt(idCard18.substring(12, 14));

        Calendar cal = Calendar.getInstance();
        int iCurrYear = cal.get(Calendar.YEAR);
        int iCurrMonth = cal.get(Calendar.MONTH);
        int iCurrDay = cal.get(Calendar.DATE);
        int iAge = iCurrYear - iYear;
        if ((iMonth > iCurrMonth) || (iMonth == iCurrMonth && iDay > iCurrDay)) {
            //如果月份不足，年龄减1
            //如果月份刚好，天数少1，也不行
            iAge--;
        }
        return iAge;
    }


}
