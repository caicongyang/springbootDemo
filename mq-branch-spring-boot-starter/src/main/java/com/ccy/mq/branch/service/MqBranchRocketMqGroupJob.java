package com.ccy.mq.branch.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.ccy.mq.branch.entity.RocketMqConsumerGroupResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class MqBranchRocketMqGroupJob {

    @Value("${mq.branch.rocketmq.console.url}")
    private String consoleUrl;

    private final String GROUP_URL = "/consumer/groupList.query";


    private Set<String> groupNameSet;

    public Set<String> getGroupNameSet() {
        return groupNameSet;
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public  Set<String> refreshGroupList()
    {
        String result = HttpUtil.get(consoleUrl+GROUP_URL);
        RocketMqConsumerGroupResponse rocketMqConsumerGroupResponse = JSON.parseObject(result, RocketMqConsumerGroupResponse.class);
        if(!CollectionUtils.isEmpty(rocketMqConsumerGroupResponse.getData())){
            List<RocketMqConsumerGroupResponse.RocketMqConsumerGroup> data = rocketMqConsumerGroupResponse.getData();
            groupNameSet = data.stream().map(RocketMqConsumerGroupResponse.RocketMqConsumerGroup::getGroup).collect(Collectors.toSet());
        }
        return groupNameSet;
    }

}
