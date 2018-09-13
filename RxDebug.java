
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

class Rx {
	long k = 7L;
	String tag="RxDebug";
	Observer<String> observerExam = new Observer<String>() {
	    @Override
	    public void onNext(String s) {
	        System.out.println(tag + " Item: " + s);
	    }

	    @Override
	    public void onError(Throwable e) {
	    	System.out.println(tag +  " Error!" + "\n");
	    }

		@Override
		public void onComplete() {
			// TODO Auto-generated method stub
			System.out.println(tag + " onComplete!" + "\n");
		}

		@Override
		public void onSubscribe(Disposable arg0) {
			// TODO Auto-generated method stub
			System.out.println(tag + " onSubscribe!" + "\n");
		}
	};
	
	Observable<String> novel = Observable.create(new ObservableOnSubscribe<String>() {
		@Override
		public void subscribe(ObservableEmitter<String> emitter) throws Exception {
			// TODO Auto-generated method stub
			System.out.print("subscribe" + "\n");
			emitter.onNext("1");
			emitter.onNext("2");
			emitter.onNext("3");
			emitter.onComplete();
		}
	});
	
	Disposable mDisposable;
	
	Observer<String> reader = new Observer<String>() {

		@Override
		public void onComplete() {
			// TODO Auto-generated method stub
			System.out.println(tag + " onComplete" + "\n");
		}

		@Override
		public void onError(Throwable e) {
			// TODO Auto-generated method stub
			System.out.println(tag + " onError=" + e.getMessage() + "\n");
		}

		@Override
		public void onNext(String arg0) {
			// TODO Auto-generated method stub
			System.out.println(tag + " onNext:" + arg0 + "\n");
			if("2".equals(arg0)) {
				mDisposable.dispose(); //cancel subscribe
				return;
			}
		}

		@Override
		public void onSubscribe(Disposable d) {
			// TODO Auto-generated method stub
			mDisposable = d;
			System.out.println(tag + " onSubscribe!" + "\n");
		}
		
	};
	
	public void subscribe() {
//		ObjectHelper
		novel.subscribe(reader);
	}
	
	
	public <T> void goObservableSubscribe() {
		Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(ObservableEmitter<String> arg0) throws Exception {
				// TODO Auto-generated method stub
				for(int i=0; i<10; i++) {
					if (i==2) {
						Thread.sleep(2000);
					}
					
					if (i==5) {
						
					}
					arg0.onNext("" + i);
				}
			}
		}).subscribe(new Observer<Object>() {
			private int i;
			private Disposable mDisposable;

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				System.out.println(tag + " onComplete" + "\n");
			}

			@Override
			public void onError(Throwable e) {
				// TODO Auto-generated method stub
				System.out.println(tag + " onError : value : " + e.getMessage() + "\n" );
			}

			@Override
			public void onNext(Object arg0) {
				// TODO Auto-generated method stub
				System.out.println(tag + " onNext" + "\n");
				i++;
				if (i == 5) {
					mDisposable.dispose();
				}
			}

			@Override
			public void onSubscribe(Disposable arg0) {
				// TODO Auto-generated method stub
				mDisposable = arg0;
				System.out.println(tag + " onSubscribe" + "\n");
			}
		});
	}
	
	public void goThreadOn() {
		Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(ObservableEmitter<String> arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.print("goThreadOn " + Thread.currentThread().getName() + "\n");
				arg0.onNext("1");
				arg0.onNext("2");
				arg0.onNext("3");
//				arg0.onComplete();
				arg0.onNext("4");
				arg0.onNext("5");
			}
		})
		.subscribeOn(Schedulers.newThread())
//		.subscribeOn(Schedulers.io())
		.observeOn(Schedulers.single())
		.doOnNext(new Consumer<String>() {

			@Override
			public void accept(String arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(arg0 + " 1doOnNext，Current thread is " + Thread.currentThread().getName() + "\n");
			}
			
		})
		.observeOn(Schedulers.io())
		.doOnNext(
			new Consumer<String>() {
				@Override
				public void accept(String arg0) throws Exception {
					// TODO Auto-generated method stub
					System.out.println(arg0 + " 2doOnNext，Current thread is " + Thread.currentThread().getName() + "\n");
				}
			}
		)		
