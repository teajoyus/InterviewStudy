# 前言
 我们特别熟悉当Activity需要跳转到另外一个Activity的时候，直接用startActivity就可以了，那么这句代码的背后涉及到什么，本篇将梳理一遍Activity的启动过程，但是由于源码篇幅过多，也比较复杂。没办法一一去解析源码，只能整理清楚这个主要流程。
# 涉及到的类
在梳理启动流程之前，我先梳理了下涉及到的主要的以下这些类：

**Instrumentation
ActivityManagerProxy
ActivityManagerNative
ActivityManagerService
ActivityStarter
ActivityStackSupervisor
ActivityStack
ApplicationThreadNative
ApplicationThreadProxy
ApplicationThread
ActivityThread
Process
RuntimeInit
LoadedApk**

# 如何查看SDK中没有的源文件

如果你手头没有一份Android的源码，查看不了一些SDK中没有的类，那么我强烈推荐你使用这个网站：http://androidxref.com

进去后选择一个Android版本，比如我选择的是Android 7.1.2:

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190227174540871.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xjX21pYW8=,size_16,color_FFFFFF,t_70)

查到源文件，然后点进去之后就可以看了，这里也推荐把他DownLoad下面然后放到Android Stduio上你的项目的随意的包下，这样就可以通过AS来快速的跟踪定位方法变量


# Activity启动过程源码追踪
从Activity的startActivity(Intent intent)开始

经过几个方法重载后会调用到startActivityForResult()：

```
 public void startActivityForResult(@RequiresPermission Intent intent, int requestCode,
            @Nullable Bundle options) {
        if (mParent == null) {
            options = transferSpringboardActivityOptions(options);
            //启动Activity
            Instrumentation.ActivityResult ar =
                mInstrumentation.execStartActivity(
                    this, mMainThread.getApplicationThread(), mToken, this,
                    intent, requestCode, options);
            ...
        } else {
           ...
        }
    }
```

startActivityForResult我们只需要关注 if (mParent == null)的内容，因为ActivityGroup已经被废弃了，现在一般都是用Activity+Fragment的方式

在if (mParent == null)里面把逻辑移交到了Instrumentation的execStartActivity方法：

```
public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
          //把contextThread强制转成IApplicationThread，如此看出IApplicationThread也是一个IBinder子类，具体IApplicationThread是什么看后面再说
        IApplicationThread whoThread = (IApplicationThread) contextThread;
        ...
        try {
           ...
           //把startActivity的逻辑移交到ActivityManager.getService()中，这里的getService()就是AMS
            int result = ActivityManager.getService()
                .startActivity(whoThread, who.getBasePackageName(), intent,
                        intent.resolveTypeIfNeeded(who.getContentResolver()),
                        token, target != null ? target.mEmbeddedID : null,
                        requestCode, 0, null, options);
             //检查Activity的启动结果
            checkStartActivityResult(result, intent);
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
        return null;
    }
```

这里插播一下Instrumentation的这个checkStartActivityResult方法，他会检查启动Activity时产生的错误，在这里抛出异常，比如我们熟悉的：have you declared this activity in your AndroidManifest.xm


我们看下ActivityManager.getService()：
```
/**
     * @hide
     */
    public static IActivityManager getService() {
        return IActivityManagerSingleton.get();
    }

    private static final Singleton<IActivityManager> IActivityManagerSingleton =
            new Singleton<IActivityManager>() {
                @Override
                protected IActivityManager create() {
                    //获取Activity Manager Service ，即AMS对象
                    final IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE);
                    final IActivityManager am = IActivityManager.Stub.asInterface(b);
                    return am;
                }
            };
```



IActivityManager是一个AIDL接口，它的一个实现是ActivityManagerProxy类，位于ActivityManagerNative（简称AMN）的一个内部类。
在这里的时候要准备把逻辑移交给了AMS，由于不同进程，所以代理类来完成这个交互。
ActivityManagerProxy的startActivity方法：
```
 public int startActivity(IApplicationThread caller, String callingPackage, Intent intent,
            String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int startFlags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
        ...
        mRemote.transact(START_ACTIVITY_TRANSACTION, data, reply, 0);
        ...
        return result;
    }
```
mRemote是则就是ActivityManagerNative，在bindler机制中，它的数据传输方法是这个transact方法，则对应于服务端则会同步执行onTranscat方法，

