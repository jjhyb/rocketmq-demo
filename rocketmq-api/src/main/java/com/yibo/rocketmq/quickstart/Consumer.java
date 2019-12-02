package com.yibo.rocketmq.quickstart;

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
 * @Date: 2019/11/22 22:37
 * @Description:
 */
public class Consumer {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_quick_producer_name");
        consumer.setNamesrvAddr(Const.NAMESER_ADDR);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe("test_quick_topic","TagA");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = msgs.get(0);
                try{
                    String topic = messageExt.getTopic();
                    String tags = messageExt.getTags();
                    String keys = messageExt.getKeys();
                    if("keyA".equals(keys)){
                        //手动模拟消息消费失败
                        System.out.println("消息消费失败");
                        int a = 1/0;
                    }
                    String body = new String(messageExt.getBody(),RemotingHelper.DEFAULT_CHARSET);
                    System.out.println("topic："+topic+"，tags："+tags+"，keys："+keys+"，body："+body);
                }catch(Exception e){
                    e.printStackTrace();
                    //获取当前消息被重发了多少次
                    int reconsumeTimes = messageExt.getReconsumeTimes();
                    System.out.println("reconsumeTimes：" + reconsumeTimes);
                    if(reconsumeTimes == 3){
                        //如果一条消息被重发了3次，且还是没有消费成功，那么就记录日志
                        //记录日志目的就是为了做补偿处理
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    //消息消费失败会自动重试
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.println("consumer start ......");
    }
}
