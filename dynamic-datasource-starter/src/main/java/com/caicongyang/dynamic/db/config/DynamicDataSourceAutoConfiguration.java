package com.caicongyang.dynamic.db.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Configuration
@ConditionalOnClass(DataSource.class)
public class DynamicDataSourceAutoConfiguration {


    @Bean
    @ConditionalOnProperty(prefix = "dynamic.datasource", name = "enabled", havingValue = "true")
    public DynamicDataSourceManager dynamicDataSourceManager(DataSourceProperties dataSourceProperties) {
        return new DynamicDataSourceManager(dataSourceProperties);

    }

    @Bean
    @RefreshScope
    public HikariDataSource hikariDataSource(DataSourceProperties dataSourceProperties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
        dataSource.setJdbcUrl(dataSource.getJdbcUrl());
        dataSource.setUsername(dataSource.getUsername());
        dataSource.setPassword(dataSource.getPassword());
        return dataSource;
    }

}