到这里经过binder IPC过程，把逻辑移交到了system_server进程，ActivityManagerNative的onTransact中:

```
@Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
            throws RemoteException {
        switch (code) {
        case START_ACTIVITY_TRANSACTION:
        {
           ...
            int result = startActivity(app, callingPackage, intent, resolvedType,
                    resultTo, resultWho, requestCode, startFlags, profilerInfo, options);
            reply.writeNoException();
            reply.writeInt(result);
            return true;
        }
        ...

```

ActivityManagerNative并没有实现这里的startActivity，它只是个抽象类，而是由它的实现类ActivityManagerService（简称AMS）

那接下来就把Activity的启动流程移交给了ActivityManagerService：


```
    @Override
    public final int startActivity(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int startFlags, ProfilerInfo profilerInfo, Bundle bOptions) {
        return startActivityAsUser(caller, callingPackage, intent, resolvedType, resultTo,
                resultWho, requestCode, startFlags, profilerInfo, bOptions,
                UserHandle.getCallingUserId());
    }
```
只是调用个重载方法，最后面的参数多传了个UserHandle.getCallingUserId()调用了startActivityAsUser：
```
  @Override
    public final int startActivityAsUser(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId) {
        ...
        return mActivityStarter.startActivityMayWait(caller, -1, callingPackage, intent,
                resolvedType, null, null, resultTo, resultWho, requestCode, startFlags,
                profilerInfo, null, null, bOptions, false, userId, null, null);
    }
```

逻辑又移交到了ActivityStarter这个类，这里要提到下，**7.0之后的源码，7.0之后才有这个类，而在7.0以下的版本，则是走了ActivityStackSupervisor**
不过没啥影响，既然是基于7.0 就按照7.0来


继续追踪ActivityStarter这个类调用的startActivityMayWait：
```
final int startActivityMayWait(IApplicationThread caller, int callingUid,
            String callingPackage, Intent intent, String resolvedType,
            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor,
            IBinder resultTo, String resultWho, int requestCode, int startFlags,
            ProfilerInfo profilerInfo, IActivityManager.WaitResult outResult, Configuration config,
            Bundle bOptions, boolean ignoreTargetSecurity, int userId,
            IActivityContainer iContainer, TaskRecord inTask) {

            //调用PackageManagerService完成intent解析
            ResolveInfo rInfo = mSupervisor.resolveIntent(intent, resolvedType, userId);
              ...:
               // Collect information about the target of the Intent.
             ActivityInfo aInfo = mSupervisor.resolveActivity(intent, rInfo, startFlags, profilerInfo);

            ...

             int res = startActivityLocked(caller, intent, ephemeralIntent, resolvedType,
                    aInfo, rInfo, voiceSession, voiceInteractor,
                    resultTo, resultWho, requestCode, callingPid,
                    callingUid, callingPackage, realCallingPid, realCallingUid, startFlags,
                    options, ignoreTargetSecurity, componentSpecified, outRecord, container,
                    inTask);

               ...

            }
```
这个方法几个重要的地方就是调用ActivityStackSupervisor都resolveIntent方法和resolveActivity方法

resolveIntent方法中最调用了：
```
    ResolveInfo resolveIntent(Intent intent, String resolvedType, int userId, int flags) {
        try {
            //AppGlobals.getPackageManager()获取PMS来完成intent解析
            return AppGlobals.getPackageManager().resolveIntent(intent, resolvedType,
                    PackageManager.MATCH_DEFAULT_ONLY | flags
                    | ActivityManagerService.STOCK_PM_FLAGS, userId);
        } catch (RemoteException e) {
        }
        return null;
    }
```

resolveIntent完成之后又调用了resolveActivity解析出ActivityInfo

最后调用了startActivityLocked，,这个方法判断了Intent中能不能正常找到相应的Component或者ActivityInfo、或者该Activity对当前用户不可见


