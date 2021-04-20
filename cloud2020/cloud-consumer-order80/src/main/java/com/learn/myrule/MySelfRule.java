package com.learn.myrule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ribbon自带的常用负载均衡策略：
 * IRule:根据特定算法从服务列表中选取一个要访问的服务
 *   com.netflix.loadbalancer.RoundRobinRule    轮询
 *
 *   com.netflix.loadbalancer.RandomRule        随机
 *
 *   com.netflix.loadbalancer.RetryRule         先按照RoundRobinRule的策略获取服务，如果获取服务失败则在指定时间内会进行重试
 *
 *   WeightedResponseTimeRule                   对RoundRobinRule的扩展，响应速度越快的实例选择权重越大，越容易被选择
 *
 *   BestAvailableRule                          会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务，然后选择一个并发量最小的服务
 *
 *   AvailabilityFilteringRule                  先过滤掉故障实例，再选择并发较小的实例
 *
 *   ZoneAvoidanceRule                          默认规则，复合判断server所在区域的性能和server的可用性选择服务器
 *
 *  添加在@ComponentScan注解外，防止这个配置类被所有的Ribbon客户端所共享，达不到特殊化定制的目的了
 */
@Configuration
public class MySelfRule {

    //替换默认的轮询负载均衡策略
    @Bean
    public IRule myrule(){
        return new RandomRule();
    }
}
