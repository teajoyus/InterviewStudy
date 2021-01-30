package com.example.interviewstudy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class FlowActivity extends BaseActivity {
    FlowLayout flowLayout;
    Random random = new Random();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    sleep(15000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//        handler.sendEmptyMessageDelayed(1,10000);
        flowLayout = findViewById(R.id.flowLayout);
        flowLayout.removeAllViews();
        flowLayout.setLineSpace(40);
        List<String> textList = new ArrayList<>();
        textList.add("我文字特别长的，我一定会占满一行。看看吧，已经占满一行了");
        textList.add("哈哈哈");
        textList.add("哈哈");
        textList.add("我短不说话");
        textList.add("哈的的哈");
        textList.add("哈哈");
        textList.add("哈哈我也短");
        textList.add("哈哈你好");
        textList.add("我文字也是特别长的，我也是一定会占满一行。看看吧，我也占满一行了");
        textList.add("随便写");
        textList.add("随便打点什么文字");
        textList.add("哈哈");
        textList.add("后面是来自wanandroid提供的搜索热词api");
        for (String string : textList) {
            flowLayout.addView(makeTextView(string));
        }
        initData();
    }

    private void initData() {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Builder()
                .url("https://www.wanandroid.com//hotkey/json")
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                final HotKey hotKey = gson.fromJson(response.body().string(), HotKey.class);
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < hotKey.getData().size(); i++) {
                            flowLayout.addView(makeTextView(hotKey.getData().get(i).getName()));
                        }
                    }
                });
            }
        });

    }

    public TextView makeTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setPadding(12, 12, 12, 12);
        ViewGroup.MarginLayoutParams mp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mp.leftMargin = 20;
        mp.rightMargin = 20;
        textView.setLayoutParams(mp);
        int randomColor = Color.rgb(random.nextInt(150) + 100, random.nextInt(150) + 100, random.nextInt(150) + 100);
        textView.setBackgroundColor(randomColor);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
            }
        });
        return textView;
    }


}