然后接下来调用startActivityUnchecked,这个方法也很长：
```
private int startActivityUnchecked(final ActivityRecord r, ActivityRecord sourceRecord,
            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor,
            int startFlags, boolean doResume, ActivityOptions options, TaskRecord inTask) {
            //前面各种对启动模式和aunchFlag的逻辑解析
            ...

            //调用ActivityStack的startActivityLocked,完成WindowManager准备切换的相关流程
            mTargetStack.startActivityLocked(mStartActivity, newTask, mKeepCurTransition, mOptions);
  if (mDoResume) {
            ...
            final ActivityRecord topTaskActivity = mStartActivity.task.topRunningActivityLocked();
            if (!mTargetStack.isFocusable()
                    || (topTaskActivity != null && topTaskActivity.mTaskOverlay
                    && mStartActivity != topTaskActivity)) {
             ...
            } else {
            //最终调用ActivityStackSupervisor的resumeFocusedStackTopActivityLocked
                mSupervisor.resumeFocusedStackTopActivityLocked(mTargetStack, mStartActivity,
                        mOptions);
            }

            }
```

startActivityUnchecked负责任务栈的的调度，会利用launchFlag和launchMode来判断Activity所属的Task栈，比如我们熟悉的singleTop、singleTask。还有另外一些Flag，如FLAG_ACTIVITY_NEW_TASK、FLAG_ACTIVITY_CLEAR_TASK等


然后接下来调用ActivityStackSupervisor类的resumeFocusedStackTopActivityLocked方法：
```
  boolean resumeFocusedStackTopActivityLocked(
            ActivityStack targetStack, ActivityRecord target, ActivityOptions targetOptions) {
         //如果activity所属栈位于前台，则用该task的resumeTopActivityUncheckedLocked
        if (targetStack != null && isFocusedStack(targetStack)) {
            return targetStack.resumeTopActivityUncheckedLocked(target, targetOptions);
        }
        //task不在前台，调用当前前台栈的resumeTopActivityUncheckedLocked，传递的参数都是null
        final ActivityRecord r = mFocusedStack.topRunningActivityLocked();
        if (r == null || r.state != RESUMED) {
            mFocusedStack.resumeTopActivityUncheckedLocked(null, null);
        }
        return false;
    }
```

可以看到这个方法只是判断要跳转的Activity所处的task是否位于前台，最后都是会调用到ActivityStack的resumeTopActivityUncheckedLocked的方法:
```
  boolean resumeTopActivityUncheckedLocked(ActivityRecord prev, ActivityOptions options) {
        if (mStackSupervisor.inResumeTopActivity) {
            // Don't even start recursing.
            return false;
        }

        boolean result = false;
        try {
           ...
            result = resumeTopActivityInnerLocked(prev, options);
        } finally {
            mStackSupervisor.inResumeTopActivity = false;
        }
        return result;
    }
```

