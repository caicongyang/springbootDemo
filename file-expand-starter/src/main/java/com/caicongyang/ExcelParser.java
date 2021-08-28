/**
 * 
 */
package com.caicongyang;

import com.caicongyang.basic.ConvertParam;
import com.caicongyang.basic.ValueUtils;
import com.caicongyang.utils.ReflectUtils;
import com.google.common.collect.Maps;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

/**
 * @author bo.wu
 * @CreateDate Oct 28, 2015
 */
@SuppressWarnings("unchecked")
public class ExcelParser {
	private DataFormatter formatter = new DataFormatter();
	
	private Workbook workbook;
    private FormulaEvaluator evaluator;
	
	private ExcelErrorCollector excelErrorCollector;
	
	public Workbook getWorkbook() {
		return workbook;
	}
	
	public ExcelErrorCollector getExcelErrorCollector() {
	    return excelErrorCollector;
	}
	
	public ExcelParser(Workbook workbook) {
		this.workbook = workbook;
		evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		this.excelErrorCollector = new ExcelErrorCollector(workbook);
	}
	
	public int getSheetRows(int sheetIndex) {
		return getWorkbook().getSheetAt(sheetIndex).getPhysicalNumberOfRows();
	}
	
	public String getSheetName(int sheetIndex) {
		return getWorkbook().getSheetAt(sheetIndex).getSheetName();
	}
	
	public <T> List<T> parseSheet2Bean(int sheetIndex,  ExcelParseConfig cfg, final Class<T> clazz) throws Exception {
		cfg.withSheetIndexes(sheetIndex);
		return parse2Bean(cfg, clazz);
	}
	
