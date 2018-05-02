package com.zz.hystrix.test;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * @author zhangzuizui
 * @date 2018/4/23 10:42
 */
public class CommandTest {
    /**
     * 熔断降级策略控制
     */
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

    /**
     * 异步执行逻辑：
     *  final Future<R> delegate = toObservable().toBlocking().toFuture();
     *  1、toObservable 先转化为一个可被观察的对象；发送指令String=Bob
     *  2、toBlocking 生成一个BlockingObservable
     *  3.toFuture 异步订阅消息，返回异步结果 value ，value在onNext()中赋值
     *  4.阻塞获取结果 f.get(); return delegate.get();
     *
     */
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
    }

    /**
     * 观察者模式1
     * @throws Exception
     */
    @Test
    public void testObservable() throws Exception {
        Observable<String> fWorld = new CommandHelloWorld("World").observe();
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
    }
    /**
     * 观察者模式2
     * @throws Exception
     */
    @Test
    public void testObservable002() {
        Observable<String> toObservable = new CommandHelloWorld("Bob").toObservable();
        toObservable.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onNext(String s) {
                System.out.println("toObservable====="+s);
            }
        });
    }
}
