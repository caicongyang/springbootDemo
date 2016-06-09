package com.caicongyang.conf;

import java.util.Properties;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tk.mybatis.spring.mapper.MapperScannerConfigurer;

@Configuration
//注意由于MapperScannerConfigurer执行的比较早，所以必须有下面的注解
@AutoConfigureAfter(MyBatisConfig.class)
public class MyBatisMapperScannerConfig {

  @Bean
  public MapperScannerConfigurer mapperScannerConfigurer() {
	  MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
      mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
      mapperScannerConfigurer.setBasePackage("com.caicongyang.mapper");
      Properties properties = new Properties();
      properties.setProperty("mappers", "com.caicongyang.utils.BaseMapper");
      properties.setProperty("notEmpty", "false");
      properties.setProperty("IDENTITY", "MYSQL");
      //这里使用的通用Mapper的MapperScannerConfigurer，所有有下面这个方法
      mapperScannerConfigurer.setProperties(properties);
      return mapperScannerConfigurer;
  }

}