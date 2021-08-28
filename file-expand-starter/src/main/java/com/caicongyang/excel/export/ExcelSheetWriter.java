package com.caicongyang.excel.export;

import com.caicongyang.utils.DateUtils;
import com.caicongyang.utils.ReflectUtils;
import com.caicongyang.basic.ValueUtils;
import com.google.common.collect.Maps;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import org.apache.commons.lang3.ClassUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.util.StringUtils;

/**
 * @author WuBo
 * @date 2017年4月24日
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExcelSheetWriter {

    private ExcelExporter exporter;
    private ExcelExportConfig cfg;
    private Sheet sheet;
    private Map<String, Integer> colNameMap;
    private String[] fieldNameArray;
    private CellType[] cellTypes;
    private int rowIndex;
    private int dataRows;
    private boolean dataTypeIsMap;
    private Map<String, CellStyle> styleCache = Maps.newHashMap();

    private boolean inited;

    public ExcelSheetWriter(ExcelExporter exporter, Sheet sheet, ExcelExportConfig cfg) {
        super();
        this.exporter = exporter;
        this.sheet = sheet;
        this.cfg = cfg;
    }

    public <T> void writeDataByMap(List<Map<String, T>> list) throws Exception {
        writeData(list);
    }

    public <T> void writeData(List<T> list) throws Exception {
        if (list != null && list.size() > 0) {
            if (!inited) {
                initSheetWriter(list.get(0));
            }
            int maxDataRows = cfg.getMaxDataRows() > 0 ? cfg.getMaxDataRows() : Integer.MAX_VALUE;
            if (dataRows > maxDataRows) {
                return;
            }
            for (T t : list) {
                createRow(sheet, t, dataTypeIsMap, colNameMap, fieldNameArray, cellTypes,
                    rowIndex++, cfg);
                dataRows++;
                if (dataRows > maxDataRows) {
                    return;
                }
            }
        } else {
            if (!inited) {
                initSheetWriter(null);
            }
        }
    }

    private <T> void initSheetWriter(T t) throws Exception {
        if (t != null) {
            if (cfg.autoMapColName) {
                Class<?> clazz = t.getClass();
                boolean dataTypeIsMap = Map.class.isAssignableFrom(clazz);
                autoMapColNames(cfg, t, dataTypeIsMap);
            }
        }
        // field --> columnIdx
        Map<String, Integer> colNameMap = getSortedColNames(cfg);

        if (cfg.autoSizeColumn || cfg.autoBreak) {
            for (String fieldName : colNameMap.keySet()) {
                int column = colNameMap.get(fieldName);
                Integer width = cfg.getColWidthMap().get(fieldName);
                if (width != null) {
                    sheet.setColumnWidth(column, width.intValue() * 256);
                } else if (cfg.autoSizeColumn) {
                    sheet.autoSizeColumn(column);
                }
                if (cfg.autoBreak) {
                    sheet.setColumnBreak(column);
                }
            }
        }

        if (t != null) {
            Class<?> clazz = t.getClass();
            boolean dataTypeIsMap = Map.class.isAssignableFrom(clazz);

            CellType[] cellTypes = getCellTypes(t, dataTypeIsMap, colNameMap, cfg);

            String[] fieldNameArray = colNameMap.keySet().toArray(new String[colNameMap.size()]);

            this.fieldNameArray = fieldNameArray;
            this.colNameMap = colNameMap;
            this.cellTypes = cellTypes;
            this.dataTypeIsMap = dataTypeIsMap;
        }

        if (cfg.writeHeader) {
            writeHeader(sheet, colNameMap, cfg.getTitleRow(), cfg);
        }

        this.rowIndex = cfg.getDataStartRow();
        this.inited = true;
    }

    private Map<String, Integer> getSortedColNames(ExcelExportConfig cfg) {
        Map<String, Integer> colNames = Maps.newHashMap();
        if (cfg.autoMapTemplateTitle) {
            Map<String, Integer> titleMap = parseTitleRow(sheet.getRow(cfg.getTitleRow()), cfg);
            int lastColIndex = Collections.max(titleMap.values());
            for (String field : cfg.colNameMap.keySet()) {
                String title = cfg.getColNameMap().get(field);
                Integer colIdx = titleMap.get(title);
                if (colIdx == null) {
                    colIdx = ++lastColIndex;
                }
                colNames.put(field, colIdx);
                cfg.mapColIndex(field, colIdx);
            }
        } else {
            int colIdx = 0;
            if (!cfg.colIndexMap.isEmpty()) {
                colNames.putAll(cfg.colIndexMap);
                colIdx = Collections.max(cfg.colIndexMap.values());
            }
            for (String key : cfg.colNameMap.keySet()) {
                if (!colNames.containsKey(key)) {
                    colNames.put(key, colIdx++);
                }
            }
        }
        // sort
        sortColNames(colNames, cfg);
        return colNames;
    }

    private void sortColNames(Map<String, Integer> colNames, ExcelExportConfig cfg) {
        Map<String, Integer> copied = new HashMap<String, Integer>(colNames);
        Map<Integer, String> colIdxMap = new HashMap<Integer, String>();
        for (Entry<String, Integer> entry : colNames.entrySet()) {
            colIdxMap.put(entry.getValue(), entry.getKey());
        }
        // sort
        for (String colName : copied.keySet()) {
            if (cfg.colIndexMap.containsKey(colName)) {
                int colIdx = cfg.colIndexMap.get(colName);
                int curIdx = colNames.get(colName);
                if (curIdx != colIdx) {
                    String replaced = colIdxMap.get(colIdx);
                    if (replaced != null) {
                        colNames.put(replaced, curIdx);
                    }
                    colNames.put(colName, colIdx);
                }
            }
        }
    }

    private void autoMapColNames(ExcelExportConfig cfg, Object t, boolean dataTypeIsMap) {
        Set<String> ignorePropertySet =
            cfg.ignoreProperties != null ? new HashSet<String>(Arrays.asList(cfg.ignoreProperties))
                : Collections.EMPTY_SET;
        if (dataTypeIsMap) {
            Map<String, Object> map = (Map) t;

            for (String key : map.keySet()) {
                if (ignorePropertySet.contains(key)) {
                    continue;
                }
                if (!cfg.getColNameMap().containsKey(key) && !cfg.getColIndexMap()
                    .containsKey(key)) {
                    cfg.mapColName(key, key);
                }
            }
        } else {
            String[] getterNames = ReflectUtils.getterNames(t.getClass());

            for (String getterName : getterNames) {
                if (ignorePropertySet.contains(getterName)) {
                    continue;
                }
                if (!cfg.getColNameMap().containsKey(getterName) && !cfg.getColIndexMap()
                    .containsKey(getterName)) {
                    cfg.mapColName(getterName, getterName);
                }
            }
        }
    }

    private Map<String, Integer> parseTitleRow(Row row, ExcelExportConfig cfg) {
        Map<String, Integer> titleMap = new HashMap<>();

        for (short cellIdx = row.getFirstCellNum(); cellIdx < row.getLastCellNum(); cellIdx++) {
            int cellIdxCopy = cellIdx;

            Cell cell = row.getCell(cellIdxCopy);
            if (cell.getCellTypeEnum() == CellType.FORMULA) {
                continue;
            }
            String colName = cell.getStringCellValue();
            if (colName != null) {
                colName = colName.trim();
            }
            if (StringUtils.hasText(colName)) {
                titleMap.put(colName, cellIdxCopy);
            }
        }
        return titleMap;
    }

    private <T> void writeHeader(Sheet sheet, Map<String, Integer> sortedColNames, int rowIdx,
        ExcelExportConfig cfg) {
        Row row = exporter.createRow(sheet, rowIdx);

        if (cfg.rowCallback != null) {
            cfg.rowCallback.onRow(row, null, true);
        }

        for (String colName : sortedColNames.keySet()) {
            int cellIdx = sortedColNames.get(colName);
            Cell cell = null;
            Object cellValue = null;

            if (colName != null) {
                if (cfg.colNameMap.containsKey(colName)) {
                    colName = cfg.colNameMap.get(colName);
                }
            }

            cellValue = colName;

            if (cellValue != null) {
                cell = row.createCell(cellIdx, CellType.STRING);
                cell.setCellValue(cellValue.toString());
            } else {
                cell = row.createCell(cellIdx++, CellType.BLANK);
            }

            if (cfg.cellCallback != null) {
                cellValue = cfg.cellCallback.onCell(cell, cellValue, true);
            }
        }
    }

    private <T> void createRow(Sheet sheet, T t, boolean dataTypeIsMap,
        Map<String, Integer> sortedColNames, String[] fieldNameArray,
        CellType[] cellTypes, int rowIdx, ExcelExportConfig cfg) throws Exception {
        Row row = exporter.createRow(sheet, rowIdx);

        if (cfg.rowCallback != null) {
            if (!cfg.rowCallback.onRow(row, t, false)) {
                sheet.removeRow(row);
                return;
            }
        }

        int idx = 0;
        for (String fieldName : fieldNameArray) {
            Object value = null;

            if (fieldName != null) {
                if (dataTypeIsMap) {
                    Map map = (Map) t;
                    value = map.get(fieldName);
                } else {
                    value = getCellValue(t, fieldName);
                }
            }

            CellType cellType = cellTypes[idx++];
            if (value == null) {
                cellType = CellType.BLANK;
            }
            Cell cell = row.createCell(sortedColNames.get(fieldName), cellType);

            // apply style
            CellStyle style = null;
            if (value != null && value instanceof Styled) {
                Styled<?> styledValue = (Styled<?>) value;
                style = styledValue.getStyle() != null ? styledValue.getStyle().cs.get() : null;
                value = styledValue.getValue();
            } else {
                style = styleCache.get(fieldName);
                if (style == null) {
                    BiConsumer<CellStyle, Cell> styleConsumer = cfg.getColStyleMap().get(fieldName);
                    if (styleConsumer != null) {
                        style = sheet.getWorkbook().createCellStyle();
                        styleConsumer.accept(style, cell);
                    }
                    styleCache.put(fieldName, style);
                }
            }
            if (style != null) {
                cell.setCellStyle(style);
            }
            if (cfg.cellCallback != null) {
                value = cfg.cellCallback.onCell(cell, value, false);
            }

            if (value != null) {
                if (value instanceof Date) {
                    Date date = (Date) value;
                    if (cfg.dateFormat != null) {
                        cell.setCellValue(DateUtils.date2Str(date, cfg.dateFormat));
                    } else {
                        cell.setCellValue(date);
                    }
                } else if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else if (cellType == CellType.BOOLEAN) {
                    cell.setCellValue(ValueUtils.convert(value, boolean.class));
                } else if (value instanceof Iterable) {
                    StringBuffer buf = new StringBuffer();
                    for (Object v : ((Iterable) value)) {
                        buf.append(cfg.delimiter);
                        buf.append(String.valueOf(v));
                    }
                    if (buf.length() > 0) {
                        cell.setCellValue(buf.substring(cfg.delimiter.length()));
                    } else {
                        cell.setCellValue("");
                    }
                } else {
                    cell.setCellValue(String.valueOf(value));
                }
            }
        }
    }

    // 得到Cell的类型
    private CellType[] getCellTypes(Object t, boolean isMap, Map<String, Integer> sortColNames,
        ExcelExportConfig cfg) throws Exception {
        CellType[] cellType = new CellType[sortColNames.size()];
        int idx = 0;
        for (String key : sortColNames.keySet()) {
            if (key == null) {
                cellType[idx++] = CellType.BLANK;
                continue;
            }
            // 使用自定义类型
            if (cfg.colTypeMap.containsKey(key)) {
                cellType[idx++] = getCellType(cfg.colTypeMap.get(key));
                continue;
            }
            // 使用默认类型
            if (cfg.defaultStringType) {
                cellType[idx++] = CellType.STRING;
                continue;
            }
            // 使用数据的类型
            Class type = null;
            if (isMap) {
                Map map = (Map) t;
                Object value = map.get(key);
                if (value != null) {
                    type = value.getClass();
                }
            } else {
                try {
                    type = getCellValueClassType(t.getClass(), key);
                } catch (NoSuchMethodException ignore) {
                }
            }
            cellType[idx++] = getCellType(type);
        }
        return cellType;
    }

    private Object getCellValue(Object obj, String name)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (obj == null) {
            return null;
        }

        if (name.contains(".")) {
            int idx = name.indexOf(".");
            String subObjAttrName = name.substring(0, idx);
            return getCellValue(ReflectUtils.callGetMethod(obj, subObjAttrName),
                name.substring(idx + 1));
        } else {
            return ReflectUtils.callGetMethod(obj, name);
        }
    }

    private Class<?> getCellValueClassType(Class<?> beanClass, String name)
        throws NoSuchMethodException {
        if (name.contains(".")) {
            int idx = name.indexOf(".");
            String subObjAttrName = name.substring(0, idx);
            return getCellValueClassType(
                ReflectUtils.getter(beanClass, subObjAttrName).getReturnType(),
                name.substring(idx + 1));
        } else {
            return ReflectUtils.getter(beanClass, name).getReturnType();
        }
    }

    private CellType getCellType(Class<?> type) {
        if (type != null) {
            if (type.isPrimitive()) {
                type = ClassUtils.primitiveToWrapper(type);
            }
            if (String.class.isAssignableFrom(type)) {
                return CellType.STRING;
            } else if (Number.class.isAssignableFrom(type)) {
                return CellType.NUMERIC;
            } else if (Boolean.class.isAssignableFrom(type)) {
                return CellType.BOOLEAN;
            }
        }

        return CellType.STRING;
    }

    public ExcelExporter getExporter() {
        return exporter;
    }

    public ExcelExportConfig getCfg() {
        return cfg;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getDataRows() {
        return dataRows;
    }
}
