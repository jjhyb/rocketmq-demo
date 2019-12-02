package com.yibo.ordercenter.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yibo.ordercenter.constants.ErrorCode;
import com.yibo.ordercenter.constants.OrderStatus;
import com.yibo.ordercenter.domain.entity.OrderCenter;
import com.yibo.ordercenter.domain.result.ResponseResult;
import com.yibo.ordercenter.mapper.OrderCenterMapper;
import com.yibo.ordercenter.producer.OrderlyProducer;
import com.yibo.ordercenter.service.OrderCenterService;
import com.yibo.ordercenter.utils.SnowflakeIdFactory;
import com.yibo.storecenter.StoreServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

/**
 * @author: huangyibo
 * @Date: 2019/11/28 15:28
 * @Description:
 */

@Service
@Slf4j
public class OrderCenterServiceImpl implements OrderCenterService {

    @Autowired
    private SnowflakeIdFactory snowflakeIdFactory;

    @Autowired
    private OrderCenterMapper orderCenterMapper;

    @Reference(version = "1.0.0",
            application = "${dubbo.application.id}",
            check = false,
            timeout = 3000,
            retries = 0//默认值2不包含第一次，retries=0 禁用超时重试，一般读请求，允许重试三次,写请求下游接口没做幂等就不要重试
    )
    private StoreServiceApi storeServiceApi;

    @Autowired
    private OrderlyProducer orderlyProducer;

    @Override
    public ResponseResult createOrder(OrderCenter orderCenter) {
        try {
            String orderId = snowflakeIdFactory.getStrCodingByPrefix("xundu_");
            orderCenter.setOrderId(orderId);
            orderCenter.setOrderType("1");
            orderCenter.setOrderStatus(OrderStatus.ORDER_CREATED.getValue());
            orderCenter.setRemark("订单附加属性");
            orderCenter.setCreator("admin");
            orderCenter.setModifier("damin");
            orderCenter.setCreateTime(new Date());
            orderCenter.setModifTime(orderCenter.getCreateTime());

            //查询库存版本号
            int currentVersion = storeServiceApi.selectVersion(orderCenter.getSupplierId(), orderCenter.getGoodsId());

            int updateResultCount = storeServiceApi.updateStoreCountByVersion(orderCenter.getSupplierId(), orderCenter.getGoodsId(),orderCenter.getCount(),currentVersion,orderCenter.getModifier(),orderCenter.getModifTime());

           if(updateResultCount == 0) {
                //库存没有更新成功，1、高并发时乐观锁生效，2、库存不足
                //查询库存
                int currentStoreCount = storeServiceApi.selectStoreCount(orderCenter.getSupplierId(), orderCenter.getGoodsId());
                if(orderCenter.getCount() > currentStoreCount){
                    log.info("-------当前库存不足，订单无法生成,currentStoreCount={},count={}",currentStoreCount,orderCenter.getCount());
                    return ResponseResult.build(ErrorCode.OUT_OF_STOCK);
                }else {
                    log.info("-------乐观锁生效，订单生成失败,currentStoreCount={},count={}",currentStoreCount,orderCenter.getCount());
                    return ResponseResult.build(ErrorCode.LOCK_TAKE_EFFECT);
                }
            }else {
//               int i = 1/0;
                //如果order创建出现Sql异常，入库失败，在catch中捕获并要对库存的数量和版本号进行回滚操作
               orderCenterMapper.insertSelective(orderCenter);
                return ResponseResult.ok(orderCenter);
            }
        } catch (Exception e) {
            //如果order创建出现Sql异常，入库失败，在catch中捕获并要对库存的数量和版本号进行回滚操作
            if(e instanceof SQLException){
                log.info("----订单生成失败，回滚库存----");
                storeServiceApi.rollbackStoreCountAndVersion(orderCenter.getSupplierId(), orderCenter.getGoodsId(),orderCenter.getCount(),orderCenter.getModifier(),orderCenter.getModifTime());
            }
            if(e instanceof ArithmeticException){
                //上面用除0异常演示订单创建失败，进行库存回滚
                log.info("----订单生成失败，回滚库存----");
                storeServiceApi.rollbackStoreCountAndVersion(orderCenter.getSupplierId(), orderCenter.getGoodsId(),orderCenter.getCount(),orderCenter.getModifier(),orderCenter.getModifTime());
            }
            e.printStackTrace();
            //这里应该捕获具体的异常，下单操作不应该重试，直接返回就可以了
            return ResponseResult.errorException(e.getMessage());
        }
    }

    public static final String PKG_TOPIC = "pkg_topic";

    public static final String PKG_TAGS = "pkg";

    @Override
    public void sendOrderlyMessage4Pkg(String userId, String orderId) {
        List<Message> messageList = new ArrayList<>();

        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("userId", userId);
        param1.put("orderId", orderId);
        param1.put("text", "创建包裹操作---1");

        String key1 = UUID.randomUUID().toString() + "$" +System.currentTimeMillis();
        Message message1 = new Message(PKG_TOPIC, PKG_TAGS, key1, JSON.toJSONString(param1).getBytes());

        messageList.add(message1);


        Map<String, Object> param2 = new HashMap<String, Object>();
        param2.put("userId", userId);
        param2.put("orderId", orderId);
        param2.put("text", "发送物流通知操作---2");

        String key2 = UUID.randomUUID().toString() + "$" +System.currentTimeMillis();
        Message message2 = new Message(PKG_TOPIC, PKG_TAGS, key2, JSON.toJSONString(param2).getBytes());

        messageList.add(message2);

        //	顺序消息投递 是应该按照 供应商ID 与topic 和 messagequeueId 进行绑定对应的
        //  supplier_id

        OrderCenter orderCenter = orderCenterMapper.selectByPrimaryKey(orderId);
        int messageQueueNumber = Integer.parseInt(orderCenter.getSupplierId());

        //对应的顺序消息的生产者 把messageList 发出去
        orderlyProducer.sendOrderlyMessages(messageList, messageQueueNumber);
    }
}
