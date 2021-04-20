package com.learn.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 加了@RefreshScope此注解后会刷新配置，此外还需要在yml文件中暴露端点，
 * 之后需要发送POST请求触发刷新"http://localhost:3355/actuator/refresh"，无需重启项目
 * 但是这个请求只能刷新这一个服务，若要刷新相同微服务名称的其他服务则要使用bus刷新
 */
@RestController
@RefreshScope
public class MyController {
    @Value("${config.info}")
    String info;

    @GetMapping("/getInfo")
    public String getInfo(){
        return info;
    }
}
