package com.yibo.rocketmq.pull;

import com.yibo.rocketmq.constants.Const;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author: huangyibo
 * @Date: 2019/11/24 15:27
 * @Description: pull简单模式
 */

public class PullConsumer {

    // Map<key,value> key为指定队列，value为这个队列拉取数据的最后位置
    private static final Map<MessageQueue, Long> offsetTable = new HashMap<MessageQueue, Long>();

    public static void main(String[] args)  {
        try {
            String group_name = "test_pull_producer_name";
            DefaultMQPullConsumer consumer = new DefaultMQPullConsumer(group_name);
            consumer.setNamesrvAddr(Const.NAMESER_ADDR);
            consumer.start();
            System.out.println("consumer start......");

            //从topicTest这个主题去获取所有队列(默认会有4个队列)
            Set<MessageQueue> messageQueues = consumer.fetchSubscribeMessageQueues("test_pull_topic");
            //遍历每一个队列进行数据拉取
            for (MessageQueue messageQueue : messageQueues) {
                System.out.println("consumer from the queue：" + messageQueue);
                SINGLE_MQ:
                while (true) {
                    try {
                        //从queue中获取数据，从什么位置开始拉取数据，单次最多拉取32条数据
                        PullResult pullResult = consumer.pullBlockIfNotFound(messageQueue, null, getMessageQueueOffset(messageQueue), 32);
                        System.out.println(pullResult);
                        System.out.println(pullResult.getPullStatus());
                        putMessageQueueOffset(messageQueue, pullResult.getNextBeginOffset());
                        switch (pullResult.getPullStatus()) {
                            case FOUND:
                                break;
                            case NO_MATCHED_MSG:
                                break;
                            case NO_NEW_MSG:
                                System.out.println("没有新的数据啦......");
                                break SINGLE_MQ;
                            case OFFSET_ILLEGAL:
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = offsetTable.get(mq);
        if (offset != null)
            return offset;

        return 0;
    }

    private static void putMessageQueueOffset(MessageQueue mq, long offset) {
        offsetTable.put(mq, offset);
    }

}