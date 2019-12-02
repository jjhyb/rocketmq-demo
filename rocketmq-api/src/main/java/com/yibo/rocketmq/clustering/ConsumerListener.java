package com.yibo.rocketmq.clustering;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/11/24 16:10
 * @Description:
 */
public class ConsumerListener implements MessageListenerConcurrently {

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        MessageExt messageExt = null;
        try {
            for (MessageExt message : list) {
                messageExt = message;
                String topic = messageExt.getTopic();
                String tags = messageExt.getTags();
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
