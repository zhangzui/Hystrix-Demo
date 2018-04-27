package com.zz.hystrix.test.simpleRx;


/**
 * @author zhangzuizui
 * @date 2018/4/27 14:37
 * 订阅源
 */
public class MyObservable<T> {

    final OnSubscribe<T> onSubscribe;

    private MyObservable(OnSubscribe<T> onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public static <T> MyObservable<T> create(OnSubscribe<T> onSubscribe) {
        return new MyObservable<T>(onSubscribe);
    }

    public void subscribe(MySubscriber<? super T> subscriber){
        onSubscribe.call(subscriber);
    }

    public interface OnSubscribe<T> {
        void call(MySubscriber<? super T> subscriber);
    }

    public interface Transformer<T ,R> {
        R call(T from);
    }

    public class ExtendStreamsBean<T, R> implements MyObservable.OnSubscribe<R> {
        final MyObservable<T> source;
        final MyObservable.Transformer<? super T, ? extends R> transformer;
        public ExtendStreamsBean(MyObservable<T> source, MyObservable.Transformer<? super T, ? extends R> transformer) {
            this.source = source;
            this.transformer = transformer;
        }

        @Override
        public void call(MySubscriber<? super R> subscriber) {
            source.subscribe(new MapSubscriber<R, T>(subscriber,transformer));
        }
    }
    public class MapSubscriber<T, R> extends MySubscriber<R> {
        final MySubscriber<? super T> actual;
        final MyObservable.Transformer<? super R, ? extends T> transformer;
        public MapSubscriber(MySubscriber<? super T> actual, MyObservable.Transformer<? super R, ? extends T> transformer) {
            this.actual = actual;
            this.transformer = transformer;
        }

        @Override
        public void completed(R var) {
            actual.completed(transformer.call(var));
        }

        @Override
        public void error(Throwable var) {

        }

        @Override
        public void start(R var) {
            actual.start(transformer.call(var));
        }
    }
    public <R> MyObservable<R> extendStreams(final Transformer<? super T, ? extends R> transformer){
        MyObservable myObservable = create(new ExtendStreamsBean(this,transformer));
       /* MyObservable myObservable = create(new OnSubscribe<R>() {
            @Override
            public void call(final MySubscriber<? super R> subscriber) {
                // 订阅上层的Observable
                MyObservable.this.subscribe(new MySubscriber<T>() {
                    @Override
                    public void completed(T var) {
                        subscriber.completed(transformer.call(var));
                    }
                    @Override
                    public void error(Throwable var) {
                    }

                    @Override
                    public void start(T var) {
                        subscriber.start(transformer.call(var));
                    }
                });
            }
        });*/
        return myObservable;
    }
}