	/**
	 * 解析Excel结果为指定的Bean集合
	 * @param cfg
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> parse2Bean(final ExcelParseConfig cfg, final Class<T> clazz) throws Exception {
    	final Map<String, Method> getterMap = new HashMap<String, Method>(8, 1F);
        final Map<String, Method> setterMap = new HashMap<String, Method>();

        final Method[] setters = ReflectUtils.setters(clazz);

        for (Integer col : cfg.colIndexMap.keySet()) {
            String name = cfg.colIndexMap.get(col);
            cacheEmbedGetter(clazz, name, getterMap);
            Method setter = getAndCacheSetter(clazz, name, setterMap, setters);
            if (setter != null){
            	Class<?> genericType = Reflection.getMethodParameterGenericType(setter, 0);
                if(genericType != null){
                	cfg.mapColType(col, genericType);
                }else{
                	cfg.mapColType(col, setter.getParameterTypes()[0]);
                }
            }
        }
        for (String field : cfg.colNameMap.values()) {
        	cacheEmbedGetter(clazz, field, getterMap);
            Method setter = getAndCacheSetter(clazz, field, setterMap, setters);
            if (setter != null){
            	Class<?> genericType = Reflection.getMethodParameterGenericType(setter, 0);
                if(genericType != null){
                	cfg.mapColType(field, genericType);
                }else{
                	cfg.mapColType(field, setter.getParameterTypes()[0]);
                }
            }
        }
    	
		return parse(cfg, new ExcelParseRowCallback<T>() {
			@Override
			public T onRow(Sheet sheet, Row row, Map<Integer, Object> values, Map<Integer, String> idxColMap) throws Exception {
				Object t = clazz.newInstance();
				for (Integer key : values.keySet()) {
					String name = idxColMap.get(key);
		            Object cellValue = values.get(key);
					try {
						setValue(clazz, t, name, cellValue, setters, setterMap, getterMap, cfg.dateFormat, cfg.delimiter);
					} catch (Exception e) {
						throw new ExcelParseException(row.getRowNum(), key, name, e);
					}
				}
				
				if (cfg.rowNoName != null) {
					setValue(clazz, t, cfg.rowNoName, row.getRowNum(), setters, setterMap, getterMap, null, cfg.delimiter);
		        }
                if (cfg.sheetNoName != null) {
                    setValue(clazz, t, cfg.sheetNoName, workbook.getSheetIndex(sheet), setters, setterMap, getterMap, null, cfg.delimiter);
                }
				return (T) t;
			}
		});
	}
   
	/**
     * 解析Excel结果为Map集合
	 * @param cfg
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> parse2Map(final ExcelParseConfig cfg) throws Exception {
		return parse(cfg, new ExcelParseRowCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> onRow(Sheet sheet, Row row, Map<Integer, Object> values, Map<Integer, String> idxColMap) throws Exception {
				Map<String, Object> map = new HashMap<>(values.size()+1, 1F);
				for (Integer key : values.keySet()) {
					String name = idxColMap.get(key);
					map.put(name, values.get(key));
				}
				if (cfg.rowNoName != null) {
					map.put(cfg.rowNoName, Integer.valueOf(row.getRowNum()));
		        }
                if (cfg.sheetNoName != null) {
                    map.put(cfg.sheetNoName, workbook.getSheetIndex(sheet));
                }
				return map;
			}
		});
	}
	/**
     * 解析Excel结果集合，集合中的元素为数组，数组中的元素顺序与Excel中列的顺序一致
	 * @param cfg
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> parse2Array(final ExcelParseConfig cfg) throws Exception {
		//数组类型此属性必须为True
		cfg.setAutoMapColName(true);
		return parse(cfg, new ExcelParseRowCallback<Object[]>() {
			@Override
			public Object[] onRow(Sheet sheet, Row row, Map<Integer, Object> values, Map<Integer, String> idxColMap) throws Exception {
				Object[] rslt = new Object[values.size()];
				int idx = 0;
				for (Integer key : values.keySet()) {
					rslt[idx ++] = values.get(key);
				}
				return rslt;
			}
		});
	}
    /**
     * 解析Excel结果集合，集合中的元素为callback返回的对象
     * @param cfg
     * @param callback
     * @return
     * @throws Exception
     */
    public <T> List<T> parse(ExcelParseConfig cfg, ExcelParseRowCallback<T> callback) throws Exception {
        List<Sheet> sheets = new ArrayList<Sheet>();
        
        if (cfg.sheetIndexes.size() > 0) {
            for (int idx : cfg.sheetIndexes) {
                Sheet sheet = workbook.getSheetAt(idx);
                if (sheet == null && ! cfg.ignoreSheetNotExistError) {
                    throw new ExcelParseException("Sheet at "+idx+" is not exists.");
                }
                sheets.add(sheet);
            }
        } else if (cfg.sheetNames.size() > 0) {
            for (String name : cfg.sheetNames) {
                Sheet sheet = workbook.getSheet(name);
                if (sheet == null && ! cfg.ignoreSheetNotExistError) {
                    throw new ExcelParseException("Sheet "+name+" is not exists.");
                }
                
                sheets.add(sheet);
            }
        } else {
            int idx = 0;
            while (true) {
                try {
                    Sheet sheet = workbook.getSheetAt(idx ++);
                    if (sheet == null) {
                        break;
                    }
                    
                    sheets.add(sheet);
                } catch (Exception e) {
                    break;
                }
            }
        }
        
        return toList(sheets, cfg, callback);
    }
    
    private <T> List<T> toList(List<Sheet> sheets, ExcelParseConfig cfg, ExcelParseRowCallback<T> callback) throws Exception {
        List<T> result = new ArrayList<T>();
        
        for (Sheet sheet : sheets) {
            result.addAll( doParse(sheet, cfg, callback) );
        }
        
        return result;
    }

