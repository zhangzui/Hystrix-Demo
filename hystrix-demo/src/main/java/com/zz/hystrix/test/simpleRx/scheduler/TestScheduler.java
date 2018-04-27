package com.zz.hystrix.test.simpleRx.scheduler;

import com.zz.hystrix.test.simpleRx.MyObservable;
import com.zz.hystrix.test.simpleRx.MySubscriber;

/**
 * @author zhangzuizui
 * @date 2018/4/27 17:49
 */
public class TestScheduler {

    public static void main(String[] args) {
        MyObservable.create(new MyObservable.OnSubscribe() {
            @Override
            public void call(MySubscriber subscriber) {
                subscriber.process("sasa");
                subscriber.start("zzz");
                subscriber.completed("zzz");
            }
        }).extendStreams(new MyObservable.Transformer<String,String>(){

            @Override
            public String call(String from) {
                return from+"xxxxxx";
            }
        }).subscribeOn(SchedulerFactory.io()).extendStreams1(SchedulerFactory.io())
                .subscribe(new MySubscriber<String>() {
            @Override
            public void completed(String var) {
                System.out.println("SchedulerFactory,shutdown==="+var);
                SchedulerFactory.shutdown();
            }

            @Override
            public void error(Throwable var) {
                System.out.println("error===");
                SchedulerFactory.shutdown();
            }

            @Override
            public void start(String var) {
                System.out.println("ThreadID=== "+Thread.currentThread().getName());
                System.out.println("start==="+var);
            }
        });
    }
}
