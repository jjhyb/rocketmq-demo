package com.yibo.storecenter;

import java.util.Date;

/**
 * @author: huangyibo
 * @Date: 2019/11/28 0:01
 * @Description:
 */
public interface StoreServiceApi {

    /**
     * 查询库存的version
     * @param supplierId
     * @param goodsId
     * @return
     */
    int selectVersion(String supplierId,String goodsId);

    /**
     * 查询库存
     * @param supplierId
     * @param goodsId
     * @return
     */
    int selectStoreCount(String supplierId,String goodsId);

    /**
     * 更新库存
     * @param supplierId
     * @param goodsId
     * @param count
     * @param version
     * @param user
     * @param updateTime
     * @return
     */
    int updateStoreCountByVersion(String supplierId,String goodsId,int count,int version,String user,Date updateTime);


    /**
     * 回滚库存
     * @param supplierId
     * @param goodsId
     * @param count
     * @return
     */
    int rollbackStoreCountAndVersion(String supplierId,String goodsId,int count,String user,Date updateTime);
}
