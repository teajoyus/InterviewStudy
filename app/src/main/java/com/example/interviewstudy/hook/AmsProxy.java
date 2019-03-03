package com.example.interviewstudy.hook;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AmsProxy implements InvocationHandler {
    //传入的需要代理的对象
    private Object mObject;

    public AmsProxy(Object object) {
        mObject = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //这里我们就可以拦截相关的方法了
        if(method.getName().equals("startActivity")){
            Log.i("lc_miao","拦截到调用startActivity方法");
//            if(args!=null&&args.length>0){
//                for (int i = 0; i < args.length; i++) {
//                    Log.i("lc_miao","第"+i+"个参数是："+args[i]);
//                }
//            }
            for (Object arg:args){
                if(arg instanceof Intent){
                    Intent intent = (Intent) arg;
                    //这是我们真正想要启动的Activity
                    ComponentName componentName = intent.getComponent();
                    Log.i("lc_miao","要跳转的目标Activity："+componentName.getClassName());
                    //在启动Activity的时候，在Intent里面传入一个假的已经注册的Activity的ComponentName
                    ComponentName fakeComponentName =intent.getParcelableExtra("fakeComponentName");
                    if(fakeComponentName!=null){
                        Log.i("lc_miao","拿来伪造的Activity："+fakeComponentName.getClassName());
                        //真的ComponentName要在后面的流程被还原出来，那么这里也有个存档
                        intent.putExtra("realComponentName",componentName);
                        //设置假的ComponentName
                        intent.setComponent(fakeComponentName);
                    }
                    //不用遍历其他参数了
                    break;
                }
            }
        }
        return method.invoke(mObject, args);
    }
}