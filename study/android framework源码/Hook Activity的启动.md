# Hook作用

**Hook就是利用反射机制动态替换运行过程中的对象，以进行方法拦截（动态代理），也就是在执行方法的前后，能执行我们自己的代码。**

本篇演示的是实现插件化中的一种思路：hook技术。基于hook，我们可以后台下发某些插件（apk，无需安装），在启动插件的Activity时由于宿主APK并没有进行注册，所以启动不了。基于hook技术后，我们可以事先在宿主APK中安插一些已经注册的空Activity，来达到运行插件apk中的Activity。

# Hook入口分析

要启动一个没有被注册过的Activity，有一种思路就是用一个已经注册过的Activity去欺骗AMS和PMS的检查，
然后真正创建Ativity和启动的时机替换成真的Activity。

在追踪了Actvity启动流程之后我们知道，在Activity中通过startActivity后会进入到Instrumentation执行它的execStartActivity方法
然后之后会经历AMS的过程，最终发送消息给ActivityThread去调用handleLaunchActivity方法。

那么我们必须在启动流程走向AMS之前就先替换掉我们的Activity信息，在经过AMS之后，到达ActivityThread时进行还原。

# 准备工作

1、本篇是基于Android M （SDK 23）的版本，所以无论是查看FrameWork源码，还是运行模拟器，最好都是基于这个版本来，不然的话就只能根据本文锁阐述的方法和原理去hook你自己的Android版本。

因为版本不同，里面的变量定义和相关类流程是有所差别的。

