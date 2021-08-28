package com.caicongyang.excel.export;

public interface IAsyncDataExportAware {
    
    void beforeExport(ExportContext ctx) throws Exception;
    
    void afterExport(ExportContext ctx);
    
    void onError(ExportContext ctx) throws Exception;
    
    default void onExport(ExportContext ctx) throws Exception {}
}
