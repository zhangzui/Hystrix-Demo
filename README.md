>微信公众号：颜家大少
如有问题或建议，请公众号留言
最近更新：`2018-05-02`

# Hystrix服务治理实践和思考
1.服务降级与熔断的理解；
2.了解RXjava的运行原理；
3.Hystrix的示例和运行机制；
##一.为什么看Hystrix？
   面对系统中各种服务和接口，它们有的是必要条件的，有的是可降级的条件，面对流量风暴或者服务不稳定，这样的服务可以选择熔断关闭，或者路由到降级方案，这样来保证服务的高可用性。恰巧最近项目中也需要这样的功能，基于现有的基础，代码嵌入式的开关，服务又很多，相同的服务，不同的降级方案，有的有开关，有的却没有。最后项目中到处都是开关，新人，老人，最后都不知道怎么去管理。时间越久，懂的人少了，大家也不敢动这些陈年代码。所以统一的开关服务就显得很重要，开关配置化，可视化，规范化显得尤为重要。所以最近突然想看一下Netflix的开源项目——Hystrix，看看降级和服务管理该如何实现。
##二.个人对服务降级的理解？
   由于资历尚浅，不敢妄自谈论一些高可用的架构和系统，但是一直在这条路上矢志不渝的追求着，完善自己的技术池，尽可能的做好手头的事情。对于熔断和降级，我个人认为，自动化的监控和决策是最好的，但是这个需要大量的监控数据，还有不同业务的自定义指标，比如日志，业务数据，错误码等。这样可以从各个维度进行策略降级，然后订制符合自己系统的一套熔断和降级的机制。当然自动化和智能化将是趋势。但是搭建起来也没有那么简单，更何况要以中间件的方式提供给各个业务系统，那就需要更多资源和时间成本。但是，保证功能的前提下，监控为辅助，大众指标（TP99,可用率，调用量，响应时间等指标）为基础，报警等手段，再进行对关键服务进行柔性降级，策略和梯度降级，甚至可以手动降级，来保证服务的高可用，也未必不可行。最后，将开关的可视化，服务监控面板化，实现动态的服务监控系统也是可以的。
##三.Hystrix是如何做的，有哪些值得一看的？
   上面讲很美好，然而Hystrix好像跟你说的不是一回事，它更加专注独立服务的指标计数监控，异步统计等。最重要还有它的线程隔离，就算大流量进来也不会让真正的服务受到冲击，服务与服务之间不相互干扰，这一点我觉得很好的保护了我们的服务可用性，更像一个保险丝。还有就是可以通过Hystrix的回调函数进行降级方案的处理，这里可以随便进行异常处理和补救方案，埋点等。而且Hystrix支持异步，同步，观察，订阅，链式传递返回值等reactive编程方式，在这个项目里你可以体验RXjava的乐趣。然而我们系统往往不是那么简单的几个超时，失败率，有的返回错误码才是真正的异常关键点。所以部分需求还需要定制化开发。
##四.Hystrix导读基础——RXjava？
首先需要了解RX的运行原理，才能看懂Hystrix的运行机制。
1.看一个简单的例子RxJavaBaseTest
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
                subscriber.onNext("{key:value}");
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
>1.Observable：发射源，可观察对象;
2.Observer：接收源，观察者;
3.Subscriber：订阅者，也是接收源;
4.Subject：Subject是一个比较特殊的对象，既可充当发射源，也可充当接收源
5.Action0：RxJava中的一个接口，它只有一个无参call（）方法，且无返回值，同样还有Action1，Action2...Action9等.Action1封装了含有* 1 个参的call（）方法，即call（T t），Action2封装了含有 2 *个参数的call方法，即call（T1 t1，T2 t2），以此类推；
6.Func0：与Action0非常相似，也有call（）方法，但是它是有返回值的，同样也有Func0、Func1...Func9;

2.下面是RXjava的响应式编程示例：
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
上面的例子中展示了响应式编程的逻辑，不停的再对已有需求进行迭代和更改，做到了动态的增加和异步处理。大家可以自己模拟实现一个简单的RXjava,创建可观察对象，然后转换为另一个被观察的对象，再订阅，等。

