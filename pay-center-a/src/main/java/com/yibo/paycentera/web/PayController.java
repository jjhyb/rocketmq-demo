package com.yibo.paycentera.web;

import com.yibo.paycentera.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: huangyibo
 * @Date: 2019/11/29 14:35
 * @Description:
 */

@RestController
//@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayService payService;

    @PostMapping("/pay")
    public String pay(@RequestParam("userId") String userId, @RequestParam("orderId")String orderId,
                      @RequestParam("accountId")String accountId,@RequestParam("money") Double money){
        return payService.payment(userId,orderId,accountId,money);
    }
}
