package com.example.interviewstudy;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.example.interviewstudy.hook.HookBActivity;

/**
 * Author:mihon
 * Time: 2019\2\15 0015.11:33
 * Description:This is BaseActivity
 */
public class BaseActivity extends AppCompatActivity {
    protected  static MyLogger myLogger;
    protected  static MyLogger lifeCycleLogger;
//    protected String TAG = getClass().getSimpleName();
    private static final boolean printLifeCycle = true;
    private static final boolean printBeforeLifeCycle = false;

    ComponentName fakeComponentName;
    @Override
    public ComponentName getComponentName() {
        return fakeComponentName!=null?fakeComponentName:super.getComponentName();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        fakeComponentName = getIntent().getParcelableExtra("fakeComponentName");
        myLogger = new MyLogger(getClass().getSimpleName());
        lifeCycleLogger = new MyLogger(getClass().getSimpleName(),"Life_Cycle@" + Integer.toHexString(hashCode()));
        if(printBeforeLifeCycle) {
            log_life("before super onCreate");
        }
        super.onCreate(savedInstanceState);
        if(printLifeCycle) {
            log_life("onCreate");
        }
    }

    @Override
    protected void onStart() {
        if(printBeforeLifeCycle) {
            log_life("before super onStart");
        }
        super.onStart();
        if(printLifeCycle)
            log_life("onStart");
    }

    @Override
    protected void onRestart() {
        if(printBeforeLifeCycle) {
            log_life("before super onRestart");
        }
        super.onRestart();
        if(printLifeCycle)
            log_life("onRestart");
    }
    //正常启动是在onAttachedToWindow之后
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(printBeforeLifeCycle) {
            log_life("before super onWindowFocusChanged："+hasFocus);
        }
        super.onWindowFocusChanged(hasFocus);
        if(printLifeCycle)
            log_life("onWindowFocusChanged："+hasFocus);
    }
    //在onResume之后
    @Override
    public void onAttachedToWindow() {
        if(printBeforeLifeCycle) {
            log_life("before super onAttachedToWindow");
        }
        super.onAttachedToWindow();
        if(printLifeCycle)
            log_life("onAttachedToWindow");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(printBeforeLifeCycle) {
            log_life("before super onCreateOptionsMenu");
        }
        super.onCreateOptionsMenu(menu);
        if(printLifeCycle) {
            log_life("onCreateOptionsMenu");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(printLifeCycle) {
            log_life("onPrepareOptionsMenu");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        if(printBeforeLifeCycle) {
            log_life("before super onResume");
        }
        super.onResume();
        if(printLifeCycle)
        log_life("onResume");
    }

    @Override
    protected void onPostResume() {
        if(printBeforeLifeCycle) {
            log_life("before super onPostResume");
        }
        super.onPostResume();
        if(printLifeCycle)
            log_life("onPostResume");
    }

    @Override
    protected void onPause() {
        if(printBeforeLifeCycle) {
            log_life("before super onPause");
        }
        super.onPause();
        if(printLifeCycle)
        log_life("onPause");
    }

    @Override
    protected void onStop() {
        if(printBeforeLifeCycle) {
            log_life("before super onStop");
        }
        super.onStop();
        if(printLifeCycle)
        log_life("onStop");
    }

    @Override
    protected void onDestroy() {
        if(printBeforeLifeCycle) {
            log_life("before super onDestroy");
        }
        super.onDestroy();
        log_life("onDestroy");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(printBeforeLifeCycle) {
            log_life("before super onNewIntent");
        }
        super.onNewIntent(intent);
        if(printLifeCycle)
            log_life("onNewIntent");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if(printBeforeLifeCycle) {
            log_life("before super onSaveInstanceState");
        }
        super.onSaveInstanceState(outState, outPersistentState);
        if(printLifeCycle)
            log_life("onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(printBeforeLifeCycle) {
            log_life("before super onRestoreInstanceState");
        }
        super.onRestoreInstanceState(savedInstanceState);
        if(printLifeCycle)
            log_life("onRestoreInstanceState");
    }
    @Override
    public void finish() {
        if(printBeforeLifeCycle) {
            log_life("before super finish");
        }
        super.finish();
        if(printLifeCycle)
            log_life("finish");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(printBeforeLifeCycle) {
            log_life("before super onActivityResult");
        }
        super.onActivityResult(requestCode,resultCode,data);
        if(printLifeCycle)
            log_life("onActivityResult");
    }

    protected  static void log_i(String str){
        myLogger.i(str);
    }
    protected  static void log_life(String str){
        lifeCycleLogger.i(str);
    }
    protected void startHookActivity(Intent intent){
        intent.putExtra("fakeComponentName", new ComponentName(getApplicationContext(), HookBActivity.class));
        startActivity(intent);
    }
    public BaseActivity getActivity(){
        return this;
    }
}
