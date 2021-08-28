/**
 *
 */
package com.caicongyang.excel.export;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.Assert;

/**
 * @author bo.wu
 * @CreateDate Oct 28, 2015
 */
public class ExcelExporter {

    private Workbook workbook;
    private boolean streaming;

    public Workbook getWorkbook() {
        return workbook;
    }

    public ExcelExporter(Workbook workbook) {
        this.workbook = workbook;
        if (workbook instanceof SXSSFWorkbook) {
            streaming = true;
        }
        for (ExcelStyle style : ExcelStyle.values()) {
            style.hook(workbook);
        }
    }

    public <T> ExcelExporter setSheetDataByMap(List<Map<String, T>> list, ExcelExportConfig cfg)
        throws Exception {
        return setSheetData(list, cfg);
    }

    public <T> ExcelExporter setSheetData(List<T> list, ExcelExportConfig cfg) throws Exception {
        ExcelSheetWriter writer = getSheetWriter(cfg);

        writer.writeData(list);

        return this;
    }

    public ExcelSheetWriter getSheetWriter(ExcelExportConfig cfg) throws Exception {
        Sheet sheet = createSheet(workbook, cfg);

        if (cfg.sheetCallback != null) {
            cfg.sheetCallback.onSheet(sheet);
        }

        return new ExcelSheetWriter(this, sheet, cfg);
    }

    /**
     * 只有调用此方法，将会将Excel数据写到输出流中
     */
    public void export(OutputStream os) throws Exception {
        Assert.notNull(os, "Parameter os cannot be null");

        workbook.write(os);

        if (streaming) {
            ((SXSSFWorkbook) workbook).dispose();
        }
    }

    protected Sheet createSheet(Workbook workbook, ExcelExportConfig cfg) {
        Sheet sheet = null;
        if (cfg.sheetName != null) {
            sheet = workbook.getSheet(cfg.sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(cfg.sheetName);
            }
        } else {
            int sheetNum = workbook.getNumberOfSheets();
            if (sheetNum > cfg.sheetIndex) {
                sheet = workbook.getSheetAt(cfg.sheetIndex);
            }
        }

        if (sheet == null) {
            sheet = workbook.createSheet();
        }
        if (sheet instanceof SXSSFSheet && cfg.sheetRandomAccessWindowSize > 0) {
            ((SXSSFSheet) sheet).setRandomAccessWindowSize(cfg.sheetRandomAccessWindowSize);
        }
        return sheet;
    }

    protected Row createRow(Sheet sheet, int rowIdx) {
        Row row = sheet.getRow(rowIdx);
        if (row != null) {
            return row;
        }
        return sheet.createRow(rowIdx);
    }

}
