package com.yibo.storecenter;

import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/11/26 21:44
 * @Description:
 */
public interface HelloServiceApi {

    String sayHello(String name);

    public String findById(String id);

    public List<String> findByIdBatch(String ids);

    public List<String> batchQuery(List<String> ids);
}
