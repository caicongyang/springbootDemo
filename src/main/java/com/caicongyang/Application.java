package com.caicongyang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot web程序主入口
 * @author Administrator
 *
 */
@Configuration//配置控制  
@EnableAutoConfiguration//启用自动配置  
@ComponentScan//组件扫描  
public class Application {
	public static void main(String[] args) {   
        //第一个简单的应用，   
        SpringApplication.run(Application.class,args);   
    }   
}
