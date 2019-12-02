package com.yibo.storecenter.mapper;

import com.yibo.storecenter.domain.entity.Store;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

public interface StoreMapper extends Mapper<Store> {

    /**
     * 查询库存的version
     * @param supplierId
     * @param goodsId
     * @return
     */
    int selectVersion(@Param("supplierId") String supplierId,@Param("goodsId") String goodsId);

    /**
     * 查询库存
     * @param supplierId
     * @param goodsId
     * @return
     */
    int selectStoreCount(@Param("supplierId")String supplierId, @Param("goodsId")String goodsId);

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
    int updateStoreCountByVersion(@Param("supplierId")String supplierId, @Param("goodsId")String goodsId,
                                  @Param("count")int count, @Param("version")int version,
                                  @Param("user")String user,@Param("updateTime") Date updateTime);

    /**
     * 更新库存
     * @param supplierId
     * @param goodsId
     * @param count
     * @return
     */
    int rollbackStoreCountAndVersion(@Param("supplierId")String supplierId, @Param("goodsId")String goodsId,
                                  @Param("count")int count,@Param("user")String user,@Param("updateTime") Date updateTime);
}