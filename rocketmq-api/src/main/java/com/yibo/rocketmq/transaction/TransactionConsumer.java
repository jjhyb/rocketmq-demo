package com.yibo.rocketmq.transaction;

import com.yibo.rocketmq.constants.Const;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/11/29 0:23
 * @Description:
 */
public class TransactionConsumer {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("tx_producer_group_name");
        consumer.setConsumeThreadMin(10);
        consumer.setConsumeThreadMax(20);
        consumer.setNamesrvAddr(Const.NAMESER_ADDR);
        /**
         * 设置consumer第一次启动是从队列头部开始消费还是从队列尾部开始消费
         * 如果非第一次启动，那么按照上一次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //订阅的主题，以及过滤的标签内容
        consumer.subscribe("tx_topic","*");
        //注册监听
        consumer.registerMessageListener(new Listener());
        consumer.start();
        System.out.println("tx consumer started...");
    }

}

class Listener implements MessageListenerConcurrently{

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        MessageExt messageExt = null;
        try {
            for (MessageExt message : list) {
                messageExt = message;
                String topic = messageExt.getTopic();
                String tags = messageExt.getTags();
                //广播模式下，当Tags的值为"TagB"时，consumer2才进行消费
                String keys = messageExt.getKeys();
                String body = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
                System.out.println("topic：" + topic + "，tags：" + tags + "，keys：" + keys + "，body：" + body);
            }
        } catch (Exception e) {
            //获取当前消息被重发了多少次
            int reconsumeTimes = messageExt.getReconsumeTimes();
            System.out.println("reconsumeTimes：" + reconsumeTimes);
            if (reconsumeTimes == 3) {
                //如果一条消息被重发了3次，且还是没有消费成功，那么就记录日志
                //记录日志目的就是为了做补偿处理
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            //消息消费失败会自动重试
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