	private <T> List<T> doParse(Sheet sheet, ExcelParseConfig cfg, ExcelParseRowCallback<T> callback) throws Exception {
        Map<Integer, String> idxColMap = Collections.EMPTY_MAP;
        Map<String, String> colTitleMap = Collections.EMPTY_MAP;
        
        int dataRows = sheet.getLastRowNum() - cfg.getDataStartRow() + 1;
        if (cfg.maxDataRows > 0 && dataRows > cfg.maxDataRows) {
        	throw new DataValidateException("import.maxRows", "import.maxRows", cfg.maxDataRows);
        }

        Row titleRow = sheet.getRow(cfg.getTitleRow());
        if (titleRow != null) {
            Map<Integer, String> titleMap = parseHeaderRow(titleRow, cfg);
            
            if (cfg.getHeaderRow() != -1 && cfg.getHeaderRow() != cfg.getTitleRow()) { // header和title同时指定
            	Row headerRow = sheet.getRow(cfg.getHeaderRow());
                
            	Map<Integer, String> headerMap = parseHeaderRow(headerRow, cfg);
            	
                colTitleMap = new HashMap<String, String>(idxColMap.size());
                for (Integer colIdx : titleMap.keySet()) {
                	colTitleMap.put(headerMap.get(colIdx), titleMap.get(colIdx));
                }
            	
                idxColMap = Maps.newHashMap();
            	for (Entry<Integer, String> entry : headerMap.entrySet()) {
            		String name = entry.getValue();
            		if (cfg.colMappingCallback != null) {
            			name = cfg.colMappingCallback.doMapping(name, titleMap.get(entry.getKey()));
            		}
            		idxColMap.put(entry.getKey(), name);
            	}
            } else {
                idxColMap = getColMap(titleRow, titleMap, cfg);
                
                colTitleMap = new HashMap<String, String>(idxColMap.size());
                for (Integer colIdx : idxColMap.keySet()) {
                	colTitleMap.put(idxColMap.get(colIdx), titleMap.get(colIdx));
                }
            }
        } else {
            idxColMap = cfg.colIndexMap;
        }
        
        if (cfg.beforeParseRowCallback != null) {
        	cfg.beforeParseRowCallback.beforeParseRow(sheet, idxColMap, colTitleMap);
        }

    	int rowIdx = cfg.getDataStartRow();
    	int maxRows = cfg.getDataStartRow() + cfg.maxRows;
        
        if (cfg.maxRows == 0 && sheet.getLastRowNum() > 0) {
        	maxRows = cfg.getTitleRow() + sheet.getLastRowNum() + 1;
        }
        
        Row row = sheet.getRow(rowIdx);

    	List<T> rslt = new LinkedList<T>();
    	int continuousNullRows = 0;

        while (true) {
        	if (maxRows > 0 && rowIdx >= maxRows) break;

            row = sheet.getRow(rowIdx ++);

            if(row == null) {
            	continuousNullRows ++;

            	if (continuousNullRows > 2) {
            		break;
            	}
            	continue;
            }
            continuousNullRows = 0;

        	Map<Integer, Object> line = parseRow(row, cfg, idxColMap, colTitleMap);

            if (line != null) {
            	T obj = callback.onRow(sheet, row, line, idxColMap);
            	rslt.add(obj);
            }
        }

        return rslt;
    }

    private Map<Integer, String> getColMap(Row row, Map<Integer, String> titleMap, ExcelParseConfig cfg) {
        Map<Integer, String> colMap = new LinkedHashMap<Integer, String>();

        for (Integer idx : titleMap.keySet()) {
            String colName = titleMap.get(idx);

            if (cfg.colNameMap.containsKey(colName)) {
                colMap.put(idx, cfg.colNameMap.get(colName));
            } else {
                if (cfg.colIndexMap.containsKey(idx)) {
                    colMap.put(idx, cfg.colIndexMap.get(idx));
                } else {
                	if (cfg.ignoreColNames.contains(colName)) {
                		continue;
                	}
                	if (cfg.ignoreColIndexes.contains(idx)) {
                		continue;
                	}
                	if (cfg.autoMapColName) {
                		String name = colName;
                		if (cfg.colMappingCallback != null) {
                			name = cfg.colMappingCallback.doMapping(colName, colName);
                		}
                		colMap.put(idx, name);
                	}
                }
            }
        }
        return colMap;
    }

