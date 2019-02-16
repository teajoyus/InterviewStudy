package com.example.interviewstudy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Author:mihon
 * Time: 2019\2\15 0015.11:33
 * Description:This is BaseActivity
 */
public class BaseActivity extends AppCompatActivity {
    protected  MyLogger myLogger;
//    protected String TAG = getClass().getSimpleName();
    private static final boolean printLifeCycle = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLogger = new MyLogger(getClass().getSimpleName());
        if(printLifeCycle) {
            log_i("onCreate");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(printLifeCycle)
        log_i("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(printLifeCycle)
        log_i("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(printLifeCycle)
        log_i("onStop");
    }

    protected  void log_i(String str){
        myLogger.i(str);
    }
}
