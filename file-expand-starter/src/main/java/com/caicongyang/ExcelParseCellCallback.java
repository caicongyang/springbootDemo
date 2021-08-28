/**
 * 
 */
package com.caicongyang;

import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;

/**
 * 单元格回调
 * @author bo.wu
 *
 */
public interface ExcelParseCellCallback {
    /**
     * 解析每个cell时，回调此方法
     * @param cell
     * @param value
     * @param colMap
     * @return 返回Cell处理后的值，如果不需要处理，直接返回value即可
     */
    Object onCell(Cell cell, Object value, Map<Integer, String> colMap);
}
