package com.yibo.ordercenter.config;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.yibo.ordercenter.domain.entity.Order;
import com.yibo.ordercenter.service.HelloService;

import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/11/28 0:07
 * @Description: 批量处理类，继承自HystrixCommand，用来处理合并之后的请求，在run方法中调用orderService中的batchQuery方法
 */
public class OrderBatchCommand extends HystrixCommand<List<Order>> {

    private List<String> ids;
    private HelloService helloService;

    protected OrderBatchCommand(List<String> ids,HelloService helloService) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CollapsingGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("CollapsingKey")));
        this.ids = ids;
        this.helloService = helloService;
    }

    /**
     * 调用批量处理的方法
     * @return
     * @throws Exception
     */
    @Override
    protected List<Order> run() throws Exception {
        return helloService.batchQuery(ids);
    }
}
