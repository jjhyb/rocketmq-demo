package com.yibo.paycenterb.domain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "platform_account")
public class PlatformAccount {
    /**
     * 账户id
     */
    @Id
    @Column(name = "account_id")
    private String accountId;

    /**
     * 银行账户
     */
    @Column(name = "account_no")
    private String accountNo;

    /**
     * 余额日期
     */
    @Column(name = "date_time")
    private Date dateTime;

    /**
     * 当前余额
     */
    @Column(name = "current_balance")
    private BigDecimal currentBalance;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;
}