经过一个判断Activity是否已在前台后，走到resumeTopActivityInnerLocked方法：
```
 private boolean resumeTopActivityInnerLocked(ActivityRecord prev, ActivityOptions options) {
    //系统没有进入booting或booted状态，则不允许启动Activity
   if (!mService.mBooting && !mService.mBooted) {
             // Not ready yet!
             return false;
         }
    // Find the first activity that is not finishing.
     final ActivityRecord next = topRunningActivityLocked();

        if (next == null) {
                ...
         //找不到需要resume的Activity，则直接回到桌面
         return isOnHomeDisplay() &&
                 mStackSupervisor.resumeHomeStackTask(returnTaskType, prev, reason);
             }
        //mResumedActivity是上一次启动的Activity
        if (mResumedActivity != null) {
            ```
           //通知上一个Activity进入pause状态
            pausing |= startPausingLocked(userLeaving, false, true, dontWaitForPause);
        }
         if (pausing) {//已经暂停了
            ```
            if (next.app != null && next.app.thread != null) {
                //如果app已经启动过
                //调度Activity所在进程的优先级，保证其不被kill
                mService.updateLruProcessLocked(next.app, true, null);
            }

        }
    mStackSupervisor.startSpecificActivityLocked(next, true, true);
 }
```

resumeTopActivityInnerLocked最主要就是判断mResumedActivity存不存在，存在则pause，不存在则回到桌面

接下来调用了ActivityStackSupervisor的startSpecificActivityLocked方法：
```
 void startSpecificActivityLocked(ActivityRecord r,
            boolean andResume, boolean checkConfig) {

      //判断activity所属进程有没有启动
  ProcessRecord app = mService.getProcessRecordLocked(r.processName,
          r.info.applicationInfo.uid, true);
   r.task.stack.setLaunchTime(r);

        if (app != null && app.thread != null) {
            try {
                if ((r.info.flags&ActivityInfo.FLAG_MULTIPROCESS) == 0
                        || !"android".equals(r.info.packageName)) {
                    // Don't add this if it is a platform component that is marked
                    // to run in multiple processes, because this is actually
                    // part of the framework so doesn't make sense to track as a
                    // separate apk in the process.
                    app.addPackage(r.info.packageName, r.info.applicationInfo.versionCode,
                            mService.mProcessStats);
                }
                //下一步调用了这个方法
                realStartActivityLocked(r, app, andResume, checkConfig);
                return;
            } catch (RemoteException e) {
                Slog.w(TAG, "Exception when starting activity "
                        + r.intent.getComponent().flattenToShortString(), e);
            }

            // If a dead object exception was thrown -- fall through to
            // restart the application.
        }
        //如果没有启动进程，则先启动
        mService.startProcessLocked(r.processName, r.info.applicationInfo, true, 0,
                "activity", r.intent.getComponent(), false, false, true);

 }
```
可以看出这个方法判断了要跳转的activity所在的app是否已经启动过，没有的话则需要创建进程后启动activcity，有的话则走realStartActivityLocked方法

```
    final boolean realStartActivityLocked(ActivityRecord r, ProcessRecord app,
            boolean andResume, boolean checkConfig) throws RemoteException {
           //确保所有之前的activity都已经暂停完成
        if (!allPausedActivitiesComplete()) {
            // While there are activities pausing we skipping starting any new activities until
            // pauses are complete. NOTE: that we also do this for activities that are starting in
            // the paused state because they will first be resumed then paused on the client side.
            if (DEBUG_SWITCH || DEBUG_PAUSE || DEBUG_STATES) Slog.v(TAG_PAUSE,
                    "realStartActivityLocked: Skipping start of r=" + r
                    + " some activities pausing...");
            return false;
        }
        //调用IApplicationThread的scheduleLaunchActivity方法
         app.thread.scheduleLaunchActivity(new Intent(r.intent), r.appToken,
                            System.identityHashCode(r), r.info, new Configuration(mService.mConfiguration),
                            new Configuration(task.mOverrideConfig), r.compat, r.launchedFromPackage,
                            task.voiceInteractor, app.repProcState, r.icicle, r.persistentState, results,
                            newIntents, !andResume, mService.isNextTransitionForward(), profilerInfo);
        }
```

可以看得，最终的启动过程又移交到IApplicationThread的scheduleLaunchActivity方法，而IApplicationThread的一个实现是ApplicationThreadProxy 代理类，是ApplicationThreadNative的一个内部类

我们看ApplicationThreadProxy的scheduleLaunchActivity方法：
```
   public final void scheduleLaunchActivity(Intent intent, IBinder token, int ident,
            ActivityInfo info, Configuration curConfig, Configuration overrideConfig,
            CompatibilityInfo compatInfo, String referrer, IVoiceInteractor voiceInteractor,
            int procState, Bundle state, PersistableBundle persistentState,
            List<ResultInfo> pendingResults, List<ReferrerIntent> pendingNewIntents,
            boolean notResumed, boolean isForward, ProfilerInfo profilerInfo) throws RemoteException {
       ...
       //发送启动的消息
        mRemote.transact(SCHEDULE_LAUNCH_ACTIVITY_TRANSACTION, data, null,
                IBinder.FLAG_ONEWAY);
        data.recycle();
    }
```
这里的mRemote是一个IBinder对象，时则就是指向ApplicationThreadNative，当执行transact的时候，对应的处理消息方法在：
```
  @Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
            throws RemoteException {
        switch (code) {
        case SCHEDULE_LAUNCH_ACTIVITY_TRANSACTION:
                {
                    ...
                    scheduleLaunchActivity(intent, b, ident, info, curConfig, overrideConfig, compatInfo,
                            referrer, voiceInteractor, procState, state, persistentState, ri, pi,
                            notResumed, isForward, profilerInfo);
                    return true;
                }
       }
```

 ApplicationThreadNative只是一个抽象类，并没有实现这个方法，它有一个实现类，就是ApplicationThread，该类位于ActivityThread的一个私有内部类中

 进去查看ActivityThread的scheduleLaunchActivity方法：
 ```
 @Override
         public final void scheduleLaunchActivity(Intent intent, IBinder token, int ident,
                 ActivityInfo info, Configuration curConfig, Configuration overrideConfig,
                 CompatibilityInfo compatInfo, String referrer, IVoiceInteractor voiceInteractor,
                 int procState, Bundle state, PersistableBundle persistentState,
                 List<ResultInfo> pendingResults, List<ReferrerIntent> pendingNewIntents,
                 boolean notResumed, boolean isForward, ProfilerInfo profilerInfo) {

             updateProcessState(procState, false);

             ActivityClientRecord r = new ActivityClientRecord();

               ...

             sendMessage(H.LAUNCH_ACTIVITY, r);
         }
 ```

 最终可以执行了一个sendMessage方法，继续追踪发现这个方法最终调用的是ActivityThread的一个私有内部类H，它继承了Handler

 在这里发送了一个消息是H.LAUNCH_ACTIVITY，那么对应的在这个H类的handleMessage方法则有：

 ```
 public void handleMessage(Message msg) {
  switch (msg.what) {
        case LAUNCH_ACTIVITY: {
        ...
        handleLaunchActivity(r, null, "LAUNCH_ACTIVITY");
        ...
    } break;
  }
  }
 ```
 那逻辑就进入到ActivityThread的handleLaunchActivity方法：
 ```
   private void handleLaunchActivity(ActivityClientRecord r, Intent customIntent, String reason) {

   //Activity经过performLaunchActivity方法被反射创建出来,这里会执行onCreate方法
    Activity a = performLaunchActivity(r, customIntent);
    f (a != null) {
            //
            handleResumeActivity(r.token, false, r.isForward,
                    !r.activity.mFinished && !r.startsNotResumed, r.lastProcessedSeq, reason);
   }
 ```

 我们先来追踪下performLaunchActivity：
 ```
  private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {

  Activity activity = null;
          try {
              ClassLoader cl = r.packageInfo.getClassLoader();
              //通过Instrumentation的newActivity方法反射生成实例
              activity = mInstrumentation.newActivity(
                      cl, component.getClassName(), r.intent);
              ...
          } catch (Exception e) {
              ...
          }
     //调用LoadedAPK的makeApplication生成实例，或者返回唯一实例
    Application app = r.packageInfo.makeApplication(false, mInstrumentation);
    `...
    if (activity != null) {
        //开始attach aactivity
        activity.attach(appContext, this, getInstrumentation(), r.token,
                            r.ident, app, r.intent, r.activityInfo, title, r.parent,
                            r.embeddedID, r.lastNonConfigurationInstances, config,
                            r.referrer, r.voiceInteractor, window);
        //执行了onCreate方法
     if (r.isPersistable()) {
                        mInstrumentation.callActivityOnCreate(activity, r.state, r.persistentState);
                    } else {
                        mInstrumentation.callActivityOnCreate(activity, r.state);
                    }
       //执行了onStart方法
     if (!r.activity.mFinished) {
                    activity.performStart();
                    r.stopped = false;
                }
    }

    ...
  }
 ```
 可以看到performLaunchActivity会执行Activity和反射创建以及Application的创建或者获取，最后走了Activity的onCreate方法和onStart方法

 Instrumentation的newActivity方法:
 ```
     public Activity newActivity(ClassLoader cl, String className,
             Intent intent)
             throws InstantiationException, IllegalAccessException,
             ClassNotFoundException {
         return (Activity)cl.loadClass(className).newInstance();
     }
 ```

 LoadedAPK的makeApplication方法：
 ```
  public Application makeApplication(boolean forceDefaultAppClass,
             Instrumentation instrumentation) {
          //如果已经有实例了 则直接返回
         if (mApplication != null) {
             return mApplication;
         }
          ...

         Application app = null;

         try {
             ClassLoader cl = getClassLoader();
             ContextImpl appContext = ContextImpl.createAppContext(mActivityThread, this);
             //利用Instrumentation反射生成实例
             app = mActivityThread.mInstrumentation.newApplication(
                     cl, appClass, appContext);
             appContext.setOuterContext(app);
         } catch (Exception e) {
             ...
         }
         mActivityThread.mAllApplications.add(app);
         //赋值到mApplication，下次就不会再创建
         mApplication = app;
         if (instrumentation != null) {
             try {
                //这里会让Application执行onCreate方法
                 instrumentation.callApplicationOnCreate(app);
             } catch (Exception e) {
               ...
             }
         }
        ...
         return app;
     }
 ```

 那么我们接下来再分析下handleLaunchActivity方法中剩下的这个handleResumeActivity方法：
 ```
  final void handleResumeActivity(IBinder token,
             boolean clearHide, boolean isForward, boolean reallyResume, int seq, String reason) {
         ...
         //注意这个方法，会执行onResume方法
         r = performResumeActivity(token, clearHide, reason);
       //wm是wm是a.getWindowManager()获取到，也就是WindowManagerImpl
       //在这个addView里面创建了ViewRootImpl
     if (a.mVisibleFromClient && !a.mWindowAdded) {
                     a.mWindowAdded = true;
                     wm.addView(decor, l);
                 }
     }
 ```

 可以看得上面先是调用了performResumeActivity来走onResume这个周期，然后调用wm.addView(decor, l);来创建ViewRootImpl。

 其中performResumeActivity方法里面会执行activity.performResume();，在Activity的performResume方法中执行了onResume方法
 而wm.addView(decor, l);这一句，wm就是a.getWindowManager()获取到的，a是Activity，getWindowManager()就是返回它的mWindowManger对象，而这个对象是WindowManagerImpl。

 看下WindowManagerImpl的addView方法
 ```
   @Override
     public void addView(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
         applyDefaultToken(params);
         mGlobal.addView(view, params, mContext.getDisplay(), mParentWindow);
     }
 ```
这里的mGlobal就是WindowManagerGlobal，我们继续追踪这个WindowManagerGlobal.的addView方法：
```
    public void addView(View view, ViewGroup.LayoutParams params,
                        Display display, Window parentWindow) {
        ...
       ViewRootImpl root;
       ...
       root = new ViewRootImpl(view.getContext(), display);
        try {
           root.setView(view, wparams, panelParentView);
       } catch (RuntimeException e) {
          ...
       }
  }
