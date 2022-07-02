package com.yibo.rocketmq.springboot;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @Author: huangyibo
 * @Date: 2022/7/2 22:31
 * @Description:
 */

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "transaction-group",
        topic = "transaction-str",
        consumeMode = ConsumeMode.ORDERLY)
public class TransactionConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("监听到消息：message:{}", message);
    }

}
