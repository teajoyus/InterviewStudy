package com.example.interviewstudy.launchermode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;

/**
 * Author:mihon
 * Time: 2019\2\16 0016.9:38
 * Description:This is LauncherModeActivity
 */
public class LauncherModeActivity extends BaseActivity {
    public Class clazz = LauncherModeActivity.class;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launchermode);



        TextView tv = findViewById(R.id.tv);
        tv.setText("我是A");
        log_i("硬件加速" + tv.isHardwareAccelerated());
        Button btn = findViewById(R.id.btn);

        btn.setText("跳转到：" + clazz.getSimpleName());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LauncherModeActivity.this, clazz);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 200);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent();
        intent.putExtra("data", "setResult方法被调用");
        setResult(100, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log_i("onActivityResult");
        if (data != null) {
            log_life("onActivityResult data: " + data.getStringExtra("data"));
        } else {
            log_life("onActivityResult intent= null");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