```
可以发现到ViewRootImpl其实是在WindowManagerGlobal中创建的，ViewRootImpl的setView方法
```
/**
 * We have one child
 */
public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
    synchronized (this) {
        if (mView == null) {
            mView = view;
            ...

            // Schedule the first layout -before- adding to the window
            // manager, to make sure we do the relayout before receiving
            // any other events from the system.
            requestLayout();

            ...

            view.assignParent(this);
            ...
        }
    }
}
```
可以看得到这里会执行了requestLayout方法，说明页面可获取焦点，**这也说明为什么onResume后页面才可以交互**

# Activity的冷启动过程
回过头去，我们在追踪到startSpecificActivityLocked方法时说到，这个方法会判断进程有没有启动，我们这里只是说到进程已经启动的情况。当进程没启动时
会走：
```
mService.startProcessLocked(r.processName, r.info.applicationInfo, true, 0,
                "activity", r.intent.getComponent(), false, false, true);
```
我们从ActivityManagerService的startProcessLocked方法入手:
```
 final ProcessRecord startProcessLocked(String processName,
            ApplicationInfo info, boolean knownToBeDead, int intentFlags,
            String hostingType, ComponentName hostingName, boolean allowWhileBooting,
            boolean isolated, boolean keepIfLarge) {
        return startProcessLocked(processName, info, knownToBeDead, intentFlags, hostingType,
                hostingName, allowWhileBooting, isolated, 0 /* isolatedUid */, keepIfLarge,
                null /* ABI override */, null /* entryPoint */, null /* entryPointArgs */,
                null /* crashHandler */);
    }


