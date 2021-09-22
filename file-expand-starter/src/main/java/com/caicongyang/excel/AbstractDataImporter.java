package com.caicongyang.excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


/**
 * 导入数据模板工具。
 */
public abstract class AbstractDataImporter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Integer MAX_IMPORT_SIZE = 2000;

    /**
     * 导入一行数据
     *
     * @param row key=列头名称,value=单元格的值
     */
    public abstract void importOneRow(Map<String, String> row);

    /**
     * 批量查询。产生的结果自行保存在实例变量里，供后续使用。
     */
    public abstract void batchQuery(List<Map<String, String>> rowList);

    /**
     * 校验数据。可由子类覆盖。
     */
    public void validate(Map<String, String> row) {
    }

    /**
     * 导入Excel文件
     *
     * @param headers 列头的名称定义。后续处理会用列头名获取单元格的值
     * @return 错误信息
     */
    public List<String> importExcelFile(MultipartFile file, List<String> headers) {
        List<String> errors = new ArrayList<>();

        List<Map<String, String>> rowList = parseExcelFile(file, headers);

        if (CollectionUtils.isEmpty(rowList)) {
            errors.add("文件中没有数据。");
            return errors;
        }
        setMaxImportSize(MAX_IMPORT_SIZE);
        if (rowList.size() > MAX_IMPORT_SIZE) {
            errors.add("导入不能超过" + MAX_IMPORT_SIZE + "条数据");
            return errors;
        }

        int rowCount = 0;
        for (Map<String, String> row : rowList) {
            try {
                this.validate(row);
            } catch (Exception e) {
                logger.error("校验异常", e);
                errors.add(String.format("第%s行：%s", rowCount + 2, e.getMessage()));
            }

            rowCount++;
        }

        if (CollectionUtils.isNotEmpty(errors)) {
            return errors;
        }

        this.batchQuery(rowList);

        rowCount = 0;
        for (Map<String, String> row : rowList) {
            try {
                importOneRow(row);
            } catch (Exception e) {
                logger.error("导入异常", e);
                errors.add(String.format("第%s行：%s", rowCount + 2, e.getMessage()));
            }

            rowCount++;
        }
        return errors;
    }

    /**
     * 解析excel文件
     *
     * @param headers 按列的顺序匹配列头，如headers为{"name","age"},返回值list.get(0).get("name")即取第一行第一列的值
     */
    public static List<Map<String, Object>> parseExcelFile(List<String> headers,
        MultipartFile file) {
        List result = new ArrayList<>();
        if (file != null) {
            Workbook workbook = getWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Map<String, Object> rowMap = new HashMap<>();
                Row row = sheet.getRow(j);
                if (checkCell(row)) {
                    continue;
                }
                for (int k = 0; k < headers.size(); k++) {
                    Cell cell = row.getCell(k);
                    Object value = null;
                    if (cell != null) {
                        if (!(cell.getCellTypeEnum() == CellType.NUMERIC && DateUtil
                            .isCellDateFormatted(cell))) {
                            // 不是日期就全转换成字符串类型
                            cell.setCellType(CellType.STRING);

                        }
                        value = getCellData(cell);
                        if (value != null && value instanceof String) {
                            value = ((String) value).trim();
                        }
                    }
                    rowMap.put(headers.get(k), value);
                }
                result.add(rowMap);
            }
        }
        return result;
    }

    /**
     * 解析excel文件
     *
     * @param headers 按列的顺序匹配列头，如headers为{"name","age"},返回值list.get(0).get("name")即取第一行第一列的值
     */
    public static List<Map<String, String>> parseExcelFile(MultipartFile file,
        List<String> headers) {
        List result = new ArrayList<>();
        if (file != null) {
            Workbook workbook = getWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Map<String, String> rowMap = new HashMap<>();
                Row row = sheet.getRow(j);
                if (checkCell(row)) {
                    continue;
                }
                for (int k = 0; k < headers.size(); k++) {
                    Cell cell = row.getCell(k);
                    String value = "";
                    if (cell != null) {
                        cell.setCellType(CellType.STRING); //1标示String强制设置为string
                        value = (String) getCellData(cell);
                    }
                    rowMap.put(headers.get(k), value == null ? "" : value.toString().trim());
                }
                result.add(rowMap);
            }
        }
        return result;
    }

    /**
     * 检查非空字段
     *
     * @param nameDescArr 二维数组:[[name,desc]], name为字段名，desc为字段描述
     */
    public void checkMandotoryFields(Map<String, String> row, String[][] nameDescArr) {
        checkMandotoryFields(row, Arrays.asList(nameDescArr));
    }

    /**
     * 检查非空字段
     *
     * @param nameDescArr 二维数组:[[name,desc]], name为字段名，desc为字段描述
     */
    public void checkMandotoryFields(Map<String, String> row, List<String[]> nameDescArr) {
        if (nameDescArr != null) {
            for (String[] arr : nameDescArr) {
                checkMandatoryField(row, arr[0], arr[1]);
            }
        }
    }

    /**
     * 检查非空字段
     *
     * @param name 字段名
     * @param desc 字段描述
     */
    public void checkMandatoryField(Map<String, String> row, String name, String desc) {
        String value = row.get(name);
        if (StringUtils.isEmpty(value)) {
            throw new RuntimeException(value + "必须填写");
        }
    }

    /**
     * 设置最大导入条数限制
     */
    public void setMaxImportSize(int maxImportSize) {
        MAX_IMPORT_SIZE = maxImportSize;

    }


    public static Workbook getWorkbook(MultipartFile file) {
        if (file == null) {

            throw new RuntimeException("请选择excel文件");
        }
        String suffix = (file.getOriginalFilename() == null ? ""
            : file.getOriginalFilename().split("\\.")[1]);
        Workbook workbook = null;
        if ("xls".equals(suffix)) {
            try {
                workbook = new HSSFWorkbook(file.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("请选择excel文件");
            }
        } else if ("xlsx".equals(suffix)) {
            try {
                workbook = new XSSFWorkbook(file.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("请选择excel文件");
            }
        } else {
            throw new RuntimeException("请选择excel文件");
        }
        return workbook;
    }


    public static Object getCellData(Cell cell) {
        return getCellData(cell, null);
    }

    public static Object getCellData(Cell cell, FormulaEvaluator formula) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                if (null != formula) {
                    return formula.evaluate(cell).getNumberValue();
                }
                return null;

            default:
                return null;
        }
    }

    public static boolean checkCell(Row row) {
        boolean flag = true;

        Cell cell = null;
        if (row == null) {
            return true;
        }
        for (int fi = row.getFirstCellNum(); fi <= row.getLastCellNum(); fi++) {
            cell = row.getCell(fi);
            Object content = getCellData(cell);
            if (!Objects.isNull(content)) {
                flag = false;
            }
        }
        return flag;
    }

}
