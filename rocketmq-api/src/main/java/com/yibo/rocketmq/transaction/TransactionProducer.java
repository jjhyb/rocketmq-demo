package com.yibo.rocketmq.transaction;

import com.yibo.rocketmq.constants.Const;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.concurrent.*;

/**
 * @author: huangyibo
 * @Date: 2019/11/29 0:22
 * @Description:
 */
public class TransactionProducer {

    public static void main(String[] args) throws Exception {
        TransactionMQProducer producer = new TransactionMQProducer("tx_producer_group_name");
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("tx_producer_group_name" + "-check-thread");
                return thread;
            }
        });
        producer.setNamesrvAddr(Const.NAMESER_ADDR);
        producer.setExecutorService(executorService);
        //这个对象主要做两件事情，1、异步执行本地事物，2、回查
        TransactionListener transactionListener = new TransactionListenerImpl();
        producer.setTransactionListener(transactionListener);
        producer.setSendMsgTimeout(10000);
        producer.start();

        Message message = new Message("tx_topic","TagA","Key",
                ("hello rocketmq tx").getBytes(RemotingHelper.DEFAULT_CHARSET));
        producer.sendMessageInTransaction(message,"我是回调的参数");

        Thread.sleep(Integer.MAX_VALUE);
        producer.shutdown();
    }
}
