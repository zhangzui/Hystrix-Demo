package com.zz.hystrix.test;

import com.netflix.hystrix.*;

import java.util.concurrent.TimeUnit;

/**
 * 一个用户请求的成功执行，可能依赖数十上百个外部服务，如果没有隔离，单个依赖的失败，可能会印象其他依赖的正常执行。
 * 所以为每个依赖配置了单独线程池
 * 线程池隔离的使用例子
 * @author zhangzuizui
 */
public class ThreadPollCommand extends HystrixCommand<String>{
    private final String name;

    public ThreadPollCommand(String name) {
        /**
         * 必须
         * 可选,默认 使用 this.getClass().getSimpleName()
         * 超时时间
         */
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(500))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ExampleGroup-pool"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(4)));
        this.name = name;
    }


    @Override
    protected String run() throws Exception {
        System.out.println("running");

        String theadName = this.getThreadPoolKey().name();
        String cmdKey=this.getThreadPoolKey().name();
        System.out.println("running begin , threadPool="+theadName+" cmdKey="+cmdKey+" name="+name);

        if("Exception".equals(name)) {
            throw new RuntimeException("this command always fails");
        }else if("Timeout".equals(name)){
            TimeUnit.SECONDS.sleep(2);
        }else if("Reject".equals(name)){
            TimeUnit.MILLISECONDS.sleep(800);
        }
        System.out.println(" run end");
        return "Hello " + name + "!";
    }

    @Override
    protected String getFallback() {
        StringBuilder sb = new StringBuilder("running fallback");
        boolean isRejected = isResponseRejected();
        boolean isException = isFailedExecution();
        boolean isTimeout= isResponseTimedOut();
        boolean isCircut = isCircuitBreakerOpen();
        sb.append(", isRejected:").append(isRejected);
        sb.append(", isException:"+isException);
        if(isException){
            sb.append(" msg=").append(getExecutionException().getMessage());
        }
        sb.append(",  isTimeout: "+isTimeout);
        sb.append(",  isCircut:"+isCircut);
        sb.append(", group:").append(this.getCommandGroup().name());
        sb.append(", threadpool:").append(getThreadPoolKey().name());
        System.out.println(sb.toString());
        String msg="Hello Failure " + name + "!";
        return msg;
    }
}
