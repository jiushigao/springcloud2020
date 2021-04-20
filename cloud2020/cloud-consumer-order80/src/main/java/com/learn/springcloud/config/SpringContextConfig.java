package com.learn.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpringContextConfig {

    @Bean
//    @LoadBalanced //赋予RestTemplate负载均衡能力
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