2、要理解本篇内容，最好是已经大致了解Activity的启动过程，如果您你没了解过，建议参考：

 [《Android开发知识（二十）Activity的启动过程源码追踪，看看startActivity方法背后干了什么事》](https://blog.csdn.net/lc_miao/article/details/88037499).


关键的点在于理解startActivity的流程

3、对Handler使用机制也有一些了解。如果不是特别了解的话，也可以参考：
 [《Android开发知识（五）消息处理机制Handler+Looper+MessageQueue的原理分析（上）》](https://blog.csdn.net/lc_miao/article/details/77504343).

# 在AMS上做hook入口
本篇章先演示的是利用AMS做hook入口，下面会继续演示 hook Instrumentation
## 反射获取ActivityManagerProxy实例
我们在了解了Activity的启动过程之后，知道在Instrumentation的execStartActivity会执行：
```
 int result = ActivityManagerNative.getDefault()
                .startActivity(whoThread, who.getBasePackageName(), intent,
                        intent.resolveTypeIfNeeded(who.getContentResolver()),
                        token, target != null ? target.mEmbeddedID : null,
                        requestCode, 0, null, options);
```

启动逻辑交给了ActivityManagerNative.getDefault()这个返回的类的startActivity，
而ActivityManagerNative.getDefault()则是：
```
  static public IActivityManager getDefault() {
        return gDefault.get();
    }
private static final Singleton<IActivityManager> gDefault = new Singleton<IActivityManager>() {
        protected IActivityManager create() {
            IBinder b = ServiceManager.getService("activity");
            if (false) {
                Log.v("ActivityManager", "default service binder = " + b);
            }
            IActivityManager am = asInterface(b);
            if (false) {
                Log.v("ActivityManager", "default service = " + am);
            }
            return am;
        }
    };
```
而Singleton类也是android.util包下的一个隐藏类，代码如下：
```
package android.util;

/**
 * Singleton helper class for lazily initialization.
 *
 * Modeled after frameworks/base/include/utils/Singleton.h
 *
 * @hide
 */
public abstract class Singleton<T> {
    private T mInstance;

    protected abstract T create();

    public final T get() {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create();
            }
            return mInstance;
        }
    }
}
```
在这里gDefault这变量则是一个Singleton类，内部是维护一个mInstance变量，这里这个变量就是实现IActivityManager接口的单例对象
那么我们这里想要在ActivityManagerNative.getDefault()的startActivity中做hook，那么前提是要能拿到这个单例对象
拿到了之后，我们采用一个动态代理的方式，去把这个单例对象给替换成我们自己写的代理对象，以便让在执行startActivity的时候可以让我们插入自己的代码，这里我们要插入的代码就是把Intent中的ComponentName替换成一个已经注册过的Activity，以达到绕过注册检查环节的作用

那么怎么拿到这个实例呢？
首先，这个对象是被放在ActivityManagerNative的gDefault静态变量中，

那么首先，我们得先获取这个gDefault静态变量吧？

其次，获取到这个变量后，我们要拿到这个变量里面维护的mInstance变量

好，思路就先到这里，我们来实现一个反射出这个mInstance变量的代码


在这里我为了演示，我们就新建一个HookActivity吧，然后在里面运行打印出来：
```
import android.os.Bundle;
 import androidx.annotation.Nullable;
 
import android.util.Log;
import com.example.hook.R;
import java.lang.reflect.Field;

public class HookActivity extends AppCompatActivity {
    private static final String TAG = "lc_miao";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook);
        hookAMS();

    }

    private void hookAMS() {
        try {
            //得到ActivityManagerNative这个类
            Class<?>  activityManagerNative= Class.forName("android.app.ActivityManagerNative");
            //拿到gDefault字段，切记这里不要用getField方法，因为gDefault这个变量是私有的，用getField方法只能拿到public字段的
            Field gDefault = activityManagerNative.getDeclaredField("gDefault");
//            Log.i(TAG,"Field gDefault:"+ gDefault.getName());
            //注意设置一下访问权限
            gDefault.setAccessible(true);
            //拿到gDefault这个变量，由于是静态成员变量，所以这里无需传入具体对象，只要传null即可
            Object singleton = gDefault.get(null);
            //打印个类名 证明下我们拿到的
            Log.i(TAG,"Object singleton:"+ singleton);
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
            Log.i(TAG,"Object mInstance:"+ ams);
            //替换mInstance实例，空方法，下面再实现
             replaceAmpInstance(singleton,mInstance,ams);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    //替换ActivityManagerProxy实例
    private void replaceAmpInstance(Object obj,Field field,Object mInstance){
        //TODO 具体内容待实现
        ...
    }

}

```
好了，运行一下，在Logcat里输入一下“lc_miao”来过滤看结果：

![证明下我们拿到的mInstance](https://img-blog.csdnimg.cn/20190303190028696.png)


nice，可以看到。我们打印出来了mInstance这个实例，其中我们也发现了原来这个实例是一个android.app.ActivityManagerProxy类

如果你已经了解过Activity启动流程，对这里的ActivityManagerProxy就不足已为其了，ActivityManagerService是一个system server，我们并没法直接使用它，所以在AIDL机制中用了代理方式来间接通信，这里就是用了ActivityManagerProxy来间接访问AMS

好了，到这一步，我们已经拿到这个实例了，那么如何拦截这个startActivity方法呢？


## 动态代理ActivityManagerProxy
前面说到，我们的思路是编写一个代理类来替换掉这个实例，以代理这个实例的方法。

在这里如果你之前没怎么了解过代理模式的话，建议你先到别处了解一下（特别是java中的InvocationHandler接口和Proxy.newProxyInstance方法的使用）

这里我们新建一个AmsProxy：
```
package com.example.hook;

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
            if(args!=null&&args.length>0){
                for (int i = 0; i < args.length; i++) {
                    Log.i("lc_miao","第"+i+"个参数是："+args[i]);
                }
            }
        }
        return method.invoke(mObject, args);
    }
}
```

这里我们先不对方法去处理具体的拦截，我们先只打印出来，证明我们能拦截方法就好了。

接下来我们继续编写代码，让这个实例替换成我们的代理对象。
这里我为了单独体现出替换实例的过程，所以便单独抽离出了一个replaceAmpInstance方法，我们来完成它：

```
 private void replaceAmpInstance(Object obj,Field field,Object mInstance){

        try {
            AmsProxy amsProxy = new AmsProxy(mInstance);
            //Amp的方法来自IActivityManager接口
            Class<?> aClass = Class.forName("android.app.IActivityManager");
            //这里我们要动态代理的是IActivityManager接口下的方法，IActivityManager里面就包含了startActivity方法
            Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{aClass},amsProxy);
            field.setAccessible(true);
            field.set(obj,proxyInstance);
            //test
            Object newInstance = field.get(obj);
            //如果有打印输出true证明我们已经替换了实例
            Log.i(TAG,"newInstance==amsProxy?:"+(newInstance==proxyInstance));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
```

好了，运行下，在Logcat里输入一下“lc_miao”来过滤看结果：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190303194144910.png)

可以看到正是打印出了 newInstance==amsProxy?:true，说明没毛病。

接下来，我们测试下方法代理，也就是拦截方法做出打印。

为了测试我们加个跳转，新建一个HookAActivity.java
然后在HookActivity里面增加个按钮点击事件：
```
findViewById(R.id.btn_hook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HookActivity.this,HookAActivity.class);
                startActivity(intent);
            }
        });
```

接下来，运行，然后点击跳转，在Logcat里输入一下“lc_miao”来过滤看结果：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190303194344608.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xjX21pYW8=,size_16,color_FFFFFF,t_70)

这一切似乎有点神奇，我们竟然真的拦截到了startActivity方法，并且打印了出来，包括它的参数列表。

好了，到这一步我们已经小有成果！

## 欺骗AMS权限检查过程

接下来，我们要如何利用一个已经注册过的Activity来骗过AMS呢？

换另外一个方式说，我们启动的目标Activity是放在Intent里面的，而Intent里面指向要跳转的目标Activity，则放在了ComponentName中
在我们使用:
```
 Intent intent = new Intent(HookActivity.this,HookAActivity.class);

```
点击进去看Intent这个构造方法则可以看出来：
```
    public Intent(Context packageContext, Class<?> cls) {
        mComponent = new ComponentName(packageContext, cls);
    }
    public ComponentName(@NonNull Context pkg, @NonNull Class<?> cls) {
        mPackage = pkg.getPackageName();
        mClass = cls.getName();
    }
```

它内部就是维护一个ComponentName对象，存放了我们要跳转的包名和类名

所以我们要把这个ComponentName对象替换成我们已经注册过的Activity的ComponentName。

也就是说，在拦截了startActivity方法后，对它的Intent参数进行ComponentName替换，再让它去执行这个方法，这样对AMS来说 就是要启动那个注册过了Activity了

我们新建一个HookBActivity.java，然后也在AndroidManifest.xml注册，这个HookBActivity我们用来欺骗AMS的。

然后，
把代理类的方法改一下，之前只是做打印，我们现在要来实现把Intent参数替换内部的ComponentName

改动如下：

```
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //这里我们就可以拦截相关的方法了
        if(method.getName().equals("startActivity")){
            Log.i("lc_miao","拦截到调用startActivity方法");
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
```

然后我们在启动的时候，给Intent put一个假的ComponentName：
```
    findViewById(R.id.btn_hook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HookActivity.this,HookAActivity.class);
                intent.putExtra("fakeComponentName",new ComponentName(getApplicationContext(),HookBActivity.class));
                startActivity(intent);
            }
        });
```

其实到这一步，我们能看到的效果就是，点击按钮实际启动的是HookBActivity。

就是这么神奇，我们来运行试一下：

模拟器运行截图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190303200829942.png)

