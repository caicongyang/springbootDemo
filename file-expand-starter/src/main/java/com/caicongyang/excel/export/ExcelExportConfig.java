package com.caicongyang.excel.export;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * @author caicongyang
 * @date 2017年4月7日
 */
public class ExcelExportConfig {
    int sheetIndex;
    String sheetName;
    
    boolean autoMapColName = false;
    
    boolean writeHeader = true;
    /**
     * 自动映射模板的标题(基于提供的模板导出时使用)
     */
    boolean autoMapTemplateTitle;
    
    int startRow = -1;
    
    int titleRow;
    int dataStartRow = 1;
    int maxDataRows;
    
    String dateFormat = "yyyy-MM-dd HH:mm:ss";
    
    /** field -> column */
    Map<String, String> colNameMap = new LinkedHashMap<>();
    /** field -> columnIdx */
    Map<String, Integer> colIndexMap = new HashMap<>();
    
    String[] ignoreProperties;
    
    ExcelExportSheetCallback sheetCallback;
    ExcelExportRowCallback rowCallback;
    ExcelExportCellCallback cellCallback;

    boolean defaultStringType = true;
    Map<String, Class<?>> colTypeMap = new HashMap<String, Class<?>>();
    Map<String, BiConsumer<CellStyle, Cell>> colStyleMap = new HashMap<>();
    Map<String, Integer> colWidthMap = new HashMap<>();
    
    int sheetRandomAccessWindowSize;
    
    boolean autoSizeColumn = true;
    boolean autoBreak = false;
    /** 列表分隔符 */
    String delimiter = ",";
    
	public ExcelExportConfig setSheetCallback(ExcelExportSheetCallback sheetCallback) {
		this.sheetCallback = sheetCallback;
		return this;
	}

	public ExcelExportConfig setRowCallback(ExcelExportRowCallback rowCallback) {
		this.rowCallback = rowCallback;
		return this;
	}

	public ExcelExportConfig setCellCallback(ExcelExportCellCallback cellCallback) {
		this.cellCallback = cellCallback;
		return this;
	}
    
    public ExcelExportConfig withSheetRandomAccessWindowSize(int sheetRandomAccessWindowSize) {
    	this.sheetRandomAccessWindowSize = sheetRandomAccessWindowSize;
    	return this;
    }
	/**
	 * 设置Cell的类型是否为文本类型（默认为true）
	 * @param defaultStringType
	 * @return
	 */
	public ExcelExportConfig setDefaultStringType(boolean defaultStringType) {
		this.defaultStringType = defaultStringType;
		return this;
	}
	
