package com.yibo.storecenter.domain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "store")
public class Store {
    /**
     * 库存id
     */
    @Id
    @Column(name = "store_id")
    private String storeId;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * 供应商id
     */
    @Column(name = "supplier_id")
    private String supplierId;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 库存数量
     */
    @Column(name = "store_count")
    private Integer storeCount;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新人
     */
    private String modifier;

    /**
     * 更新时间
     */
    @Column(name = "modif_time")
    private Date modifTime;
}