日志输出：


![在这里插入图片描述](https://img-blog.csdnimg.cn/20190303200748835.png)

从运行结果来看，我们的确是启动了伪造的HookBActivity了，到这里我们已经离成功不远了

那么我们在哪个环节去把真的Activity还原出来呢？

## 还原目标Activity的启动

我们知道，在经过AMS后来到了我们熟悉的ActivityThread

当启动流程**调用到ActivityThread时，是ActivityThread里面的一个内部类H（继承于Handler），它接收到了一个启动Activity的消息：**

```
public void handleMessage(Message msg) {
            if (DEBUG_MESSAGES) Slog.v(TAG, ">>> handling: " + codeToString(msg.what));
            switch (msg.what) {
                case LAUNCH_ACTIVITY: {
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStart");
                    final ActivityClientRecord r = (ActivityClientRecord) msg.obj;
                    r.packageInfo = getPackageInfoNoCheck(
                            r.activityInfo.applicationInfo, r.compatInfo);
                    handleLaunchActivity(r, null, "LAUNCH_ACTIVITY");
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                } break;

                ...
                ｝
              ...
    ｝
```

当收到一个LAUNCH_ACTIVITY的消息后，会调用handleLaunchActivity方法，在这个方法里面会经历performLaunchActivity方法去反射创建Activity，并且走了onCreate方法和onStart方法

还有调用handleResumeActivity方法去创建ViewRootImpl后调用requestFocus方法，之后页面便可以与用户交互。

所以我们在handleLaunchActivity方法中，去把它的参数中的Activity信息替换掉即可

handleLaunchActivity方法的声明是这样的：
```
private void handleLaunchActivity(ActivityClientRecord r, Intent customIntent, String reason)
```

而调用的时候是这样的：
```
handleLaunchActivity(r, null, "LAUNCH_ACTIVITY");
```

不难看出，**目标Activity信息一定是放在ActivityClientRecord中**，为什么呢？

其实这里如果跟踪下源码流程，便可以知晓怎么替换了。
在经过追踪源码后，我们知道performLaunchActivity方法会去反射创建Activity，那在这一步之前我们就要替换掉了，而且也可以追踪到它是拿什么字段来做反射的
部分源码如下：
```
private void handleLaunchActivity(ActivityClientRecord r, Intent customIntent, String reason){
     ...
     ComponentName component = r.intent.getComponent();
     Activity activity = null;
             try {
                 ClassLoader cl = r.packageInfo.getClassLoader();
                 activity = mInstrumentation.newActivity(
                         cl, component.getClassName(), r.intent);
                 ...
             } catch (Exception e) {
                 ...
             }
        ...
}
```

到这里，就知晓了吧，原来ComponentName是放在ActivityClientRecord对象里面的intent中，也就是ActivityClientRecord就是维护了一个我们启动时创建的Intent实例

那就好办了，我们可以在这里还原Intent，把我们之前存档的realComponentName拿出来即可.
那么，我们如何拦截呢？在这里没法用我们上面的代理方法啊，ActivityThread并没有继承任何接口。

然而，在handleLaunchActivity方法执行之前，是因为ActivityThread的handler收到消息后才执行的
而如果清楚Handler消息机制的话，我们知道Handler还可以有个CallBack接口，而在Handler的消息分发方法如下：
```
    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }
```

**它会优先处理CallBack接口的handleMessage(msg)方法，当返回false时才会继续执行Handler的handleMessage(msg);方法**

那我们对ActivityThread里的Handler去安插入一个CallBack貌似不错，而且看源码，它本身并没有实现这个接口，所以我们直接设置这个接口进去就行了，不需要做代理


那要插入这个接口，那得拿到这个实例吧？ 这个实例是ActivityThread里的一个内部类H，继承了Handler：
```
 final H mH = new H();

 private class H extends Handler {
    ...
 }
```

其次，这个字段不是静态的，我们需要知道具体的ActivityThread实例，咋获取呢？找啊找，发现ActivityThread里面维护了一个静态实例：
```
private static volatile ActivityThread sCurrentActivityThread;
```

所以我们就可以反射拿到mH这个对象了，这里反射的步骤就跟上面反射获取ActivityManagerProxy原理一样了，所以不累赘了，直接上代码：
```
private void hookActivityThread() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThread = activityThread.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThread.setAccessible(true);
            //这里拿到了ActivityThread实例
            Object activityThreadObject = sCurrentActivityThread.get(null);
            Log.i(TAG,"ActivityThread实例："+activityThreadObject);
            Field mH = activityThread.getDeclaredField("mH");
            mH.setAccessible(true);
            //通过ActivityThread实例拿到它的内部类 H类的实例,我们已经知道它继承Handler
            Handler h = (Handler) mH.get(activityThreadObject);
            Log.i(TAG,"ActivityThread的内部类 H类实例："+h);
            hookActivityThreadH(h);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private  void hookActivityThreadH(Handler h){
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
                    if(msg.what==launch_msg){
                        Log.i(TAG,"拦截在handleMessage中启动Activity的消息");
                        //根据源码得知，这个msg.obj就是ActivityClientRecord实例
                        Object o = msg.obj;
                        try {
                            //获取Intent对象
                            final Field intentField = activityClientRecord.getDeclaredField("intent");
                            intentField.setAccessible(true);
                            Intent intent = (Intent) intentField.get(o);
                            //拿到真实的ComponentName
                            ComponentName componentName = intent.getParcelableExtra("realComponentName");
                            if(componentName!=null){
                                Log.i(TAG,"handleMessage中还原真实的Activity:"+componentName.getClassName());
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
            mCallback.set(h,callback);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
```


好了，运行一下：

模拟器运行截图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190303210105115.png)

