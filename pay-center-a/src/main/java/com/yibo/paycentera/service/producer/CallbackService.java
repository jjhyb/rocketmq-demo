package com.yibo.paycentera.service.producer;

import com.yibo.paycentera.utils.FastJsonConvertUtil;
import com.yibo.paycentera.utils.SnowflakeIdFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2019/11/29 23:00
 * @Description:
 */

@Component
@Slf4j
public class CallbackService {

    public static final String CALLBACK_PAY_TOPIC = "callback_pay_topic";

    public static final String CALLBACK_PAY_TAGS = "callback_pay";

    @Autowired
    private SyncProducer syncProducer;

    @Autowired
    private SnowflakeIdFactory snowflakeIdFactory;

    public void sendOKMessage(String orderId, String userId) {

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("orderId", orderId);
        params.put("status", "2");	//ok

        String keys = snowflakeIdFactory.getStrCodingByPrefix("pay_callback_");
        Message message = new Message(CALLBACK_PAY_TOPIC, CALLBACK_PAY_TAGS, keys, FastJsonConvertUtil.convertObjectToJSON(params).getBytes());

        SendResult sendResult = syncProducer.sendMessage(message);

        //如果消息发送失败可以进行重试，可以设置3次重试，3次如果还失败
        //SyncProducer设置了重试，这里就不要设置
        if(sendResult.getSendStatus() != SendStatus.SEND_OK){
            int count = 0;
            while(true){
                sendResult = syncProducer.sendMessage(message);
                count++;
                if(sendResult.getSendStatus() == SendStatus.SEND_OK || count == 3){
                    return;
                }
            }
        }
    }
}
