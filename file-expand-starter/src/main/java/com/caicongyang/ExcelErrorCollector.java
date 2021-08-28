package com.caicongyang;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelErrorCollector {
    private List<ExcelCellError> excelErrors = new ArrayList<ExcelCellError>();

    private Workbook workbook;
    
    public ExcelErrorCollector(Workbook workbook) {
        this.workbook = workbook;
    }
    
    public Workbook getWorkbook() {
        return workbook;
    }

    public void collect(Cell cell, String header, DataValidateException e) {
        excelErrors.add(new ExcelCellError(cell, header, e.getMessage()));
    }
    
    public void collect(ExcelCellError error) {
    	excelErrors.add(error);
    }
    
    public boolean hasAnyError() {
        return ! excelErrors.isEmpty();
    }
    
    public List<ExcelCellError> getExcelErrors() {
        return excelErrors;
    }
    
    public ExcelRowErrorIterator getRowIterator() {
        return new ExcelRowErrorIterator(excelErrors);
    }

}
