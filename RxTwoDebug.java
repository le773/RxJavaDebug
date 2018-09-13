import com.orhanobut.logger.Logger;
import com.sun.deploy.util.ArrayUtil;
import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.*;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Observable;

public class RxTwoDebug {
    private static final String TAG = RxTwoDebug.class.getName();
    static Subscription mSubscription;
    public static void request(long n) {
        mSubscription.request(n);
    }

    public void demo3() {
        Flowable
                .create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                        String threadName = Thread.currentThread().getName();
                        System.out.println(threadName + "开始发射数据" + System.currentTimeMillis());
                        for (int i = 1; i <= 500; i++) {
                            System.out.println(threadName + "发射---->" + i);
                            e.onNext(i);
//                            Thread.sleep(10);
                        }
                        System.out.println(threadName + "发射数据结束" + System.currentTimeMillis());
                        e.onComplete();
                    }
                }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        s.request(Long.MAX_VALUE);            //注意此处，暂时先这么设置
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (integer%10 == 0)
                            Log.d(TAG, "onNext" + integer);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ignore) {
                        }
                        System.out.println(Thread.currentThread().getName() + "接收---------->" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("接收----> 完成");
                    }
                });
    }

    public void demo4() {
        Flowable
                .create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                        String threadName = Thread.currentThread().getName();
                        System.out.println(threadName + "开始发射数据" + System.currentTimeMillis());
                        for (int i = 1; i <= 500; i++) {
                            System.out.println("当前未完成的请求数量-->" + e.requested());
                            System.out.println(threadName + "发射---->" + i);
                            e.onNext(i);
                            try {
                                Thread.sleep(100);//每隔100毫秒发射一次数据
                            } catch (Exception ex) {
                                e.onError(ex);
                            }
                        }
                        System.out.println(threadName + "发射数据结束" + System.currentTimeMillis());
                        e.onComplete();
                    }
                }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(3);            //注意此处，暂时先这么设置
                    }

                    @Override
                    public void onNext(Integer integer) {
                        try {
                            Thread.sleep(300);//每隔300毫秒接收一次数据
                        } catch (InterruptedException ignore) {
                        }
                        System.out.println(Thread.currentThread().getName() + "接收---------->" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println(Thread.currentThread().getName() + "接收----> 完成");
                    }
                });
    }

    public void demo8() {
        Flowable.range(0, 500)
                .onBackpressureDrop()
                .subscribeOn(Schedulers.trampoline())
                .observeOn(Schedulers.trampoline())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        if (integer%10 == 0)
                            System.out.println(integer);
                    }
                });
    }

    public void demo18() {
        Flowable
                .create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                        int i = 0;
                        while (true) {
                            if (e.requested() == 0) continue;//此处添加代码，让flowable按需发送数据
                            System.out.println("发射---->" + i);
                            i++;
                            e.onNext(i);
                        }
                    }
                }, BackpressureStrategy.MISSING)
                .subscribeOn(Schedulers.trampoline())
                .observeOn(Schedulers.trampoline())
                .subscribe(new Subscriber<Integer>() {
                    private Subscription mSubscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);            //设置初始请求数据量为1
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        try {
                            Thread.sleep(50);
                            System.out.println("接收------>" + integer);
                            mSubscription.request(1);//每接收到一条数据增加一条请求量
                        } catch (InterruptedException ignore) {
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void singleDemo1() {
        Single
                .create(
                    new SingleOnSubscribe<Integer>() {

                        @Override
                        public void subscribe(SingleEmitter<Integer> singleEmitter) throws Exception {
                            Log.d(TAG, "subscribe");
                            singleEmitter.onSuccess(1);
                        }
                    }
                ).subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d(TAG, "onSuccess " + integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, "onError");
                    }
        });
    }

    public void singleDemo2() {
        Single
                .create(
                        new SingleOnSubscribe<Integer>() {

                            @Override
                            public void subscribe(SingleEmitter<Integer> singleEmitter) throws Exception {
                                Log.d(TAG, "subscribe");
                                singleEmitter.onError(new Exception("subscribe"));
                            }
                        }
                ).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onSuccess(Integer integer) {
                Log.d(TAG, "onSuccess " + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "onError: " + throwable.getMessage());
            }
        });
    }

    public void competableDemo1() {
        Completable
                .create(
                        new CompletableOnSubscribe() {
                            @Override
                            public void subscribe(CompletableEmitter completableEmitter) throws Exception {
                                Log.d(TAG, "subscribe");
                                completableEmitter.onComplete();
                            }
                        }
                ).subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable disposable) {
                            Log.d(TAG, "onSubscribe");
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.d(TAG, "onError: " + throwable.getMessage());
                        }
        });
    }


    public void competableDemo2() {
        Completable
                .create(
                        new CompletableOnSubscribe() {
                            @Override
                            public void subscribe(CompletableEmitter completableEmitter) throws Exception {
                                Log.d(TAG, "subscribe");
                                completableEmitter.onError(new Exception("CompletableOnSubscribe"));
                            }
                        }
                ).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable disposable) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "onError: " + throwable.getMessage());
            }
        });
    }

    public void maybeDemo1() {
        Maybe
                .create(
                        new MaybeOnSubscribe<Integer>() {

                            @Override
                            public void subscribe(MaybeEmitter<Integer> maybeEmitter) throws Exception {
                                Log.d(TAG, "subscribe");
                                maybeEmitter.onSuccess(1);
                                maybeEmitter.onComplete();
//                                maybeEmitter.onError(new Exception("maybeDemo1 error"));
                            }
                        }
                ).subscribe(
                new MaybeObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Log.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d(TAG, "onSuccess" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "finish");
                    }
                }
        );
    }

    public void runDistinct() {
        Flowable.just(1,1,2,1,2,2,4,5,3)
                .distinct()
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return true;
                    }
                })
                .map(new Function<Integer, String>() {

                    @Override
                    public String apply(Integer integer) throws Exception {
                        return "----->" + integer*2;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String string) throws Exception {
                        Log.d(TAG, string.getClass().getTypeName());
                    }
        });
    }

    public void concatDebug() {
        Flowable.concat(
            Flowable.just("H", "e", "l", "l", "o"),
            Flowable.just(" "),
            Flowable.just("W", "o", "r", "l", "d"),
            Flowable.just("!")
        ).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    public void flatMapDebug() {
        Integer[] num1 = new Integer[]{1,2,3,4};
        Integer[] num2 = new Integer[]{5,6};
        Integer[] num3 = new Integer[]{7,8,9};
        Flowable.just(num1, num2, num3)
                .flatMap(new Function<Integer[], Publisher<Integer>>() {
                    @Override
                    public Publisher<Integer> apply(Integer[] integers) throws Exception {
                        Log.d(TAG, Arrays.toString(integers));
                        return Flowable.fromArray(integers);
                    }
                }).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer s) throws Exception {
                        Log.d(TAG, s + "");
                    }
        });
    }

    public void zipWithDebug() {
        String[] names = {"A", "B", "C", "D", "E", "F"};
        Flowable.just(1,2,3,4,5,6)
                .zipWith(Flowable.fromArray(names), new BiFunction<Integer, String, String>() {

                    @Override
                    public String apply(Integer integer, String s) throws Exception {
                        return integer + s;
                    }
                }).subscribe(
                 /*new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, s);
                    }
                }*/
                new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, s);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }
        );
    }


    public void zipWithDebug2() {
//        String[] names = {"A", "B", "C", "D", "E", "F"};
        ArrayList<String> names = new ArrayList<String>(Arrays.asList("A", "B", "C", "D", "E", "F"));
        Flowable.just(1,2,3,4,5,6)
                .zipWith(Flowable.fromIterable(names), new BiFunction<Integer, String, String>() {

                    @Override
                    public String apply(Integer integer, String s) throws Exception {
//                        throw new Exception("string is not found");
                        Logger.d("logger " + integer);
                        return integer + s;
                    }
                }).subscribe(
                new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Logger.i("logger " + s);
//                        Log.d(TAG, s);
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "accept " + throwable.getMessage());

                    }
                },
                new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "finished");
                    }
                }
        );
    }

    /**
     * @param httpArg
     *            :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        RxTwoDebug rxDebug = new RxTwoDebug();
        rxDebug.zipWithDebug2();
//        rxDebug.maybeDemo1();
//        System.out.println("request");
//        RxDebug.request(Long.MAX_VALUE);
        /*
        while (true) {
            System.out.println("deb");
            System.out.println();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        String httpUrl="http://api.tianapi.com/social/";
        String httpArgs="key=APIKEY&num=10";
    }
}
