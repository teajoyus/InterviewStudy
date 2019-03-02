package com.example.interviewstudy.hook;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Author:mihon
 * Time: 2019\3\1 0001.17:14
 * Description:This is AmsProxy
 */
public class AmsProxy implements InvocationHandler {
    //这个就是我们在构造器里面传入的需要代理的对象
    private Object mObject;

    public AmsProxy(Object object) {
        mObject = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        //这个方法是在我们代理的这个对象的每一个方法被调用的时候都会执行，所以，我们想要拦截startActivity方法，就必须在这里做参数的判断，如果符合条件，我们才去做替换
//        if (args != null) {
//            int index = -1;
//            for (int i = 0; i < args.length - 1; i++) {
//                if (args[i] instanceof Intent) {
//                    index = i;
//                }
//            }
//            if (index != -1) {
//                //这里我做的是，找到Intent参数，然后将他的ComponentName 参数设置假的Activity，并把真的ComponentName 保存起来，以便我们在另一边恢复
//                Log.d("linmh", "invoke:");
//                final Intent intent = (Intent) args[index];
//                final ComponentName intentComponent = intent.getComponent();
//                //这个ProxyActivity就是我自己创建的负责验证的Activity，这个是在清单文件已经注册了的
//                ComponentName componentProxy = new ComponentName(mContext, ProxyActivity.class);
//                intent.setComponent(componentProxy);
//                intent.putExtra("realComponentName", intentComponent);
//            }
//        }
        Log.i("linmh","proxy methed："+method.getName());
        //虽然我们在上面做了那么多的事情，但最终还是得调用原本对象的对应方法去继续执行，我们所做的就是在这个对象执行方法之前做一些我们自己的判断
        return method.invoke(mObject, args);
    }
}