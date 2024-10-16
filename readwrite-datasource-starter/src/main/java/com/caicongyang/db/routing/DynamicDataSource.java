package com.caicongyang.db.routing;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // 获取当前线程的数据库类型（读/写）
        return DataSourceContextHolder.getDataSourceType();
    }
}
