package com.caicongyang.dynamic.db.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;


public class DynamicDataSourceManager {

    private DataSourceProperties dataSourceProperties;

    private HikariDataSource dataSource;


    public DynamicDataSourceManager(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    public DataSource getDataSource() {
        if (this.dataSource == null) {
            this.dataSource = createDataSource();
        }
        return dataSource;
    }


    public void reloadDataSource() {
        if (this.dataSource != null) {
            this.dataSource.close();  // 关闭现有的数据源
        }
        this.dataSource = createDataSource();  // 创建新的数据源
    }

    private HikariDataSource createDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(dataSourceProperties.getUrl());
        hikariDataSource.setUsername(dataSourceProperties.getUsername());
        hikariDataSource.setPassword(dataSourceProperties.getPassword());
        // 其他配置...
        return hikariDataSource;
    }

}

