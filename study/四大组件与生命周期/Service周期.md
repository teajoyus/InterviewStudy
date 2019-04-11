#生命周期
自己测试

调用startService时，如果未启动那么是走
- onCreate
- onStartCommand
- onStart

如果已经启动是走：
- onStartCommand
- onStart

并且退出Activity，并不影响Service，除非主动调用stopService


调用bindService时，那么是走：
- onCreate
- onBind
- onServiceConnected（如果onBind方法不是返回null的话）

多次调用也不会继续走onCreate和onBind，但是onServiceConnected会被调用多次（当然是在onBind返回值不为空的情况下）



返回键退出Activity时（并没有调用unBindService）是在Activtiy的onDestroy方法之后，走了：
- onUnbind
- onDestory


unbindService方法只能调用一次，多次调用应用会抛出异常。

虽然上面返回的时候没有问题，但是实际上会抛出一个内存泄漏的异常
```
03-13 16:27:06.493 4465-4465/com.example.interviewstudy E/ActivityThread: Activity com.example.interviewstudy.service.ServiceActivity has leaked ServiceConnection com.example.interviewstudy.service.ServiceActivity$1$1@ce83939 that was originally bound here
    android.app.ServiceConnectionLeaked: Activity com.example.interviewstudy.service.ServiceActivity has leaked ServiceConnection com.example.interviewstudy.service.ServiceActivity$1$1@ce83939 that was originally bound here
        at android.app.LoadedApk$ServiceDispatcher.<init>(LoadedApk.java:1201)
        at android.app.LoadedApk.getServiceDispatcher(LoadedApk.java:1095)
        at android.app.ContextImpl.bindServiceCommon(ContextImpl.java:1402)
        at android.app.ContextImpl.bindService(ContextImpl.java:1385)
        at android.content.ContextWrapper.bindService(ContextWrapper.java:604)
        at com.example.interviewstudy.service.ServiceActivity$1.onClick(ServiceActivity.java:33)
        at android.view.View.performClick(View.java:5317)
        at android.view.View$PerformClick.run(View.java:21648)
        at android.os.Handler.handleCallback(Handler.java:815)
        at android.os.Handler.dispatchMessage(Handler.java:104)
```


同时调用bind和start service的情况下
只要service启动过了，onCreate方法不会再被调用

同时调用bind和start并没有先后顺序的问题，哪个前哪个后都可以
只不过bindService就会对应onBind而且还有onServiceConnected方法，startService就会对应onStartCommand

而退出Service的时候，也没有前后顺序之分，哪个前哪个后都可以。但是如果一个Service已经start了又同时有bind
那么它必须被调用stopService和unBindService时，这个Service才会销毁，才会走onDestory方法




# 之前没掌握的问题


关于onStartCommand的flag参数、startId参数和返回值
源码是这样：
```
public @StartResult int onStartCommand(Intent intent, @StartArgFlags int flags, int startId) {
        onStart(intent, startId);
        return mStartCompatibility ? START_STICKY_COMPATIBILITY : START_STICKY;
    }
```
mStartCompatibility是在attach方法里面赋值:
```
 mStartCompatibility = getApplicationInfo().targetSdkVersion
                < Build.VERSION_CODES.ECLAIR;
```

所以是当targetSdkVersion小于2.0的时候，才会默认是START_STICKY_COMPATIBILITY，不然就默认是START_STICKY
它的返回值有4个，另外两种返回值START_NOT_STICKY 和 START_REDELIVER_INTENT可选

**START_STICKY**
被系统kill掉之后，过不久会重新创建，但是不保留Intent

**START_NOT_STICKY**
被kill后不会重新创建，除非有startService，也也意味着onStartCommand的intent一定不为null


**START_REDELIVER_INTENT**

被系统kill掉之后，过不久会重新创建，然后也会保留Intent，且这个时候onStartCommand传入的flag的值是START_FLAG_REDELIVERY
bindService时指定的flag


**START_STICKY_COMPATIBILITY**
只是一个START_STICKY的兼容版本，效果是一样的

上面说的onStartCommand传进来的flag参数，是当返回值是START_REDELIVER_INTENT的时候下次自动重新创建的时候就是START_FLAG_REDELIVERY
而正常启动的话，这个flag就是0，如果是启动过程被杀了然后重新启动的话，那么就是START_FLAG_RETRY


**startId**
传入的startId用来代表唯一启动请求，可以用stopSelf时传入这个startId

stopSelfResult方法传入的startId，必须是最后一次启动Service时传入的startId，才能终止Service。
万一我们想终止Service的时候又来了个启动请求，这时候是不应该终止的，而我们还没拿到最新请求的startId，如果用stopService的话就直接终止了，而用stopSelfResult方法就会及时避免终止。

使用场景：

如果同时有多个服务启动请求发送到onStartCommand(),不应该在处理完一个请求后调用stopSelf()；因为在调用此函数销毁service之前，可能service又接收到新的启动请求，如果此时service被销毁，新的请求将得不到处理。此情况应该调用stopSelf(int startId)。请参见：IntentService




进程保活的话题：https://baijiahao.baidu.com/s?id=1594886880275880582&wfr=spider&for=pc

