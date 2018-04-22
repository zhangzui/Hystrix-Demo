package com.zz.hystrix.test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import java.util.concurrent.TimeUnit;

/**
 * 一个用户请求的成功执行，肯能依赖数十上百个外部服务，如果没有隔离，单个依赖的失败，可能会印象其他依赖的正常执行。
 * 所以为每个依赖配置了单独线程池
 * 线程池隔离的使用例子
 */
public class ThreadPollCommandTest extends HystrixCommand<String>{
    private final String name;

    public ThreadPollCommandTest(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))  //必须
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ExampleGroup-pool"))  //可选,默认 使用 this.getClass().getSimpleName();
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(4)));

        this.name = name;
    }


    @Override
    protected String run() throws Exception {
        System.out.println("running");
        TimeUnit.MILLISECONDS.sleep(1000);
        return "Hello " + name + "!";
    }
}
