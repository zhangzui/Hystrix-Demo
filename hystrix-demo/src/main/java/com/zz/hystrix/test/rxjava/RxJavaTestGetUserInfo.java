package com.zz.hystrix.test.rxjava;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

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
public class RxJavaTestGetUserInfo {

    public static void main(String[] args) {
        test001();
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
        }).flatMap(new Func1<List<String>, Observable<String>>() {
            @Override
            public Observable<String> call(List<String> users) {
                return Observable.from(users);
            }
        }).filter(new Func1<String,Boolean>(){
            @Override
            public Boolean call(String userName) {
                return userName.equals("AAA");
            }
        }).map(new Func1<String,String>(){
            @Override
            public String call(String userName) {
                return userName+"222";
            }
        }).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return "xxxxxx";
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });
    }
}
