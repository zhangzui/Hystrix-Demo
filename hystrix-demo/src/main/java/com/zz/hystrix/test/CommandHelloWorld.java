package com.zz.hystrix.test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * @author zhangzuizui
 */
public class CommandHelloWorld extends HystrixCommand {
    private final String name;


    public CommandHelloWorld(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(500000))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerRequestVolumeThreshold(10)));
        this.name = name;
    }

    /**
     * 网络调用 或者其他一些业务逻辑，可能会超时或者抛异常
     */
    @Override
    protected String run() {
        System.out.println("sss");
        return "Hello " + name + "!";
    }
}
