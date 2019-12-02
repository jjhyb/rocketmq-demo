package com.yibo.rocketmq.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author: huangyibo
 * @Date: 2019/11/29 0:35
 * @Description: 事务监听器
 */

public class TransactionListenerImpl implements TransactionListener {

    /**
     * 执行本地事务
     * @param message
     * @param arg
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object arg) {
        System.out.println("------执行本地事物----");
        String callArg = (String) arg;
        System.out.println("callArg：" + callArg);
        System.out.println("message：" + message);
        //begin tx
        //数据库的落库操作

        //tx.commit
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    /**
     * 未决事务，Rocket服务器端回查客户端
     * @param messageExt
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        System.out.println("------回调消息检查，查询数据库是否存在该条消息----"+messageExt);

        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
