package com.caicongyang.stock.component;/*
package com.caicongyang.component;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class RocketMsgListener implements MessageListenerConcurrently {

    private static final Logger logger = LoggerFactory.getLogger(RocketMqConsumerConfig.class);


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
        ConsumeConcurrentlyContext context) {
        if (CollectionUtils.isEmpty(list)) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt messageExt = list.get(0);
        logger.info("接受到的消息为：" + new String(messageExt.getBody()));
        int reConsume = messageExt.getReconsumeTimes();
        // 消息已经重试了3次，如果不需要再次消费，则返回成功
        if (reConsume == 3) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        String tags = messageExt.getTags();
        switch (tags) {
            case "rocketTag":
                logger.info("rocketTag == >>" + tags);
                break;
            default:
                logger.error("未匹配到Tag == >>" + tags);
                break;
        }
        // 消息消费成功
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}*/
