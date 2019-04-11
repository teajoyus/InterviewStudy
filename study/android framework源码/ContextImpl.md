
这里我们为了学习，我们不再直接通过ActivityThread的静态变量sCurrentActivityThread获取ActivityThread实例了，这里介绍另外一种获取ActivityThread实例的方法。



那就是在ContextImpl里面，如果去追踪ContextImpl的创建过程，会发现它的构造方法里面传入了ActivityThread实例:
```
final ActivityThread mMainThread;

 private ContextImpl(ContextImpl container, ActivityThread mainThread,
            LoadedApk packageInfo, IBinder activityToken, UserHandle user, int flags,
            Display display, Configuration overrideConfiguration, int createDisplayWithId){

           mMainThread = mainThread;
 }
```

ContextImpl实现了Context类中未实现的大量方法，ContextImpl并不是全局共享的。
每个Application对象会持有自己的一个ContextImpl对象，每个Activity也持有自己的一个ContextImpl对象。

它们是何时被绑定在一起的呢，我们举例Activity，前面说到，启动Activity后最后是调用到ActivityThread，
Activity的创建过程是在performLaunchActivity方法里，该方法里面除了反射创建Activity，还有这个环节：
```
private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {

        ...

        if (activity != null) {
               Context appContext = createBaseContextForActivity(r, activity);
                ...
          }
       activity.attach(appContext, this, getInstrumentation(), r.token,
                              r.ident, app, r.intent, r.activityInfo, title, r.parent,
                              r.embeddedID, r.lastNonConfigurationInstances, config,
                              r.referrer, r.voiceInteractor, window);


}

private Context createBaseContextForActivity(ActivityClientRecord r, final Activity activity)｛

     ...

     ContextImpl appContext = ContextImpl.createActivityContext(
                    this, r.packageInfo, r.token, displayId, r.overrideConfig);

     ...
     Context baseContext = appContext;

     ...

    return baseContext;

｝


```

在ContextImpl的createActivityContext方法中，创建了实例：

```
    static ContextImpl createActivityContext(ActivityThread mainThread,
            LoadedApk packageInfo, IBinder activityToken, int displayId,
            Configuration overrideConfiguration) {
        if (packageInfo == null) throw new IllegalArgumentException("packageInfo");
        return new ContextImpl(null, mainThread, packageInfo, activityToken, null, 0,
                null, overrideConfiguration, displayId);
    }
```


我们这里都是测试，我们就在Activity上测试，去获取这个Activity的ContextImpl对象，拿到ActivityThread实例

经过上面的分析，我们追踪Activity的attach方法，最后发现持有ContextImpl对象最终是被赋值在ContextWrapper里：
```
public class ContextWrapper extends Context {
    Context mBase;
   protected void attachBaseContext(Context base) {
          if (mBase != null) {
              throw new IllegalStateException("Base context already set");
          }
          mBase = base;
      }
   public Context getBaseContext() {
       return mBase;
   }
 }
```

我们可以直接调用getBaseContext拿到这个mBase，然后反射出它的字段mMainThread