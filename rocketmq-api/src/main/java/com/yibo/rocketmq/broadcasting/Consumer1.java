package com.yibo.rocketmq.broadcasting;

import com.yibo.rocketmq.constants.Const;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * @author: huangyibo
 * @Date: 2019/11/24 15:27
 * @Description: Broadcasting广播模式
 *
 * Broadcasting广播模式下
 * 同一group_name下的Consumer通过consumer.subscribe("test_model_topic","TagA");监听同一topic，用tags来区分消费消息是不行的
 * 这种效果必须要在Listener里面手动实现
 *
 * 不同group_name下的Consumer可以通过监听同一topic，用tags来区分消费消息
 *
 */

public class Consumer1 {

    public Consumer1()  {
        try {
            String group_name = "test_model_producer_name";
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group_name);
            consumer.setNamesrvAddr(Const.NAMESER_ADDR);
            consumer.subscribe("test_model_topic","*");
            consumer.setMessageModel(MessageModel.BROADCASTING);//广播模式
            consumer.registerMessageListener(new Consumer1Listener());
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Consumer1 consumer1 = new Consumer1();
        System.out.println("consumer1 start...");
    }
}