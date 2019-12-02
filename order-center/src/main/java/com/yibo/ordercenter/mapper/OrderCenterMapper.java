package com.yibo.ordercenter.mapper;

import com.yibo.ordercenter.domain.entity.OrderCenter;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

public interface OrderCenterMapper extends Mapper<OrderCenter> {

    int updateOrderStatus(@Param("orderId")String orderId, @Param("orderStatus")String orderStatus,
                          @Param("updateBy")String updateBy, @Param("updateTime")Date updateTime);

}