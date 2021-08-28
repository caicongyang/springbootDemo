/**
 * 
 */
package com.caicongyang;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Excel解析配置
 * @author bo.wu
 * @CreateDate Oct 28, 2015
 */
public class ExcelParseConfig {
    // 忽略Sheet不存在错误
	boolean ignoreSheetNotExistError;
    // 自动映射列名
    boolean autoMapColName = false;
	// 第一行为标题行
    boolean firstRowIsTitle = true;
    // 是否自动对String类型的值trim
    boolean autoTrimString = true;
    // 默认cell的值都为String类型
    boolean defaultStringType = true;
    // 日期格式
    String dateFormat = "yyyy-MM-dd";
    
    // 以列名为KEY的类型MAP
    Map<String, ColDataType> colNameTypeMap = new HashMap<String, ColDataType>();
    // 以列索引为KEY的类型MAP
    Map<Integer, ColDataType> colIndexTypeMap = new HashMap<Integer, ColDataType>();
    
    Map<String, String> colNameMap = new HashMap<String, String>();
    Map<Integer, String> colIndexMap = new HashMap<Integer, String>();
    
    Set<Integer> sheetIndexes = new HashSet<Integer>();
    Set<String> sheetNames = new HashSet<String>();
    
    Set<Integer> ignoreColIndexes = new HashSet<Integer>();
    Set<String> ignoreColNames = new HashSet<String>();
    Set<String> requiredColNames = new HashSet<>();

    int headerRow = -1;
    int titleRow;
    int dataStartRow = 1;
    int maxRows;
    int maxCols;
    
    // 最大数据行数
	int maxDataRows;
    
    ExcelParseCellCallback cellCallback;
    
    ExcelColMappingCallback colMappingCallback;
    
    ExcelBeforeParseRowCallback beforeParseRowCallback;
    
    ExcelValidator validator;
    
    /** 列表分隔符 */
    String delimiter = "[,，]";
    
    /** 仅用于parse2Bean时，压制String->List转换错误 */
    boolean suppressTypeCastError = false;
    
    String sheetNoName;
    String rowNoName;
    
    @Deprecated
    private int startRow;
    
    public ExcelParseConfig() {
    	sheetIndexes.add(0);
    }
    /**
     * 映射sheet号字段
     * @param sheetNo
     * @return
     */
    public ExcelParseConfig mapSheetNo(String sheetNo) {
        this.sheetNoName = sheetNo;
        return this;
    }
    /**
     * 映射行号字段
     * @param rowNo
     * @return
     */
    public ExcelParseConfig mapRowNo(String rowNo) {
        this.rowNoName = rowNo;
        return this;
    }
    /**
     * 设置Cell回调
     * @param cellCallback
     * @return
     */
    public ExcelParseConfig setCellCallback(ExcelParseCellCallback cellCallback) {
        this.cellCallback = cellCallback;
        return this;
    }
    /**
     * 设置映射回调
     * @param colMappingCallback
     * @return
     */
    public ExcelParseConfig setColMappingCallback(ExcelColMappingCallback colMappingCallback) {
    	this.colMappingCallback = colMappingCallback;
    	return this;
    }
    /**
     * 设置映射回调
     * @param beforeParseRowCallback
     * @return
     */
    public ExcelParseConfig setBeforeParseRowCallback(ExcelBeforeParseRowCallback beforeParseRowCallback) {
    	this.beforeParseRowCallback = beforeParseRowCallback;
    	return this;
    }
    /**
     * 设置验证器
     * @param validator
     * @return
     */
    public ExcelParseConfig setValidator(ExcelValidator validator) {
        this.validator = validator;
        return this;
    }

