package com.example.interviewstudy.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;

public class HookAActivity extends BaseActivity {
    ComponentName fakeComponentName;
    @Override
    public ComponentName getComponentName() {
        return fakeComponentName!=null?fakeComponentName:super.getComponentName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fakeComponentName = getIntent().getParcelableExtra("fakeComponentName");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook_a);
    }
}