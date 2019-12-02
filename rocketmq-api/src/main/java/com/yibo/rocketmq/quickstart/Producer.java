package com.yibo.rocketmq.quickstart;

import com.yibo.rocketmq.constants.Const;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/11/22 22:37
 * @Description:
 */
public class Producer {

    public static void main(String[] args) throws Exception {

        //RocketMQ 同步消息发送
        syncMsg();

        //RocketMQ 异步消息发送
        asyncMsg();
    }

    /**
     * RocketMQ 将消息发送到指定队列
     */
    public static void asyncMsgCustomQueue() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test_quick_producer_name");
        producer.setNamesrvAddr(Const.NAMESER_ADDR);

        producer.start();
        producer.setSendMsgTimeout(10000);
        //1、创建消息
        Message message = new Message("test_quick_topic",  //主题
                "TagA",          //标签
                "keyA",          //用户自定义的key，唯一的标识
                ("hello RocketMq").getBytes());//消息体
        //2、将消息发送到指定队列
        producer.send(message, new MessageQueueSelector() {
            @Override
            public MessageQueue select(List<MessageQueue> list, Message message, Object arg) {
                Integer queueNumber = (Integer) arg;
                int size = list.size();
                int index = queueNumber % size;
                return list.get(index);
            }
        },1);


//        producer.shutdown();
    }


    /**
     * RocketMQ 发送单向消息
     */
    public static void syncOneWay() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test_quick_producer_name");
        //同步发送消息，如果5秒内没有发送成功，则重试3次
        producer.setRetryTimesWhenSendFailed(3);
        producer.setNamesrvAddr(Const.NAMESER_ADDR);

        producer.start();
        //设置消息发送超时时间，默认为3000ms
        producer.setSendMsgTimeout(5000);
        //1、创建消息
        Message message = new Message("test_quick_topic",  //主题
                "TagA",          //标签
                "keyA",          //用户自定义的key，唯一的标识
                ("hello RocketMq").getBytes());//消息体

        //2、发送单向消息
        producer.sendOneway(message);
        System.out.println("单向消息已发出");

        producer.shutdown();
    }


    /**
     * RocketMQ 异步消息发送
     */
    public static void asyncMsg() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test_quick_producer_name");
        producer.setNamesrvAddr(Const.NAMESER_ADDR);

        producer.setRetryTimesWhenSendAsyncFailed(0);
        producer.start();
        //设置消息发送超时时间，默认为3000ms
        producer.setSendMsgTimeout(5000);
        //1、创建消息
        Message message = new Message("test_quick_topic",  //主题
                "TagA",          //标签
                "keyA",          //用户自定义的key，唯一的标识
                ("hello RocketMq").getBytes());//消息体
        //2、发送异步消息
        producer.send(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("msgId：" + sendResult.getMsgId()+"，status："+sendResult.getSendStatus());
                //通过status可以知道此异步消息是否发送成功
                //如果要保障100%可靠性投递的消息的话，在获取到status的时候对消息数据做变更操作
            }

            @Override
            public void onException(Throwable throwable) {
                //消息发送失败的话，进行补偿或者重发
                System.out.println("消息发送失败");
            }
        });


//        producer.shutdown();
    }

    /**
     * RocketMQ 同步消息发送
     */
    public static void syncMsg() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test_quick_producer_name");
        //同步发送消息，如果5秒内没有发送成功，则重试3次
        producer.setRetryTimesWhenSendFailed(3);
        producer.setNamesrvAddr(Const.NAMESER_ADDR);

        producer.start();
        //设置消息发送超时时间，默认为3000ms
        producer.setSendMsgTimeout(5000);
        //1、创建消息
        Message message = new Message("test_quick_topic",  //主题
                "TagA",          //标签
                "keyA",          //用户自定义的key，唯一的标识
                ("hello RocketMq").getBytes());//消息体
        //2、发送同步消息
        SendResult sendResult = producer.send(message,5000L);
        System.out.println("消息发出：" + sendResult);

        producer.shutdown();
    }
}