    private Map<Integer, String> parseHeaderRow(Row row, ExcelParseConfig cfg) {
        int cellIdx = 0;

        Map<Integer, String> headerMap = new HashMap<Integer, String>();

        while (true) {
            int cellIdxCopy = cellIdx;
            cellIdx ++;

            Cell cell = row.getCell(cellIdxCopy);

            if (cell == null) {
                if (cellIdxCopy >= row.getLastCellNum()) {
                   break;
                } else {
                    continue;
                }
            }

            if (cell.getCellTypeEnum() == CellType.FORMULA) {
            	continue;
            }

            String colName = cell.getStringCellValue();

            if (cfg.autoTrimString && colName != null) {
            	colName = colName.trim();
            }

            if (StringUtils.hasText(colName)) {
                headerMap.put(cellIdxCopy, colName);
            }

            if (cfg.maxCols > 0 && cellIdx >= cfg.maxCols) {
                break;
            }
        }

        return headerMap;
    }

    private Map<Integer, Object> parseRow(Row row, ExcelParseConfig cfg, Map<Integer, String> colMap, Map<String, String> colTitleMap) throws Exception {
    	int cellIdx = 0;

        int maxCellIdx = cfg.maxCols;
        if (maxCellIdx == 0) {
	        if (colMap.size() > 0 && ! cfg.autoMapColName) {
	            maxCellIdx = Collections.max(colMap.keySet());
	        } else {
	        	int lastCellNum = row.getLastCellNum(); // 1-based
	        	if (lastCellNum > 0) {
	        		maxCellIdx = lastCellNum;
	        	}
                if (colMap.size() > 0) {
                    maxCellIdx = Math.max(maxCellIdx, Collections.max(colMap.keySet()));
                }
	        }
        }

        Map<Integer, Object> rslt = new LinkedHashMap<Integer, Object>();

        boolean isBlankRow = true;
    	int continuousNullCells = 0;
    	List<ExcelCellError> errors = new ArrayList<>();

        while (true) {
            int cellIdxCopy = cellIdx;
            cellIdx ++;

            if(maxCellIdx > 0 && cellIdxCopy > maxCellIdx) {
                break;
            }

            Cell cell = row.getCell(cellIdxCopy);

            ColDataType cellType = getCellType(cellIdxCopy, colMap, cfg);
            Class<?> dataType = cellType != null ? cellType.getType() : null;

            if (cell == null) {
            	continuousNullCells ++;
            	if (continuousNullCells > 50) {
            		break;
            	}

	            if (cfg.validator != null) {
	                String header = colMap.get(cellIdxCopy);
	                String title = colTitleMap.get(header);
	                try {
	                    cfg.validator.validate(cell, header, title, dataType, null);
	                } catch (DataValidateException e) {
	                	errors.add(new ExcelCellError(row.createCell(cellIdxCopy), header, e.getMessage()));
	                }
	            }

            	continue;
            }

            continuousNullCells = 0;

            if (! colMap.containsKey(cellIdxCopy) && ! cfg.autoMapColName) {
            	continue;
            }

            try {
	            Object cellValue = getCellValue(cell, cellType, cfg);

	            if (cfg.cellCallback != null) {
	            	cellValue = cfg.cellCallback.onCell(cell, cellValue, colMap);
	            }

	            if (isBlankRow && cellValue != null && StringUtils.hasText(cellValue.toString())) {
	                isBlankRow = false;
	            }

	            if (cfg.validator != null) {
	                String header = colMap.get(cellIdxCopy);
	                String title = colTitleMap.get(header);
	                if (header == null && cfg.autoMapColName) {
	                	continue;
	                }
	                try {
	                    cfg.validator.validate(cell, header, title, dataType, cellValue);
	                } catch (DataValidateException e) {
	                	errors.add(new ExcelCellError(cell, header, e.getMessage()));
	                	continue;
	                }
	            }

	            if (cellType != null) {
					try {
						rslt.put(cellIdxCopy, ValueUtils.convert(cellValue, dataType,
                                new ConvertParam().dateFormatPattern(ValueUtils.ifNull(cellType.getDateFormat(), cfg.dateFormat))));
					} catch (ClassCastException cce) {
						// 当String->List转换错误，直接传递String给下一层
						if (cfg.suppressTypeCastError && Collection.class.isAssignableFrom(dataType)) {
							rslt.put(cellIdxCopy, cellValue);
						} else {
							throw cce;
						}
					}
	            } else {
	            	rslt.put(cellIdxCopy, cellValue);
	            }
             } catch (Exception e) {
                 throw new ExcelParseException(row.getRowNum(), cell.getColumnIndex(), colMap.get(cell.getColumnIndex()), e);
             }
        }
        
        if (isBlankRow) {
            return null;
        }
        
        for (ExcelCellError error : errors) {
        	excelErrorCollector.collect(error);
        }
        
        return rslt;
    }
    
