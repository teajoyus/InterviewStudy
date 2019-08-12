每个Activity、Application、的getBaseContext都有一个ContextImpl

Activity的base是通过ContextImpl的createActivityContext方法

ActivityThread的performLaunchActivity里面调用createBaseContextForActivity方法，里面就用到了createActivityContext方法作为Activity的base



Application的base是通过ContextImpl的createAppContext方法
在LoadedApk里面的makeApplication：
```
 ContextImpl appContext = ContextImpl.createAppContext(mActivityThread, this);
            app = mActivityThread.mInstrumentation.newApplication(
                    cl, appClass, appContext);

```

之后在Instrumentation：
```
    static public Application newApplication(Class<?> clazz, Context context)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        Application app = (Application)clazz.newInstance();
        app.attach(context);
        return app;
    }
```


Service的base也是来自的createAppContext方法

在ActivityThread里面的handleCreateService方法



ContextImpl的三个创建方法：
```
static ContextImpl createSystemContext(ActivityThread mainThread) {
        LoadedApk packageInfo = new LoadedApk(mainThread);
        ContextImpl context = new ContextImpl(null, mainThread,
                packageInfo, null, null, 0, null, null, Display.INVALID_DISPLAY);
        context.mResources.updateConfiguration(context.mResourcesManager.getConfiguration(),
                context.mResourcesManager.getDisplayMetrics());
        return context;
    }

    static ContextImpl createAppContext(ActivityThread mainThread, LoadedApk packageInfo) {
        if (packageInfo == null) throw new IllegalArgumentException("packageInfo");
        return new ContextImpl(null, mainThread,
                packageInfo, null, null, 0, null, null, Display.INVALID_DISPLAY);
    }

    static ContextImpl createActivityContext(ActivityThread mainThread,
            LoadedApk packageInfo, IBinder activityToken, int displayId,
            Configuration overrideConfiguration) {
        if (packageInfo == null) throw new IllegalArgumentException("packageInfo");
        return new ContextImpl(null, mainThread, packageInfo, activityToken, null, 0,
                null, overrideConfiguration, displayId);
    }
```
其中createSystemContext方法是ActivityThread调用的，用来自己维护一个ContextImpl实例

createSystemContext方法中创建了一个LoadedApk对象

Activity的token机制：https://blog.csdn.net/guoqifa29/article/details/46819377