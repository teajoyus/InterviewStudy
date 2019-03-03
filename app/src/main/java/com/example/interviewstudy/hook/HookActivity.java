package com.example.interviewstudy.hook;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.interviewstudy.R;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Author:mihon
 * Time: 2019\2\27 0027.10:52
 * Description:This is HookActivity
 */
public class HookActivity extends AppCompatActivity {
    private static final String TAG = "lc_miao";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook);
        hookAMS();
        hookActivityThread();
        testRegistActivity();
        findViewById(R.id.btn_hook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HookActivity.this, HookAActivity.class);
                intent.putExtra("fakeComponentName", new ComponentName(getApplicationContext(), HookBActivity.class));
                startActivity(intent);
            }
        });
    }

    private void testRegistActivity() {
        ComponentName componentName = new ComponentName(getApplicationContext(), HookAActivity.class);
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(componentName, PackageManager.GET_META_DATA);
            Log.i(TAG, "有注册HookAActivity");
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "无注册HookAActivity");
        }
    }

    private void hookActivityThread() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThread = activityThread.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThread.setAccessible(true);
            //这里拿到了ActivityThread实例
            Object activityThreadObject = sCurrentActivityThread.get(null);
            Log.i(TAG, "ActivityThread实例：" + activityThreadObject);
            Field mH = activityThread.getDeclaredField("mH");
            mH.setAccessible(true);
            //通过ActivityThread实例拿到它的内部类 H类的实例,我们已经知道它继承Handler
            Handler h = (Handler) mH.get(activityThreadObject);
            Log.i(TAG, "ActivityThread的内部类 H类实例：" + h);
            hookActivityThreadH(h);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void hookActivityThreadH(Handler h) {
        try {
            final Field LAUNCH_ACTIVITY = h.getClass().getDeclaredField("LAUNCH_ACTIVITY");
            LAUNCH_ACTIVITY.setAccessible(true);
            //得到Activity启动的消息代号
            final int launch_msg = LAUNCH_ACTIVITY.getInt(h);
            //拿到ActivityClientRecord类，注意它是ActivityThread一个内部类，所以类全称时要用$符号
            final Class<?> activityClientRecord = Class.forName("android.app.ActivityThread$ActivityClientRecord");
            Handler.Callback callback = new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == launch_msg) {
                        Log.i(TAG, "拦截在handleMessage中启动Activity的消息");
                        //根据源码得知，这个msg.obj就是ActivityClientRecord实例
                        Object o = msg.obj;
                        try {
                            //获取Intent对象
                            final Field intentField = activityClientRecord.getDeclaredField("intent");
                            intentField.setAccessible(true);
                            Intent intent = (Intent) intentField.get(o);
                            //拿到真实的ComponentName
                            ComponentName componentName = intent.getParcelableExtra("realComponentName");
                            if (componentName != null) {
                                Log.i(TAG, "handleMessage中还原真实的Activity:" + componentName.getClassName());
                                //还原ComponentName
                                intent.setComponent(componentName);
                            }
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
            };
            //
            final Field mCallback = h.getClass().getSuperclass().getDeclaredField("mCallback");
            mCallback.setAccessible(true);
            //设置我们的Callback
            mCallback.set(h, callback);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void hookAMS() {
        try {
            //得到ActivityManagerNative这个类
            Class<?> activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            //拿到gDefault字段，切记这里不要用getField方法，因为gDefault这个变量是私有的，用getField方法只能拿到public字段的
            Field gDefault = activityManagerNative.getDeclaredField("gDefault");
//            Log.i(TAG,"Field gDefault:"+ gDefault.getName());
            //注意设置一下访问权限
            gDefault.setAccessible(true);
            //拿到gDefault这个变量，由于是静态成员变量，所以这里无需传入具体对象，只要传null即可
            Object singleton = gDefault.get(null);
            //打印个类名 证明下我们拿到的
            Log.i(TAG, "Object singleton:" + singleton);
            /**
             * 注意这里用singleton.getClass().getSuperclass()，因为singleton.getClass()是一个匿名内部类对象
             * 并不能拿到声明的mInstance字段，所以通过它的getSuperclass()则声明了这个字段
             * 不过当然这里也可以Class.forName("android.util.Singleton");来拿到这个类
             */

            Field mInstance = singleton.getClass().getSuperclass().getDeclaredField("mInstance");
            mInstance.setAccessible(true);
            //这里由于mInstance不是静态的，所以就需要传入具体对象咯，这里具体对象则就是singleton
            Object ams = mInstance.get(singleton);
            //打印个类名 证明下我们拿到的mInstance
            Log.i(TAG, "Object mInstance:" + ams);
            replaceAmpInstance(singleton, mInstance, ams);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void replaceAmpInstance(Object obj, Field field, Object mInstance) {

        try {
            AmsProxy amsProxy = new AmsProxy(mInstance);
            Class<?> aClass = Class.forName("android.app.IActivityManager");
            Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{aClass}, amsProxy);
            field.setAccessible(true);
            field.set(obj, proxyInstance);
            //test
            Object newInstance = field.get(obj);
            //如果有打印输出true证明我们已经替换了实例
            Log.i(TAG, "newInstance==amsProxy?:" + (newInstance == proxyInstance));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
