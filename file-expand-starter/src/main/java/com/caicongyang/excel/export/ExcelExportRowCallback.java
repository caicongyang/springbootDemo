package com.caicongyang.excel.export;

import org.apache.poi.ss.usermodel.Row;

/**
 * Row回调
 * @author bo.wu
 *
 */
public interface ExcelExportRowCallback {
	/**
	 * 
	 * @param row
	 * @param data
	 * @param isHeader
	 * @return skip row if return false
	 */
	<T> boolean onRow(Row row, T data, boolean isHeader);
}
