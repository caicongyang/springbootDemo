package com.ccy.mq.branch.service;

import com.ccy.mq.branch.util.RocketMqBranchRegexUtil;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;


/**
 * mq分支隔离
 * 通过修改topic进行隔离消费
 */
public class MqBranchIsolationBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private Environment environment;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 在Bean初始化之前进行处理
        // 可以对Bean进行修改或者扩展
        if (bean instanceof DefaultRocketMQListenerContainer) {
            DefaultRocketMQListenerContainer mqListenerContainer = (DefaultRocketMQListenerContainer) bean;
            String branch = environment.getProperty("git.branch", String.class, "master").toUpperCase();
            branch = RocketMqBranchRegexUtil.getLegalStr(branch);
            String newTopic = mqListenerContainer.getTopic() + "_" + branch;
            String newConsumerGroup = mqListenerContainer.getConsumerGroup() + "*" + newTopic;
            mqListenerContainer.setTopic(newTopic);
            mqListenerContainer.setConsumerGroup(newConsumerGroup);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 在Bean初始化之后进行处理
        // 可以对Bean进行修改或者扩展
        return bean;
    }
}
