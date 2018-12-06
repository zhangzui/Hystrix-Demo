package com.zz.hystrix.test.bizmock;

import com.netflix.hystrix.*;

/**
 * 熔断器设置:
 * withCircuitBreakerErrorThresholdPercentage(20)     //（出错百分比阈值，当达到此阈值后，开始短路。默认50%）
 * withCircuitBreakerRequestVolumeThreshold(3)        //在统计数据之前，必须在10秒内发出3个请求。  默认是20
 * withCircuitBreakerSleepWindowInMilliseconds(8000)  //（断路多久以后开始尝试是否恢复，默认5s）
 * @author zhangzuizui
 * @date 18-5-1 上午11:51
 */
public class SmsServiceFuse extends HystrixCommand{

    /**
     * 发送短信请求参数
     */
    private SmsRequestVo smsRequestVo;

    public SmsServiceFuse(SmsRequestVo smsRequestVo) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SmsServiceGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("SmsServiceCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("SmsServiceGroup-pool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(1000)
                    .withCircuitBreakerEnabled(true) //默认true，  // 配置熔断器
                    .withCircuitBreakerErrorThresholdPercentage(20)     //（出错百分比阈值，当达到此阈值后，开始短路。默认50%）
                    .withCircuitBreakerRequestVolumeThreshold(3)
                )
        );
        this.smsRequestVo = smsRequestVo;
    }

    @Override
    protected SmsResponseVo run() throws Exception {
        SmsResponseVo smsResponseVo = new SmsResponseVo();
        smsResponseVo.setCode("SUCCESS-AAA");
        if((smsRequestVo.getTime() % 2) ==0){
            throw new RuntimeException("send service error!");
        }
        System.out.println("TIME:"+smsRequestVo.getTime()+",run:发送短信AAA");
        return smsResponseVo;
    }
    @Override
    protected SmsResponseVo getFallback() {
        System.out.println("熔断处理:"+smsRequestVo.getTime()+",run:发送短信BBB");
        return null;
    }
}
