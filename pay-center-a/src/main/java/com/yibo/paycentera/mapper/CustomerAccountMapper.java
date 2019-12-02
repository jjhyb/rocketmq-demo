package com.yibo.paycentera.mapper;

import com.yibo.paycentera.domain.entity.CustomerAccount;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Date;

public interface CustomerAccountMapper extends Mapper<CustomerAccount> {

    int updateBalance(@Param("accountId") String accountId, @Param("newBalance")BigDecimal newBalance,
                      @Param("currentVersion")int currentVersion, @Param("updateTime")Date currentTime);
}