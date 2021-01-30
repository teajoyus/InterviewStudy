package com.example.interviewstudy.launchermode;

import android.content.Intent;
import android.os.Bundle;
 import androidx.annotation.Nullable;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;

/**
 * Author:mihon
 * Time: 2019\2\16 0016.9:38
 * Description:This is LauncherModeActivity
 */
public class LauncherModeActivityB extends BaseActivity {
    public Class clazz=  LauncherModeActivityD.class;
                @Override
                protected void onCreate(@Nullable Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_launchermode);
                    TextView tv = findViewById(R.id.tv);
                    tv.setText("我是B");
                    Button btn = findViewById(R.id.btn);
                    btn.setText("跳转到："+clazz.getSimpleName());
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(LauncherModeActivityB.this,clazz);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
                    Intent intent = new Intent();
                    intent.putExtra("data","接受到返回数据");
                    setResult(100,intent);
        super.onBackPressed();

    }
}