	public ExcelExportConfig mapColType(String col, Class<?> valueType) {
		colTypeMap.put(col, valueType);
		return this;
	}
    public ExcelExportConfig mapColStyle(String col, BiConsumer<CellStyle, Cell> style) {
        colStyleMap.put(col, style);
        return this;
    }
    public ExcelExportConfig mapColWidth(String col, Integer width) {
        colWidthMap.put(col, width);
        return this;
    }
	/**
	 * 设置数据从第几行开始，包括Header(如果有)，如果是自定义Header，则需要设置此值
	 * @param startRow
	 * @return
	 */
	public ExcelExportConfig setStartRow(int startRow) {
        if (! writeHeader) {
            withDataStartRow(startRow);
        } else {
            withTitleRow(startRow);
            withDataStartRow(startRow + 1);
        }
		return this;
	}
    public ExcelExportConfig setMaxDataRows(int maxDataRows) {
        this.maxDataRows = maxDataRows;
        return this;
    }
	public ExcelExportConfig setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
        return this;
    }
	public ExcelExportConfig setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }
	/**
	 * 是否写数据头（默认为true）
	 * @param writeHeader
	 * @return
	 */
    public ExcelExportConfig setWriteHeader(boolean writeHeader) {
        this.writeHeader = writeHeader;
        if (! writeHeader && startRow != -1) {
            setStartRow(startRow);
        }
        return this;
    }

    public ExcelExportConfig setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public ExcelExportConfig setIgnoreProperties(String... ignoreProperties) {
        this.ignoreProperties = ignoreProperties;
        return this;
    }
    /**
     * 设置自动映射列名（默认为true）
     * @param autoMapColName
     * @return
     */
    public ExcelExportConfig setAutoMapColName(boolean autoMapColName) {
    	this.autoMapColName = autoMapColName;
    	return this;
    }
    /**
     * 设置是否自动调整列宽（默认为true）
     * @param autoSizeColumn
     * @return
     */
    public ExcelExportConfig setAutoSizeColumn(boolean autoSizeColumn) {
        this.autoSizeColumn = autoSizeColumn;
        return this;
    }
    /**
     * 设置是否自动换行（默认为false）
     * @param autoBreak
     * @return
     */
    public ExcelExportConfig setAutoBreak(boolean autoBreak) {
        this.autoBreak = autoBreak;
        return this;
    }
    /**
     * 设定分隔符（默认为半角逗号）
     * @param delimiter
     * @return
     */
    public ExcelExportConfig setDelimiter(String delimiter){
    	this.delimiter = delimiter;
    	return this;
    }
    
    /**
     * 映射Excel中列的标题，默认标题为key
     * @param key
     * @param column
     * @return
     */
    public ExcelExportConfig mapColName(String key, String column) {
        colNameMap.put(key, column);
        mapColIndex(key, colIndexMap.size());
        return this;
    }
    /**
     * 映射Excel中列的索引，默认按mapColName(..)的添加顺序，如果未调用mapColIndex(..)，则可能为乱序
     * @param key
     * @param column
     * @return
     */
    public ExcelExportConfig mapColIndex(String key, int column) {
    	if (colIndexMap.values().contains(column)) {
    		// 列顺序冲突，需要作移位处理
    		String oldKey = null;
    		for (String k : colIndexMap.keySet()) {
    			if (colIndexMap.get(k) == column) {
    				oldKey = k;
    				break;
    			}
    		}
    		colIndexMap.replace(oldKey, colIndexMap.getOrDefault(key, colIndexMap.size()));
    	}
        colIndexMap.put(key, column);
        return this;
    }
    
    /*------------------------------------------------------------------------------*/

	public String getSheetName() {
		return sheetName;
	}

	public boolean isWriteHeader() {
		return writeHeader;
	}
    
    /**
     * 第一行
     * @deprecated 由titleRow和dataStartRow替代
     * @return
     */
    @Deprecated
	public int getStartRow() {
		return writeHeader ? titleRow : dataStartRow;
	}
    
    public int getTitleRow() {
        return titleRow;
    }
    public int getDataStartRow() {
        return dataStartRow;
    }
    public boolean getAutoMapTemplateTitle() {
        return autoMapTemplateTitle;
    }
    public ExcelExportConfig withTitleRow(int titleRow) {
        // 兼容旧版本
        if (startRow == -1) {
            startRow = titleRow;
        }
        this.titleRow = titleRow;
        return this;
    }
    public ExcelExportConfig withDataStartRow(int dataStartRow) {
        this.dataStartRow = dataStartRow;
        return this;
    }
    public ExcelExportConfig withAutoMapTemplateTitle(boolean autoMapTemplateTitle) {
        this.autoMapTemplateTitle = autoMapTemplateTitle;
        return this;
    }
	
	public int getMaxDataRows() {
	    return maxDataRows;
    }

	public String getDateFormat() {
		return dateFormat;
	}

	public Map<String, String> getColNameMap() {
		return colNameMap;
	}

	public Map<String, Integer> getColIndexMap() {
		return colIndexMap;
	}

	public boolean isAutoMapColName() {
		return autoMapColName;
	}

	public String[] getIgnoreProperties() {
		return ignoreProperties;
	}

	public ExcelExportSheetCallback getSheetCallback() {
		return sheetCallback;
	}

	public ExcelExportRowCallback getRowCallback() {
		return rowCallback;
	}

	public ExcelExportCellCallback getCellCallback() {
		return cellCallback;
	}

	public boolean isDefaultStringType() {
		return defaultStringType;
	}

    public Map<String, Class<?>> getColTypeMap() {
        return colTypeMap;
    }
    public Map<String, BiConsumer<CellStyle, Cell>> getColStyleMap() {
        return colStyleMap;
    }
    public Map<String, Integer> getColWidthMap() {
        return colWidthMap;
    }

	public String getDelimiter() {
		return delimiter;
	}
	
}
