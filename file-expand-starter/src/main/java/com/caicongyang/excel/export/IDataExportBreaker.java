package com.caicongyang.excel.export;

import java.util.List;
import javax.annotation.Nullable;


/**
 * 导出中断
 */
public interface IDataExportBreaker {
    /**
     * 是否中断导出
     * @param data 最后一次返回的数据集
     * @param maxDataRows 设置的最大导出行数
     * @param total 当前已经导出的行数
     * @param param 导出参数 (可能为null)
     * @param ctx 导出上下文（可能为null，仅在异步导出时有值）
     * @param <T>
     * @return
     */
    <T> boolean isBreak(List<T> data, int maxDataRows, int total,
        @Nullable DataExportParam param, @Nullable ExportContext ctx);
}
