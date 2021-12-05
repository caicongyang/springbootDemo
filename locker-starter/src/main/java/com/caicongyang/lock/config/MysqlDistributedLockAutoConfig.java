package com.caicongyang.lock.config;

import com.caicongyang.lock.MysqlDistributedLock;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(
        sqlSessionFactoryRef = "lockerSqlSessionFactoryBean",
        basePackages = "com.caicongyang.lock.mapper"
)
public class MysqlDistributedLockAutoConfig {


    @Bean(name = "lockSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(
            @Qualifier("lockerSqlSessionFactoryBean") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "lockTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("lockerDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


    @Bean(name = "lockerSqlSessionFactoryBean")
    @ConditionalOnMissingBean(name = "lockerSqlSessionFactoryBean")
    @DependsOn("lockerDataSource")
    public SqlSessionFactoryBean exceptionSqlSessionFactoryBean(
            @Qualifier("lockerDataSource") DataSource lockerDataSource) {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(lockerDataSource);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultStatementTimeout(10);
        factoryBean.setConfiguration(configuration);
        return factoryBean;
    }

    @ConditionalOnMissingBean(name = "lockerDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.locker", ignoreInvalidFields = true)
    @Bean(name = "lockerDataSource")
    public DataSource lockerDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean
    @ConditionalOnExpression("#{'true'.equals(environment['conditional.mysql.locker'])}")
    public MysqlDistributedLock mysqlDistributedLock() {
        return new MysqlDistributedLock();
    }


}