##五.Hystrix的运行机制
了解Rxjava之后，我们来看一个Hystrix的例子。发送短信的网关服务，里边运营商不一样可能服务也不一样，所以很适合作为一个可降级服务。
例子：服务A是正常服务，服务B是降级方案，条件是超时5000ms，并发支持线程池设置（核心线程数coresize=2.最大MaximumSize：5等）这里将A服务中设置一个超时时间，以便测试用！
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
        //调用短信服务A
        smsResponseVo.setCode("SUCCESS-AAA");
        Thread.sleep(smsRequestVo.getTime());
        System.out.println("TIME:"+smsRequestVo.getTime()+",run:发送短信AAA");
        return smsResponseVo;
    }
    @Override
    protected SmsResponseVo getFallback() {
        SmsResponseVo smsResponseVo = new SmsResponseVo();
        //调用短信服务B
        smsResponseVo.setCode("SUCCESS-BBB");
        System.out.println("TIME:"+smsRequestVo.getTime()+",run:发送短信BBB");
        return smsResponseVo;
    }
}
```
当超时或者超过线程池处理能力，就会触发回调降级函数。
#####运行机制：
>1.创建HystrixCommand 或者 HystrixObservableCommand 对象
    其中两种配置信息类：HystrixCommandProperties和HystrixThreadPoolProperties，分别对应的是基础配置信息和线程池的配置信息。
    HystrixCommandProperties：超时时间，缓存是否生效，是否打印日志，circuitBreaker（断路器）的请求量信息，以及统计窗口信息等；
    HystrixThreadPoolProperties：核心线程数，最大线程数，心跳时间，最大队列数，队列拒绝阈值等.
2.执行命令（即上述 Command 对象包装的逻辑）
    execute() —— 阻塞，当依赖服务响应（或者抛出异常/超时）时，返回结果
    queue() —— 返回 Future 对象，通过该对象异步得到返回结果
    observe() —— 返回 Observable 对象，立即发出请求，在依赖服务响应（或者抛出异常/超时）时，通过注册的 Subscriber 得到返回结果
    toObservable() —— 返回 Observable 对象，但只有在订阅该对象时，才会发出请求，然后在依赖服务响应（或者抛出异常/超时）时，通过注册的 Subscriber 得到返回结果
    这些方法都有相互依赖，其实最重要的是toObservable()，最后都会封装出可观察对象进行传递调用。
3.结果是否有缓存:
    如果请求结果缓存这个特性被启用，并且缓存命中，则缓存的回应会立即通过一个 Observable 对象的形式返回。（缓存需要实现它的获取缓存Key的方法，抽象类默认是返回null，不启用缓存）
4.请求线路（类似电路）是否是开路:
    当执行一个命令时，Hystrix 会先检查熔断器状态，确定请求线路是否是开路,如果请求线路是开路，Hystrix 将不会执行这个命令，而是直接使用『失败回退逻辑』
5.线程池/请求队列/信号量占满时会发生什么?
    如果和当前需要执行的命令相关联的线程池和请求队列（或者信号量，如果不使用线程池），Hystrix 将不会执行这个命令，而是直接使用『失败回退逻辑』
6.使用 HystrixObservableCommand.construct() 还是 HystrixCommand.run()
    Hystrix 将根据你使用类的不同，内部使用不同的方式来请求依赖服务：
    a.HystrixCommand.run() —— 返回回应或者抛出异常
    b.HystrixObservableCommand.construct() —— 返回 Observable 对象，并在回应到达时通知 observers，或者回调 onError 方法通知出现异常
7.计算链路健康度.
    Hystrix 会将请求成功，失败，被拒绝或超时信息报告给熔断器，熔断器维护一些用于统计数据用的计数器。这些计数器产生的统计数据使得熔断器在特定的时刻，能短路某个依赖服务的后续请求，直到恢复期结束，若恢复期结束根据统计数据熔断器判定线路仍然未恢复健康，熔断器会再次关闭线路。
8.失败回退，或者正常响应.

##六.总结
总体来说改项目还是值得一看的，思想和设计思路也挺清晰的，源码的难度挺高，很多东西值得学习。如果要用在自己的项目中，建议还是做好容错。毕竟大量的线程池使用，新老手都可能会掉坑。
##参考文档
1、翻译Hystrix文档-实现原理：
http://youdang.github.io/2016/02/05/translate-hystrix-wiki-how-it-works
2.RXjava学习博客：
https://blog.csdn.net/tellh/article/details/71534704
3、Hystrix官方文档：
https://github.com/Netflix/Hystrix/wiki/How-To-Use#Reactive-Execution

