package com.caicongyang.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Excel导出数据针对单行扩展处理
 *
 * @author luozhuowei
 */
@FunctionalInterface
public interface ExportRowHandle {

    /**
     * 行数据扩展处理
     *
     * @param field 字段名
     * @param value 列对应的值
     * @param cell 列对象
     * @return 是否处理该行数据
     */
    boolean rowDataHandle(String field, Object value, Cell cell);

}
