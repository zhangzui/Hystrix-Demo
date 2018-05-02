# Hystrix服务治理实践和思考
1.Hystrix项目的简单示例
2.服务降级与熔断的理解
3.RXjava-demo和simple Rx的实现
4.Hystrix源码解读

##一.为什么看这个？
   面对系统中各种服务和接口，它们有的是必要条件的，有的是可降级的条件，面对流量风暴或者服务不稳定，这样的服务可以选择熔断关闭，或者路由到降级方案，这样来保证服务的高可用性。恰巧最近项目中也需要这样的功能，基于现有的基础，代码嵌入式的开关，服务又很多，相同的服务，不同地方的降级，有的有开关，有的却没有。最后项目中到处都是开关，新人，老人，最后都不知道怎么去管理。时间越久，懂的人少了，大家也不敢动这些陈年代码。所以统一的开关服务就显得很重要，开关配置化，可视化，开关代码规范化闲的尤为重要。所以最近突然想看一下Netflix的开源项目Hystrix的实现，来看看这个统一的降级管理服务如何搭建。
##二.个人对服务降级的理解？
   由于资历尚浅，不敢妄自谈论一些高可用的架构和系统，但是一直在这条路上矢志不渝的追求着，完善自己的技术池，尽可能的做好手头的事情。对于熔断和降级，我个人认为，自动化的监控和决策是最好的，但是这个需要大量的监控数据，还有不同业务的自定义指标，比如日志，业务数据，错误码等。这样可以从各个维度进行统一降级，然后订制符合自己系统的一套熔断和降级的机制。当然自动化和智能化将是趋势。但是搭建起来也没有那么简单，更何况要以中间件的方式提供给各个业务系统，那就需要更多资源和时间成本。但是，保证功能的前提下，智能监控为辅助，大众指标（TP99,可用率，调用量，响应时间等指标）为基础，报警等手段，再进行对关键服务进行柔性降级，策略和梯度降级，甚至可以手动降级，来保证服务的高可用，也未必不可行。最后，将开关的可视化，服务监控面板化，实现动态的服务监控系统也是可以的。
##三.Hystrix是如何做的，有哪些值得一看的？
   上面讲了那么多，好像很美好，然而Hystrix好像跟你说的不是一回事，它更加专注独立服务的指标异常监控，最重要的线程隔离，就算大流量进来也不会让真正的服务受到冲击，这一点我觉得很好的保护了我们的服务可用性，更像一个保险丝。还有就是可以通过Hystrix的回调函数进行降级方案的处理，这里可以随便进行异常处理和补救方案，埋点等。而且Hystrix支持异步，同步，观察，订阅，链式传递返回值等reactive编程方式，这都是也是未来的趋势。在这个项目里你可以体验RXjava的乐趣。虽然在jdk8中流式编程也在逐步完善，可以看出未来的编程趋势，更加函数化，响应式而我们系统往往不是那么简单的几个超时，有的返回错误码，等才是真正的异常关键点。所以这样的还需要订制。
##四.Hystrix源码导读基础——RXjava？
```
public class RxJavaBaseTest {
    public static void main(String[] args) {
        /**
         * 先触发onNext(),再调用onComplete()
         */
        Observable<String> sender = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                //发送数据
                subscriber.onNext("do something!!!");
                subscriber.onCompleted();
            }
        });
        Observer receiver = new Observer() {
            @Override
            public void onCompleted() {
                System.out.println("after completed do something");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("throw throwable!!!");
            }

            @Override
            public void onNext(Object o) {
                System.out.println("onNext：get sender's data and do something!The request data is :"+o);
            }
        };

        sender.subscribe(receiver);
    }
}
```
运行结果：
```
get sender's data and do something!The request data is :{key:value}
after completed do something
```

上面是一个最基础的例子，很清楚是一个观察者模式，Observable发送数据，Observer 订阅数据。下面是几个概念介绍：
Observable：发射源，英文释义“可观察的”，在观察者模式中称为“被观察者”或“可观察对象”；
Observer：接收源，英文释义“观察者”，没错！就是观察者模式中的“观察者”，可接收Observable、Subject发射的数据；
Subscriber：“订阅者”，也是接收源
Subject：Subject是一个比较特殊的对象，既可充当发射源，也可充当接收源
Action0：RxJava中的一个接口，它只有一个无参call（）方法，且无返回值，同样还有Action1，Action2...Action9等，Action1封装了含有* 1 个参的call（）方法，即call（T t），Action2封装了含有 2 *个参数的call方法，即call（T1 t1，T2 t2），以此类推；
Func0：与Action0非常相似，也有call（）方法，但是它是有返回值的，同样也有Func0、Func1...Func9;
下面来一个响应式的例子：
```
public class RxJavaTestGetUserInfo {

    public static void main(String[] args) {
        /**
         * 创建一个Observable
         */
        Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                List<String> userList = new ArrayList<String>();
                userList.add("AAA");
                userList.add("BBB");
                userList.add("CCC");
                subscriber.onNext(userList);
            }
        }).flatMap(new Func1<List<String>, Observable<String>>() {
            @Override
            public Observable<String> call(List<String> users) {
                //查询Users
                return Observable.from(users);
            }
        }).filter(new Func1<String,Boolean>(){
            @Override
            public Boolean call(String userName) {
                //找到AAA的user信息
                return userName.equals("AAA");
            }
        }).map(new Func1<String,String>(){
            @Override
            public String call(String userName) {
                //返回AAA的其他信息
                return userName+"222";
            }
        }).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                //继续根据AAA信息，获取其他信息
                return "xxxxxx"+s;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                //最后订阅，响应结果
                System.out.println(s);
            }
        });
    }

}
```
例子中展示了响应式编程的逻辑，不停的再对已有需求进行迭代和更改。做到了动态的增加和异步处理。
##五.Hystrix的运行机制
    了解了RX的运行原理，就可以很轻松的了解Hystrix的运行机制了。
    首先来一个demo：这是一个发送短信的网关服务，里边运营商不一样可能服务也不一样，所以很适合作为一个可降级服务。
    简单的说一下例子：
    服务AAA是正常服务，BBB是降级方案，条件是超时5000ms，并发支持线程池设置（核心线程数coresize=2.最大MaximumSize：5等）
    这里将AAA服务中获取一个超时时间，以便测试用！
```
public class SmsService extends HystrixCommand{

    /**
     * 发送短信请求参数
     */
    private SmsRequestVo smsRequestVo;

    public SmsService(SmsRequestVo smsRequestVo) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(5000))
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
```

##六.如何设计服务降级？

##参考文档
1、Hystrix文档-实现原理：
http://youdang.github.io/2016/02/05/translate-hystrix-wiki-how-it-works
2、我所理解的RxJava——上手其实很简单
https://www.jianshu.com/p/5e93c9101dc5
3、Hystrix官方文档
https://github.com/Netflix/Hystrix/wiki/How-To-Use#Reactive-Execution

