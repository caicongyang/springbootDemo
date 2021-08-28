package com.caicongyang.excel.export;

import com.caicongyang.ExcelFileType;
import com.caicongyang.Excels;
import com.caicongyang.SpringApplicationContext;
import com.caicongyang.utils.TriPredicate;
import com.google.common.collect.Maps;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataExporter {


    private static Logger logger = LoggerFactory.getLogger(DataExporter.class);


    private int maxRows;
    private int bigDataThreshold;

    private ExecutorService executor;


    private IDataExportConfig dataExportConfig;



    private DataExporter() {
        this.maxRows = 10000;
        this.bigDataThreshold = 2000;
    }

    public <T> ExportContext exportData(IAsyncDataExportHandler<T> handler, DataExportParam param) throws Exception {
        return this.exportData(handler, this.getConfig(handler, param), param);
    }

    public <T> ExportContext exportData(String type, DataExportParam param) throws Exception {
        IAsyncDataExportHandler<T> handler = getHandler(type);
        return exportData(handler, getConfig(handler, param), param);
    }

    public <T> ExportContext exportData(String type, ExcelExportConfig cfg, DataExportParam param) throws Exception {
        IAsyncDataExportHandler<T> handler = getHandler(type);
        return exportData(handler, cfg, param);
    }

    public <T> ExportContext exportData(IAsyncDataExportHandler<T> handler, ExcelExportConfig cfg, DataExportParam param) throws Exception {
        setExportParam(param, getExportType(handler, param));

        final ExportContext ctx = new ExportContext();
        ctx.setAsyncExecutor(executor);
        ctx.setExportType(handler.getExportType());
        ctx.setTaskType(handler.getTaskType(param));
        ctx.setDataExportParam(param);

        IAsyncDataExportAware aware = handler.getAsyncDataExportAware();
        aware.beforeExport(ctx);

        executor.execute(() -> {
            try {
                aware.onExport(ctx);
                BiFunction<Integer, Integer, List<T>> dataProvider = (start, limit) -> {
                    handler.beforeListData(param);
                    List<T> list = handler.listExportData(start, limit, param, ctx);
                    handler.afterListData(list);
                    return list;
                };
                TriPredicate<List<T>, Integer , Integer> breaker = null;
                if (handler instanceof IDataExportBreaker) {
                    breaker = (list, maxDataRows, total) -> ((IDataExportBreaker) handler).isBreak(list, maxDataRows, total, param, ctx);
                }
                if (param.getExportFileTemplate() != null && param.isUseTemplateTitle()) {
                    cfg.withAutoMapTemplateTitle(true).withDataStartRow(cfg.getTitleRow() + 1).setWriteHeader(false);
                }
                Pair<ExcelExporter, Integer> pair = doExportData(dataProvider, cfg, param.getExportFileTemplate(), breaker);
                String filePath = handler.exportData(pair.getLeft(), cfg, ctx);

                DataExportResult result = new DataExportResult(pair.getRight(), pair.getLeft().getWorkbook(), cfg);
                ctx.setFilePath(filePath);
                ctx.setResult(result);

                aware.afterExport(ctx);
            } catch (Exception e) {
                logger.error("导出数据时发生异常", e);
                ctx.setException(e);
                try {
                    aware.onError(ctx);
                } catch (Exception se) {
                    logger.error("处理导出错误时发生异常", se);
                }
            }
        });

        return ctx;
    }



    protected <T> Pair<ExcelExporter, Integer> doExportData(BiFunction<Integer, Integer, List<T>> dataProvider,
        ExcelExportConfig cfg, InputStream exportFileTemplate, TriPredicate<List<T>, Integer , Integer> breaker) throws Exception {
        int start = 0;
        int limit = bigDataThreshold + 1;
        int maxDataRows = cfg.getMaxDataRows();
        if (maxDataRows == 0) {
            maxDataRows = maxRows;
        }
        List<T> list = dataProvider.apply(start, limit);
        if (list.size() > maxDataRows) {
            list = list.subList(0, maxDataRows);
        }
        boolean isBigData = list.size() > bigDataThreshold;

        ExcelExporter exporter = isBigData ?
            (exportFileTemplate != null ? Excels
                .newExcelExporterForBigData(exportFileTemplate, bigDataThreshold) : Excels.newExcelExporterForBigData(bigDataThreshold)) :
            (exportFileTemplate != null ? Excels.newExcelExporter(exportFileTemplate, ExcelFileType.XLSX) : Excels.newExcelExporter(ExcelFileType.XLSX));
        ExcelSheetWriter writer = exporter.getSheetWriter(cfg);
        writer.writeData(list);
        int total = list.size();

        boolean isBreak = list.size() < limit;
        if (breaker != null) {
            isBreak = breaker.test(list, maxDataRows, total);
        }

        if (!isBreak) {
            while (true) {
                limit = Math.min(maxDataRows - total, bigDataThreshold);
                if (limit <= 0) {
                    break;
                }

                start += list.size();
                list = dataProvider.apply(start, limit);
                if (! list.isEmpty()) {
                    writer.writeData(list);
                }
                total += list.size();
                if (breaker != null) {
                    if (breaker.test(list, maxDataRows, total)) {
                        break;
                    }
                } else if (list.isEmpty() && list.size() < limit) {
                    break;
                }
            }
        }

        return Pair.of(exporter, total);
    }








    private ExcelExportConfig getConfig(IDataExportHandler<?> handler, DataExportParam param) {
        Asserts.notNull(dataExportConfig, "Property dataExportConfig cannot be null");
        String exportType = getExportType(handler, param);
        return dataExportConfig.getConfig(exportType);
    }
    private String getExportType(IDataExportHandler<?> handler, DataExportParam param) {
        String exportType = handler.getExportType();
        if (exportType == null) {
            exportType = handler.getTaskType(param);
        }
        Asserts.notNull(exportType, "Property handler.exportType cannot be null");
        return exportType;
    }

    private <T> IAsyncDataExportHandler<T> getHandler(String exportType) {
        Asserts.notNull(exportType, "Property handler.exportType cannot be null");
        return SpringApplicationContext.getBean(dataExportConfig.getHandler(exportType));
    }

    private void setExportParam(DataExportParam param, String exportType) {
        String fileName = dataExportConfig.getFileName(exportType);
        if (StringUtils.isNotBlank(fileName)) {
            param.setExportFileName(fileName);
        }
        param.setSelectSql(dataExportConfig.getSelectSql(exportType));
        if (Objects.isNull(param.getParameters())) {
            param.setParameters(Maps.newHashMap());
        }
    }

}
