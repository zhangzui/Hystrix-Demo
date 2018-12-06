package com.zz.hystrix.test.bizmock.test;

import com.zz.hystrix.test.bizmock.SmsRequestVo;
import com.zz.hystrix.test.bizmock.SmsResponseVo;
import com.zz.hystrix.test.bizmock.SmsServiceFuse;
import com.zz.hystrix.test.bizmock.SmsServiceSemaphore;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 熔断测试
 * 同时并发支持多少请求，超过并发量则进入失败处理
 * @author zhangzuizui
 * @date 18-5-1 下午12:01
 */
public class SmsFuseTest {

    /**
     i=1时报错，但是还未超过withCircuitBreakerRequestVolumeThreshold的配置的值（3）。所以不会熔断。
     但是当i=3时，过去【0,1,2】错了一个，错误率是33.3%，超过了阈值20%，所以熔断。
     i=11时，过了8秒（withCircuitBreakerSleepWindowInMilliseconds配置项），再次尝试请求原服务，发现服务可用，解除熔断。
     i=17时，再次出错一次。这时，i=[11,17]出错一次，错误率14.2%，未超阈值，不进行熔断。

     TIME:9,run:发送短信AAA
     TIME:9,run:发送短信AAA
     熔断处理:8,run:发送短信BBB
     熔断处理:7,run:发送短信BBB
     熔断处理:6,run:发送短信BBB
     熔断处理:5,run:发送短信BBB
     熔断处理:4,run:发送短信BBB
     熔断处理:3,run:发送短信BBB
     熔断处理:2,run:发送短信BBB
     TIME:1,run:发送短信AAA
     */

    /**
     * 服务降级或者服务切换
     */
    @Test
    public void testSendMessage001() throws InterruptedException {
        final SmsRequestVo smsRequestVo = new SmsRequestVo();
        smsRequestVo.setPhone("18881881818");
        smsRequestVo.setContext("send messages !!!");
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 10; i >0; i--) {
            Thread.sleep(2000);
            long time = i;
            smsRequestVo.setTime(time);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    SmsResponseVo smsResponseVo = (SmsResponseVo) new SmsServiceFuse(smsRequestVo).execute();
                }
            });
        }
    }

}
