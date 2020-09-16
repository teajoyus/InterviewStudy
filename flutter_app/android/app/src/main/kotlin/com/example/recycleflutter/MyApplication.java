package com.example.recycleflutter;

import android.app.Application;

import io.flutter.app.FlutterApplication;

/**
 * @Author linmh
 * @email 625321521@qq.com
 * @date 2020-08-20 17:45
 * @desc
 */
public class MyApplication extends FlutterApplication {
    public static MyApplication myApp;

    @Override
    public void onCreate() {
        myApp = this;
        super.onCreate();
    }
}
