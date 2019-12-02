package com.yibo.packagecenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author: huangyibo
 * @Date: 2019/11/27 16:23
 * @Description:
 */

@SpringBootApplication
@MapperScan("com.yibo.packagecenter.mapper")
public class PackageCenterApplication {

    public static void main(String[] args) {

        SpringApplication.run(PackageCenterApplication.class,args);
    }
}
