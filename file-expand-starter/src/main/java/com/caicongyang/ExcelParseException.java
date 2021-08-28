/**
 * 
 */
package com.caicongyang;

@SuppressWarnings("serial")
public class ExcelParseException extends RuntimeException {
	private int row;
	private int column;
	private String colName;

	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}

    public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public ExcelParseException(String message) {
        super(message);
    }

	public ExcelParseException(int row, int column, String message) {
		super(message);
		this.row = row;
		this.column = column;
	}

    public ExcelParseException(int row, int column, Throwable error) {
        super(error.getMessage(), error);
        this.row = row;
        this.column = column;
    }

    public ExcelParseException(int row, int column, String message, Throwable error) {
        super(message, error);
        this.row = row;
        this.column = column;
    }
	
	@Override
	public String toString(){
		return getMessage()+", row: " + (row+1) + ", col: " + (column+1) + ", colName:" + colName;
	}
	
}
