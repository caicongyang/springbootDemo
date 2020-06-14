package com.caicongyang.conf;

import com.caicongyang.shiro.MyRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {


//    @Bean
//    public MyRealm myRealm() {
//        return new MyRealm();
//    }
//
//
//    @Bean
//    public SecurityManager getSecurityManager() {
//        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
//        manager.setRealm(myRealm());
//        return manager;
//    }
//
//
//    /**
//     * 配置Shiro生命周期处理器
//     */
//    @Bean
//    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
//        return new LifecycleBeanPostProcessor();
//    }
//
//    /**
//     * 自动创建代理类，若不添加，Shiro的注解可能不会生效。
//     */
//    @Bean
//    @DependsOn({"lifecycleBeanPostProcessor"})
//    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        advisorAutoProxyCreator.setProxyTargetClass(true);
//        return advisorAutoProxyCreator;
//    }
//
//
//    /**
//     * 开启Shiro的注解
//     */
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(getSecurityManager());
//        return authorizationAttributeSourceAdvisor;
//    }
//
//    @Bean
//    ShiroFilterFactoryBean shiroFilterFactoryBean() {
//        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
//        bean.setSecurityManager(getSecurityManager());
//        bean.setLoginUrl("/login");
//        bean.setSuccessUrl("/index");
//        bean.setUnauthorizedUrl("/unauthorizedurl");
//        Map<String, String> map = new LinkedHashMap<>();
//        //不需要授权访问
//        map.put("/doLogin", "anon");
//        map.put("/swagger-ui.html", "anon");
//        map.put("/webjars/**", "anon");
//        map.put("/v2/**", "anon");
//        map.put("/swagger-resources/**", "anon");
//
//
//        //其他所有的需要授权访问
//        map.put("/**", "authc");
//        bean.setFilterChainDefinitionMap(map);
//        return bean;
//    }


}