    public int getHeaderRow() {
    	return headerRow;
    }
    public int getTitleRow() {
		return titleRow;
	}
	public int getDataStartRow() {
		return dataStartRow;
	}
    public ExcelParseConfig withHeaderRow(int headerRow) {
    	this.headerRow = headerRow;
    	return this;
    }
	public ExcelParseConfig withTitleRow(int titleRow) {
		// 兼容旧版本
		if (headerRow == -1) {
			headerRow = titleRow;
		}
		this.titleRow = titleRow;
		return this;
	}
	public ExcelParseConfig withDataStartRow(int dataStartRow) {
		this.dataStartRow = dataStartRow;
		return this;
	}
    /**
     * 设置第一行是否为标题行，默认为true
     * @param firstRowIsHeader
     * @return
     */
	@Deprecated
    public ExcelParseConfig setFirstRowIsHeader(boolean firstRowIsHeader) {
		setFirstRowIsTitle(firstRowIsHeader);
        return this;
    }
    /**
     * 设置第一行是否为标题行，默认为true
     * @param firstRowIsTitle
     * @return
     */
    public ExcelParseConfig setFirstRowIsTitle(boolean firstRowIsTitle) {
        this.firstRowIsTitle = firstRowIsTitle;
        
        if (! firstRowIsTitle && startRow != 0) {
        	setStartRow(startRow);
        }
        return this;
    }
	/**
     * 设置起始行，起始值为0
     * @param startRow
     * @return
     */
    public ExcelParseConfig setStartRow(int startRow) {
    	if (! firstRowIsTitle) {
            withDataStartRow(startRow);
    	} else {
            withTitleRow(startRow);
            withDataStartRow(startRow + 1);
    	}
        return this;
    }
    /**
     * 设置是否忽略当sheet不存在时引起的错误
     * @param ignoreSheetNotExistError
     * @return
     */
    public ExcelParseConfig setIgnoreSheetNotExistError(boolean ignoreSheetNotExistError) {
        this.ignoreSheetNotExistError = ignoreSheetNotExistError;
        return this;
    }
    
