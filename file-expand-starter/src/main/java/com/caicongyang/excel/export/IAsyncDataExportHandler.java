package com.caicongyang.excel.export;

import java.util.List;

public interface IAsyncDataExportHandler<T> extends IDataExportHandler<T> {


    String exportData(ExcelExporter exporter, ExcelExportConfig cfg, ExportContext ctx)
        throws Exception;

    default List<T> listExportData(int start, int limit, DataExportParam param, ExportContext ctx) {
        return this.listExportData(start, limit, param);
    }

    IAsyncDataExportAware getAsyncDataExportAware();


}