final ProcessRecord startProcessLocked(String processName, ApplicationInfo info,
            boolean knownToBeDead, int intentFlags, String hostingType, ComponentName hostingName,
            boolean allowWhileBooting, boolean isolated, int isolatedUid, boolean keepIfLarge,
            String abiOverride, String entryPoint, String[] entryPointArgs, Runnable crashHandler) {


            ...

             startProcessLocked(
                            app, hostingType, hostingNameStr, abiOverride, entryPoint, entryPointArgs);


            ...

  }


 private final void startProcessLocked(ProcessRecord app, String hostingType,
            String hostingNameStr, String abiOverride, String entryPoint, String[] entryPointArgs) {

   ...
  if (entryPoint == null) entryPoint = "android.app.ActivityThread";
     Process.ProcessStartResult startResult = Process.start(entryPoint,
                       app.processName, uid, uid, gids, debugFlags, mountExternal,
                       app.info.targetSdkVersion, app.info.seinfo, requiredAbi, instructionSet,
                       app.info.dataDir, entryPointArgs);

   ...

 ｝
```

在AMS中经过一系列的startProcessLocked方法重载，最终到了这里，会执行Process.start去创建一个新的进程，值得注意的是这个entryPoint，指向字符串：android.app.ActivityThread
在Process执行start方法中，如果一直追踪下去，发现最终会去连接socket：
```
 /**
     * Tries to open socket to Zygote process if not already open. If
     * already open, does nothing.  May block and retry.
     */
    private static ZygoteState openZygoteSocketIfNeeded(String abi) throws ZygoteStartFailedEx {
        if (primaryZygoteState == null || primaryZygoteState.isClosed()) {
            try {
                primaryZygoteState = ZygoteState.connect(ZYGOTE_SOCKET);
            } catch (IOException ioe) {
                throw new ZygoteStartFailedEx("Error connecting to primary zygote", ioe);
            }
        }
        ...
    }

```

而连接的Socket是来自哪里呢？就是ZygoteInit这个类，在系统启动时便已经运行。它会开启Socket服务，然后一直轮询检测有没有client接入进来
```
  private static void runSelectLoop(String abiList) throws MethodAndArgsCaller {

  while (true) {
      ZygoteConnection newPeer = acceptCommandPeer(abiList);
  }
  }
```

在接收到新进程初始化的请求后，最终调用了RuntimeInit.zygoteInit方法：
```
   public static final void zygoteInit(int targetSdkVersion, String[] argv, ClassLoader classLoader)
            throws ZygoteInit.MethodAndArgsCaller {
        ...
        applicationInit(targetSdkVersion, argv, classLoader);
    }
```

接下来RuntimeInit的applicationInit方法：
```
private static void applicationInit(int targetSdkVersion, String[] argv, ClassLoader classLoader)
            throws ZygoteInit.MethodAndArgsCaller {


        // Remaining arguments are passed to the start class's static main
        invokeStaticMain(args.startClass, args.startArgs, classLoader);
    }

```

接下来RuntimeInit的invokeStaticMain方法：
```
   private static void invokeStaticMain(String className, String[] argv, ClassLoader classLoader)
            throws ZygoteInit.MethodAndArgsCaller {
        Class<?> cl;

        try {
            //装载类，className就是我们前面传入的android.app.ActivityThread
            cl = Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(
                    "Missing class when invoking static main " + className,
                    ex);
        }

        Method m;
        try {
        //反射调用android.app.ActivityThread类的main方法
            m = cl.getMethod("main", new Class[] { String[].class });
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(
                    "Missing static main on " + className, ex);
        } catch (SecurityException ex) {
            throw new RuntimeException(
                    "Problem getting static main on " + className, ex);
        }

       ...
    }
```

由此最终就是创建了一个进程，然后在新的进程里载入android.app.ActivityThread类，并执行它的main方法，我们知道，app启动时会从它的main方法开始执行。


到这里我们就大致的过了一遍Activity的启动流程了，包括冷启动。


# 启动流程回顾

这里整理下启动过程中会经历的主要方法：

Activity#startActivity
Activity#startActivityForResult
Instrumentation#execStartActivity
ActivityManagerProxy#startActivity
ActivityManagerService#startActivity
ActivityManagerService#startActivityAsUser
ActivityStarter#startActivityMayWait
ActivityStarter#startActivityLocked
ActivityStarter#startActivityUnchecked
ActivityStackSupervisor#resumeFocusedStackTopActivityLocked
ActivityStack#resumeTopActivityUncheckedLocked
ActivityStack#resumeTopActivityInnerLocked
ActivityStackSuperviso#startSpecificActivityLocked
进程存在：
ActivityStackSuperviso#realStartActivityLocked
ApplicationThreadProxy#scheduleLaunchActivity
ApplicationThread#scheduleLaunchActivity
ActivityThread#handleLaunchActivity
ActivityThread#performLaunchActivity
ActivityThread#handleResumeActivity
ActivityThread#performResumeActivity
进程不存在：
ActivityManagerService#startProcessLocked
Process#start
RuntimeInit#zygoteInit
RuntimeInit#applicationInit
RuntimeInit#invokeStaticMain

