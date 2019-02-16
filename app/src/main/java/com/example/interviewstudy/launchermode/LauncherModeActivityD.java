package com.example.interviewstudy.launchermode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.interviewstudy.R;

/**
 * Author:mihon
 * Time: 2019\2\16 0016.9:38
 * Description:This is LauncherModeActivity
 */
public class LauncherModeActivityD extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launchermode);
        TextView tv = findViewById(R.id.tv);
        tv.setText("我是D");
        Button btn = findViewById(R.id.btn);
        btn.setText("跳转到A");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherModeActivityD.this,LauncherModeActivity.class));
            }
        });
    }
}
