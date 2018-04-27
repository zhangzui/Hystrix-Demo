package com.zz.hystrix.test.simpleRx.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author zhangzuizui
 * @date 2018/4/27 17:42
 */
public class MyScheduler {

    final ExecutorService executorService;
    public MyScheduler(ExecutorService executorService) {
        this.executorService = executorService;
    }
    public Worker createWorker() {
        return new Worker(executorService);
    }
    public void shutdown() {
        executorService.shutdown();
    }

    public static class Worker {
        final Executor executor;
        public Worker(Executor executor) {
            this.executor = executor;
        }

        /**
         * 这里接受的是Runnable而不是Action0，其实这没什么关系，主要是懒得自定义函数式接口了。
         * @param runnable
         */
        public void schedule(Runnable runnable) {
            executor.execute(runnable);
        }
    }
}
