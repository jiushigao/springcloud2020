package com.learn.springcloud.service;

import cn.hutool.core.util.IdUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @HystrixProperty 中可配置的属性可在HystrixCommandProperties类中查看
 */
@Service
public class PaymentService {

    //===========服务降级fallback
    /**
     * 正常访问
     * @param id
     * @return
     */
    public String paymentInfo_OK(Integer id){
        return "当前线程："+Thread.currentThread().getName()+"   paymentInfo_OK,id:"+id+"\t"+"<{=．．．．(嘎~嘎~嘎~)";
    }

    /**
     * 超时
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod="paymentInfo_TimeoutHandler",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "3000")//超过3秒等待算超时
    })
    public String paymentInfo_Timeout(Integer id){
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        int i = 10/0;//超时或报错都会调用fallback
        return "当前线程："+Thread.currentThread().getName()+"   paymentInfo_Timeout,id:"+id+"\t"+"<{=．．．．(嘎~嘎~嘎~)";
    }

    /**
     * fallback
     * @param id
     * @return
     */
    public String paymentInfo_TimeoutHandler(Integer id){
        return "当前线程："+Thread.currentThread().getName()+"   paymentInfo_TimeoutHandler,id:"+id+"\t"+"o(╥﹏╥)o";
    }

    //========服务熔断

    /**
     * 时间窗口期内，10次以上请求中失败率超过60%则开启熔断器（时间窗口期、次数、比例见配置）
     * 开启熔断器后，只要求有请求过来直接执行fallback方法，等过了时间窗口期后会让下一次请求通过，此时熔断器就处于半开状态，
     * 若这个请求成功则熔断器关闭，若请求失败则熔断器开启，之后需要再等待一个时间窗口期后进行尝试直到熔断器关闭
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),//是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),//请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),//时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60")//失败率达到60%开启熔断
    })
    public String paymentCircuitBreaker(Integer id){
        System.out.println("*******paymentCircuitBreaker***id:"+id);
        if(id<0){
            throw  new RuntimeException("*****id 不能为负数");
        }
        return Thread.currentThread().getName()+"调用成功，流水号："+ IdUtil.simpleUUID();
    }

    public String paymentCircuitBreaker_fallback(Integer id){
        return "id 不能为负数，id:"+id;
    }
}
