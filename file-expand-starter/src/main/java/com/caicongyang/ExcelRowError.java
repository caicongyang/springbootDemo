package com.caicongyang;

import org.apache.poi.ss.usermodel.Row;

public class ExcelRowError {
    private Row row;
    private String[] errors;

    public ExcelRowError() {}
    
    public ExcelRowError(Row row, String[] errors) {
        this.row = row;
        this.errors = errors;
    }
    
    public Row getRow() {
        return row;
    }
    public void setRow(Row row) {
        this.row = row;
    }
    public String[] getErrors() {
        return errors;
    }
    public void setError(String[] errors) {
        this.errors = errors;
    }
}
