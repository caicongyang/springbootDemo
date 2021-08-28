package com.caicongyang.basic;

/**
 * 基础的日期与时间格式，支持正则的group（每个字段由正则匹配出<b>1</b>个group）
 * 
 * @author ZhangXiaoye
 */
class DFHelper{
	static final String P_YEAR = "(19[789]\\d|[2-9]\\d{3})";
	static final String P_MONTH = "(10|11|12|0?[1-9])";
	static final String P_MONTH_STRICT = "(10|11|12|0[1-9])";
//	static final String P_MONTH_ENG = "([Jj][Aa][Nn]|[Ff][Ee][Bb]|[Mm][Aa][Rr]|[Aa][Pp][Rr]|[Mm][Aa][Yy]|[Jj][Uu][Nn]|[Jj][Uu][Ll]|[Aa][Uu][Gg]|[Ss][Ee][Pp]|[Oo][Cc][Tt]|[Nn][Oo][Vv]|[Dd][Ee][Cc])";
	static final String P_DAY = "([12]\\d|30|31|0?[1-9])";
	static final String P_DAY_STRICT = "([12]\\d|30|31|0[1-9])";
	
	static final String P_HOUR = "(1\\d|2[0-3]|0?\\d)";
	static final String P_MINTUE = "([1-5]\\d|0?\\d)";
	static final String P_SECOND = "([1-5]\\d|0?\\d)";
	
	static final String P_DATE_COMPACT = P_YEAR + P_MONTH_STRICT + P_DAY_STRICT;
	static final String P_DATE_DASH = P_YEAR + "\\-" + P_MONTH + "\\-" + P_DAY;
	static final String P_DATE_SLASH = P_YEAR + "\\/" + P_MONTH + "\\/" + P_DAY;
	static final String P_DATE_CHN = P_YEAR + "\\s?年\\s?" + P_MONTH + "\\s?月\\s?" + P_DAY + "\\s?[日号]";
	
	static final String P_TIME_COMPACT = P_HOUR + P_MINTUE + P_SECOND;
	static final String P_TIME_COMPACT_NO_SECOND = P_HOUR + P_MINTUE;
	static final String P_TIME_COLON = P_HOUR + ":" + P_MINTUE + ":" + P_SECOND;
	static final String P_TIME_COLON_NO_SECOND = P_HOUR + ":" + P_MINTUE;
	static final String P_TIME_CHN = P_HOUR + "\\s?[时点]\\s?" + P_MINTUE + "\\s?分\\s?" + P_SECOND + "\\s?秒";
	static final String P_TIME_CHN_NO_SECOND = P_HOUR + "\\s?[时点]\\s?" + P_MINTUE + "\\s?分";
    
}