package com.caicongyang.utils;

import com.google.common.base.Joiner;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.util.StringUtils;

/**
 * 日期工具类
 *
 * @author WuBo
 * @CreateDate 2012-8-13 下午12:21:25
 */
public class DateUtils {

    public static final SafeSimpleDateFormat YYYY_MM_DD = new SafeSimpleDateFormat("yyyy-MM-dd");
    public static final SafeSimpleDateFormat YYYY_MM_DD_HH = new SafeSimpleDateFormat(
        "yyyy-MM-dd HH");
    public static final SafeSimpleDateFormat YYYY_MM_DD_HH_MI = new SafeSimpleDateFormat(
        "yyyy-MM-dd HH:mm");
    public static final SafeSimpleDateFormat YYYY_MM_DD_HH_MI_SS = new SafeSimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
    public static final SafeSimpleDateFormat DEFAULT = YYYY_MM_DD_HH_MI;

    /**
     * 根据pattern得到SafeSimpleDateFormat
     *
     * @author WuBo
     * @CreateDate 2012-8-13 下午12:36:19
     */
    public static SafeSimpleDateFormat getDateFormat(String pattern) {
        if ("yyyy-MM-dd".equals(pattern)) {
            return YYYY_MM_DD;
        } else if ("yyyy-MM-dd HH".equals(pattern)) {
            return YYYY_MM_DD_HH;
        } else if ("yyyy-MM-dd HH:mm".equals(pattern)) {
            return YYYY_MM_DD_HH_MI;
        } else if ("yyyy-MM-dd HH:mm:ss".equals(pattern)) {
            return YYYY_MM_DD_HH_MI_SS;
        } else {
            return new SafeSimpleDateFormat(pattern);
        }
    }

    public static String date2Str(Date date) {
        return date2Str(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 转换日期为自定义的格式字符串
     *
     * @return 格式化的日期字符串
     */
    public static String date2Str(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        return getDateFormat(pattern).format(date);
    }

    public static Date str2Date(String date, String pattern) throws ParseException {
        if (!StringUtils.hasText(date)) {
            return null;
        }

        return getDateFormat(pattern).parse(date);
    }

    /**
     * 智能化的将字符转换成日期格式
     */
    public static Date str2Date(String date) throws ParseException {
        if (!StringUtils.hasText(date)) {
            return null;
        }

        if (date.indexOf(":") != -1) {
            if (date.length() == 8) {
                return parse2Time(date);
            } else {
                return parse2DateTime(date);
            }
        } else {
            return parse2Date(date);
        }
    }

    public static Date parse2Date(String date) throws ParseException {
        if (!StringUtils.hasText(date)) {
            return null;
        }

        if (date.matches("\\d+")) {
            return new Date(Long.parseLong(date));
        }
        ;

        return getDateFormat(getDatePattern(date)).parse(date);
    }

    public static Date parse2DateTime(String dateTime) throws ParseException {
        if (dateTime.matches("\\d+")) {
            return new Date(Long.parseLong(dateTime));
        }

        String[] spt = dateTime.split("[ ]+");
        String d = spt[0];
        String pattern = "";
        if (d.indexOf(":") != -1) {
            pattern += getTimePattern(d);
        } else {
            pattern = getDatePattern(d);
        }

        if (spt.length > 1) {
            pattern += " " + getTimePattern(spt[1]);
        }

        return getDateFormat(pattern).parse(dateTime);
    }

    public static Date parse2Time(String time) throws ParseException {
        return getDateFormat(getTimePattern(time)).parse(time);
    }

    private static String getTimePattern(String time) {
        int l = time.split(":").length;
        if (l > 2) {
            return "HH:mm:ss";
        } else if (l == 2) {
            return "HH:mm";
        }
        return "HH";
    }

    private static String getDatePattern(String date) {
        String split = "-";
        if (date.indexOf("/") != -1) {
            split = "/";
        }

        String[] spt = date.split(split);
        List<String> arr = new ArrayList<String>(3);
        boolean month = false, day = false;
        for (String t : spt) {
            if (t.length() == 4) {
                arr.add("yyyy");
            } else {
                int n = Integer.parseInt(t);
                int l = t.length();
                if (n > 31) {
                    arr.add("yy");
                } else if (n > 12) {
                    if (!day) {
                        arr.add("dd");
                        day = true;
                    } else {
                        arr.add("yy");
                    }
                } else {
                    if (month) {
                        if (l == 2) {
                            arr.add("dd");
                        } else {
                            arr.add("d");
                        }
                    } else {
                        if (l == 2) {
                            arr.add("MM");
                        } else {
                            arr.add("M");
                        }
                        month = true;
                    }
                }
            }
        }
        return Joiner.on(split).join(arr.toArray());
    }

    /**
     * 得到最原始的日期，对于Java来说是从1970-01-01开始的
     */
    public static Date getOriginal() {
        return new Date(0);
    }

    /**
     * 得到一天的开始时间
     */
    public static Date getDayBegin(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 得到一天的结束时间
     */
    public static Date getDayEnd(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date max(Date... dates) {
        Date maxDate = null;
        for (Date d : dates) {
            if (maxDate == null) {
                maxDate = d;
                continue;
            }
            if (d != null) {
                if (d.compareTo(maxDate) > 0) {
                    maxDate = d;
                }
            }
        }
        return maxDate;
    }

    public static Date min(boolean nullIsMin, Date... dates) {
        Date minDate = null;
        boolean hasNull = false;
        for (Date d : dates) {
            if (minDate == null) {
                minDate = d;
                continue;
            }
            if (d != null) {
                if (d.compareTo(minDate) < 0) {
                    minDate = d;
                }
            } else {
                hasNull = true;
            }
        }
        if (nullIsMin) {
            return hasNull ? null : minDate;
        }
        return minDate;
    }

}
