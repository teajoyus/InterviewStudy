最近有同事问我，他想添加一个层级最高的Window（用于水印提示），并且在切换Activity时不会被跳转的Activity所遮挡。
然而，移植了另外一个弹出框的代码后发现，怎么样设置都避免不了新跳转的activity会遮住这个Window
部分重要代码如下：
```
mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				
WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
mParams.gravity = Gravity.BOTTOM;
if (!tipsView.isAttachedToWindow()) {
	mWindowManager.addView(tipsView, mParams);
}
```

在检查了其它不一致的地方后，始终觉得不应该，怎么添加的window就还是会被新的Activity遮住呢。

最后注意到了，同事把这段代码是放在Activity中调用，而原本的代码是放在Service中调用。
都是利用Context去拿到一个WindowManagerImpl实例，难道这有什么区别？
答案是：还真有区别。

首先，在Service中，调用getSystemService时是它的基类ContextWrapper：
```
 @Override
    public Object getSystemService(String name) {
        return mBase.getSystemService(name);
    }
```
mBase也是一个Conext对象，实际代表的是它的实现类ConextImpl（后面解释），ContextWrapper相当于一个装饰者对象，对mBase进行装饰。

而在ConextImpl中则实现了getSystemService方法：
```
 @Override
    public Object getSystemService(String name) {
        return SystemServiceRegistry.getSystemService(this, name);
    }
```
查看SystemServiceRegistry类可以看出，系统服务的注册都是在这个类里面，当我们获取系统服务的时候也是用这个类。

那么也就是说如果是在Service里面的话，确实是拿了一个WindowsManagerImpl的实例。

那在Activity中呢？

Activity中实现了getSystemService方法：
```
 @Override
    public Object getSystemService(String name) {
        if (getBaseContext() == null) {
            throw new IllegalStateException(
                    "System services not available to Activities before onCreate()");
        }

        if (WINDOW_SERVICE.equals(name)) {
            return mWindowManager;
        } else if (SEARCH_SERVICE.equals(name)) {
            ensureSearchManager();
            return mSearchManager;
        }
        return super.getSystemService(name);
    }
```
有点巧，刚好Activity会判断如果想要获取的是WINDOW_SERVICE这个服务的话，那么则返回自己持有的mWindowManager。
那么这个mWindowManager与在SystemServiceRegistry类里面拿的实例不一样么？
我们看看这个mWindowManager的赋值时机：
```
 final void attach(Context context, ActivityThread aThread,
            Instrumentation instr, IBinder token, int ident,
            Application application, Intent intent, ActivityInfo info,
            CharSequence title, Activity parent, String id,
            NonConfigurationInstances lastNonConfigurationInstances,
            Configuration config) {
            
      ...
      //创建一个新的Window
       mWindow = PolicyManager.makeNewWindow(this);
      ...
      //为创建的Window设置一个WindowManger对象
      mWindow.setWindowManager(
                (WindowManager)context.getSystemService(Context.WINDOW_SERVICE),
                mToken, mComponent.flattenToString(),
                (info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0);  
      ...
      //得到Window持有的一个WindowManger对象
       mWindowManager = mWindow.getWindowManager();
      ...
            
            
   }

```
到这里，问题就出在这里，mWindowManager并不是直接调用(WindowManager)context.getSystemService(Context.WINDOW_SERVICE)拿到的
我们看下 mWindow.getWindowManager()方法：
```
public WindowManager getWindowManager() {
        return mWindowManager;
    }
```
没啥，看它的赋值时机，发现正是在调用setWindowManager的时候：
```
    public void setWindowManager(WindowManager wm, IBinder appToken, String appName,
            boolean hardwareAccelerated) {
        mAppToken = appToken;
        mAppName = appName;
        mHardwareAccelerated = hardwareAccelerated
                || SystemProperties.getBoolean(PROPERTY_HARDWARE_UI, false);
        if (wm == null) {
            wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        mWindowManager = ((WindowManagerImpl)wm).createLocalWindowManager(this);
    }
```
可以看出，传进来的WindowManager对象并不是直接赋值给mWindowManager，而是调用了其实现类WindowManagerImpl的createLocalWindowManager方法，并把自身作为参数传了进来。
我们看下WindowManagerImpl的createLocalWindowManager方法：
```
 public WindowManagerImpl createLocalWindowManager(Window parentWindow) {
        return new WindowManagerImpl(mDisplay, parentWindow);
    }

```
原来Activity持有的WindowManager对象并不是来自于Conext的getSystemService方法，而是自己创建了一个“本地”的。
它传入了Activity自身的一个Window参数作为parentWindow，说到这里相比也清楚了是因为这样，导致新添加的window变成作为了Activity的子Window而存在了。

我们继续看看。既然Activity创建WindowManagerImpl传入了自己的一个window对象作为parentWindow，那么通过Context拿到的WindowManagerImpl对象呢，

我们直接看上面提到的SystemServiceRegistry类，在它的静态代码块中注册了这个服务（也就是创建了这个实例）
```
   static {
   
   ...
   
     registerService(Context.WINDOW_SERVICE, WindowManager.class,
                new CachedServiceFetcher<WindowManager>() {
            @Override
            public WindowManager createService(ContextImpl ctx) {
                return new WindowManagerImpl(ctx);
            }});
   ...
   
   
   }
```

可以看出默认的WindowManagerImpl对象创建时传入的只是一个ContextImpl对象，而在WindowManagerImpl中的parentWindow也就为空
```
 public WindowManagerImpl(Context context) {
        this(context, null);
    }

    private WindowManagerImpl(Context context, Window parentWindow) {
        mContext = context;
        mParentWindow = parentWindow;
    }
```

那么有了这个区别之后，在WindowManagerImpl的addView方法过程中：
```
   @Override
    public void addView(View view, ViewGroup.LayoutParams params) {
        mGlobal.addView(view, params, mDisplay, mParentWindow);
    }
```
调用到WindowManagerGlobal的addView方法：
```
  public void addView(View view, ViewGroup.LayoutParams params,
            Display display, Window parentWindow) {
         ...
         
         
          if (parentWindow != null) {
            parentWindow.adjustLayoutParamsForSubWindow(wparams);
        } else {
            // If there's no parent, then hardware acceleration for this view is
            // set from the application's hardware acceleration setting.
            final Context context = view.getContext();
            if (context != null
                    && (context.getApplicationInfo().flags
                            & ApplicationInfo.FLAG_HARDWARE_ACCELERATED) != 0) {
                wparams.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
            }
        }
         ...   
            
            
  ｝
```