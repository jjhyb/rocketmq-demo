package com.yibo.storecenter.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: huangyibo
 * @Date: 2019/11/26 19:45
 * @Description:
 */

@RestController
public class IndexController {

    @GetMapping("/index")
    public String index(){

        return "index";
    }
}
