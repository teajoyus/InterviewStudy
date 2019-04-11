# HandlerThread

在看IntentService源码之前先了解一下HandlerThread，
HandlerThread继承了Thread，也是相当于一个Thread，只是里面封装了个Handler
构造方法：
```
   public HandlerThread(String name) {
        super(name);
        mPriority = Process.THREAD_PRIORITY_DEFAULT;
    }
  public HandlerThread(String name, int priority) {
        super(name);
        mPriority = priority;
    }
```

既然是Thread，那么主要看run方法：
```
   @Override
    public void run() {
        mTid = Process.myTid();
        Looper.prepare();
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(mPriority);
        onLooperPrepared();
        Looper.loop();
        mTid = -1;
    }
```

run方法里面先Looper.prepare();拿到Looper，然后调用onLooperPrepared，这个是让我们来重写的方法，开启消息循环

在里面有个同步关键字，为什么拿到looper的时候要同步一下？

主要是getLooper方法：
```
    public Looper getLooper() {
        if (!isAlive()) {
            return null;
        }

        // If the thread has been started, wait until the looper has been created.
        synchronized (this) {
            while (isAlive() && mLooper == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return mLooper;
    }
```
如果线程已经启动，它可以一直阻塞到拿到mLooper，而不会在多线程环境下，刚开始执行run方法时拿不到looper
isAlive()方法：“活动状态”是指线程处于运行或者准备开始运行的状态。(也就是run方法开始和结束)

所以HandlerThread没啥，就是Thread+Handler，我们可以拥有这个Thread的Handler




# IntentService源码

继承IntentService必须实现它的onHandleIntent方法，另外他有个一参数的构造方法也需要重写

## onCreate
```
   @Override
    public void onCreate() {
        // TODO: It would be nice to have an option to hold a partial wakelock
        // during processing, and to have a static startService(Context, Intent)
        // method that would launch the service & hand off a wakelock.

        super.onCreate();
        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }
```
可以看到先开启了一个HandlerThread，然后用自己的ServiceHandler绑定这个线程的Looper
## onStartCommand
```
  public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }
 @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }
```

提供了一个setIntentRedelivery方法来设置一个onStartCommand的返回值，而默认mRedelivery是false，所以是START_NOT_STICKY，也就是被系统kill掉的时候不会重启

## onStart
```
  @Override
    public void onStart(@Nullable Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }

```

onStart里面让mServiceHandler去发送出一个消息，这消息包含了startId和intent



那么既然发出来了话，mServiceHandler就有对应的处理方法：
```
  private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
            stopSelf(msg.arg1);
        }
    }

```
可以看到ServiceHandler会自己执行以下onHandleIntent方法，注意这个Handler是来自刚才创建的HandlerThread的Looper，所以是运行在那个HandlerThread线程里的，不是在主线程，所以这个方法可以做耗时操作

当这个方法做了耗时操作后，会自己调用stopSelf方法，传入这次消息的startId结束掉Service（如果startId不是最新的（系统自己会累加），那么就不会停止这个Service）

## onBind
```
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }
```
直接返回null，可以看出，IntentService并没有提供IBinder给调用者进行交互，也没有让Handler发送消息
足以看出，IntentService只能是以startService启动时做耗时操作才有效果，不然就跟普通的Service一样了

## onDestory
```
    @Override
    public void onDestroy() {
        mServiceLooper.quit();
    }
```

销毁的时候退出HandlerThread的消息循环


## 使用意义

综上分析可以看出，IntentService是要用startService来用的，然后可以在它的onHandlerIntent方法来做耗时处理，耗时方法处理完之后自己就会销毁掉

这样我们就可以让Service来做一些耗时操作，而且不用自己去关闭它，它自己耗时操作完会自己关闭

要比我们自己在Service中卡其Thread要方便一些。

## 懵逼记录
IntentService自己没有重写无参构造方法，我们自己继承的时候也要求重写一个参数的构造方法
这样的话，在Manifest.xml文件中注册的话就提示说没有无参方法了，所以自己继承IntentService后还要自己写一个无参的构造方法（可以去super(name)）


