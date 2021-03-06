
package com.zz.hystrix.test.rxjava;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * Observable：发射源，英文释义“可观察的”，在观察者模式中称为“被观察者”或“可观察对象”；
 * Observer：接收源，英文释义“观察者”，没错！就是观察者模式中的“观察者”，可接收Observable、Subject发射的数据；
 * Subscriber：“订阅者”，也是接收源
 * Subject：Subject是一个比较特殊的对象，既可充当发射源，也可充当接收源
 * Action0：RxJava中的一个接口，它只有一个无参call（）方法，且无返回值，同样还有Action1，Action2...Action9等，Action1封装了含有* 1 个参的call（）方法，即call（T t），Action2封装了含有 2 *个参数的call方法，即call（T1 t1，T2 t2），以此类推；
 * Func0：与Action0非常相似，也有call（）方法，但是它是有返回值的，同样也有Func0、Func1...Func9;
 */
public class RxJavaTestAction {

    public static void main(String[] args) {

        /** 1.基本数据发射模式
         * 发射完成,这种方法需要手动调用onCompleted，才会回调Observer的onCompleted方法
         * 发射一个"create1"的String
         * 发射一个"create2"的String
         */
        Observable<String> sender = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //发送数据"Hi，Weavey！"
                subscriber.onNext("Hi，Weavey！");
                subscriber.onNext("create1");
                subscriber.onNext("create2");
                subscriber.onCompleted();
            }
        });

        //数据接收
        sender.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }});
    }
    public static void test001(){
        Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                List<String> userList = new ArrayList<String>();
                userList.add("AAA");
                userList.add("BBB");
                userList.add("CCC");
                subscriber.onNext(userList);
            }
        }).subscribe(new Action1<List<String>>() {
            @Override
            public void call(List<String> users) {
                for (String s : users){
                    System.out.println("user:"+s);
                }
            }
        });
    }
}
