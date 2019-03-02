package com.example.interviewstudy.hook;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.example.interviewstudy.BaseActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Author:mihon
 * Time: 2019\2\27 0027.10:52
 * Description:This is HookActivity
 */
public class HookActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hookAMS();
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

}
