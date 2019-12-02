package com.yibo.ordercenter.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.yibo.ordercenter.constants.ErrorCode;
import com.yibo.ordercenter.domain.entity.Order;
import com.yibo.ordercenter.domain.entity.OrderCenter;
import com.yibo.ordercenter.domain.result.ResponseResult;
import com.yibo.ordercenter.service.OrderCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: huangyibo
 * @Date: 2019/11/27 17:00
 * @Description:
 */

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderCenterService orderCenterService;

    @PostMapping("/create")
    public ResponseResult createOrder(@RequestBody OrderCenter orderCenter){
        ResponseResult responseResult = orderCenterService.createOrder(orderCenter);
        //如果扣库存导致订单生成失败，那么重试一次
        if(responseResult.getStatus() == ErrorCode.LOCK_TAKE_EFFECT.getCode()){
            responseResult = orderCenterService.createOrder(orderCenter);
        }
        return responseResult;
    }


    /**
     * 限流策略：线程池方式
     * @param order
     * @return
     */
    @PostMapping("/add")
    @HystrixCommand(commandKey = "/orders/add",
                    commandProperties = {
                    @HystrixProperty(name="execution.isolation.strategy",value="THREAD")
                    },
                    threadPoolKey = "addOrderThreadPool",
                    threadPoolProperties = {
                        @HystrixProperty(name="coreSize",value="10"),
                        @HystrixProperty(name="maxQueueSize",value="2000"),
                        @HystrixProperty(name="queueSizeRejectionThreshold",value="30")
                    },
                    fallbackMethod = "addOrderFallbackMethodThread")
    public String addOrder(@RequestBody Order order){

        return "success";
    }

    /**
     *  限流策略：线程池方式 addOrder限流降级
     * @param order
     * @return
     */
    public String addOrderFallbackMethodThread(Order order){
        log.info("--------线程池限流降级策略执行--------");
        return "Hystrix threadPool";
    }

    /**
     * 限流策略：信号量方式
     * @param order
     * @return
     */
    @PostMapping("/insert")
    @HystrixCommand(commandKey = "/orders/insert",
                    commandProperties = {
                        @HystrixProperty(name="execution.isolation.strategy",value="SEMAPHORE"),
                        @HystrixProperty(name="execution.isolation.semaphore.maxConcurrentRequests",value="3"),
                    },
                    fallbackMethod = "insertOrderFallbackMethodSemaphore")
    public String insertOrder(@RequestBody Order order){

        return "success";
    }

    /**
     * 限流策略：信号量方式 insertOrder限流降级
     * @param order
     * @return
     */
    public String insertOrderFallbackMethodSemaphore( Order order){
        log.info("--------信号量限流降级策略执行--------");
        return "Hystrix semaphore";
    }


    /**
     * 超时降级策略
     * @param order
     * @return
     */
    @PostMapping("/build")
    @HystrixCommand(commandKey = "/orders/build",
            commandProperties = {
                    @HystrixProperty(name="execution.timeout.enabled",value="true"),
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="3000")
            },
            fallbackMethod = "buildOrderFallbackMethodTimeout")
    public String buildOrder(@RequestBody Order order){

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * 超时降级策略 buildOrder超时降级
     * @param order
     * @return
     */
    public String buildOrderFallbackMethodTimeout(Order order){
        log.info("--------超时降级策略执行--------");
        return "Hystrix time out";
    }


}