日志输出截图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190303210040586.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xjX21pYW8=,size_16,color_FFFFFF,t_70)

可以看到，我们启动后已经替换会了HookAActivity了


## 还原目标Activity后遇到的问题

不对，我们应该去掉HookAActivity的注册啊，不然能正面个卵，我们去掉注册：
```
        <activity android:name=".HookActivity" />
        <!--<activity android:name=".HookAActivity" />-->
        <activity android:name=".HookBActivity" />
```

我们还可以利用下面的方法来证明没有注册HookAActivity：
```
    private void testRegistActivity() {
         ComponentName componentName = new ComponentName(getApplicationContext(), HookAActivity.class);
         try {
             ActivityInfo info = getPackageManager().getActivityInfo(componentName, PackageManager.GET_META_DATA);
             Log.i(TAG, "有注册HookAActivity");
         } catch (PackageManager.NameNotFoundException e) {
             e.printStackTrace();
             Log.i(TAG, "无注册HookAActivity");
         }
     }
```


我们再运行看下：

日志输出截图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190303211437934.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xjX21pYW8=,size_16,color_FFFFFF,t_70)

的确是没注册HookAActivity，然而我们模拟器却挂掉了。报了错误如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190303211745336.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xjX21pYW8=,size_16,color_FFFFFF,t_70)


