package com.yibo.paycentera.service.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author: huangyibo
 * @Date: 2019/11/29 15:34
 * @Description:
 */

@Component
@Slf4j
public class TransactionProducer implements InitializingBean {

    private TransactionMQProducer producer;

    private ExecutorService executorService;

    @Autowired
    private TransactionListener transactionListener;

    private static final String NAMESER_ADDR = "192.168.218.131:9876";

    private static final String producer_group_name = "tx_pay_producer_group_name";

    private TransactionProducer(){
        this.producer = new TransactionMQProducer(producer_group_name);
        this.executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(producer_group_name+"-chech-thread");
                return thread;
            }
        });
        this.producer.setExecutorService(executorService);
        this.producer.setNamesrvAddr(NAMESER_ADDR);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.producer.setTransactionListener(transactionListener);
        this.start();
    }

    private void start(){
        try {
            this.producer.start();
            log.info("rocketmq producer start......");
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("rocketmq producer start exception e={}",e);
        }
    }

    public TransactionSendResult sendMessage(Message message,Object argument){
        TransactionSendResult sendResult = null;
        try {
            sendResult = this.producer.sendMessageInTransaction(message,argument);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return sendResult;
    }

    public void shutdown(){
        this.producer.shutdown();;
    }
}
