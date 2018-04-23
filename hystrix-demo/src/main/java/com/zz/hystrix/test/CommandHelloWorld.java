package com.zz.hystrix.test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import rx.Observable;

import java.util.concurrent.Future;

/**
 * @author zhangzuizui
 */
public class CommandHelloWorld extends HystrixCommand {
    private final String name;


    public CommandHelloWorld(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
    }

    @Override
    protected String run() {
        /**
         * 网络调用 或者其他一些业务逻辑，可能会超时或者抛异常
         */
        return "Hello " + name + "!";
    }
}
