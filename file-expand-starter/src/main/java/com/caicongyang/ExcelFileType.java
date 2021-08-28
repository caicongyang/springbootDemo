package com.caicongyang;

import org.springframework.util.StringUtils;

public enum ExcelFileType {
	XLS,
	XLSX;
	
    public static ExcelFileType getFileType(String filename) {
    	String extension = StringUtils.getFilenameExtension(filename).toLowerCase();
        if (extension.equals("xls"))
            return ExcelFileType.XLS;
        if (extension.equals("xlsx"))
            return ExcelFileType.XLSX;
        throw new RuntimeException("Not supported file type : "+extension);
    }
}
