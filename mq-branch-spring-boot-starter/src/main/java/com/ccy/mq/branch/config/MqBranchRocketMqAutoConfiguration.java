package com.ccy.mq.branch.config;


import com.ccy.mq.branch.contant.MqBranchPropertyConstants;
import com.ccy.mq.branch.service.MqBranchIsolationBeanPostProcessor;
import com.ccy.mq.branch.service.MqBranchRocketMqGroupJob;
import com.ccy.mq.branch.service.MqBranchRocketMqTemplateAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(MqBranchPropertyConstants.MQ_BRANCH_ENABLE)
@Import(MqBranchRocketMqGroupJob.class)
public class MqBranchRocketMqAutoConfiguration {


    @Bean
    @ConditionalOnProperty(name = MqBranchPropertyConstants.MQ_BRANCH_TYPE,havingValue = MqBranchPropertyConstants.MQ_BRANCH_SUPPORT_ROCKET_MQ)
    public MqBranchIsolationBeanPostProcessor mqBranchIsolationBeanPostProcessor(){
        return new MqBranchIsolationBeanPostProcessor();
    }


    @Bean
    @ConditionalOnProperty(name = MqBranchPropertyConstants.MQ_BRANCH_TYPE,havingValue = MqBranchPropertyConstants.MQ_BRANCH_SUPPORT_ROCKET_MQ)
    public MqBranchRocketMqTemplateAspect mqBranchRocketMqTemplateAspect(){
        return new MqBranchRocketMqTemplateAspect();
    }

}
