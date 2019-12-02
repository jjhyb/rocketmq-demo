package com.yibo.paycentera.service.producer;

import com.yibo.paycentera.mapper.CustomerAccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author: huangyibo
 * @Date: 2019/11/29 15:36
 * @Description:
 */

@Component
@Slf4j
public class TransactionListenerImpl implements TransactionListener {

    @Autowired
    private CustomerAccountMapper customerAccountMapper;

    /**
     * 执行本地事务
     * @param message
     * @param arg
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object arg) {
        log.info("执行本地事务单元------------");
        CountDownLatch currentCountDown = null;
        try {
            Map<String,Object> params = (Map<String,Object>) arg;
            String userId = (String)params.get("userId");
            String orderId = (String)params.get("orderId");
            String accountId = (String)params.get("accountId");
            BigDecimal payMoney = (BigDecimal)params.get("money"); //当前支付款
            BigDecimal newBalance = (BigDecimal)params.get("newBalance");   //前置扣款的余额
            int currentVersion = (int)params.get("currentVersion");
            currentCountDown = (CountDownLatch)params.get("currentCountDown");

            //updateBalance 传递当前支付款，数据库操作：
            int count = customerAccountMapper.updateBalance(accountId,newBalance,currentVersion,new Date());
            if(count == 1){
                //将count值减1，让调用countDownLatch.await()方法的线程执行
                currentCountDown.countDown();
                return LocalTransactionState.COMMIT_MESSAGE;
            }
            return LocalTransactionState.ROLLBACK_MESSAGE;
        } catch (Exception e) {
            log.error("本地事物异常 e={}",e);
            currentCountDown.countDown();
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

    /**
     * 未决事务，Rocket服务器端回查客户端
     * @param messageExt
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        return null;
    }

}
