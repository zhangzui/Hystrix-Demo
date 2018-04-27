package com.zz.hystrix.test.simpleRx;


/**
 * @author zhangzuizui
 * @date 2018/4/27 14:35
 * 抽象的观察者，只专注干一件事process
 */
public abstract class MySubscriber<T> implements MyObserver<T>{

    public void process(String key){
        System.out.println(key);
    }
}
