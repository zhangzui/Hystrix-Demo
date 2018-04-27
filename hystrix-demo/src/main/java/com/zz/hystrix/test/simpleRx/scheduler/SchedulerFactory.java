package com.zz.hystrix.test.simpleRx.scheduler;

import java.util.concurrent.Executors;

/**
 * @author zhangzuizui
 * @date 2018/4/27 17:44
 */
public class SchedulerFactory {
    private static final MyScheduler ioScheduler = new MyScheduler(Executors.newSingleThreadExecutor());

    public static MyScheduler io() {
        return ioScheduler;
    }

    public static void shutdown() {
        ioScheduler.shutdown();
    }
}
