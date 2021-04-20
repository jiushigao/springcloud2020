package com.learn.springcloud.lb;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyLB implements LoadBalancer {
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public final int getAndIncrement(){
        int current;
        int next;
        /**
         * 此处用了自旋锁，current为atomicInteger当前的value值,next为下一次要用的值
         * 多线程的情况下，可能会出现我得到当前值current到我要给atomicInteger赋新值的
         * 这段时间内，atomicInteger的value值可能已经被其他线程给改了，于是使用compareAndSet对其进行赋值，
         * 若atomicInteger当前的value值和之前获取的current值相同则说明没有被改过，则将value值赋值为next并返回true，
         * 否则不赋值返回false，自旋锁会再次循环重新获取当前值current，直到compareAndSet返回为true，
         * 这样可以保证各个线程更新value值前这个值没有被修改过，并且不需要加锁，加锁阻塞时会有上下文切换，短时间的自旋会比上下文切换性能要好
         * atomicInteger里面的value值被volatile修饰，这个值赋值时没有运算的话是线程安全的（好像是这样）
         */
        do{
            current = this.atomicInteger.get();
            next = current>=Integer.MAX_VALUE?0:current+1;
        }while (!this.atomicInteger.compareAndSet(current,next));
        System.out.println("********第几次访问，次数next:"+next);
        return next;
    }

    @Override
    public ServiceInstance instances(List<ServiceInstance> instances) {
        int index = getAndIncrement() % instances.size();
        return instances.get(index);
    }
}
