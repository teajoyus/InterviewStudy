package com.example.interviewstudy.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.example.interviewstudy.BaseActivity;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:mihon
 * Time: 2019\1\25 0025.11:38
 * Description:This is SlideViewActivity
 */
public class RxJavaActivity extends BaseActivity {
    private static final String TAG = RxJavaActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 1; i < 4; i++) {
                    Log.i(TAG, "观察者1发送事件：" + i);
                    emitter.onNext(i);
                    Thread.sleep(1000);
                }
//                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Exception {
                String[] arr = new String[]{"A", "B", "C", "D"};
                for (int i = 0; i < arr.length; i++) {
                    Log.i(TAG, "观察者2发送事件：" + arr[i]);
                    emitter.onNext(arr[i]);
                    Thread.sleep(1000);
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable, observable2, new BiFunction<Integer, String, String>() {

            @NonNull
            @Override
            public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                return "收到和并：" + integer + "+" + s;
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.d(TAG, "onNext:" + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

        testRxjava2();
    }

    Subscription subscription;
    private void testRxjava2() {
        Flowable.just(1, 2, 3, 4).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        Log.i(TAG,"testRxjava2() onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG,"testRxjava2() onNext"+integer);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        subscription.request(2);

    }

}
