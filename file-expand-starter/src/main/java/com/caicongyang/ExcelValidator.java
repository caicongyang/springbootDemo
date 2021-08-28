package com.caicongyang;

import org.apache.poi.ss.usermodel.Cell;

public interface ExcelValidator {

    void validate(Cell cell, String header, String title, Class<?> cellType, Object cellValue) throws DataValidateException;
}
