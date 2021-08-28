package com.caicongyang.excel.export;

/**
 * 使用某种样式warp一个值，导出时应用在对应的cell上。
 * 
 * @param <T> 值类型
 * @author Zhang Xiaoye
 * @date 2017年11月22日 下午5:38:45
 */
public class Styled<T> {
	
	private final ExcelStyle style;
	
	private final T value;
	
	public Styled(ExcelStyle style, T value){
		this.style = style;
		this.value = value;
	}
	
	public static <T> Styled<T> of(ExcelStyle style, T value){
		return new Styled<T>(style, value);
	}

	public ExcelStyle getStyle() {
		return style;
	}

	public T getValue() {
		return value;
	}

}
