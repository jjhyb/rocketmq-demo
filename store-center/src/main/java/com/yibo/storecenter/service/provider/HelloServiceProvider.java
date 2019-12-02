package com.yibo.storecenter.service.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.yibo.storecenter.HelloServiceApi;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * @author: huangyibo
 * @Date: 2019/11/26 21:52
 * @Description:
 */

@Service(version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}")
@Slf4j
public class HelloServiceProvider implements HelloServiceApi {

    @Override
    public String sayHello(String name) {
        log.info("----------name={}",name);
        return "hello " + name;
    }

    @Override
    public String findById(String id) {
        log.info("----------findById id={}",id);
        return "hello "+id;
    }

    @Override
    public List<String> findByIdBatch(String ids) {
        log.info("----------batchQuery ids={}",ids);
        List<String> list = new ArrayList<>();
        list.add("batch 1" + ids);
        list.add("batch 2" + ids);
        list.add("batch 3" + ids);
        return list;
    }

    @Override
    public List<String> batchQuery(List<String> ids) {
        log.info("----------batchQuery ids={}",ids);
        List<String> list = new ArrayList<>();
        list.add("batch 1" + ids);
        list.add("batch 2" + ids);
        list.add("batch 3" + ids);
        return list;
    }
}
