package com.caicongyang.excel.export;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Cell回调
 * @author bo.wu
 * 
 */
public interface ExcelExportCellCallback {
    /**
     * 
     * @param cell
     * @param cellValue
     * @param isHeaderRow 是否标题行
     * @return
     */
	Object onCell(Cell cell, Object cellValue, boolean isHeaderRow);
}
