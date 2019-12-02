package com.yibo.paycentera.service.impl;

import com.alibaba.fastjson.JSON;
import com.yibo.paycentera.domain.entity.CustomerAccount;
import com.yibo.paycentera.mapper.CustomerAccountMapper;
import com.yibo.paycentera.service.PayService;
import com.yibo.paycentera.service.producer.CallbackService;
import com.yibo.paycentera.service.producer.TransactionProducer;
import com.yibo.paycentera.utils.SnowflakeIdFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author: huangyibo
 * @Date: 2019/11/29 14:38
 * @Description:
 */

@Service
@Slf4j
public class PayServiceImpl implements PayService {

    @Autowired
    private CustomerAccountMapper customerAccountMapper;

    @Autowired
    private SnowflakeIdFactory snowflakeIdFactory;

    @Autowired
    private TransactionProducer transactionProducer;

    @Autowired
    private CallbackService callbackService;

    private static final String TX_PAY_TOPIC = "tx_pay_topic";
    private static final String TX_PAY_TAGS = "PAY";

    @Override
    public String payment(String userId, String orderId, String accountId, double money) {
        String paymentResult = "";
        try {
            //最开始有一步token验证操作，用于解决重复提交问题
            BigDecimal payMoney = new BigDecimal(money);
            CustomerAccount customerAccount = customerAccountMapper.selectByPrimaryKey(accountId);
            BigDecimal currentBalance = customerAccount.getCurrentBalance();
            int currentVersion = customerAccount.getVersion();

            //这里进行判断是要对大概率事件进行预判(小概率事件放过到后面处理，最后保障数据的一致性即可)
            //在扣款更新数据库的时候可以使用Redis分布式锁防止同一账号在不同终端上操作，即使获取不到分布式锁也放行
            //在数据落库的时候使用数据库乐观锁进行操作，用以保障数据的最终一致性
            BigDecimal newBalance =  currentBalance.subtract(payMoney);
            if(newBalance.doubleValue() > 0){
                //1、组装消息且执行本地事物
                String keys = snowflakeIdFactory.getStrCodingByPrefix("pay_");
                Map<String,Object> params = new HashMap<>();
                params.put("userId",userId);
                params.put("orderId",orderId);
                params.put("accountId",accountId);
                params.put("money",payMoney);

                Message message = new Message(TX_PAY_TOPIC,TX_PAY_TAGS,keys,JSON.toJSONString(params).getBytes());
                //可能需要用到的参数
                params.put("newBalance",newBalance);
                params.put("currentVersion",currentVersion);
                //	同步阻塞
                CountDownLatch countDownLatch = new CountDownLatch(1);
                params.put("currentCountDown", countDownLatch);

                //消息发送并且本地的事物执行
                TransactionSendResult sendResult = transactionProducer.sendMessage(message, params);
                //主线程调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
                countDownLatch.await();

                if(sendResult.getSendStatus() == SendStatus.SEND_OK &&
                        sendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE){
                    //	回调order通知支付成功消息
                    callbackService.sendOKMessage(orderId,userId);
                    paymentResult = "支付成功!";
                }else {
                    log.info("支付失败!");
                    paymentResult = "支付失败!";
                }
            }else {
                log.info("余额不足!");
                paymentResult = "余额不足";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("支付出现异常,e={}",e);
            paymentResult = "支付失败!";
        }
        return paymentResult;
    }
}
