package com.learn.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * feign消费方添加hystrix：
 * 1.pom文件映入hystrix依赖
 * 2.yml添加feign.hystrix.enabled: true
 * 3.启动类添加@EnableHystrix
 * 4.在需要添加hystrix的方法上加@HystrixCommand并配置fallbackMethod等属性并添加fallback方法，
 * hystrix属性可以在类上添加@DefaultProperties用于公共属性的配置
 * 5.也可以在feignclient上添加fallback属性，用于配置fallback处理类，处理类需要实现feign客户端的接口，
 * 这样发生异常时会调用处理类中对应的方法用于返回
 */
@SpringBootApplication
@EnableFeignClients
@EnableHystrix
public class OrderFeignHystrixMain80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderFeignHystrixMain80.class,args);
    }
}
