package com.caicongyang.excel.export;


import com.caicongyang.basic.BaseContext;
import java.util.concurrent.ExecutorService;
import org.apache.poi.ss.usermodel.Workbook;

@SuppressWarnings("unchecked")
public class ExportContext extends BaseContext<String, Object> {
    public void setResult(DataExportResult result) {
        super.set("result", result);
    }
    public DataExportResult getResult() {
        return (DataExportResult) super.get("result");
    }
    
    public void setFilePath(String filePath) {
        super.set("filePath", filePath);
    }
    public String getFilePath() {
        return (String) super.get("filePath");
    }
    
    public void setDataExportParam(DataExportParam param) {
        super.set("dataExportParam", param);
    }
    public DataExportParam getDataExportParam() {
        return (DataExportParam) super.get("dataExportParam");
    }
    
    public void setExportType(String exportType) {
        super.set("exportType", exportType);
    }
    public String getExportType() {
        return (String) super.get("exportType");
    }
    
    public void setTaskType(String taskType) {
        super.set("taskType", taskType);
    }
    public String getTaskType() {
        return (String) super.get("taskType");
    }
    
    public void setAsyncExecutor(ExecutorService executor) {
        super.set("executor", executor);
    }
    public ExecutorService getAsyncExecutor() {
        return (ExecutorService) super.get("executor");
    }
    
    public void setWorkbook(Workbook workbook) {
        super.set("workbook", workbook);
    }
    public Workbook getWorkbook() {
        return (Workbook) super.get("workbook");
    }
    
    public void setException(Exception exception) {
        super.set("exception", exception);
    }
    public Exception getException() {
        return (Exception) super.get("exception");
    }
}
