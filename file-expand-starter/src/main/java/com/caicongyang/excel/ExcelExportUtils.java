
package com.caicongyang.excel;

import com.caicongyang.utils.TomDateUtils;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ExcelExportUtils {


    private final static Logger LOGGER = LoggerFactory.getLogger(ExcelExportUtils.class);


    /**
     * 获得导出的Excel对象
     *
     * @param : headTitle 列表头 | outputList 查询出的List
     * @return : SXSSFWorkbook
     * @Description :
     */
    public static <T> SXSSFWorkbook getWorkbook(String[] headTitleArr, List<T> outputList,
        List<String> topInfoList, SXSSFWorkbook workbook, String sheetName) throws Exception {
        if (workbook == null) {
            workbook = new SXSSFWorkbook();
        }
        if (headTitleArr == null || headTitleArr.length == 0) {
            return workbook;
        }
        Sheet sheet = null;
        if (sheetName == null) {
            sheet = workbook.createSheet();
        } else {
            sheetName = sheetName.replaceAll("/", "").replaceAll("\\\\", "").replaceAll("\\?", "").
                replaceAll("\\*", "").replaceAll("\\[", "").replaceAll("\\]", "");
            sheet = workbook.createSheet(sheetName);
        }
        int rowNum = 0;
        if (CollectionUtils.isNotEmpty(topInfoList)) {
            for (String str : topInfoList) {
                Row row = sheet.createRow(rowNum);// 创建一行
                Cell cell = row.createCell(0);
                cell.setCellValue(str);
                rowNum++;
            }
        }
        List<ExcelVo> heads = new ArrayList<>();
        for (int i = 0; i < headTitleArr.length; i++) {//根据顺序
            heads.add(new ExcelVo(headTitleArr[i], CellType.STRING, ""));
        }
        generateNoHeaderStyleExcelHeader(sheet, heads, workbook, rowNum++);
        //解决poi自动将0.00转换成0的问题
        DataFormat df = workbook.createDataFormat();  //此处设置数据格式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(df.getFormat(
            "#,#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));
        if (CollectionUtils.isNotEmpty(outputList)) {
            for (Object obj : outputList) {
                Row row = sheet.createRow(rowNum++);// 创建一行
                generRowByStrArr(row, heads, obj, cellStyle);
            }
        }
        return workbook;
    }


    /**
     * 获得导出的Excel对象
     *
     * @param : headMap 列表头 | outputList 查询出的List
     * @return : SXSSFWorkbook
     * @Description :
     */
    public static <T> SXSSFWorkbook getWorkbook(Map<String, String> headMap, List<T> outputList)
        throws Exception {
        return ExcelExportUtils.getWorkbook(headMap, outputList, null, null, null, null, null);
    }

    /**
     * 获得导出的Excel对象
     *
     * @param : headMap 列表头 | outputList 查询出的List
     * @return : SXSSFWorkbook
     * @Description :
     */
    public static <T> SXSSFWorkbook getWorkbook(Map<String, String> headMap, List<T> outputList,
        ExportRowHandle rowHandle) throws Exception {
        return ExcelExportUtils.getWorkbook(headMap, outputList, null, null, null, rowHandle, null);
    }

    /**
     * 获得导出的Excel对象
     *
     * @param headMap headMap 列表头
     * @param outputList 查询出的List
     */
    public static <T> SXSSFWorkbook getWorkbook(Map<String, String> headMap, List<T> outputList,
        List<String> topInfoList, SXSSFWorkbook workbook, String sheetName,
        ExportRowHandle rowHandle, String format) throws Exception {
        if (workbook == null) {
            workbook = new SXSSFWorkbook();
        }
        if (headMap == null || headMap.isEmpty()) {
            return workbook;
        }
        Sheet sheet = null;
        if (sheetName == null) {
            sheet = workbook.createSheet();
        } else {
            sheetName = sheetName.replaceAll("/", "").replaceAll("\\\\", "").replaceAll("\\?", "").
                replaceAll("\\*", "").replaceAll("\\[", "").replaceAll("\\]", "");
            sheet = workbook.createSheet(sheetName);
        }
        int rowNum = 0;
        if (CollectionUtils.isNotEmpty(topInfoList)) {
            for (String str : topInfoList) {
                Row row = sheet.createRow(rowNum);// 创建一行
                Cell cell = row.createCell(0);
                cell.setCellValue(str);
                rowNum++;
            }
        }

        List<ExcelVo> heads = new ArrayList<>();
        for (String key : headMap.keySet()) {
            heads.add(new ExcelVo(String.valueOf(headMap.get(key)), CellType.STRING, key));
        }

        generateExcelHeader(sheet, heads, workbook, rowNum++);
        //解决poi自动将0.00转换成0的问题
        DataFormat df = workbook.createDataFormat();  //此处设置数据格式
        CellStyle cellStyle = workbook.createCellStyle();
        if (StringUtils.isNotBlank(format)) {
            cellStyle.setDataFormat(df.getFormat(format));
        } else {
            cellStyle.setDataFormat(df.getFormat(
                "#,#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));
        }
        if (CollectionUtils.isNotEmpty(outputList)) {
            for (Object obj : outputList) {
                Row row = sheet.createRow(rowNum++);// 创建一行
                generRow(row, heads, obj, cellStyle, rowHandle);
            }
        }
        return workbook;
    }


    /**
     * 有追加功能的sheet
     */
    public static <T> SXSSFWorkbook fillData2WorkSheet(SXSSFWorkbook workbook,
        Map<String, String> headMap,
        List<T> outputList, String sheetName) throws Exception {
        boolean isNewSheet = false;
        if (workbook == null) {
            workbook = new SXSSFWorkbook();
        }
        if (headMap == null || headMap.isEmpty()) {
            return workbook;
        }
        //创建sheet
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            isNewSheet = true;
            if (sheetName == null) {
                sheet = workbook.createSheet();
            } else {
                sheetName = sheetName.replaceAll("/", "").replaceAll("\\\\", "")
                    .replaceAll("\\?", "").replaceAll("\\*", "")
                    .replaceAll("\\[", "").replaceAll("\\]", "");
                sheet = workbook.createSheet(sheetName);
            }
        }
        //记录当前sheet中正在写入的行号 在sheet末尾追加
        int currCursor = (sheet.getLastRowNum() > 0) ? (sheet.getLastRowNum() + 1) : 0;
        List<ExcelVo> heads = new ArrayList<>();
        for (int i = 0; i < headMap.size(); i++) { // 根据顺序添加
            Iterator<String> it = headMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toString();
                String index = key.substring(0, key.indexOf("|"));
                if (index.equals(String.valueOf(i))) {
                    heads.add(new ExcelVo(String.valueOf(headMap.get(key)), CellType.STRING,
                        key.substring(key.indexOf("|") + 1)));
                    break;
                }
            }
        }
        //如果是首次新建sheet 则添加头Header
        if (isNewSheet) {
            generateExcelHeader(sheet, heads, workbook, currCursor++);
        }
        // 解决poi自动将0.00转换成0的问题
        DataFormat df = workbook.createDataFormat(); // 此处设置数据格式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(df.getFormat(
            "#,#0.00")); // 小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));
        if (CollectionUtils.isNotEmpty(outputList)) {
            for (Object obj : outputList) {
                Row row = sheet.createRow(currCursor++);// 创建一行
                generRow(row, heads, obj, cellStyle, null);

            }
        }
        return workbook;
    }

    public static class ExcelVo {

        public ExcelVo(String rowNameCn, CellType rowType, String rowNameEg) {
            this.rowNameCn = rowNameCn;
            this.rowNameEg = rowNameEg;
            this.rowType = rowType;
        }

        /**
         * 字段中文描述
         */
        private String rowNameCn;
        /**
         * 字段类型
         */
        private CellType rowType;
        /**
         * 字段名
         */
        private String rowNameEg;
        /**
         * 列宽
         */
        private int cellWith = 25 * 256;

        public String getRowNameCn() {
            return rowNameCn;
        }

        public void setRowNameCn(String rowNameCn) {
            this.rowNameCn = rowNameCn;
        }

        public CellType getRowType() {
            return rowType;
        }

        public void setRowType(CellType rowType) {
            this.rowType = rowType;
        }

        public String getRowNameEg() {
            return rowNameEg;
        }

        public void setRowNameEg(String rowNameEg) {
            this.rowNameEg = rowNameEg;
        }

        public int getCellWith() {
            return cellWith;
        }

        public void setCellWith(int cellWith) {
            this.cellWith = cellWith;
        }
    }

    private static void generRowByStrArr(Row row, List<ExcelVo> header, Object obj,
        CellStyle cellStyle) throws Exception {
        String[] values = null;
        if (obj.getClass().isArray() && obj instanceof String[]) {
            values = (String[]) obj;
        }
        if (values == null) {
            return;
        }
        for (int j = 0; j < header.size(); j++) {
            if (values.length <= j) {
                continue;
            }
            Cell cell = row.createCell(j);// 创建一列
            cell.setCellType(header.get(j).getRowType());
            Object value = values[j];
            if (null == value) {
                value = "";
            }
            header.get(j).getRowType();
            if (value instanceof String) {
                cell.setCellValue(String.valueOf(value));
            } else if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Double) {
                cell.setCellStyle(cellStyle);
                cell.setCellValue((Double) value);
            } else if (value instanceof Long) {
                cell.setCellValue(((Long) value).toString());
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else if (value instanceof BigDecimal) {
                cell.setCellStyle(cellStyle);
                cell.setCellValue(transMoneyForExport((BigDecimal) value));
            } else if (value instanceof Date) {
                cell.setCellValue(TomDateUtils.getDayTimePattern((Date) value));
            }
        }
    }

    private static void generRow(Row row, List<ExcelVo> header, Object obj, CellStyle cellStyle,
        ExportRowHandle rowHandle) throws Exception {
        for (int j = 0; j < header.size(); j++) {
            CellType cellType = header.get(j).getRowType();
            Cell cell = row.createCell(j);// 创建一列
            cell.setCellType(cellType);
            String field = header.get(j).getRowNameEg();
            Method method = obj.getClass()
                .getMethod("get" + toUpperCaseFirstOne(field));
            method.setAccessible(true);
            Object value = method.invoke(obj);
            if (null == value) {
                value = "";
            }
            header.get(j).getRowType();
            if (rowHandle != null && rowHandle.rowDataHandle(field, value, cell)) {
            } else if (value instanceof String) {
                cell.setCellValue(String.valueOf(value));
            } else if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Double) {
                cell.setCellStyle(cellStyle);
                cell.setCellValue((Double) value);
            } else if (value instanceof Long) {
                cell.setCellValue(String.valueOf((Long) value));
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else if (value instanceof BigDecimal) {
                cell.setCellStyle(cellStyle);
                cell.setCellValue(transMoneyForExport((BigDecimal) value));
            } else if (value instanceof Date) {
                cell.setCellValue(TomDateUtils.getDayTimePattern((Date) value));
            }
        }
    }

    private static void generateNoHeaderStyleExcelHeader(Sheet sheet, List<ExcelVo> header,
        SXSSFWorkbook workbook, int rowNum) {
        CellStyle headerStyle = workbook.createCellStyle();// 单元格样式
        Font font = workbook.createFont();// 字体样式
        font.setFontHeightInPoints((short) 12);
        headerStyle.setFont(font);
        if (header != null && header.size() > 0) {// 生成头行
            Row row = sheet.createRow(rowNum);// 创建一行
            for (int i = 0; i < header.size(); i++) {
                Cell cell = row.createCell(i);// 创建一列
                cell.setCellType(header.get(i).getRowType());
                cell.setCellValue(header.get(i).getRowNameCn());
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, header.get(i).getCellWith());
            }
        }
    }

    private static void generateExcelHeader(Sheet sheet, List<ExcelVo> header,
        SXSSFWorkbook workbook, int rowNum) {
        CellStyle headerStyle = workbook.createCellStyle();// 单元格样式
        Font font = workbook.createFont();// 字体样式
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        headerStyle.setFont(font);
        if (header != null && header.size() > 0) {// 生成头行
            Row row = sheet.createRow(rowNum);// 创建一行
            for (int i = 0; i < header.size(); i++) {
                Cell cell = row.createCell(i);// 创建一列
                cell.setCellType(header.get(i).getRowType());
                cell.setCellValue(header.get(i).getRowNameCn());
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, header.get(i).getCellWith());
            }
        }
    }


    /**
     * 设置合并后单元格边框
     */
    private static void setRegionBorder(short borderStyle, CellRangeAddress address, Sheet sheet,
        SXSSFWorkbook workbook) {
        RegionUtil.setBorderBottom(BorderStyle.THIN, address, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, address, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, address, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, address, sheet);
    }


    /**
     * 导出使用：把BigDecimal的金额转换为保留2位小数的double
     */
    public static Double transMoneyForExport(BigDecimal money) {
        if (money == null) {
            return 0.00;
        }
        DecimalFormat df = new DecimalFormat("###########0.00");
        return Double.valueOf(df.format(money));
    }


    /**
     * 首字母转大写
     */
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0)))
                .append(s.substring(1)).toString();
        }
    }
}
