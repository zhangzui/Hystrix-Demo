package com.zz.hystrix.test;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * @author zhangzuizui
 * @date 2018/4/23 10:42
 */
public class CommandTest {


    @Test
    public void testThreadPollCommand() {
        System.out.println(new ThreadPollCommand("Reject").execute());
    }

    @Test
    public void testCommandHelloWorld() {
        //同步执行
        String execute = (String) new CommandHelloWorld("Bob").execute();
        System.out.println("execute=======" + execute);

    }

    @Test
    public void testCommandHelloWorld002() {
        //异步执行
        try {
            Future<String> queue = new CommandHelloWorld("Bob").queue();
            System.out.println("queue======="+queue.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Observable<String> observe = new CommandHelloWorld("Bob").observe();
        System.out.println("observe======="+observe.toBlocking().single());
        Observable<String> toObservable = new CommandHelloWorld("Bob").toObservable();
        System.out.println("toObservable======="+toObservable.toString());
    }

    @Test
    public void testObservable() throws Exception {

        Observable<String> fWorld = new CommandHelloWorld("World").observe();
        Observable<String> fBob = new CommandHelloWorld("Bob").observe();

        assertEquals("Hello World!", fWorld.toBlocking().single());
        assertEquals("Hello Bob!", fBob.toBlocking().single());

        fWorld.subscribe(new Observer<String>() {

            @Override
            public void onCompleted() {
                // nothing needed here
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(String v) {
                System.out.println("onNext: " + v);
            }

        });

        fBob.subscribe(new Action1<String>() {

            @Override
            public void call(String v) {
                System.out.println("onNext: " + v);
            }

        });
    }
}
