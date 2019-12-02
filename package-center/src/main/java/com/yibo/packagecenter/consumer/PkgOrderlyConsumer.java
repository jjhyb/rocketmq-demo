package com.yibo.packagecenter.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

/**
 * @author: huangyibo
 * @Date: 2019/11/30 18:15
 * @Description:
 */
public class PkgOrderlyConsumer {

    private static final String NAMESER_ADDR = "192.168.218.131:9876";

    public static final String PRODUCER_GROUP_NAME = "orderly_producer_group_name";

    public static final String PKG_TOPIC = "pkg_topic";

    public static final String PKG_TAGS = "pkg";

    private DefaultMQPushConsumer consumer;

    private PkgOrderlyConsumer() throws MQClientException {
        this.consumer = new DefaultMQPushConsumer(PRODUCER_GROUP_NAME);
        this.consumer.setConsumeThreadMin(10);
        this.consumer.setConsumeThreadMax(30);
        this.consumer.setNamesrvAddr(NAMESER_ADDR);
        this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        this.consumer.subscribe(PKG_TOPIC,PKG_TAGS);
    }

}
