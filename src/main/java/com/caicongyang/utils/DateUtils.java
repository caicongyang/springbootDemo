package com.caicongyang.utils;

import java.util.Date;

/**
 * @author caicongyang
 * @version $Id: JsonUtils.java, v 0.1 2015年7月17日 上午11:19:30 caicongyang Exp $
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

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
}
