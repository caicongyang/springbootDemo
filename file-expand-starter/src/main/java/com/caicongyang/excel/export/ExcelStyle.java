package com.caicongyang.excel.export;

import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;


/**
 * 几种常用的样式
 * 
 * @author Zhang Xiaoye
 * @date 2017年11月22日 下午5:39:34
 */
public enum ExcelStyle {
	
	/** 浅蓝背景 */
	SKY_BLUE(HSSFColorPredefined.SKY_BLUE, HSSFColorPredefined.OLIVE_GREEN),
	
	/** 深蓝背景 */
	ROYAL_BLUE(HSSFColorPredefined.ROYAL_BLUE, HSSFColorPredefined.LIGHT_GREEN),
	
	/** 浅绿背景 */
	LIGHT_GREEN(HSSFColorPredefined.LIGHT_GREEN, HSSFColorPredefined.DARK_TEAL),
	
	/** 深绿背景 */
	LIME(HSSFColorPredefined.LIME, HSSFColorPredefined.LIGHT_TURQUOISE),
	
	/** 浅红背景 */
	CORAL(HSSFColorPredefined.CORAL, HSSFColorPredefined.INDIGO),
	
	/** 橘红背景 */
	ORANGE(HSSFColorPredefined.ORANGE, HSSFColorPredefined.PALE_BLUE)
	
	;
	
	public final HSSFColorPredefined bg;
	
	public final HSSFColorPredefined fg;
	
	public final ThreadLocal<CellStyle> cs = new ThreadLocal<>();
	
	private ExcelStyle(HSSFColorPredefined bgColor, HSSFColorPredefined fgColor){
		this.bg = bgColor;
		this.fg = fgColor;
	}
	
	public void hook(Workbook wb){
		CellStyle cs = wb.createCellStyle();
		cs.setFillForegroundColor(bg.getIndex());
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		Font f = wb.createFont();
		f.setColor(fg.getIndex());
		cs.setFont(f);
		this.cs.set(cs);
	}
	
}
