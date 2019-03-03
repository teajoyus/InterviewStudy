package com.example.interviewstudy.hook;

import android.content.ComponentName;
import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ActivityThreadProxy implements InvocationHandler {
    //这个就是我们在构造器里面传入的需要代理的对象
    private Object mObject;

    public ActivityThreadProxy(Object object) {
        mObject = object;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equals("handleLaunchActivity")){
            if(args!=null&&args.length>0){
                for (int i = 0; i < args.length; i++) {
                    if(args[i] instanceof Intent){
                        Intent intent = (Intent) args[i];
                        ComponentName componentName = intent.getParcelableExtra("fakeComponentName");
                        if(componentName!=null){
                            intent.setComponent(componentName);
                        }
                        break;
                    }
                }
            }
        }
        return method.invoke(mObject,args);
    }
}
