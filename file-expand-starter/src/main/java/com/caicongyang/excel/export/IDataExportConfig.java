package com.caicongyang.excel.export;

public interface IDataExportConfig {

    /**
     * 返回导出配置
     * @param type
     * @return
     */
    ExcelExportConfig getConfig(String type);

    /**
     * 返回导出的查询sql
     * @param type
     * @return
     */
    String getSelectSql(String type);

    /**
     * 返回导出的handler
     * @param type
     * @return
     */
    String getHandler(String type);

    default String getFileName(String type) {
        return type + ".xlsx";
    }
}
