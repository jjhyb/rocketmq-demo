package com.yibo.packagecenter.domain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "logistic_package")
public class LogisticPackage {
    @Id
    @Column(name = "package_id")
    private String packageId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "address_id")
    private String addressId;

    private String remark;

    @Column(name = "package_status")
    private String packageStatus;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}