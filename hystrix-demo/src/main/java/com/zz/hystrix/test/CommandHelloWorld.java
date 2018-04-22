package com.zz.hystrix.test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import rx.Observable;

import java.util.concurrent.Future;

public class CommandHelloWorld extends HystrixCommand {
    private final String name;


    public CommandHelloWorld(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")); //必须
        this.name = name;
    }

    @Override
    protected String run() {
        /*
         网络调用 或者其他一些业务逻辑，可能会超时或者抛异常
        */
        return "Hello " + name + "!";
    }

    public static void main(String[] args) {
        String s = (String) new CommandHelloWorld("Bob").execute();
        Future<String> s0 = new CommandHelloWorld("Bob").queue();
        Observable<String> s1 = new CommandHelloWorld("Bob").observe();
        Observable<String> s2 = new CommandHelloWorld("Bob").toObservable();
    }
}
