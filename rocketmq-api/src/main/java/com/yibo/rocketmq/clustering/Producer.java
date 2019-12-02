package com.yibo.rocketmq.clustering;

import com.yibo.rocketmq.constants.Const;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * @author: huangyibo
 * @Date: 2019/11/24 15:21
 * @Description:
 */

public class Producer {

    public static void main(String[] args) throws Exception {
        String group_name = "test_model_producer_name";
        DefaultMQProducer producer = new DefaultMQProducer(group_name);
        producer.setNamesrvAddr(Const.NAMESER_ADDR);
        producer.start();
        producer.setSendMsgTimeout(10000);

        for (int i=0;i<8;i++){
            String tags = i % 2 == 0 ? "TagA" : "TagB";
            //1、创建消息
            Message message = new Message("test_model_topic",  //主题
                    tags,          //标签
                    "keyA",          //用户自定义的key，唯一的标识
                    ("消息内容" + i).getBytes());//消息体

            //2、将消息发送到指定队列
            SendResult sendResult = producer.send(message);
            System.out.println(sendResult);
        }

//        producer.shutdown();
    }
}
