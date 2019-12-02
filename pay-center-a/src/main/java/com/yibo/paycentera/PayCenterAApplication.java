package com.yibo.paycentera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author: huangyibo
 * @Date: 2019/11/27 16:30
 * @Description:
 */

@SpringBootApplication
@MapperScan("com.yibo.paycentera.mapper")//扫描Mapper接口
public class PayCenterAApplication {

    public static void main(String[] args) {

        SpringApplication.run(PayCenterAApplication.class,args);
    }
}
