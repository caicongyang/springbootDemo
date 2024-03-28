package com.caicongyang.config;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.boot.ApolloAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Description TODO
 * @Author caicongyang
 * @Date 2020/9/8 17:56
 */
@Configuration
@ConditionalOnClass({ApolloAutoConfiguration.class, RefreshScope.class})
@Import(LoggerConfiguration.class)
public class ApolloReFreshAutoConfig implements ApplicationContextAware {

    private Logger log = LoggerFactory.getLogger(ApolloReFreshAutoConfig.class);

    private  ApplicationContext applicationContext;

    @Autowired
    private ContextRefresher contextRefresher;


    @ApolloConfigChangeListener
    private void refresh(ConfigChangeEvent changeEvent) {
        log.info("Apollo config refresh start");
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
        contextRefresher.refresh();
        log.info("Apollo config refresh end");
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }


}