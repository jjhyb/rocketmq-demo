package com.yibo.rocketmq.order;

import com.yibo.rocketmq.constants.Const;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

/**
 * @author: huangyibo
 * @Date: 2019/11/30 2:15
 * @Description:
 */
public class OrderConsumer {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_order_producer_group_name");
        consumer.setNamesrvAddr(Const.NAMESER_ADDR);

        /**
         * 设置consumer第一次启动是从队列头部开始消费还是从队列尾部开始消费
         * 如果非第一次启动，那么按照上一次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //订阅的主题，以及过滤的标签内容
        consumer.subscribe("test_order_topic","TagA");
        //注册监听
        consumer.registerMessageListener(new OrderListener());

        consumer.start();
        System.out.println("consumer started......");
    }
}
