package com.example.interviewstudy.router;

import android.net.Uri;
import android.os.Bundle;
 import androidx.annotation.Nullable;
import android.view.View;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;
import com.github.mzule.activityrouter.router.Routers;

/**
 * Author:mihon
 * Time: 2019\2\25 0025.9:15
 * Description:This is RouterDemoActivity
 */
public class RouterDemoActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router);
    }

    public void onClickA(View view) {

//        Routers.open(this,Uri.parse("demo://activityA"));
        Routers.open(this,Uri.parse("demo://router_demo"));

    }

    public void onClickB(View view) {
        Routers.open(this,Uri.parse("demo://activityB"));
    }
}
