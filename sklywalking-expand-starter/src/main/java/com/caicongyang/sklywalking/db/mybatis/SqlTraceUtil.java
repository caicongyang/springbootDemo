package com.caicongyang.sklywalking.db.mybatis;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SqlTraceUtil {


    protected static final String DATE_FORMAT = "yyyy-MM-dd";
    protected static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    protected static final String TIME_FORMAT = "HH:mm:ss";


    protected static final String NULL_STRING = "null";


    private static Map<Type, Integer> type2JdbcType = new HashMap<>(16);

    static {

        /**
         * 默认不需要考虑的类型，
         * boolean  JdbcType.Boolean
         * 数字类型
         */
        type2JdbcType.put(String.class, Types.VARCHAR);

        type2JdbcType.put(java.sql.Date.class, Types.DATE);
        type2JdbcType.put(java.sql.Time.class, Types.TIME);
        type2JdbcType.put(java.sql.Timestamp.class, Types.TIMESTAMP);
        type2JdbcType.put(Date.class, Types.TIMESTAMP);


        type2JdbcType.put(byte[].class, Types.BLOB);
        type2JdbcType.put(Byte[].class, Types.BLOB);


    }


    public static String dealPlaceholder(String sql, List<String> parameter) {
        int startPos = 0;
        int findPos = 0;
        int count = 0;
        StringBuilder result = new StringBuilder();
        do {
            findPos = StringUtils.indexOf(sql, "?", startPos);
            if (findPos == -1 || parameter.size() <= count) {
                result.append(StringUtils.substring(sql, startPos, sql.length()));
                break;
            }
            result.append(StringUtils.substring(sql, startPos, findPos));
            result.append(parameter.get(count));
            startPos = findPos + 1;
            count++;
        } while (true);

        return result.toString();

    }

    public static Integer getJdbcType(Object param) {
        Integer jdbcType = type2JdbcType.get(param.getClass());
        if (jdbcType != null) {
            return jdbcType;
        } else {
            if (param instanceof InputStream) {
                return Types.BLOB;
            }
        }
        return jdbcType;
    }


    public static String dealJdbcType(Integer jdbcType, Object value, MyBatisSqlUtil.ParamResolveHelper helper) {
        if (value == null) {
            return NULL_STRING;
        }
        if (jdbcType == null) {
            return String.valueOf(value);
        }
        String result = "";
        switch (jdbcType) {
            case Types.DATE:
                result = helper.formatDate(value);
                break;
            case Types.TIME:
                result = helper.formatTime(value);
                break;
            case Types.TIMESTAMP:
                result = helper.formatDateTime(value);
                break;

            case Types.BLOB:
            case Types.CLOB:
            case Types.NCLOB:
                result = NULL_STRING;
                break;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                result = NULL_STRING;
                break;
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
                result = ParamResolveHelper.addQuote(String.valueOf(value));
                break;

            default:

                // 各种数字类型，JdbcType.BOOLEAN
                result = String.valueOf(value);
        }

        return result;


    }

    public static class ParamResolveHelper {
        private SimpleDateFormat dateFormat = null;
        private SimpleDateFormat timeFormat = null;
        private SimpleDateFormat dateTimeFormat = null;

        public String formatDate(Object value) {

            if (value instanceof Date) {

                return formatDate((Date) value);
            }

            return addQuote(String.valueOf(value));

        }


        public String formatDate(Date date) {

            if (dateFormat == null) {

                dateFormat = new SimpleDateFormat(DATE_FORMAT);
            }

            return addQuote(dateFormat.format(date));


        }

        public String formatTime(Object value) {

            if (value instanceof Date) {

                return formatTime((Date) value);
            }

            return addQuote(String.valueOf(value));

        }


        public String formatTime(Date date) {

            if (timeFormat == null) {

                timeFormat = new SimpleDateFormat(TIME_FORMAT);
            }

            return addQuote(timeFormat.format(date));

        }

        public String formatDateTime(Object value) {

            if (value instanceof Date) {

                return formatDateTime((Date) value);
            }

            return addQuote(String.valueOf(value));

        }


        public String formatDateTime(Date date) {

            if (dateTimeFormat == null) {

                dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            }

            return addQuote(dateTimeFormat.format(date));

        }


        public static String addQuote(String s) {

            return new StringBuilder("\"").append(s).append("\"").toString();
        }

        public static String addSignalQuote(String s) {

            return new StringBuilder("\'").append(s).append("\'").toString();
        }
    }

}
