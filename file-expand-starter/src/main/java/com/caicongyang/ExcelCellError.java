package com.caicongyang;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelCellError {
    private Cell cell;
    private String header;
    private String error;
    
    public ExcelCellError() {}
    
    public ExcelCellError(Cell cell, String header, String error) {
        this.cell = cell;
        this.header = header;
        this.error = error;
    }
    
    public Cell getCell() {
        return cell;
    }
    public void setCell(Cell cell) {
        this.cell = cell;
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