    private Map<String, Field> fieldCacheMap = new HashMap<String, Field>();
    
    private Field getField(Class<?> clazz, String fieldName) throws Exception {
        if (fieldCacheMap.containsKey(fieldName)) {
            return fieldCacheMap.get(fieldName);
        }
        Field field = ReflectUtils.getDeclaredField(clazz, fieldName, true);
        fieldCacheMap.put(fieldName, field);
        return field;
    }
    
    protected ColDataType getCellType(int cellIdx, Map<Integer, String> colMap, ExcelParseConfig cfg) {
    	if (cfg.colIndexTypeMap.containsKey(cellIdx)) {
    		return cfg.colIndexTypeMap.get(cellIdx);
    	}
    	String colName = colMap.get(cellIdx);
    	if (cfg.colNameTypeMap.containsKey(colName)) {
    		return cfg.colNameTypeMap.get(colName);
    	}
    	
    	if (cfg.defaultStringType) {
        	return ColDataType.STRING_TYPE;
    	}
    	
    	return null;
    }
    
    protected Object getCellValue(Cell cell, ColDataType cellDataType, ExcelParseConfig cfg) {
        Class<?> type = cellDataType != null ? cellDataType.getType() : null;
        CellType cellType = cell.getCellTypeEnum();
        boolean isStringType = type != null && String.class.isAssignableFrom(type);
        
        if (cellType == CellType.FORMULA) {
        	cellType = evaluator.evaluateFormulaCellEnum(cell);
        }
        
        switch (cellType) {
            case BLANK : {
                return null;
            }
            case NUMERIC : {
                if (isStringType) {
                	return getStringValue(formatter.formatCellValue(cell,evaluator), cfg);
                }
            	if (HSSFDateUtil.isCellDateFormatted(cell)) {
            		Date date = cell.getDateCellValue();
            		if (isStringType) return ValueUtils.convert(date, String.class,
                            new ConvertParam().dateFormatPattern(ValueUtils.ifNull(cellDataType.getDateFormat(), cfg.dateFormat)));
            		else return date;
            	}
                return cell.getNumericCellValue();
            }
            case STRING : {
                return getStringValue(cell.getStringCellValue(), cfg);
            }
            case BOOLEAN : {
                return cell.getBooleanCellValue();
            }
            case ERROR : {
            	return null;
            }
            default : {
                return getStringValue(cell.getStringCellValue(), cfg);
            }
        }
    }
    
    private String getStringValue(String cellValue, ExcelParseConfig cfg) {
        if (cfg.autoTrimString) return cellValue.trim();
        return cellValue;
    }

