package com.caicongyang.stock.conf;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(
        sqlSessionFactoryRef = "stockSqlSessionFactoryBean",
        basePackages = "com.caicongyang.stock.mapper"
)
public class DatasourceAutoConfig {

    @Value("${spring.datasource.stock.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.stock.url}")
    private String url;
    @Value("${spring.datasource.stock.username}")
    private String username;
    @Value("${spring.datasource.stock.password}")
    private String password;


    @Bean(name = "stockSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate(
            @Qualifier("stockSqlSessionFactoryBean") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "stockTransactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("stockDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


    @Primary
    @Bean(name = "stockSqlSessionFactoryBean")
    @ConditionalOnMissingBean(name = "stockSqlSessionFactoryBean")
    public SqlSessionFactory sqlSessionFactoryBean(
            @Qualifier("stockDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*.xml"));
        return factoryBean.getObject();
    }

    @Primary
    @ConditionalOnMissingBean(name = "stockDataSource")
    @Bean(name = "stockDataSource")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl(url);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setPoolName("masterDataSource");
        return dataSource;
    }


}
