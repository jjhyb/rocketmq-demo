package com.yibo.packagecenter.consumer;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author: huangyibo
 * @Date: 2019/11/30 18:22
 * @Description:
 */
public class PkgOrderlyListener implements MessageListenerOrderly {
    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
        Random random = new Random();
        MessageExt messageExt = null;
        for (MessageExt message : list) {
            try {
                messageExt = message;
                String topic = messageExt.getTopic();
                String tags = messageExt.getTags();
                //广播模式下，当Tags的值为"TagB"时，consumer2才进行消费
                String keys = messageExt.getKeys();
                String body = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
                System.out.println("topic：" + topic + "，tags：" + tags + "，keys：" + keys + "，body：" + body);

                Map<String ,Object> map = JSON.parseObject(body,Map.class);
                String orderId = (String)map.get("orderId");
                String userId = (String)map.get("userId");
                String text = (String)map.get("text");

                //模拟实际的业务耗时操作
                //比如创建包裹信息，对物流服务的通知调用(异步调用)
                TimeUnit.SECONDS.sleep(random.nextInt(3)+1);
                System.out.println("业务操作"+text);
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
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
