package com.yibo.storecenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author: huangyibo
 * @Date: 2019/11/26 19:42
 * @Description:
 */

@SpringBootApplication
@MapperScan("com.yibo.storecenter.mapper")
public class StoreCenterApplication {

    public static void main(String[] args) {

        SpringApplication.run(StoreCenterApplication.class,args);
    }
}
