package com.yibo.ordercenter.service;

import com.yibo.ordercenter.domain.entity.OrderCenter;
import com.yibo.ordercenter.domain.result.ResponseResult;

/**
 * @author: huangyibo
 * @Date: 2019/11/28 15:27
 * @Description:
 */
public interface OrderCenterService {

    public ResponseResult createOrder(OrderCenter orderCenter);

    void sendOrderlyMessage4Pkg(String userId, String orderId);
}
