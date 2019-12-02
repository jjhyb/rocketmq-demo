package com.yibo.storecenter.service.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.yibo.storecenter.StoreServiceApi;
import com.yibo.storecenter.mapper.StoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author: huangyibo
 * @Date: 2019/11/28 0:02
 * @Description:
 */

@Service(version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}")
@Slf4j
public class StoreServiceProvider implements StoreServiceApi {

    @Autowired
    private StoreMapper storeMapper;

    @Override
    public int selectVersion(String supplierId, String goodsId) {

        return storeMapper.selectVersion(supplierId,goodsId);
    }

    @Override
    public int selectStoreCount(String supplierId, String goodsId) {

        return storeMapper.selectStoreCount(supplierId,goodsId);
    }

    @Override
    public int updateStoreCountByVersion(String supplierId, String goodsId, int count, int version, String user, Date updateTime) {

        return storeMapper.updateStoreCountByVersion(supplierId,goodsId,count,version,user,updateTime);
    }

    @Override
    public int rollbackStoreCountAndVersion(String supplierId,String goodsId,int count,String user,Date updateTime) {

        return storeMapper.rollbackStoreCountAndVersion(supplierId,goodsId,count,user,updateTime);
    }
}
