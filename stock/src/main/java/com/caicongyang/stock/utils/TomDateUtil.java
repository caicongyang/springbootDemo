package com.caicongyang.stock.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author caicongyang
 * @version $Id: JsonUtils.java, v 0.1 2015年7月17日 上午11:19:30 caicongyang Exp $
 */
public class TomDateUtil extends org.apache.commons.lang3.time.DateUtils {


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
     * 返回当前日期的字符串，格式：yyyy-MM-dd
     *
     * @return
     */
    public static String getDayPatternCurrentDay() {
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_PATTERN);
        return sdf.format(new Date());
    }


    /**
     * 返回前一天日期的字符串 格式：yyyy-MM-dd
     *
     * @return
     */
    public static String getBeforeDayPatternCurrentDay() {
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
     * 返回字符串
     *
     * @return
     */
    public static String getDateTimePattern(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_TIME_PATTERN);
        return sdf.format(date);
    }


    /**
     * 返回字符串
     *
     * @return
     */
    public static String getDatePattern(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DAY_PATTERN);
        return date.format(formatter);
    }


    /**
     * 返回字符串
     *
     * @return
     */
    public static String getDateTimePattern(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DAY_TIME_PATTERN);
        return date.format(formatter);
    }


    /**
     * Date 转 localDate
     */
    public static LocalDate date2LocalDate(Date date) {
        if (null == date) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date LocalDate2date(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }


    /**
     * Date 转 localDate
     */
    public static LocalDateTime timestamp2LocalDateTime(Long timestamp) {
        if (null == timestamp) {
            return null;
        }
        // 将时间戳转换为 LocalDateTime
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }



    public static LocalDateTime getTimeBefore15Minutes(LocalDateTime dateTime) {
        // 计算15分钟之前的时间
        return dateTime.minus(Duration.ofMinutes(15));
    }


}
