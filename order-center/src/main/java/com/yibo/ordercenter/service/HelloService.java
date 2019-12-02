package com.yibo.ordercenter.service;

import com.yibo.ordercenter.domain.entity.Order;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author: huangyibo
 * @Date: 2019/11/27 23:56
 * @Description:
 */
public interface HelloService {

    public Future<Order> findById(String id);

    public List<Order> batchQuery( List<String> ids);
}
