package com.yibo.ordercenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author: huangyibo
 * @Date: 2019/11/26 16:16
 * @Description:
 */

@SpringBootApplication
@MapperScan("com.yibo.ordercenter.mapper")//扫描Mapper接口
public class OrderCenterApplication {

    public static void main(String[] args) {

        SpringApplication.run(OrderCenterApplication.class,args);
    }
}
