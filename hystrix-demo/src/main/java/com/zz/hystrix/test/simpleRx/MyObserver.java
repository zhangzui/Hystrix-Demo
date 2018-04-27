package com.zz.hystrix.test.simpleRx;

/**
 * @author zhangzuizui
 * @date 2018/4/27 14:33
 * 观察者
 */
public interface MyObserver<T> {
    void completed(T var);
    void error(Throwable var);
    void start(T var);
}
