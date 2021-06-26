package com.caicongyang.stock.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author caicongyang
 * @version $Id: JsonUtils.java, v 0.1 2015年7月17日 上午11:19:30 caicongyang Exp $
 */
public class TomDateUtils extends org.apache.commons.lang3.time.DateUtils {


    private static final String DAY_PATTERN = "yyyy-MM-dd";

    private static final String DAY_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 1天的毫秒数
     */
    private static final long DAY = 24 * 60 * 60 * 1000L;

    /**
     * 1小时的毫秒数
     */
    private static final long HOUR = 60 * 60 * 1000L;

    /**
     * 1分钟的毫秒数
     */
    private static final long MIN = 60 * 1000L;


    /**
     * 计算两个日期之间的天数偏移量
     * dt1 &lt; dt2 返回正数  否则返回负数
     *
     * @param dt1
     * @param dt2
     * @return
     */
    public static int getDayOffset(Date dt1, Date dt2) {
        long diff = dt2.getTime() - dt1.getTime();
        return (int) (diff / DAY);
    }

    /**
     * 计算两个日期之间的小时偏移量
     * dt1 &lt; dt2 返回正数  否则返回负数
     *
     * @param
     * @param
     * @return
     */
    public static int getHourOffset(Date dt1, Date dt2) {
        long diff = dt2.getTime() - dt1.getTime();
        return (int) (diff / HOUR);
    }

    /**
     * 计算两个日期之间的分钟偏移量
     * dt1 &lt; dt2 返回正数  否则返回负数
     *
     * @param
     * @param
     * @return
     */
    public static int getMinOffset(Date dt1, Date dt2) {
        long diff = dt2.getTime() - dt1.getTime();
        return (int) (diff / MIN);
    }


    public static Date formateDayPattern2Date(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_PATTERN);
        Date date = sdf.parse(dateString);
        return date;
    }


    public static Date formateDayTimePattern2Date(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_TIME_PATTERN);
        Date date = sdf.parse(dateString);
        return date;
    }

    /**
     * 返回字符串
     *
     * @return
     */
    public static String getDayPatternCurrentDay() {
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_PATTERN);
        return sdf.format(new Date());
    }



    /**
     * 返回字符串
     *
     * @return
     */
    public static String getDatePattern(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_PATTERN);
        return sdf.format(date);
    }


    /**
     * Date 转 localDate
     */
    public static LocalDate date2LocalDate(Date date) {
        if(null == date) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    public static Date LocalDate2date(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }



}
