Activity：
Context在什么时候赋值的？
在ActivityThread中：

- handleLaunchActivity
- performLaunchActivity
- createBaseContextForActivity

在createBaseContextForActivity方法里面创建了ContextImpl实例：
```
  ContextImpl appContext = ContextImpl.createActivityContext(
                this, r.packageInfo, r.token, displayId, r.overrideConfig);
        appContext.setOuterContext(activity);
```

实际是在ContextImpl的静态方法createActivityContext中创建的：
```
  static ContextImpl createActivityContext(ActivityThread mainThread,
            LoadedApk packageInfo, IBinder activityToken, int displayId,
            Configuration overrideConfiguration) {
        if (packageInfo == null) throw new IllegalArgumentException("packageInfo");
        return new ContextImpl(null, mainThread, packageInfo, activityToken, null, 0,
                null, overrideConfiguration, displayId);
    }
```

在创建之后createBaseContextForActivity方法返回这个实例给performLaunchActivity方法中，
然后performLaunchActivity方法调用了activity的attach方法传入这个ContextImpl实例：
```
 activity.attach(appContext, this, getInstrumentation(), r.token,
                        r.ident, app, r.intent, r.activityInfo, title, r.parent,
                        r.embeddedID, r.lastNonConfigurationInstances, config,
                        r.referrer, r.voiceInteractor, window);
```

然后Activity的attach方法中会调用attachBaseContext方法传入这个context，最终调用到ContextWrapper中
  ContextWrapper维护了一个：
```
  Context mBase;
```



Application:
Application的创建是在LoadedApk类中（当然具体的反射创建实例是在Instrumentration）

```
   public Application makeApplication(boolean forceDefaultAppClass,
            Instrumentation instrumentation) {
  if (mApplication != null) {
            return mApplication;
        }
    ...
      ContextImpl appContext = ContextImpl.createAppContext(mActivityThread, this);
            app = mActivityThread.mInstrumentation.newApplication(
                    cl, appClass, appContext);
            appContext.setOuterContext(app);
    ...
     mApplication = app;
    ...
     return app; 
               
 }
```
Instrumentation的newApplication方法：
```
  static public Application newApplication(Class<?> clazz, Context context)
            throws InstantiationException, IllegalAccessException, 
            ClassNotFoundException {
        Application app = (Application)clazz.newInstance();
        app.attach(context);
        return app;
    }
```
可以看到Application的Context是来自于ContextImpl的createAppContext方法：
```
 static ContextImpl createAppContext(ActivityThread mainThread, LoadedApk packageInfo) {
        if (packageInfo == null) throw new IllegalArgumentException("packageInfo");
        return new ContextImpl(null, mainThread,
                packageInfo, null, null, 0, null, null, Display.INVALID_DISPLAY);
    }
```


Service:

从创建开始，在ActivityThread的handleCreateService方法中：
```
private void handleCreateService(CreateServiceData data) {
    
    ...
         ContextImpl context = ContextImpl.createAppContext(this, packageInfo);
            context.setOuterContext(service);

            Application app = packageInfo.makeApplication(false, mInstrumentation);
            service.attach(context, this, data.info.name, data.token, app,
                    ActivityManagerNative.getDefault());
            service.onCreate();
     ...

}
```

看起来Service和Application一样都是调用ContextImpl的createAppContext方法。


那么三者的Context有何区别呢？既然上面的Application和Service都是调用createAppContext方法创建的，他们持有的Context都是一样的
而Activity相比之下，则在创建ConextImpl的时候传入了一个activityToken和界面的配置信息。
关于activityToken的创建时机可以看这篇：https://blog.csdn.net/guoqifa29/article/details/46819377

