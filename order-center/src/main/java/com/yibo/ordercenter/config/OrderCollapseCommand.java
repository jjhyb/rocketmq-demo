package com.yibo.ordercenter.config;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;
import com.yibo.ordercenter.domain.entity.Order;
import com.yibo.ordercenter.service.HelloService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: huangyibo
 * @Date: 2019/11/28 0:15
 * @Description:
 *
 * 创建OrderCollapseCommand继承自HystrixCollapser来实现请求合并
 */
public class OrderCollapseCommand extends HystrixCollapser<List<Order>, Order, String> {

    private HelloService helloService;
    private String id;

    /**
     * 构造方法中，我们设置了请求时间窗为100ms，即请求时间间隔在100ms之内的请求会被合并为一个请求
     * @param helloService 调用的服务
     * @param id 单次需要传递的业务id
     */
    public OrderCollapseCommand(HelloService helloService, String id) {
        super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("bookCollapseCommand")).andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(100)));
        this.helloService = helloService;
        this.id = id;
    }

    @Override
    public String getRequestArgument() {
        return id;
    }

    /**
     * createCommand方法主要用来合并请求，在这里获取到各个单个请求的id，将这些单个的id放到一个集合中，
     * 然后再创建出一个OrderBatchCommand对象，用该对象去发起一个批量请求
     * @param collection
     * @return
     */
    @Override
    protected HystrixCommand<List<Order>> createCommand(Collection<CollapsedRequest<Order, String>> collection) {
        List<String> ids = new ArrayList<>(collection.size());
        ids.addAll(collection.stream().map(CollapsedRequest::getArgument).collect(Collectors.toList()));
        OrderBatchCommand orderBatchCommand = new OrderBatchCommand(ids, helloService);
        return orderBatchCommand;
    }

    /**
     * mapResponseToRequests方法主要用来为每个请求设置请求结果。
     * 该方法的第一个参数batchResponse表示批处理请求的结果，第二个参数collapsedRequests则代表了每一个被合并的请求，
     * 然后我们通过遍历batchResponse来为collapsedRequests设置请求结果
     * @param orders 这里是批处理后返回的结果集
     * @param collection 所有被合并的请求
     */
    @Override
    protected void mapResponseToRequests(List<Order> orders, Collection<CollapsedRequest<Order, String>> collection) {
        int count = 0;
        for (CollapsedRequest<Order, String> collapsedRequest : collection) {
            //从批响应集合中按顺序取出结果
            Order order = orders.get(count++);
            //将结果放回原Request的响应体内
            collapsedRequest.setResponse(order);
        }
    }
}
