package com.yibo.rocketmq.order;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/11/30 2:21
 * @Description:
 */
public class OrderListener implements MessageListenerOrderly {

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext context) {
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
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
            //消息消费失败会自动重试
            return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
