package com.yibo.ordercenter.constants;

/**
 * @author: huangyibo
 * @Date: 2019/11/28 17:41
 * @Description:
 */

public enum ErrorCode {

    OUT_OF_STOCK(502,"库存不足"),

    LOCK_TAKE_EFFECT(503,"乐观锁生效，订单生成失败");

    private Integer code;
    private String message;

    // 构造方法
    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