    /**
     * 设置是否自动映射列名，默认为true
     * <p>当没有用mapColumnName(..)或mapColumnIndex(..)时，自动映射标题列名是唯一的映射方式，
     * 设置为false时，一般用于排除未映射的列</p>
     * @param autoMapColName
     * @return
     */
    public ExcelParseConfig setAutoMapColName(boolean autoMapColName) {
        this.autoMapColName = autoMapColName;
        return this;
    }
    /**
     * 设置是否自动对String类型的值调用trim方法
     * @param autoTrimString
     * @return
     */
    public ExcelParseConfig setAutoTrimString(boolean autoTrimString) {
    	this.autoTrimString = autoTrimString;
    	return this;
    }
    /**
     * 设置日期格式
     * @param dateFormat
     * @return
     */
    public ExcelParseConfig setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }
    /**
     * 设置最大行，起始值为0
     * @param maxRows
     * @return
     */
    public ExcelParseConfig setMaxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }
    /**
     * 设置最大列，起始值为0
     * @param maxCols
     * @return
     */
    public ExcelParseConfig setMaxCols(int maxCols) {
        this.maxCols = maxCols;
        return this;
    }
    public ExcelParseConfig setMaxDataRows(int maxDataRows) {
    	this.maxDataRows = maxDataRows;
    	return this;
    }
	
    /**
     * 根据列名映射
     * @param column
     * @param key
     * @return
     */
    public ExcelParseConfig mapColName(String column, String key) {
    	this.colNameMap.put(column, key);
        return this;
    }
    /**
     * 根据列索引映射
     * @param column
     * @param key
     * @return
     */
    public ExcelParseConfig mapColIndex(int column, String key) {
    	this.colIndexMap.put(column, key);
        return this;
    }
    /**
     * 包含需要解析的Sheet名
     * @param sheetName
     * @return
     */
    public ExcelParseConfig withSheetNames(String... sheetName) {
    	if (this.sheetNames.size() > 0) {
        	this.sheetNames.clear();
    	}
    	this.sheetNames.addAll(Arrays.asList(sheetName));
        return this;
    }
    /**
     * 包含需要解析的Sheet索引
     * @param sheetIndex
     * @return
     */
    public ExcelParseConfig withSheetIndexes(int... sheetIndex) {
    	if (this.sheetIndexes.size() > 0) {
        	this.sheetIndexes.clear();
    	}
        for (int idx : sheetIndex) {
        	this.sheetIndexes.add(idx);
        }
        return this;
    }
    /**
     * 过滤列索引
     * @param colIndex
     * @return
     */
    public ExcelParseConfig ignoreColIndex(int... colIndex) {
        for (int idx : colIndex) {
        	this.ignoreColIndexes.add(idx);
        }
        return this;
    }
    /**
     * 过滤列名
     * @param colName
     * @return
     */
    public ExcelParseConfig ignoreColName(String... colName) {
        for (String idx : colName) {
        	this.ignoreColNames.add(idx);
        }
        return this;
    }
	/**
	 * 设置Cell的类型是否为文本类型（默认为true）
	 * @param defaultStringType
	 * @return
	 */
	public ExcelParseConfig setDefaultStringType(boolean defaultStringType) {
		this.defaultStringType = defaultStringType;
		return this;
	}
	/**
	 * 设定分隔符（默认为半角逗号）
	 * @param delimiter
	 * @return
	 */
    public ExcelParseConfig setDelimiter(String delimiter) {
		this.delimiter = delimiter;
		return this;
	}
	/**
     * 根据Key名(类字段名或Map的Key)映射数据类型
     * @param key
     * @param type
     * @return
     */
    public ExcelParseConfig mapColType(String key, Class<?> type) {
    	this.colNameTypeMap.put(key, ColDataType.of(type));
        return this;
    }
    
    /**
     * 根据Key名(类字段名或Map的Key)为列配置数据类型
     * @param key
     * @param type
     * @return
     */
    public ExcelParseConfig mapColType(String key, ColDataType type) {
        this.colNameTypeMap.put(key, type);
        return this;
    }
    /**
     * 根据列索引映射数据类型
     * @param column
     * @param type
     * @return
     */
    public ExcelParseConfig mapColType(int column, Class<?> type) {
    	this.colIndexTypeMap.put(column, ColDataType.of(type));
        return this;
    }
    
    /**
     * 根据列索引映射数据类型
     * @param column
     * @param type
     * @return
     */
    public ExcelParseConfig mapColType(int column, ColDataType type) {
        this.colIndexTypeMap.put(column, type);
        return this;
    }
    
    public ExcelParseConfig requireCols(String... colNames) {
        for (String colName : colNames) {
            this.requiredColNames.add(colName);
        }
        return this;
    }
    
    /*------------------------------------------------------------------*/

	public boolean isIgnoreSheetNotExistError() {
		return ignoreSheetNotExistError;
	}

	@Deprecated
	public boolean isFirstRowIsHeader() {
		return firstRowIsTitle;
	}
	public boolean isFirstRowIsTitle() {
		return firstRowIsTitle;
	}

	public boolean isAutoMapColName() {
		return autoMapColName;
	}

	public boolean isAutoTrimString() {
		return autoTrimString;
	}

	public boolean isDefaultStringType() {
		return defaultStringType;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public Map<String, ColDataType> getColNameTypeMap() {
		return colNameTypeMap;
	}

	public Map<Integer, ColDataType> getColIndexTypeMap() {
		return colIndexTypeMap;
	}

	public Map<String, String> getColNameMap() {
		return colNameMap;
	}

	public Map<Integer, String> getColIndexMap() {
		return colIndexMap;
	}

	public Set<Integer> getSheetIndexes() {
		return sheetIndexes;
	}

	public Set<String> getSheetNames() {
		return sheetNames;
	}

	public Set<Integer> getIgnoreColIndexes() {
		return ignoreColIndexes;
	}

	public Set<String> getIgnoreColNames() {
		return ignoreColNames;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public int getMaxCols() {
		return maxCols;
	}
	
	public int getMaxDataRows() {return maxDataRows;}

	public ExcelParseCellCallback getCellCallback() {
		return cellCallback;
	}

	public String getSheetNoName() {
	    return sheetNoName;
    }
	public String getRowNoName() {
		return rowNoName;
	}
	
	public Set<String> getRequiredColNames() {
	    return requiredColNames;
    }
	
	public String getDelimiter() {
		return delimiter;
	}
	
	/** 尽在{@link ExcelUtil}中使用 */
	public void setSuppressTypeCastError(boolean suppressTypeCastError) {
		this.suppressTypeCastError = suppressTypeCastError;
	}
	
	
}
