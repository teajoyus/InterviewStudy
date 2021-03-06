package com.example.interviewstudy.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
 import androidx.annotation.Nullable;

import com.example.interviewstudy.MyLogger;

/**
 * Author:mihon
 * Time: 2019\3\13 0013.15:36
 * Description:This is AService
 */
public class BaseService extends Service {
    protected MyLogger lifeCycleLogger = new MyLogger(getClass().getSimpleName(), "Life_Cycle@" + Integer.toHexString(hashCode()));

    @Override
    public void onCreate() {
        super.onCreate();
        log_life("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log_life("onStartCommand flags："+flags+",startId:"+startId);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        log_life("onStart");
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        log_life("onRebind");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        log_life("onBind");
        return null;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        log_life("onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        log_life("onDestroy");
        super.onDestroy();
    }

    protected void log_life(String str) {
        lifeCycleLogger.i(str);
    }
}
