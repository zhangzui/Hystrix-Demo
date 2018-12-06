package com.zz.hystrix.test.bizmock.test;

import com.zz.hystrix.test.bizmock.SmsRequestVo;
import com.zz.hystrix.test.bizmock.SmsResponseVo;
import com.zz.hystrix.test.bizmock.SmsServiceSemaphore;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 信号量测试
 * 同时并发支持多少请求，超过并发量则进入失败处理
 * @author zhangzuizui
 * @date 18-5-1 下午12:01
 */
public class SmsSemaphoreTest {


    /**
     * 服务降级或者服务切换
     */
    @Test
    public void testSendMessage001(){
        final SmsRequestVo smsRequestVo = new SmsRequestVo();
        smsRequestVo.setPhone("18881881818");
        smsRequestVo.setContext("send messages !!!");
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 6; i >0; i--) {
            long time = i*200;
            smsRequestVo.setTime(time);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    SmsResponseVo smsResponseVo = (SmsResponseVo) new SmsServiceSemaphore(smsRequestVo).execute();
                }
            });
        }
    }

}