//		.observeOn(Schedulers.computation())
//		.doOnComplete(new Action() {
//			
//			@Override
//			public void run() throws Exception {
//				// TODO Auto-generated method stub
//				System.out.println(" doOnComplete " + Thread.currentThread().getName());
//			}
//		})
		.subscribe(
/*			new Observer<String>() {

				@Override
				public void onComplete() {
					// TODO Auto-generated method stub
					System.out.println(" 1onComplete " + Thread.currentThread().getName());
				}

				@Override
				public void onError(Throwable arg0) {
					// TODO Auto-generated method stub
					System.out.println(arg0 + " 1onError " + Thread.currentThread().getName());
				}

				@Override
				public void onNext(String arg0) {
					// TODO Auto-generated method stub
					System.out.println(arg0 + " 1onNext " + Thread.currentThread().getName());
				}

				@Override
				public void onSubscribe(Disposable arg0) {
					// TODO Auto-generated method stub
					System.out.println(" 1onSubscribe " + Thread.currentThread().getName());
				}
				
			},
			new Observer<String>() {

				@Override
				public void onComplete() {
					// TODO Auto-generated method stub
					System.out.println(" 2onComplete " + Thread.currentThread().getName());
				}

				@Override
				public void onError(Throwable arg0) {
					// TODO Auto-generated method stub
					System.out.println(arg0 + " 2onError " + Thread.currentThread().getName());
				}

				@Override
				public void onNext(String arg0) {
					// TODO Auto-generated method stub
					System.out.println(arg0 + " 2onNext " + Thread.currentThread().getName());
				}

				@Override
				public void onSubscribe(Disposable arg0) {
					// TODO Auto-generated method stub
					System.out.println(" 2onSubscribe " + Thread.currentThread().getName());
				}
				
			}*/
		new Consumer<String>() {
			@Override
			public void accept(String arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("After observeOn(mainThread)，Current thread is " + Thread.currentThread().getName() + "\n");
				System.out.println("onNext: " + arg0);
			}
		}/*,
		new Consumer<String>() {
			@Override
			public void accept(String arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("After observeOn(mainThread)，Current thread is " + Thread.currentThread().getName() + "\n");
				System.out.println("onNext: " + arg0);
			}
		}*/
		);
	}
	
	public void runMap() {
		Observable.create(new ObservableOnSubscribe<Integer>() {

			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				// TODO Auto-generated method stub
				emitter.onNext(1);
				emitter.onNext(2);
				emitter.onNext(3);
			}
		})
		.map(new Function<Integer, String>() {

			@Override
			public String apply(Integer arg0) throws Exception {
				// TODO Auto-generated method stub
				return "" + arg0;
			}
			
		})
		.map(new Function<String, String>() {

			@Override
			public String apply(String arg0) throws Exception {
				// TODO Auto-generated method stub
				return "line>" + arg0;
			}
			
		})
		.subscribe(new Consumer<String>() {

			@Override
			public void accept(String arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(arg0);
			}
			
		});
	}
	
	public void runFlatMap() {//no out
		Observable.create(new ObservableOnSubscribe<Integer>() {

			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				// TODO Auto-generated method stub
				emitter.onNext(1);
				emitter.onNext(2);
				emitter.onNext(3);
			}
			
		}).flatMap(new Function<Integer, ObservableSource<String>>() {

			@Override
			public ObservableSource<String> apply(Integer arg0) throws Exception {
				final ArrayList<String> list = new ArrayList<>();
				for(int i=0; i<4; i++) {
					list.add("I am value:" + arg0);
				}
				return Observable.fromIterable(list).delay(1, TimeUnit.MILLISECONDS);
			}
			
		})
		.subscribeOn(Schedulers.io())
		.subscribe(new Observer<String>() {

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onError(Throwable arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onNext(String arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0);
			}

			@Override
			public void onSubscribe(Disposable arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void runConcatMap() {
		Observable.create(new ObservableOnSubscribe<Integer>() {

			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				// TODO Auto-generated method stub
				emitter.onNext(1);
				emitter.onNext(2);
				emitter.onNext(3);
			}
			
		}).concatMap(new Function<Integer, ObservableSource<String>>() {

			@Override
			public ObservableSource<String> apply(Integer arg0) throws Exception {
				final ArrayList<String> list = new ArrayList<>();
				for(int i=0; i<4; i++) {
					list.add("I am value:" + arg0);
				}
				return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
			}
			
		}).subscribe(new Consumer<String>() {

			@Override
			public void accept(String arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(arg0);
			}
			
		});
	}
	
	public void runZip() {
		Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {

			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				System.out.println(Thread.currentThread().getName());
				// TODO Auto-generated method stub
				int i=0;
				while(i<100) {
					i++;
					emitter.onNext(Integer.valueOf(i));
//					emitter.onNext(1);
//					emitter.onNext(2);
//					emitter.onNext(3);
//					emitter.onNext(4);
	//				emitter.onComplete();
				}
			}
			
		}).subscribeOn(Schedulers.io());
		
		Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {

			@Override
			public void subscribe(ObservableEmitter<String> emitter) throws Exception {
				System.out.println(Thread.currentThread().getName());
				// TODO Auto-generated method stub
				emitter.onNext("A");
				emitter.onNext("B");
				emitter.onNext("C");
				emitter.onNext("D");
//				emitter.onComplete();
			}
			
		}).subscribeOn(Schedulers.io());
		
		Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {

			@Override
			public String apply(Integer arg0, String arg1) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("zip" + arg0 + arg1);
				return arg0 + arg1;
			}
			
		})
		.observeOn(Schedulers.io())
		.subscribe(
				new Consumer<String>() {

					@Override
					public void accept(String arg0) throws Exception {
						// TODO Auto-generated method stub
						System.out.println(arg0);
					}
					
				},
				new Consumer<Throwable>() {

					@Override
					public void accept(Throwable e) throws Exception {
						// TODO Auto-generated method stub
						System.out.println(e.getMessage());
					}
					
				}
				/*new Observer<String>() {

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNext(String arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0);
			}

			@Override
			public void onSubscribe(Disposable arg0) {
				// TODO Auto-generated method stub
				
			}
			
		}*/);
	}
	
	public void runBackPressureSync() {
		Observable.create(new ObservableOnSubscribe<Integer>() {

			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				// TODO Auto-generated method stub
				while(true) {
					emitter.onNext(2);
				}
			}
			
		}).subscribe(new Consumer<Integer>() {

			@Override
			public void accept(Integer arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(arg0);
			}
			
		});
	}
	public void runBackPressureASync() {
		Observable.create(new ObservableOnSubscribe<Integer>() {

			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(Thread.currentThread().getName());
				// TODO Auto-generated method stub
				int i=0;
				while (i<100) {
					i++;
					emitter.onNext(Integer.valueOf(i));
	//				emitter.onComplete();
					if (i%10 == 0) {
						System.out.println(i);
					}
				}
			}
			
		})
		.subscribeOn(Schedulers.single())
		.filter(new Predicate<Integer>() {

			@Override
			public boolean test(Integer arg0) throws Exception { // filter condition, only arg0%10 == 0 can pass
				// TODO Auto-generated method stub
				return arg0%10 == 0;
			}
			
		})
		.observeOn(Schedulers.io())
		.subscribe(new Consumer<Integer>() {

			@Override
			public void accept(Integer arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(arg0);
			}
			
		})
		;
	}
	
	public void runSampleASync() {
		Observable.create(new ObservableOnSubscribe<Integer>() {

			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(Thread.currentThread().getName());
				// TODO Auto-generated method stub
				int i=0;
				while (i<100) {
					i++;
					emitter.onNext(Integer.valueOf(i));
	//				emitter.onComplete();
					if (i%10 == 0) {
						System.out.println(i);
					}
				}
			}
			
		})
		.subscribeOn(Schedulers.single())
		.sample(1, TimeUnit.MILLISECONDS) //每隔1秒取一个事件给下游
		.observeOn(Schedulers.io())
		.subscribe(new Consumer<Integer>() {

			@Override
			public void accept(Integer arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(arg0);
			}
			
		})
		;
	}
	
	public void runDelayASync() {
		Observable.create(new ObservableOnSubscribe<Integer>() {

			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(Thread.currentThread().getName());
				// TODO Auto-generated method stub
				int i=0;
				while (i<100) {
					i++;
					emitter.onNext(Integer.valueOf(i));
	//				emitter.onComplete();
					if (i%10 == 0) {
						System.out.println(i);
					}
					Thread.sleep(200); //reduce speed
				}
			}
			
		})
		.subscribeOn(Schedulers.single())
		.observeOn(Schedulers.io())
		.subscribe(new Consumer<Integer>() {

			@Override
			public void accept(Integer arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(arg0);
			}
			
		})
		;
	}
	
	public void showFlowableSubscriber() {
		Flowable<Integer> upstream = Flowable.create(new FlowableOnSubscribe<Integer>() {

			@Override
			public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
				// TODO Auto-generated method stub
				Log.d(tag, "emit 1");
                emitter.onNext(1);
                Log.d(tag, "emit 2");
                emitter.onNext(2);
                Log.d(tag, "emit 3");
                emitter.onNext(3);
                Log.d(tag, "emit complete");
                emitter.onComplete();
			}
			
		}, BackpressureStrategy.ERROR);
		
		Subscriber<Integer> downstream = new Subscriber<Integer>() {

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				Log.d(tag, "onComplete");
			}

			@Override
			public void onError(Throwable e) {
				// TODO Auto-generated method stub
				Log.d(tag, "onError " + e.getMessage());
			}

			@Override
			public void onNext(Integer arg0) {
				// TODO Auto-generated method stub
				Log.d(tag, "onNext" + arg0);
			}

			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				Log.d(tag, "onSubscribe " + s);
				s.request(Long.MAX_VALUE);  //注意这句代码
			}
		};
		
		upstream.subscribe(downstream);
	}
	public void showFlowableSubscriberASync() {
		Flowable.create(new FlowableOnSubscribe<Integer>() {

			@Override
			public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
				// TODO Auto-generated method stub
				Log.d("Flowable thread name: ", Thread.currentThread().getName());
				emitter.onNext(0);
				Log.d(tag, "emit 1");
                emitter.onNext(1);
                Log.d(tag, "emit 2");
                emitter.onNext(2);
                Log.d(tag, "emit 3");
                emitter.onNext(3);
                Log.d(tag, "emit complete");
                emitter.onComplete();
			}
			
		}, BackpressureStrategy.ERROR)
				.subscribeOn(Schedulers.trampoline())
				.observeOn(Schedulers.trampoline())
				.subscribe(new Subscriber<Integer>() {

					@Override
					public void onComplete() {
						// TODO Auto-generated method stub
						Log.d(tag, "onComplete");
					}

					@Override
					public void onError(Throwable e) {
						// TODO Auto-generated method stub
						Log.d(tag, "onError " + e.getMessage());
					}

					@Override
					public void onNext(Integer arg0) {
						// TODO Auto-generated method stub
						Log.d("Subscriber thread name: ", Thread.currentThread().getName());
						Log.d(tag, "onNext" + arg0);
					}

					@Override
					public void onSubscribe(Subscription s) {
						// TODO Auto-generated method stub
						Log.d(tag, "onSubscribe " + s);
						s.request(20);  //if not exist this line, means Flowable think Subscriber no ability deal
						// else means Flowable knows the number of Subscriber can deal
					}
				});
	}
	
	private static Subscription mSubscription;
	
	public static void request(long n) {
		mSubscription.request(n);
	}
	
	public void showFlowableSubscriberASync2() {
		Flowable.create(new FlowableOnSubscribe<Integer>() {

			@Override
			public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
				Log.d("Flowable thread name: ", Thread.currentThread().getName());
				// TODO Auto-generated method stub
				for (int i=0; i<256; i++) {
					Log.d(tag, "emit " + i);
	                emitter.onNext(i);
	                Thread.sleep(10);
				}
                Log.d(tag, "emit complete");
                emitter.onComplete();
			}
			
		}, BackpressureStrategy.ERROR)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.trampoline())
				.subscribe(new Subscriber<Integer>() {

					@Override
					public void onComplete() {
						// TODO Auto-generated method stub
						Log.d(tag, "onComplete ");
					}

					@Override
					public void onError(Throwable e) {
						// TODO Auto-generated method stub
						Log.d(tag, "onError " + e.getMessage());
					}

					@Override
					public void onNext(Integer arg0) {
						Log.d("Subscriber thread name: ", Thread.currentThread().getName());
						// TODO Auto-generated method stub
						Log.d(tag, "onNext " + arg0);
					}

					@Override
					public void onSubscribe(Subscription s) {
						// TODO Auto-generated method stub
						Log.d(tag, "onSubscribe " + s);
						mSubscription = s;
						//s.request(20);  //if not exist this line, means Flowable think Subscriber no ability deal
						// else means Flowable knows the number of Subscriber can deal
					}
				});
	}
	
	public void showFlowableSubscriberASync3() {
		Flowable.create(new FlowableOnSubscribe<Integer>() {

			@Override
			public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
				Log.d("Flowable thread name: ", Thread.currentThread().getName());
				Log.d(tag, " capacity " + emitter.requested());
				// TODO Auto-generated method stub
				for (int i=0; i<1000; i++) {
					Log.d(tag, " emit " + i);
	                emitter.onNext(i);
//	                Thread.sleep(10);
				}
                Log.d(tag, "emit complete");
                emitter.onComplete();
			}
			
		}, BackpressureStrategy.DROP)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.trampoline())
				.subscribe(new Subscriber<Integer>() {

					@Override
					public void onComplete() {
						// TODO Auto-generated method stub
						Log.d(tag, "onComplete ");
					}

					@Override
					public void onError(Throwable e) {
						// TODO Auto-generated method stub
						Log.d(tag, "onError " + e.getMessage());
					}

					@Override
					public void onNext(Integer arg0) {
						Log.d("Subscriber thread name: ", Thread.currentThread().getName());
						// TODO Auto-generated method stub
						Log.d(tag, "onNext " + arg0);
					}

					@Override
					public void onSubscribe(Subscription s) {
						// TODO Auto-generated method stub
						Log.d(tag, "onSubscribe " + s);
						s.request(12);
						s.request(12);
						mSubscription = s;
						//s.request(20);  //if not exist this line, means Flowable think Subscriber no ability deal
						// else means Flowable knows the number of Subscriber can deal
					}
				});
	}
	
	public void runFlowableInterval() {
		Flowable.interval(1, TimeUnit.MILLISECONDS)
		.onBackpressureBuffer()
		.subscribeOn(Schedulers.io())
		.observeOn(Schedulers.trampoline())
		.subscribe(new Subscriber<Long>() {

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				Log.d(tag, " onComplete");
			}

			@Override
			public void onError(Throwable e) {
				// TODO Auto-generated method stub
				Log.d(tag, " onError" + e.getMessage());
			}

			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				Log.d(tag, " onSubscribe" + s);
				mSubscription = s;
				s.request(10);
			}

			@Override
			public void onNext(Long arg0) {
				// TODO Auto-generated method stub
				Log.d(tag, " onNext " + arg0);
				mSubscription.request(10);
			}
			
		});
	}
	
	public static void practice() {
        Flowable
                .create(new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                        try {
                            FileReader reader = new FileReader("guoxiang.txt");
                            BufferedReader br = new BufferedReader(reader);

                            String str;

                            while ((str = br.readLine()) != null && !emitter.isCancelled()) {
                                while (emitter.requested() == 0) {
                                    if (emitter.isCancelled()) {
                                        break;
                                    }
                                }
                                emitter.onNext(str);
                            }

                            br.close();
                            reader.close();

                            emitter.onComplete();
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.trampoline())
                .subscribe(new Subscriber<String>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(String string) {
                        System.out.println(string);
                        try {
                            Thread.sleep(500);
                            mSubscription.request(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println(t);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
	
	public void demo3() {
        Flowable
                .create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                        for (int i = 1; i <= 129; i++) {
                            e.onNext(i);
                        }
                        e.onComplete();
                    }
                }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.trampoline())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);            //注意此处，暂时先这么设置
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ignore) {
                        }
                        System.out.println(integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("接收----> 完成");
                    }
                });
    }
}


public class RxDebug {
	public static void main(String[] args) {
		System.gc();
		Rx rx = new Rx();
		/*
		rx.showFlowableSubscriberASync3();
		for (int i=0; i<10; i++) {
			Rx.request(128);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		rx.demo3();
		Rx.request(128);
	}
}
