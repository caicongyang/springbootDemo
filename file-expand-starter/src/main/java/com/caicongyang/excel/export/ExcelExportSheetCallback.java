package com.caicongyang.excel.export;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * Sheet回调
 * @author bo.wu
 *
 */
public interface ExcelExportSheetCallback {
    /**
     * 
     * @param sheet
     */
	void onSheet(Sheet sheet);
}
