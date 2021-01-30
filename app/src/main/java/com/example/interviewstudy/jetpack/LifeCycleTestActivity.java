package com.example.interviewstudy.jetpack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import android.os.Build;
import android.os.Bundle;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;

import java.util.logging.Logger;

public class LifeCycleTestActivity extends BaseActivity {
    private static final String REPORT_FRAGMENT_TAG = "androidx.lifecycle"
            + ".LifecycleDispatcher.report_fragment_tag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_cycle_test);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //默认插入了一个ReportFragment 注意是在getFragmentManager里 不是在getSupportFragmentManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            log_i("getFragments :"+ getFragmentManager().getFragments());
            log_i("getFragments :"+ getSupportFragmentManager().getFragments());
        }
        log_i("getFragments :"+ getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass());
    }

    @Override
    protected void onPause() {
        super.onPause();
        getLifecycle().addObserver(new androidx.lifecycle.LifecycleEventObserver(){

            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                log_i("onStateChanged event:"+event.name());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}