package com.zz.hystrix.test.bizmock;

import com.netflix.hystrix.*;

/**
 * 熔断器设置:
 * 说明：withExecutionIsolationThreadInterruptOnTimeout用于配置超时后是否中断run方法的执行。
 * 这个需要根据具体业务逻辑具体分析，如果你的代码允许中断，那么最好中断，以节省开销。反之则禁止中断。
 * @author zhangzuizui
 * @date 18-5-1 上午11:51
 */
public class SmsServiceTimeOut extends HystrixCommand{

    /**
     * 发送短信请求参数
     */
    private SmsRequestVo smsRequestVo;

    public SmsServiceTimeOut(SmsRequestVo smsRequestVo) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SmsServiceGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("SmsServiceCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("SmsServiceGroup-pool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutEnabled(false)//是否启用超时中断
                        .withExecutionTimeoutInMilliseconds(100)//配置超时时间
                        .withExecutionIsolationThreadInterruptOnTimeout(true)));//超时后是否中断
        this.smsRequestVo = smsRequestVo;
    }

    @Override
    protected SmsResponseVo run() throws Exception {
        SmsResponseVo smsResponseVo = new SmsResponseVo();
        smsResponseVo.setCode("SUCCESS-AAA");
        Thread.sleep(smsRequestVo.getTime());
        System.out.println("超时时间-TIME,并且不中断超时:"+smsRequestVo.getTime()+"ms,run:发送短信AAA");
        return smsResponseVo;
    }
    @Override
    protected SmsResponseVo getFallback() {
        System.out.println("超时熔断处理:"+smsRequestVo.getTime()+" ms");
        return null;
    }
}
