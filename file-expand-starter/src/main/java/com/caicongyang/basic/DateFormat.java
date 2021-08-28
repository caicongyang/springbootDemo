package com.caicongyang.basic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常见的{@link Date}格式。可以用static方法{@link DateFormat#match(String)}来匹配格式<br/>
 * 对于一种{@link DateFormat}，常用的方法是:<br/>
 * {@link DateFormat#canParse(String)}<br/>
 * {@link DateFormat#parse(String)}<br/>
 * {@link DateFormat#format(Date)}<br/>
 * 
 * @author ZhangXiaoye
 *
 */
public enum DateFormat {
	
	// 顺序按照从长到短
	
	/** yyyy年MM月dd日 HH点mm分ss秒 */
	DATE_TIME_CHN(DFHelper.P_DATE_CHN + "\\s?" + DFHelper.P_TIME_CHN, "yyyy年MM月dd日 HH点mm分ss秒"),
	/** yyyy-MM-dd HH:mm:ss */
	DATE_DASH_TIME_COLON(DFHelper.P_DATE_DASH + "\\s?" + DFHelper.P_TIME_COLON, "yyyy-MM-dd HH:mm:ss"),
	/** yyyy/MM/dd HH:mm:ss */
	DATE_SLASH_TIME_COLON(DFHelper.P_DATE_SLASH + "\\s?" + DFHelper.P_TIME_COLON, "yyyy/MM/dd HH:mm:ss"),
	/** yyyyMMddHHmmss */
	DATE_TIME_COMPACT(DFHelper.P_DATE_COMPACT + "\\s?" + DFHelper.P_TIME_COMPACT, "yyyyMMddHHmmss"),
	
	/** yyyy-MM-dd HH:mm:ss 同DATE_DASH_TIME_COLON*/
	DEFAULT(DATE_DASH_TIME_COLON.getPattern(), DATE_DASH_TIME_COLON.getFormat()),
	
	/** yyyy年MM月dd日 HH点mm分 */
	DATE_TIME_CHN_NO_SECOND(DFHelper.P_DATE_CHN + "\\s?" + DFHelper.P_TIME_CHN_NO_SECOND, "yyyy年MM月dd日 HH点mm分"),
	/** yyyy-MM-dd HH:mm */
	DATE_DASH_TIME_COLON_NO_SECOND(DFHelper.P_DATE_DASH + "\\s?" + DFHelper.P_TIME_COLON_NO_SECOND, "yyyy-MM-dd HH:mm"),
	/** yyyy/MM/dd HH:mm */
	DATE_SLASH_TIME_COLON_NO_SECOND(DFHelper.P_DATE_SLASH + "\\s?" + DFHelper.P_TIME_COLON_NO_SECOND, "yyyy/MM/dd HH:mm"),
	/** yyyyMMddHHmm */
	DATE_TIME_COMPACT_NO_SECOND(DFHelper.P_DATE_COMPACT + DFHelper.P_TIME_COMPACT_NO_SECOND, "yyyyMMddHHmm"),
	
	/** yyyy年MM月dd日 */
	DATE_CHN(DFHelper.P_DATE_CHN, "yyyy年MM月dd日", true, false),
	/** yyyy-MM-dd */
	DATE_DASH(DFHelper.P_DATE_DASH, "yyyy-MM-dd", true, false),
	/** yyyy/MM/dd */
	DATE_SLASH(DFHelper.P_DATE_SLASH, "yyyy/MM/dd", true, false),
	/** yyyyMMdd */
	DATE_COMPACT(DFHelper.P_DATE_COMPACT, "yyyyMMdd", true, false),
	
	/** HHmmss */
	TIME_COMPACT(DFHelper.P_TIME_COMPACT, "HHmmss", false, true),
	/** HH:mm:ss */
	TIME_COLON(DFHelper.P_TIME_COLON, "HH:mm:ss", false, true),
	/** HH时mm分ss秒 */
	TIME_CHN(DFHelper.P_TIME_CHN, "HH时mm分ss秒", false, true),
	/** HH时mm分 */
	TIME_CHN_NO_SECOND(DFHelper.P_TIME_CHN_NO_SECOND, "HH时mm分", false, true),
	/** HH:mm */
	TIME_COLON_NO_SECOND(DFHelper.P_TIME_COLON_NO_SECOND, "HH:mm", false, true),
	/** HHmm */
	TIME_COMPACT_NO_SECOND(DFHelper.P_TIME_COMPACT_NO_SECOND, "HHmm", false, true),
	
	;
	
	/**
	 * @param dateString 包含日期的字符串
	 * @return 尝试以能够匹配的格式转换，如果没有可以匹配的返回null。
	 */
	public static Date tryParse(String dateString){
		DateFormat format = match(dateString);
		if(format == null){
			return null;
		}
		return format.parse(dateString);
	}
	
	/**
	 * @param dateString 包含日期的字符串
	 * @return 能够匹配的格式，如果没有可以匹配的返回null。
	 */
	public static DateFormat match(String dateString){
		for(DateFormat format: DateFormat.values()){
			if(format.canParse(dateString)){
				return format;
			}
		}
		return null;
	}
	
	private final String pattern;
	
	private final Pattern regexPattern;
	
	private final String format;
	
	private final boolean hasDate;
	
	private final boolean hasTime;
	
	private final Map<Integer, Integer> groupCalendarPartMap;
	
	private final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>();
	
	private DateFormat(String pattern, String format){
		this(pattern, format, true, true);
	}
	
	private DateFormat(String pattern, String format, boolean hasDate, boolean hasTime){
		this.pattern = pattern;
		this.regexPattern = Pattern.compile(pattern);
		this.format = format;
		this.hasDate = hasDate;
		this.hasTime = hasTime;
		// 根据format计算：groupId到Calendar字段的对应关系
		Pattern partPattern = Pattern.compile("(yyyy|MM|dd|HH|mm|ss)");
		Matcher partMatcher = partPattern.matcher(format);
		Map<Integer, Integer> partMap = new TreeMap<Integer, Integer>();
		int groupId = 1;
		while(partMatcher.find()){
			if("yyyy".equals(partMatcher.group())){
				partMap.put(groupId ++, Calendar.YEAR);
			}else if("MM".equals(partMatcher.group())){
				partMap.put(groupId ++, Calendar.MONTH);
			}else if("dd".equals(partMatcher.group())){
				partMap.put(groupId ++, Calendar.DAY_OF_MONTH);
			}else if("HH".equals(partMatcher.group())){
				partMap.put(groupId ++, Calendar.HOUR_OF_DAY);
			}else if("mm".equals(partMatcher.group())){
				partMap.put(groupId ++, Calendar.MINUTE);
			}else if("ss".equals(partMatcher.group())){
				partMap.put(groupId ++, Calendar.SECOND);
			}
		}
		this.groupCalendarPartMap = partMap;
	}
	
	/**
	 * @return 格式正则
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @return 编译后的格式正则，{@link #canParse(String)}以及{@link #parse(String)}中使用
	 */
	public Pattern getRegexPattern() {
		return regexPattern;
	}

	/**
	 * @return 格式定义，{@link #format(Date)}中使用
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * @return 格式中是否包含日期部分
	 */
	public boolean isHasDate() {
		return hasDate;
	}

	/**
	 * @return 格式中是否包含时间部分
	 */
	public boolean isHasTime() {
		return hasTime;
	}

	private SimpleDateFormat getFormatter(){
		SimpleDateFormat formatter = this.formatter.get();
		if(formatter == null){
			formatter = new SimpleDateFormat(getFormat());
			this.formatter.set(formatter);
		}
		return formatter;
	}
	
	/**
	 * @param dateString 包含日期的字符串
	 * @return 是否能将dateString转为java.util.Date格式
	 */
	public boolean canParse(String dateString){
		return regexPattern.matcher(dateString).matches();
	}
	
	/**
	 * @param dateString 包含日期的字符串
	 * @return 如果不能转换返回null
	 */
	public Date parse(String dateString){
		Matcher m = regexPattern.matcher(dateString);
		if(m.matches()){
			Calendar cal = Calendar.getInstance();
			cal.clear();
			for(int g = 1; g <= m.groupCount(); g ++){
				Integer calIndex = groupCalendarPartMap.get(g);
				if(calIndex.equals(Calendar.MONTH)){
					cal.set(calIndex, Integer.parseInt(m.group(g)) - 1);
				}else{
					cal.set(calIndex, Integer.parseInt(m.group(g)));
				}
			}
			return cal.getTime();
		}
		return null;
	}
	
	/**
	 * @param date
	 * @return 该格式对应的日期字符串
	 */
	public String format(Date date){
		return getFormatter().format(date);
	}
	
	public String format(long timestamp){
		return getFormatter().format(new Date(timestamp));
	}

}

