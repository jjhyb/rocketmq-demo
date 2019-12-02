package com.yibo.paycentera.domain.entity;

import java.util.Date;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table(name = "broker_message_log")
public class BrokerMessageLog {
    /**
     * 消息id
     */
    @Id
    @Column(name = "message_id")
    private String messageId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 重试次数
     */
    @Column(name = "try_count")
    private Integer tryCount;

    /**
     * 消息状态
     */
    private String status;

    /**
     * 下次重试时间
     */
    @Column(name = "next_retry")
    private Date nextRetry;

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