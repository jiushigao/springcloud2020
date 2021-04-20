package com.learn.springcloud.controller;

import com.learn.springcloud.entities.CommonResult;
import com.learn.springcloud.entities.Payment;
import com.learn.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class PaymentController {

    @Value("${server.port}")
    private String port;
    @Autowired
    PaymentService paymentService;

    @PostMapping("/payment/create")
    public CommonResult create(@RequestBody Payment payment){
        int result = paymentService.create(payment);
        log.info("****数据插入结果："+result);
        if(result>0){
            return new CommonResult(200,"数据插入成功！port:"+port,result);
        }else{
            return new CommonResult(444,"数据插入失败！");
        }
    }

    @GetMapping("/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("****数据查询结果："+payment);
        if(payment!=null){
            return new CommonResult(200,"数据查询成功！port:"+port,payment);
        }else{
            return new CommonResult(444,"没有对应记录，对于记录ID："+id);
        }
    }

    @GetMapping("/payment/lb")
    public String getPayment(){
        return port;
    }
}
