package com.yibo.rocketmq.pull;

import com.yibo.rocketmq.constants.Const;
import org.apache.rocketmq.client.consumer.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/11/24 15:27
 * @Description: Pull模式下提供的负载均衡样例
 */

public class PullScheduleService {


    public static void main(String[] args) throws MQClientException {
        String group_name = "test_pull_consumer_name";

        final MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService(group_name);

        scheduleService.getDefaultMQPullConsumer().setNamesrvAddr(Const.NAMESER_ADDR);

        scheduleService.setMessageModel(MessageModel.CLUSTERING);

        scheduleService.registerPullTaskCallback("test_pull_topic", new PullTaskCallback() {

            @Override
            public void doPullTask(MessageQueue mq, PullTaskContext context) {
                MQPullConsumer consumer = context.getPullConsumer();
                System.err.println("-------------- queueId: " + mq.getQueueId()  + "-------------");
                try {
                    // 获取从哪里拉取
                    long offset = consumer.fetchConsumeOffset(mq, false);
                    if (offset < 0)
                        offset = 0;

                    PullResult pullResult = consumer.pull(mq, "*", offset, 32);
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            List<MessageExt> list = pullResult.getMsgFoundList();
                            for(MessageExt msg : list){
                                //消费数据...
                                System.out.println(new String(msg.getBody()));
                            }
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }
                    consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());
                    // 设置再过3000ms后重新拉取
                    context.setPullNextDelayTimeMillis(3000);

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        scheduleService.start();
    }

}