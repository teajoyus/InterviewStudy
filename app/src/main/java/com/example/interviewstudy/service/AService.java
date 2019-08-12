package com.example.interviewstudy.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Author:mihon
 * Time: 2019\3\13 0013.15:36
 * Description:This is AService
 */
public class AService extends BaseService {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
         super.onBind(intent);
         return new MyBind();
    }
    class MyBind extends Binder{
        public String getString(){
            return "我是binder";
        }
    }
}
