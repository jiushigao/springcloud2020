package com.learn.springcloud.controller;

import com.learn.springcloud.service.PaymentService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@DefaultProperties(defaultFallback = "patmentGlobalHandler")//hystrix属性全局配置
public class OrderHystrixController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/consumer/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id){
        String s = paymentService.paymentInfo_OK(id);
        return s;
    }

    /**
     * hystrix熔断器在消费方和服务提供方都可以配,一般配在消费方
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod="paymentInfo_TimeoutHandler",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "1500")//超时时间
    })
    @GetMapping("/consumer/hystrix/timeout/{id}")
    public String paymentInfo_Timeout(@PathVariable("id") Integer id){
//        int i = 10/0;
        String s = paymentService.paymentInfo_Timeout(id);
        return s;
    }

    /**
     * fallback
     * @param id
     * @return
     */
    public String paymentInfo_TimeoutHandler(Integer id){
        return "我是消费者80，对方支付系统繁忙，请稍后重试o(╥﹏╥)o";
    }

    /**
     * 全局异常处理
     * @return
     */
    public String patmentGlobalHandler(){
        return "全局异常处理，请稍后重试";
    }
}
