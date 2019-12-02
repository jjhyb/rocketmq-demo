package com.yibo.rocketmq.clustering;

import com.yibo.rocketmq.constants.Const;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * @author: huangyibo
 * @Date: 2019/11/24 15:27
 * @Description: Clustering集群模式
 */

public class Consumer1 {

    public Consumer1()  {
        try {
            String group_name = "test_model_producer_name";
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group_name);
            consumer.setNamesrvAddr(Const.NAMESER_ADDR);
            consumer.subscribe("test_model_topic","TagA || TagB");
            consumer.setMessageModel(MessageModel.CLUSTERING);//集群模式
            consumer.registerMessageListener(new ConsumerListener());
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