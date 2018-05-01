package com.zz.hystrix.test.bizmock;

import com.netflix.hystrix.*;

/**
 * @author zhangzuizui
 * @date 18-5-1 上午11:51
 */
public class SmsService extends HystrixCommand{

    /**
     * 发送短信请求参数
     */
    private SmsRequestVo smsRequestVo;

    public SmsService(SmsRequestVo smsRequestVo) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(500))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ExampleGroup-pool"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(2))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaximumSize(5))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withAllowMaximumSizeToDivergeFromCoreSize(true)));
        this.smsRequestVo = smsRequestVo;
    }

    @Override
    protected SmsResponseVo run() throws Exception {
        SmsResponseVo smsResponseVo = new SmsResponseVo();
        smsResponseVo.setCode("SUCCESS-AAA");
        Thread.sleep(smsRequestVo.getTime());
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
