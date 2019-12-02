package com.yibo.ordercenter.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.yibo.ordercenter.config.OrderCollapseCommand;
import com.yibo.ordercenter.domain.entity.Order;
import com.yibo.ordercenter.service.HelloService;
import com.yibo.storecenter.HelloServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author: huangyibo
 * @Date: 2019/11/26 21:59
 * @Description:
 */

@RestController
@Slf4j
public class HelloController {

    @Reference(version = "1.0.0",
            application = "${dubbo.application.id}",
            check = false,
            timeout = 3000,
            retries = 0//默认值2不包含第一次，retries=0 禁用超时重试，一般读请求，允许重试三次,写请求下游接口没做幂等就不要重试
    )
    private HelloServiceApi helloServiceApi;

    @Autowired
    private HelloService helloService;

    @GetMapping("/hello")
    public String hello(@RequestParam("name") String name){
        log.info("name={}",name);
        return helloServiceApi.sayHello(name);
    }

    @GetMapping("/find/{id}")
    public Order findOrder(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        OrderCollapseCommand orderCollapseCommand = new OrderCollapseCommand(helloService, id);
        Future<Order> future = orderCollapseCommand.queue();
        return future.get();
    }

    @GetMapping("/get/{id}")
    public Order getOrder(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        Future<Order> future = helloService.findById(id);
        return future.get();
    }

}
