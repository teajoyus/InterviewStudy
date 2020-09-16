package com.example.interviewstudy.trace;

import android.os.SystemClock;

import android.os.Bundle;
import android.view.View;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;

public class TraceMethedActivity extends BaseActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_trace_methed);
//
//    }

    public static void main(String[] args){
        System.out.println("123");
        final TraceMethedActivity activity = new TraceMethedActivity();
        new Thread(new Runnable() {
            @Override
            public void run() {
                activity.testANR();
            }
        }).start();

        SystemClock.sleep(10);
        activity.initView();
    }
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    new Thread(new Runnable() {
        @Override
        public void run() {
            testANR();
        }
    }).start();

    SystemClock.sleep(10);
    initView();
}

    private synchronized void testANR() {
        System.out.println("testANR");
        SystemClock.sleep(5 * 1000);
        System.out.println("testANR2");
    }

    private synchronized void initView(){
        System.out.println("initView");
    }

    public void onClicktrace(View view) {
        blockMethed1();
        blockMethed2();
        blockMethed3();
    }

    private void blockMethed1() {
        log_i("call blockMethed1");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log_i("call blockMethed1 end");

    }

    private void blockMethed2() {
        log_i("call blockMethed2");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log_i("call blockMethed2 end");

    }

    private void blockMethed3() {
        log_i("call blockMethed3");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log_i("call blockMethed3 end");

    }
}
