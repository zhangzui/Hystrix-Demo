package com.zz.hystrix.test.simpleRx;


/**
 * @author zhangzuizui
 * @date 2018/4/27 14:43
 */
public class TestSimpleDemo {

    public static void main(String[] args) {
        test002();
    }
    public static void test001(){
        MyObservable.create(new MyObservable.OnSubscribe<String>() {

            @Override
            public void call(MySubscriber<? super String> subscriber) {
                try {
                    subscriber.process("key");
                    subscriber.start("key");
                    subscriber.completed("key");
                } catch (Exception e) {
                    subscriber.error(e);
                }
            }
        }).subscribe(new MySubscriber<String>() {

            @Override
            public void completed(String var) {
                System.out.println("completed");
            }

            @Override
            public void error(Throwable var) {
                System.out.println("start===");
            }

            @Override
            public void start(String var) {
                System.out.println("start==="+var);
            }
        });
    }
    public static void test002(){
        MyObservable.create(new MyObservable.OnSubscribe<String>() {

            @Override
            public void call(MySubscriber<? super String> subscriber) {
                try {
                    subscriber.process("key");
                    subscriber.start("key");
                    subscriber.completed("key");
                } catch (Exception e) {
                    subscriber.error(e);
                }
            }
        }).extendStreams(new MyObservable.Transformer<String,String>() {
            @Override
            public String call(String from) {
                return "Transformer111="+from;
            }
        }).extendStreams(new MyObservable.Transformer<String,String>() {
            @Override
            public String call(String from) {
                return "Transformer2222="+from;
            }
        }).subscribe(new MySubscriber<String>() {
            @Override
            public void completed(String var) {
                System.out.println("completed");
            }

            @Override
            public void error(Throwable var) {
                System.out.println("start===");
            }

            @Override
            public void start(String var) {
                System.out.println("start==="+var);
            }
        });
    }
}
