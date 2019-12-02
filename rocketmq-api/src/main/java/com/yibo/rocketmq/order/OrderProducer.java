package com.yibo.rocketmq.order;

import com.yibo.rocketmq.constants.Const;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/11/30 1:57
 * @Description: 顺序消息 保障同一个topic下的同一组消息发往同一个MessageQueue即可
 */
public class OrderProducer {

    public static void main(String[] args) {
        try {
            DefaultMQProducer producer = new DefaultMQProducer("test_order_producer_group_name");
            producer.setNamesrvAddr(Const.NAMESER_ADDR);
            producer.setSendMsgTimeout(10000);
            producer.start();

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = sdf.format(date);
            //这5条消息是一个大的业务操作
            for (int i = 0; i < 5; i++) {
                //时间戳
                String body = dateStr + "Hello RocketMQ " + i;
                //参数: topic tag message
                Message message = new Message("test_order_topic","TagA","keyA"+i,body.getBytes(RemotingHelper.DEFAULT_CHARSET));
                //发送数据：如果使用顺序消费，则需自己实现MessageQueueSelector 保证消息进入同一个队列

                SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                    /**
                     *
                     * @param list 队列集合
                     * @param message 消息对象
                     * @param arg 业务标识的参数
                     * @return
                     */
                    @Override
                    public MessageQueue select(List<MessageQueue> list, Message message, Object arg) {
                        Integer id = (Integer) arg;
                        System.out.println("id : " + id);
                        int queueIndex = id % list.size();
                        return list.get(queueIndex);
                    }
                }, 1);//1是队列的下标
                System.out.println(sendResult + "，body："+body);
            }

            for (int i = 0; i < 5; i++) {
                //时间戳
                String body = dateStr + "Hello RocketMQ " + i;
                //参数: topic tag message
                Message message = new Message("test_order_topic","TagA","keyB"+i,body.getBytes(RemotingHelper.DEFAULT_CHARSET));
                //发送数据：如果使用顺序消费，则需自己实现MessageQueueSelector 保证消息进入同一个队列

                SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> list, Message message, Object arg) {
                        Integer id = (Integer) arg;
                        System.out.println("id : " + id);
                        int queueIndex = id % list.size();
                        return list.get(queueIndex);
                    }
                }, 2);//2是队列的下标
                System.out.println(sendResult + "，body："+body);
            }

            for (int i = 0; i < 5; i++) {
                //时间戳
                String body = dateStr + "Hello RocketMQ " + i;
                //参数: topic tag message
                Message message = new Message("test_order_topic","TagA","keyC"+i,body.getBytes(RemotingHelper.DEFAULT_CHARSET));
                //发送数据：如果使用顺序消费，则需自己实现MessageQueueSelector 保证消息进入同一个队列

                SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> list, Message message, Object arg) {
                        Integer id = (Integer) arg;
                        System.out.println("id : " + id);
                        int queueIndex = id % list.size();
                        return list.get(queueIndex);
                    }
                }, 3);//3是队列的下标
                System.out.println(sendResult + "，body："+body);
            }
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (MQClientException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
