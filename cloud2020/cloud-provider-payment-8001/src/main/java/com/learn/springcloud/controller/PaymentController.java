package com.learn.springcloud.controller;

import com.learn.springcloud.entities.CommonResult;
import com.learn.springcloud.entities.Payment;
import com.learn.springcloud.service.PaymentService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class PaymentController {

    @Value("${server.port}")
    private String port;
    @Autowired
    PaymentService paymentService;

    @Resource
    DiscoveryClient discoveryClient;

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

    @GetMapping("/payment/discovery")
    public Object discovery(){
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            System.out.println("****servcer:"+service);
        }

        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances) {
            System.out.println(instance.getHost()+","+instance.getPort()+","+instance.getUri()+","+instance.getInstanceId());
        }

        return this.discoveryClient;
    }

    @GetMapping("/payment/lb")
    public String getPayment(){
        return port;
    }

    @GetMapping("/payment/feign/timeout")
    public String paymentFeignTimout(){
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return port;
    }
}
