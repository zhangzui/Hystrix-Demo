package com.zz.hystrix.test;

import org.junit.Test;
import rx.Observable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author zhangzuizui
 * @date 2018/4/23 10:42
 */
public class ThreadPollTest {


    @Test
    public void testThreadPollCommand() {
        System.out.println(new ThreadPollCommand("Reject").execute());
    }

}
