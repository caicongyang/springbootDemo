package com.caicongyang.excel.export;

import java.util.List;
import java.util.Optional;

public interface IDataExportHandler<T> {


    default List<T> listExportData(int start, int limit, Optional<DataExportParam> optional) {
        return this.listExportData(start, limit, (DataExportParam) optional.get());
    }

    default List<T> listExportData(int start, int limit, DataExportParam param) {
        return this.listExportData(start, limit, Optional.ofNullable(param));
    }

    default void beforeListData(DataExportParam param) {
    }

    default void afterListData(List<T> list) {
    }


    default String getExportType() {
        return null;
    }

    default String getTaskType(DataExportParam param) {
        return this.getExportType();
    }

}