	private void setValue(Class<?> clazz, Object t,
			String key, Object cellValue, Method[] setters,
			Map<String, Method> setterMap, Map<String, Method> getterMap, String dateFormat, String delimiter) throws Exception {
		try {
            Method setter = getAndCacheSetter(clazz, key, setterMap, setters);
            
            int idx = key.indexOf(".");
            if (idx != -1) {
                String embedObjField = key.substring(0, idx);
                Method getter = getterMap.get(embedObjField);
                Object embed = getEmbedObject(clazz, t, embedObjField, getter);
                if (embed == null) {
                    Method embedSetter = setterMap.get(embedObjField);
                    embed = embedSetter.getParameterTypes()[0].newInstance();
                    embedSetter.invoke(t, embed);
                }
                // 更改外层类的引用为嵌套类
                clazz = embed.getClass();
                key = key.substring(idx + 1);
                t = embed;
            }
            
			if (setter != null && ! Collection.class.isAssignableFrom(setter.getParameterTypes()[0])) {
				// 如果setter可用，并且不是set一个集合类型（集合类型交由Field的反射方式，见else）
				setter.invoke(t, convertValue(cellValue, setter.getParameterTypes()[0], dateFormat));
			} else {
				Field field = getField(clazz, key);
				field.setAccessible(true);
				
				Class<?> genericType = Reflection.getFieldGenericType(field);
				Class<?> listType = genericType != null? Reflection.getCollectionImplements(field.getType()): null;
				if(genericType != null && listType != null){
					// 直接声明了范型类型的集合类型，并且能够获得可实例化的实现类。
					String[] cvs = String.valueOf(cellValue).split(delimiter);
					Collection<Object> list = (Collection<Object>) listType.newInstance();
					for(String cv: cvs){
						if(cv != null && cv.length() > 0 && !"null".equals(cv)){
							Object item = convertValue(cv.trim(), genericType, dateFormat);
							if(item != null){
								list.add(item);
							}
						}
					}
					field.set(t, list);
				} else {
					// 其他情况走默认逻辑
					field.set(t, convertValue(cellValue, field.getType(), dateFormat));
				}
			}
		} catch (Exception e) {
			throw new ExcelParseException(-1, -1, "Failed to set value["+cellValue+"] to field["+key+"]", e);
		}
	}

    private Object getEmbedObject(final Class<?> clazz, Object t, String embedField, Method getter) 
            throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        if (getter != null) {
            return getter.invoke(t);
        } else {
            Field field = clazz.getDeclaredField(embedField);
            field.setAccessible(true);
            return field.get(t);
        }
    }
	
	private Object convertValue(Object value, Class<?> type, String dateFormat) {
		if (dateFormat != null) {
			return ValueUtils.convert(value, type, new ConvertParam().dateFormatPattern(dateFormat));
		}
		return ValueUtils.convert(value, type);
	}

    private <T> void cacheEmbedGetter(final Class<T> clazz, String name, final Map<String, Method> getterMap)
            throws NoSuchMethodException {
        int idx = name.indexOf(".");
        if (idx != -1) {
            String embedObjField = name.substring(0, idx);
            try {
                getterMap.put(embedObjField, ReflectUtils.getter(clazz, embedObjField));
            } catch (NoSuchMethodException e) {
                // ignore
            }
        }
    }

    private <T> Method getAndCacheSetter(final Class<T> clazz, String name, final Map<String, Method> setterMap, Method[] setters)
            throws NoSuchMethodException {
    	Method rslt = setterMap.get(name);
    	
    	if (rslt == null) {
            int idx = name.indexOf(".");
            if (idx != -1) {
                String embedObjField = name.substring(0, idx);
                
                Method setter = getSetter(embedObjField, setters);
                if (setter != null) {
                    setterMap.put(embedObjField, setter);
                    
                	Class<?> paramClass = setter.getParameterTypes()[0];
                	
                	if (! BeanUtils.isSimpleProperty(paramClass)) {
                		Method[] embedClassSetters = ReflectUtils.setters(paramClass);
                		Method embedSetter = getSetter(name.substring(idx + 1), embedClassSetters);

                		if (setter != null) setterMap.put(name, embedSetter);
                	}
                }
            } else {
                Method setter = getSetter(name, setters);
        		if (setter != null) setterMap.put(name, setter);
            }
    	}
    	
    	return setterMap.get(name);
    }
    
    private Method getSetter(String name, Method[] setters) {
    	for (Method setter : setters) {
    		String fieldName = StringUtils.uncapitalize(setter.getName().substring(3));
    		if (fieldName.equals(name)) {
    			return setter;
    		}
    	}
    	return null;
    }
    
}