根据错误堆栈，我们可以定位出是因为下面这句话出现异常：
```
ActivityInfo info = pm.getActivityInfo(componentName, PackageManager.GET_META_DATA);
```

有点熟悉，我们上面的testRegistActivity不也用到这个，出异常说明没注册。

那咋办呢？看堆栈，是由于我们Activity默认继承于了AppCompatActivity，在AppCompatActivity里面会走堆栈截图中那些方法导致

如果我们替换会继承Activity那就没事。

但是这样也太勉强了吧，逼得我们不能用AppCompatActivity？而且我们也不能保证在其他地方有没有使用到pm.getActivityInfo啊

这是**源于Activity中的getComponentName方法，它记录的是我们的目标Activity，所以并不通过pm.getActivityInfo**


所以就是要在**生成目标Activity后，又把ComponentName替换成假的Activity**，

经过追踪发现，Activity里面的 mComponent对象是在它被调用attach的时候调用的：
```
final void attach(...){

 mComponent = intent.getComponent();
}

```

最简单的做法是吧目标的Activity重写getComponentName方法：
```

public class HookAActivity extends AppCompatActivity {
    ComponentName fakeComponentName;
    @Override
    public ComponentName getComponentName() {
        return fakeComponentName!=null?fakeComponentName:super.getComponentName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fakeComponentName = getIntent().getParcelableExtra("fakeComponentName");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook_a);
    }
}
```


# 利用Instrumentation类做hook入口
我们的Hook入口可以是在AMS，也可以在Instrumentation类中。

每个Activity内部都有一个Instrumentation成员变量，而这个Instrumentation成员变量都是共享自ActivityThread中创建的mInstrumentation。

所以可以反射获取ActivityThread实例，然后把它的mInstrumentation变量替换成我们的Instrumentation代理对象。
然后我们在Instrumentation代理对象中去拦截它的execStartActivity方法，修改它的Intent参数里面的ComponentName

原理差不多。

需要注意的是，每个Activity都持有一个Instrumentation对引用，这个引用来自ActivityThread类里面创建的Instrumentation

所以我们在hook的Instrumentation的时机应该是要放在Application创建时，倘若等到Launcher Activity创建之后再hook，那么Activity持有的引用还是原来那个Instrumentation。


这里不多说 ，直接上代码了：
```
public class HookApplication extends Application {
    private static final String TAG = "lc_miao";

    @Override
    public void onCreate() {
        super.onCreate();
        hookInstrumentation();
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
 }
```

InstrumentationProxy是我们写的代理类，我们需要代理Instrumentation这个对象，所以也需要把原本的这个Instrumentation引用构建进来

在代理execStartActivity方法时，由于这个方法是隐藏的，我们没法直接调用，所以只能用反射来调用原本的Instrumentation对象方法。
而newActivity方法则没隐藏而且还是共有的，所以我们可以直接使用，不需要反射。

InstrumentationProxy类如下：
```
public static class InstrumentationProxy extends Instrumentation {
        protected Instrumentation instrumentation;

        public InstrumentationProxy(Instrumentation instrumentation) {
            this.instrumentation = instrumentation;
        }

        public ActivityResult execStartActivity(
                Context who, IBinder contextThread, IBinder token, Activity target,
                Intent intent, int requestCode, Bundle options) {
            Log.i(TAG, "proxy execStartActivity");
            //在启动Activity的时候，在Intent里面传入一个假的已经注册的Activity的ComponentName
            ComponentName fakeComponentName =intent.getParcelableExtra("fakeComponentName");
            if(fakeComponentName!=null){
                intent.putExtra("realComponentName",intent.getComponent());
                intent.setComponent(fakeComponentName);
            }
            Class[] classes = new Class[]{Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class};
            try {
                Method method = getClass().getSuperclass().getDeclaredMethod("execStartActivity", classes);
                Log.i(TAG, "proxy execStartActivity Method:" + method);
                if (method != null) {
                    //注意这里的obj必须用原本的Instrumentation
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
```

# 结束语

本篇分别演示了hook AMS和Instrumentation的过程，两者相比，代理Instrumentation会相对简单一些。

