package com.zz.hystrix.test;

import org.junit.Test;
import rx.Observable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author zhangzuizui
 * @date 2018/4/23 10:42
 */
public class CommandTest {


    @Test
    public void testCommandHelloWorld() {
        //同步执行
        String execute = (String) new CommandHelloWorld("Bob").execute();
        System.out.println("execute======="+execute);

        //异步执行
        try {
            Future<String> queue = new CommandHelloWorld("Bob").queue();
            System.out.println("queue======="+queue.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //
        Observable<String> observe = new CommandHelloWorld("Bob").observe();
        System.out.println("observe======="+observe.toBlocking().single());

        Observable<String> toObservable = new CommandHelloWorld("Bob").toObservable();
        System.out.println("toObservable======="+toObservable.toString());
    }

}
