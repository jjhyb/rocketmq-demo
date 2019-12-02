package com.yibo.ordercenter.domain.result;

/**
 * @author: huangyibo
 * @Date: 2019/11/28 18:01
 * @Description:
 */
public class ServiceResult {

    // 业务响应码
    private Integer status;

    // 返回信息描述
    private String msg;

    // 响应中的数据
    private Object data;
}
