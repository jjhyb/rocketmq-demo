package com.yibo.ordercenter.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.yibo.ordercenter.domain.entity.Order;
import com.yibo.ordercenter.service.HelloService;
import com.yibo.storecenter.HelloServiceApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author: huangyibo
 * @Date: 2019/11/27 23:58
 * @Description:
 */

@Service
public class HelloServiceImpl implements HelloService {

    @Reference(version = "1.0.0",
            application = "${dubbo.application.id}",
            check = false,
            timeout = 3000,
            retries = 0//默认值2不包含第一次，retries=0 禁用超时重试，一般读请求，允许重试三次,写请求下游接口没做幂等就不要重试
    )
    private HelloServiceApi helloServiceApi;

    @HystrixCollapser(batchMethod = "findByIdBatch",
                        collapserProperties = {
                            @HystrixProperty(name ="timerDelayInMilliseconds",value = "100"),//单个请求的延迟时间
                            @HystrixProperty(name ="maxRequestsInBatch",value = "50"),//允许最大的合并请求数量
                            @HystrixProperty(name ="requestCache.enabled",value = "false")//是否允许开启请求的本地缓存
                        })
    @Override
    public Future<Order> findById(String id) {
        return null;
    }

    @HystrixCommand
    public List<Order> findByIdBatch(List<String> ids) {
        List<Order> orders = new ArrayList<>();

        List<String> list = helloServiceApi.findByIdBatch(StringUtils.join(ids,","));
        for (String storeId : list) {
            Order order = new Order();
            order.setGoodsId(storeId);
            orders.add(order);
        }
        return orders;
    }

    @Override
    public List<Order> batchQuery(List<String> ids) {
        List<Order> orders = new ArrayList<>();
        List<String> list = helloServiceApi.batchQuery(ids);
        for (String storeId : list) {
            Order order = new Order();
            order.setGoodsId(storeId);
            orders.add(order);
        }
        return orders;
    }
}
