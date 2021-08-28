package com.caicongyang.excel.export;

import com.caicongyang.DataParam;
import java.io.InputStream;

public class DataExportParam extends DataParam {

    private String exportFileName;
    /**
     * 导出文件模板
     */
    private InputStream exportFileTemplate;
    /**
     * 使用模板中的标题
     */
    private boolean useTemplateTitle;

    /**
     * 查询数据SQL
     */
    private String selectSql;

    public DataExportParam() {
    }

    public DataExportParam(String exportFileName) {
        this.exportFileName = exportFileName;
    }

    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }

    public String getExportFileName() {
        return exportFileName;
    }

    public InputStream getExportFileTemplate() {
        return exportFileTemplate;
    }

    public void setExportFileTemplate(InputStream exportFileTemplate) {
        this.exportFileTemplate = exportFileTemplate;
    }

    public boolean isUseTemplateTitle() {
        return useTemplateTitle;
    }

    public void setUseTemplateTitle(boolean useTemplateTitle) {
        this.useTemplateTitle = useTemplateTitle;
    }

    public String getSelectSql() {
        return selectSql;
    }

    public void setSelectSql(String selectSql) {
        this.selectSql = selectSql;
    }
}
