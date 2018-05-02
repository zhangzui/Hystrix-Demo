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
public class RxJavaBaseTest {

    public static void main(String[] args) {

        /** 1.基本数据发射模式
         * 发射完成,这种方法需要手动调用onCompleted，才会回调Observer的onCompleted方法
         */
        Observable<String> sender = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                //发送数据
                subscriber.onNext("{key:value}");
                subscriber.onCompleted();
            }
        });
        Observer receiver = new Observer() {
            @Override
            public void onCompleted() {
                System.out.println("after completed do something");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("throw throwable!!!");
            }

            @Override
            public void onNext(Object o) {
                System.out.println("get sender's data and do something!The request data is :"+o);
            }
        };

        sender.subscribe(receiver);
    }
}
