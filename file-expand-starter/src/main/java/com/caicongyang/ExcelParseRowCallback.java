package com.caicongyang;

import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * 行回调
 * @author bo.wu
 *
 * @param <T>
 */
public interface ExcelParseRowCallback<T> {
	/**
	 * 解析每一行时，回调此方法
	 * @param sheet
	 * @param row
	 * @param values
	 * @param idxColMap
	 * @return
	 * @throws Exception
	 */
	T onRow(Sheet sheet, Row row, Map<Integer, Object> rowValues, Map<Integer, String> idxColMap) throws Exception;
}
