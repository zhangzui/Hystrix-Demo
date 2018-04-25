package com.zz.hystrix.test.rxjava;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func0;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Observable：发射源，英文释义“可观察的”，在观察者模式中称为“被观察者”或“可观察对象”；
 * Observer：接收源，英文释义“观察者”，没错！就是观察者模式中的“观察者”，可接收Observable、Subject发射的数据；
 * Subscriber：“订阅者”，也是接收源
 * Subject：Subject是一个比较特殊的对象，既可充当发射源，也可充当接收源
 * Action0：RxJava中的一个接口，它只有一个无参call（）方法，且无返回值，同样还有Action1，Action2...Action9等，Action1封装了含有* 1 个参的call（）方法，即call（T t），Action2封装了含有 2 *个参数的call方法，即call（T1 t1，T2 t2），以此类推；
 * Func0：与Action0非常相似，也有call（）方法，但是它是有返回值的，同样也有Func0、Func1...Func9;
 */
public class RxJavaTest {

    public static void main(String[] args) {

        //1.基本数据发射模式
        Observable<String> sender = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                //发送数据"Hi，Weavey！"
                subscriber.onNext("Hi，Weavey！");
                subscriber.onNext("create1"); //发射一个"create1"的String
                subscriber.onNext("create2"); //发射一个"create2"的String
                subscriber.onCompleted();//发射完成,这种方法需要手动调用onCompleted，才会回调Observer的onCompleted方法
            }
        });
        //2.just方法依次发送
        Observable justObservable = Observable.just("just1","just2");//依次发送"just1"和"just2"

        /**
         * 3.from方式
         * 注意，just()方法也可以传list，但是发送的是整个list对象，而from（）发送的是list的一个item** /
         */
        List<String> list = new ArrayList<>();
        list.add("from1");
        list.add("from2");
        list.add("from3");
        Observable fromObservable = Observable.from(list);  //遍历list 每次发送一个

        //4.defer()
        Observable deferObservable = Observable.defer(new Func0<Observable<String>>() {

            //注意此处的call方法没有Subscriber参数
            @Override
            public Observable<String> call() {
                return Observable.just("deferObservable");
            }});

        //5.interval定时
        Observable intervalObservable = Observable.interval(1, TimeUnit.SECONDS);//每隔一秒发送一次
        //数据接收
        Observer<String> receiver = new Observer<String>() {

            @Override
            public void onCompleted() {
                //数据接收完成时调用
                System.out.println("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                //发生错误调用
            }

            @Override
            public void onNext(String s) {
                //正常接收数据调用，将接收到来自sender的问候"Hi，Weavey！"
                System.out.println(s);
            }
        };


        sender.subscribe(receiver);
        justObservable.subscribe(receiver);
        fromObservable.subscribe(receiver);
        deferObservable.subscribe(receiver);
        intervalObservable.subscribe(receiver);

        synchronized (RxJavaTest.class) {
            while (true) {
                try {
                    RxJavaTest.class.wait();
                } catch (InterruptedException e) {
                }
            }
        }


    }
}
