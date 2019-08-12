package com.example.interviewstudy.hook;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author:mihon
 * Time: 2019\3\4 0004.15:16
 * Description:This is HookApplication
 */
public class HookApplication extends Application {
    private static final String TAG = "lc_miao";

    @Override
    public void onCreate() {
        Log.i(TAG,"HookApplication onCreate");
        super.onCreate();
//        hookInstrumentation();
    }

    private void hookInstrumentation() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Field mMainThread = getBaseContext().getClass().getDeclaredField("mMainThread");
            mMainThread.setAccessible(true);
            Object o = mMainThread.get(getBaseContext());
            Log.i(TAG, "mMainThread:" + o);
            Field mInstrumentation = o.getClass().getDeclaredField("mInstrumentation");
            mInstrumentation.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) mInstrumentation.get(o);
            Log.i(TAG, "instrumentation:" + instrumentation);
            InstrumentationProxy proxy = new InstrumentationProxy(instrumentation);
            mInstrumentation.set(o, proxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static class InstrumentationProxy extends Instrumentation {
        protected Instrumentation instrumentation;

        public InstrumentationProxy(Instrumentation instrumentation) {
            this.instrumentation = instrumentation;
        }

        public ActivityResult execStartActivity(
                Context who, IBinder contextThread, IBinder token, Activity target,
                Intent intent, int requestCode, Bundle options) {
            Log.i(TAG, "proxy execStartActivity");
//            Method[] methods = getClass().getDeclaredMethods();
//            for (int i = 0; i < methods.length; i++) {
//                if(methods[i].getName().equals("execStartActivity")){
//
//                }
//            }
            //在启动Activity的时候，在Intent里面传入一个假的已经注册的Activity的ComponentName
            ComponentName fakeComponentName =intent.getParcelableExtra("fakeComponentName");
            if(fakeComponentName!=null){
                intent.putExtra("realComponentName",intent.getComponent());
                intent.setComponent(fakeComponentName);
            }
            Class[] classes = new Class[]{Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class};
            try {
//                instrumentation.execStartActivity(who, contextThread, token, target,
//                        intent, requestCode, options);
                Method method = getClass().getSuperclass().getDeclaredMethod("execStartActivity", classes);
                Log.i(TAG, "proxy execStartActivity Method:" + method);
                if (method != null) {
                    return (ActivityResult) method.invoke(instrumentation, new Object[]{who, contextThread, token, target, intent, requestCode, options});
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Activity newActivity(ClassLoader cl, String className,
                                    Intent intent)throws InstantiationException, IllegalAccessException,
                ClassNotFoundException {
            Log.i(TAG, "proxy newActivity");
            //拿到真实的ComponentName
            ComponentName componentName = intent.getParcelableExtra("realComponentName");
            if (componentName != null) {
                className = componentName.getClassName();
            }
           return instrumentation.newActivity(cl,className,intent);

        }
    }
}
