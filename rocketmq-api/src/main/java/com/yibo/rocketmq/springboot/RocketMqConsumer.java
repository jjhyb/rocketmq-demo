package com.yibo.rocketmq.springboot;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @Author: huangyibo
 * @Date: 2022/7/2 22:22
 * @Description:
 * topic需要和生产者的topic一致，consumerGroup属性是必须指定的，内容可以随意
 * selectorExpression的意思指的就是tag，默认为“*”，不设置的话会监听所有消息
 */

@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "Con_Group_One",
        topic = "RLT_TEST_TOPIC",
        selectorExpression = "tag1")
public class RocketMqConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("监听到消息：message:{}", message);
    }
}
