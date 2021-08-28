/**
 * 
 */
package com.caicongyang;


import com.caicongyang.excel.export.ExcelExporter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author bo.wu
 * @CreateDate Oct 28, 2015
 */
public class Excels {
    
    public static ExcelExporter newExcelExporter(ExcelFileType type) throws Exception {
    	return new ExcelExporter(newWorkbook(type));
    }
    
    public static ExcelExporter newExcelExporter(InputStream tmpl, ExcelFileType type) throws Exception {
    	return new ExcelExporter(newWorkbook(tmpl, type));
    }
    /**
     * 大数据量导出，采用POI的streaming方式，只支持xlsx格式
     * @date 2017年4月21日
     * @param rowsInMemory 内存中的行数，超出则flush到临时文件
     * @return
     */
    public static ExcelExporter newExcelExporterForBigData(int rowsInMemory) {
    	return new ExcelExporter(new SXSSFWorkbook(rowsInMemory));
    }
    /**
     * 大数据量导出，采用POI的streaming方式，只支持xlsx格式
     * @date 2017年4月21日
     * @param Excel模板，必须为xlsx格式
     * @param rowsInMemory 内存中的行数，超出则flush到临时文件
     * @return
     */
    public static ExcelExporter newExcelExporterForBigData(InputStream tmpl, int rowsInMemory) throws Exception {
    	return new ExcelExporter(new SXSSFWorkbook(new XSSFWorkbook(tmpl), rowsInMemory));
    }
    
	public static ExcelParser newExcelParser(File file) throws Exception {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return newExcelParser(is, ExcelFileType.getFileType(file.getName()));
		} finally {
			Closer.close(is);
		}
	}
	
    public static ExcelParser newExcelParser(InputStream is, ExcelFileType type) throws Exception {
    	return new ExcelParser(newWorkbook(is, type));
    }
    
    public static Workbook newWorkbook(InputStream is, ExcelFileType type) throws Exception {
    	if (type == ExcelFileType.XLS) {
            return new HSSFWorkbook(is);
        } else if (type == ExcelFileType.XLSX) {
            return new XSSFWorkbook(is);
        }
    	
        throw new RuntimeException("Not supported excel file type");
    }
    
    public static Workbook newWorkbook(ExcelFileType type) throws Exception {
        if (type == ExcelFileType.XLS) {
            return new HSSFWorkbook();
        } else if (type == ExcelFileType.XLSX) {
            return new XSSFWorkbook();
        }

        throw new RuntimeException("Not supported excel file type");
    }
    
}
