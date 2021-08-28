package com.caicongyang;

import java.util.Map;
import org.apache.poi.ss.usermodel.Sheet;

public interface ExcelBeforeParseRowCallback {
	
	void beforeParseRow(Sheet sheet, Map<Integer, String> colMap, Map<String, String> titleMap) throws Exception;
}
