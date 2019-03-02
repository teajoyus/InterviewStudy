package com.example.interviewstudy.router;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.interviewstudy.BaseActivity;
import com.github.mzule.activityrouter.annotation.Router;

/**
 * Author:mihon
 * Time: 2019\2\25 0025.9:15
 * Description:This is RouterDemoActivity
 */
@Router("activityB")
public class RouterActivityB extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tv.setText("RouterActivityB");
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(30);
        setContentView(tv);
    }
}
