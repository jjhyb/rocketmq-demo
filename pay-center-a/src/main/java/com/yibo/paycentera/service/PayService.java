package com.yibo.paycentera.service;

/**
 * @author: huangyibo
 * @Date: 2019/11/29 14:36
 * @Description:
 */
public interface PayService {

    String payment(String userId,String orderId,String accountId,double money);
}
