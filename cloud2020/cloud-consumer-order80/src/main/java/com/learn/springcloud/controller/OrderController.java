package com.learn.springcloud.controller;

import com.learn.myrule.MySelfRule;
import com.learn.springcloud.entities.CommonResult;
import com.learn.springcloud.entities.Payment;
import com.learn.springcloud.lb.LoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLSessionContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RibbonClient(name = "CLOUD-PAYMENT-SERVICE",configuration = MySelfRule.class)//可以加在Controller上，方便定制化
@RestController
@Slf4j
public class OrderController {

//    private static final String PAYMENT_URL = "http://localhost:8001";
    /**
     * restTemplate加上@LoadBalaned注解后被赋予负载均衡能力，可以通过服务名调用服务
     */
    private static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";

    @Autowired
    DiscoveryClient discoveryClient;
    
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LoadBalancer loadBalancer;

    private Integer servIndex = 0;

    @GetMapping("/consumer/payment/create")
    public CommonResult<Payment> create(Payment payment){
        return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment,CommonResult.class);
    }

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id){
        return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id,CommonResult.class);
    }

    @GetMapping("/consumer/payment/getForEntity/{id}")
    public CommonResult<Payment> getPayment2(@PathVariable("id") Long id){
        //getForEntity可以获取一些详细信息，header消息头等
        ResponseEntity<CommonResult> forEntity = restTemplate.getForEntity(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
        if(forEntity.getStatusCode().is2xxSuccessful()){
            return forEntity.getBody();
        }else{
            return new CommonResult<>(444,"操作失败");
        }
    }

    //乞丐版手写轮询负载均衡
    @GetMapping("/consumer/myLoadBalanced")
    public String myLoadBalanced(){
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        int servAllNum = instances.size();
        if(servAllNum==0){
            return null;
        }
        ServiceInstance serviceInstance = null;
        synchronized (servIndex){
            serviceInstance = instances.get(servIndex%servAllNum);
            servIndex++;
        }
        if(serviceInstance==null){
            return "没找到服务！";
        }
        System.out.println(serviceInstance.getUri());
        System.out.println(serviceInstance.getHost()+":"+serviceInstance.getPort());
        String forObject = restTemplate.getForObject("http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/payment/lb", String.class);
        return forObject;
    }

    //高级版手写轮询负载均衡
    @GetMapping("/consumer/payment/lb")
    public String getPaymentLB(){
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        if(instances==null||instances.size()<=0){
            return null;
        }

        ServiceInstance serviceInstance = loadBalancer.instances(instances);

        String forObject = restTemplate.getForObject(serviceInstance.getUri() + "/payment/lb", String.class);
        return forObject;
    }

}
