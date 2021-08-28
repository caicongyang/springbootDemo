package com.caicongyang.excel.export;

import org.apache.poi.ss.usermodel.Workbook;

public class DataExportResult {
    private int total;
    private Workbook workbook;
    private ExcelExportConfig excelExportConfig;
    
    public DataExportResult(int total, Workbook workbook, ExcelExportConfig excelExportConfig) {
        this.total = total;
        this.workbook = workbook;
        this.excelExportConfig = excelExportConfig;
    }
    
    public int getTotal() {
        return total;
    }
    public Workbook getWorkbook() {
        return workbook;
    }
    public ExcelExportConfig getExcelExportConfig() {
        return excelExportConfig;
    }
    
}
