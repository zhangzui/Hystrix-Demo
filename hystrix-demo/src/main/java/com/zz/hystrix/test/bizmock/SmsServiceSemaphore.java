package com.zz.hystrix.test.bizmock;

import com.netflix.hystrix.*;

/**
 * @author zhangzuizui
 * @date 18-5-1 上午11:51
 */
public class SmsServiceSemaphore extends HystrixCommand{

    /**
     * 发送短信请求参数
     */
    private SmsRequestVo smsRequestVo;

    public SmsServiceSemaphore(SmsRequestVo smsRequestVo) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SmsServiceGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("SmsServiceCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("SmsServiceGroup-pool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(1000)
                 //设置信号量隔离
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                .withExecutionIsolationSemaphoreMaxConcurrentRequests(3)
                .withFallbackIsolationSemaphoreMaxConcurrentRequests(2))
        );
        this.smsRequestVo = smsRequestVo;
    }

    @Override
    protected SmsResponseVo run() throws Exception {
        SmsResponseVo smsResponseVo = new SmsResponseVo();
        smsResponseVo.setCode("SUCCESS-AAA");

        System.out.println("TIME:"+smsRequestVo.getTime()+",run:发送短信AAA");
        return smsResponseVo;
    }
    @Override
    protected SmsResponseVo getFallback() {
        SmsResponseVo smsResponseVo = new SmsResponseVo();
        smsResponseVo.setCode("SUCCESS-BBB");
        System.out.println("TIME:"+smsRequestVo.getTime()+",run:发送短信BBB");
        return smsResponseVo;
    }
}
