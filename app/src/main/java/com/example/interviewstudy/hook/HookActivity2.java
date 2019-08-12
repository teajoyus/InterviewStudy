package com.example.interviewstudy.hook;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Author:mihon
 * Time: 2019\2\27 0027.10:52
 * Description:This is HookActivity
 */
public class HookActivity2 extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hookAMS();
        hookActivityThread();
        hookPMS();
        setContentView(R.layout.activity_hook);
        findViewById(R.id.btn_hook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HookActivity2.this,HookAActivity.class);
                ComponentName componentName = new ComponentName(getPackageName(),HookBActivity.class.getName());
                log_i("componentName"+componentName);
                intent.putExtra("shellComponentName",componentName);
                startActivity(intent);
            }
        });
    }


    private void hookActivityThread2() {
        try {
            final Class<?> activityThread = Class.forName("android.app.ActivityThread");
            final Field sCurrentActivityThread = activityThread.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThread.setAccessible(true);
            Object o = sCurrentActivityThread.get(null);
            log_i("hookActivityThread o:"+o);
            ActivityThreadProxy proxy = new ActivityThreadProxy(o);
            Object proxyObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{activityThread}, proxy);
            sCurrentActivityThread.set(o,proxyObject);
             o = sCurrentActivityThread.get(null);
            log_i("hookActivityThread o2:"+o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private void hookActivityThread() {
        try {
            final Class<?> activityThread = Class.forName("android.app.ActivityThread");
            final Field sCurrentActivityThread = activityThread.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThread.setAccessible(true);
            Object o = sCurrentActivityThread.get(null);
            log_i("hookActivityThread o:"+o);
            final Field mH = activityThread.getDeclaredField("mH");
            mH.setAccessible(true);
            Handler handler = (Handler) mH.get(o);
            log_i("hookActivityThread handler:"+handler);
//            ActivityThreadProxy proxy = new ActivityThreadProxy(o);
//            Object proxyObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{activityThread}, proxy);
//            sCurrentActivityThread.set(o,proxyObject);
//             o = sCurrentActivityThread.get(null);
//            log_i("hookActivityThread o2:"+o);
            final Field mCallback = handler.getClass().getSuperclass().getDeclaredField("mCallback");
            mCallback.setAccessible(true);
            final Field LAUNCH_ACTIVITY = handler.getClass().getDeclaredField("LAUNCH_ACTIVITY");
            LAUNCH_ACTIVITY.setAccessible(true);
            final int launch_msg = LAUNCH_ACTIVITY.getInt(handler);

            final Class<?> activityClientRecord = Class.forName("android.app.ActivityThread$ActivityClientRecord");

            Handler.Callback callback = new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
//                    log_i("handleMessage msg what:"+msg.what);
                    if(msg.what==launch_msg){
                        Object o = msg.obj;
                        try {
                            final Field intent = activityClientRecord.getDeclaredField("intent");
                            intent.setAccessible(true);
                            Intent intent1 = (Intent) intent.get(o);
                            ComponentName componentName = intent1.getParcelableExtra("realComponentName");
                            log_i("handleMessage componentName:"+componentName);
                            intent1.setComponent(componentName);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
            };
            mCallback.setAccessible(true);
            log_i("mCallback:"+mCallback.get(handler));
            mCallback.set(handler,callback);
            log_i("mCallback2:"+mCallback.get(handler));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void hookAMS() {
        try {
            //先获取ActivityManager的class文件
            final Class<?> activityManager = Class.forName("android.app.ActivityManagerNative");
//            Field[] fields = activityManager.getDeclaredFields();
//            for (Field field:fields){
//                log_i(field.getName());
//            }
            //通过反射机制获取ActivityManager类的iActivityManagerSingleton字段
            final Field gDefault = activityManager.getDeclaredField("gDefault");
            log_i("gDefault:"+gDefault);
            gDefault.setAccessible(true);
            Object gDefaultObject = gDefault.get(null);
            log_i("gDefaultObject:"+gDefaultObject);

            final Class<?> singleton = Class.forName("android.util.Singleton");

            final Field mInstance = singleton.getDeclaredField("mInstance");
            mInstance.setAccessible(true);
            final Object mAms = mInstance.get(gDefaultObject);

            log_i("mAms:"+mAms);
            AmsProxy proxy = new AmsProxy(mAms);
            final Class<?> aClass = Class.forName("android.app.IActivityManager");
            //使用Proxy生成代理对象，第一个参数是类加载器。第二个是对象继承的接口，第三个就是我们的代理了，然后会返回一个对象，这个对象就会在系统运行时被插入到系统里面去执行AMS的startActivity功能
            final Object o = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{aClass}, proxy);
            log_i("o:"+o);
            //这里是将我们构造出来的代理对象扔给系统
            mInstance.set(gDefaultObject, o);

            final Object proxyAms = mInstance.get(gDefaultObject);
            log_i("proxyAms:"+proxyAms);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
                e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void hookPMS() {
        //            Class<?> PackageManagerService = Class.forName("com.android.server.pm.PackageManagerService");
//            final Field
        log_i(  "getPackageManager:"+getPackageManager());

    }
    private void hookAMS2() {
        try {
            //先获取ActivityManagerNative的class文件
            final Class<?> activityManager = Class.forName("android.app.ActivityManagerNative");
            //通过反射机制获取ActivityManagerNative类的iActivityManagerSingleton字段
            final Field gDefault = activityManager.getDeclaredField("gDefault");
            log_i("gDefault:"+gDefault);
            gDefault.setAccessible(true);
            Object gDefaultObject = gDefault.get(null);
            log_i("gDefaultObject:"+gDefaultObject);

            final Class<?> singleton = Class.forName("android.util.Singleton");

            final Field mInstance = singleton.getDeclaredField("mInstance");
            mInstance.setAccessible(true);
            final Object mAms = mInstance.get(gDefaultObject);

            log_i("mAms:"+mAms);
            AmsProxy proxy = new AmsProxy(mAms);
            final Class<?> aClass = Class.forName("android.app.IActivityManager");
            //使用Proxy生成代理对象，第一个参数是类加载器。第二个是对象继承的接口，第三个就是我们的代理了，然后会返回一个对象，这个对象就会在系统运行时被插入到系统里面去执行AMS的startActivity功能
            final Object o = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{aClass}, proxy);
            log_i("o:"+o);
            //这里是将我们构造出来的代理对象扔给系统
            mInstance.set(gDefaultObject, o);

            final Object proxyAms = mInstance.get(gDefaultObject);
            log_i("proxyAms:"+proxyAms);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
