https://blog.csdn.net/shaochen2015821426/article/details/79748487

自己在主app里面加了个content resolver的按钮，提供了MyContentProvider。同个应用去调用CURD，没问题

然后在modulea里面也加了个content resolver的按钮，去调用CURD，跨进程测试没问题

# 好处
- 统一封装
对数据进行封装，提供统一的接口，使用者完全不必关心这些数据是在DB，XML、Preferences或者网络请求来的。当项目需求要改变数据来源时，使用我们的地方完全不需要修改。
- 跨进程
提供了一种跨进程的数据共享方式

# Uri

URI主要分三个部分：scheme, authority and path。其中authority又分为host和port

<srandard_prefix>://<authority>/<data_path>/<id>

PS：要记住他们的名称，对应Uri里面的方法

- <srandard_prefix>：ContentProvider的srandard_prefix始终是 content://。
- <authority>：一般是ContentProvider的名称。
- <data_path>：请求的数据类型。
- <id>：指定请求的特定数据。

## 生成Uri
Uri可以通过Uri.parse(String)来得到，

## 拼接
拼接的话可以用：
```
uri = Uri.withAppendedPath(uri,"/1");
```

## 得到参数

用uri.getPathSegments()
集合里是依次截取Uri内的字符串的，List集合的下标从0开始。第一个元素为第一个“/”右边的字符。即：第一个子部分（此处为notes.），不包含元字符串最左端的部分。（content://不算的，从后面的斜杠

uri:content://com.example.interviewstudy.contentprovider.MyContentProvider/study//1
然后getPathSegments打印的结果是：getPathSegments:[study, 1]
说明一个斜杆或者两个斜杠不受影响

## UriMatcher

https://blog.csdn.net/sunqiujing/article/details/75011871

UriMatcher 类主要用于匹配Uri.

指定一个authority和一个path，后面是一个匹配码
```
UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
matcher.addURI("com.yfz.Lesson", "people", 1);
matcher.addURI("com.yfz.Lesson", "person/#", 2);
```
（addURI这个方法，第二个参数开始时不需要"/"， 否则是无法匹配成功的。）


```
Uri uri = Uri.parse("content://" + "com.yfz.Lesson" + "/people");
int match = matcher.match(uri);
```
这样就得到了math是1，也就是上面matcher.addURI方法的第三个参数

它的作用就是匹配Uri得到code，

--常量 UriMatcher.NO_MATCH 表示不匹配任何路径的返回码

--# 号为通配符

--* 号为任意字符


## ContentUris
ContentUris 类用于获取Uri路径后面的ID部分

```
Uri uri = Uri.parse("content://com.yfz.Lesson/people")
Uri resultUri = ContentUris.withAppendedId(uri, 10);
```



最后resultUri为: content://com.yfz.Lesson/people/10

```
Uri uri = Uri.parse("content://com.yfz.Lesson/people/10")
long personid = ContentUris.parseId(uri);
```

# 注册

## 基础写法
注册的时候重要是指定authorities属性，通常会指定这个类的类全称：
```
        <provider
            android:name="com.example.interviewstudy.contentprovider.MyContentProvider"
            android:authorities="com.example.interviewstudy.contentprovider.MyContentProvider"
            android:enabled="true"
            android:exported="true" />
```

## 调用权限
调用权限,比如指定对同一签名的应用才能调用，使用一个自定义的permission标签，用android:protectionLevel来定义权限等级是singture

```
 <permission android:name="com.example.interviewstudy.contentprovider.permission.READ"
                    android:protectionLevel="signature" />

        <permission android:name="com.example.interviewstudy.contentprovider.permission.WRITE"
                    android:protectionLevel="signature" />
   <provider
            android:name="com.example.interviewstudy.contentprovider.MyContentProvider"
            android:authorities="com.example.interviewstudy.contentprovider.MyContentProvider"
            android:grantUriPermissions="true"
            android:readPermission="com.example.interviewstudy.contentprovider.permission.READ"
            android:writePermission="com.example.interviewstudy.contentprovider.permission.WRITE"
            android:enabled="true"
            android:exported="true" />
```

## 部分开发权限
```
   <provider
            android:name="com.example.interviewstudy.contentprovider.MyContentProvider"
            android:authorities="com.example.interviewstudy.contentprovider.MyContentProvider"
            android:grantUriPermissions="true"
            android:readPermission="com.example.interviewstudy.contentprovider.permission.READ"
            android:writePermission="com.example.interviewstudy.contentprovider.permission.WRITE"
            android:enabled="true"
            android:exported="true"
            <path-permission
                    android:pathPrefix="/search_suggest_query"
                    android:readPermission="android.permission.GLOBAL_SEARCH" />
            <path-permission
                    android:pathPrefix="/search_suggest_shortcut"
                    android:readPermission="android.permission.GLOBAL_SEARCH" />
            <path-permission
                    android:pathPattern="/contacts/.*/photo"
                    android:readPermission="android.permission.GLOBAL_SEARCH" />
            <grant-uri-permission android:pathPattern=".*" />
             />
```

# 继承ContentProvider

继承后会重写ContentProvider的insert、delete、update、query方法，这个跟sqlite是差不多的

然后通常会用一个UriMatch，在static代码块来addUrl，用来匹配Uri


里面还有重写个onCreate和getType方法

## ContentProvider#onCreate()什么时候执行
ContentProvider#onCreate()方法比Application的onCreate方法要先执行

原因（源码角度）：在ActivityThread中的handleBindApplication方法中：
```
   Application app = data.info.makeApplication(data.restrictedBackupMode, null);
            mInitialApplication = app;
  if (!data.restrictedBackupMode) {
                if (!ArrayUtils.isEmpty(data.providers)) {
                    installContentProviders(app, data.providers);
                    // For process that contains content providers, we want to
                    // ensure that the JIT is enabled "at some point".
                    mH.sendEmptyMessageDelayed(H.ENABLE_JIT, 10*1000);
                }
            }

try {
                mInstrumentation.onCreate(data.instrumentationArgs);
            }
            catch (Exception e) {
                throw new RuntimeException(
                    "Exception thrown in onCreate() of "
                    + data.instrumentationName + ": " + e.toString(), e);
            }
```

里面先执行了installContentProviders方法之后再调用app的onCreate，而installContentProviders方法里面走了installProvider方法

里面执行了ContentProvider的attachInfo方法，该方法会调用它的onCreate方法。

细心一点会发现，尽管在installContentProviders方法之前，会先调用makeApplication，但是由于传入的Instrumentation对象是null，导致不走Application的onCreate方法：
```
   if (instrumentation != null) {
            try {
                instrumentation.callApplicationOnCreate(app);
            } catch (Exception e) {
                if (!instrumentation.onException(app, e)) {
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    throw new RuntimeException(
                        "Unable to create application " + app.getClass().getName()
                        + ": " + e.toString(), e);
                }
            }
        }
```



## ContentProvider#getContext()来自哪个
但是虽然在Application的onCreate之前执行，但在ContentProvider的onCreate里面调用getContext能打印出对象来，那就是Application
原因：追中源码，在调用ContentProvider的attachInfo方法时会传入一个Context，而这个Context会来自于ActivityThread的handleBindApplication方法中：

```
 try {
            // If the app is being launched for full backup or restore, bring it up in
            // a restricted environment with the base application class.
            Application app = data.info.makeApplication(data.restrictedBackupMode, null);
            mInitialApplication = app;

            // don't bring up providers in restricted mode; they may depend on the
            // app's custom Application class
            if (!data.restrictedBackupMode) {
                if (!ArrayUtils.isEmpty(data.providers)) {
                    installContentProviders(app, data.providers);
                    // For process that contains content providers, we want to
                    // ensure that the JIT is enabled "at some point".
                    mH.sendEmptyMessageDelayed(H.ENABLE_JIT, 10*1000);
                }
            }
```

可以看到会先调用Application app = data.info.makeApplication(data.restrictedBackupMode, null);
也就是LoadedApk的makeApplication方法

## ContentProvider#onCreate()返回值

看源码是说：true if the provider was successfully loaded, false otherwise

而源码里面并没有对返回值进行处理，所以我们返回false or true都不影响

可以作为我们自己的初始化工作的判断，比如有个自定义基类的ConetentProvider，我们可以：
```
if(super.onCreate()){
    //子类再对一些参数进行操作

    return true;

}else{
    //失败这样子

}
```



## ContentProvider运行在哪个线程

如果是同个进程的话，那么哪个线程调用getContentResolver()的话，ContentProvider就运行在那个线程,也就意味着主线程调用的话可能会阻塞


因为ContentProvider是基于BInder的，在Binder机制中，同进程的binder调用就是直接的对象调用，所以还是会在调用者的线程中

不同进程的话：运行在Binder线程，会阻塞调用者。并发请求时不是线程安全的
## ContentProvider有多少个实例
默认的情况下，android:multiprocess为false，所以ContentProvider只会生成一个实例，不管是进程内的调用还是外部进程调用，都会共享这个实例。
如果指定了：
```

        <provider
            android:name="com.example.interviewstudy.contentprovider.MyContentProvider"
            android:authorities="com.example.interviewstudy.contentprovider.MyContentProvider"
            android:permission="com.example.interviewstudy.contentprovider"
            android:multiprocess="true"
            android:process=":provider"
            android:enabled="true"
            android:exported="true" />
```
那么MyContentProvider会运行在一个单独的线程，并且会未每个调用进程创建一个单独的实例
（必须指定android:multiprocess="true"，同时还需要指定一下 android:process）

## ContentProvider未启动时访问

## registerContentObserver

一款应用要使用多个ContentProvider，若需要了解每个ContentProvider的不同实现从而再完成数据交互，操作成本高 & 难度大，所以再ContentProvider类上加多了一个 ContentResolver类对所有的ContentProvider进行统一管理。
调用者可以通过：
```
  getContentResolver().registerContentObserver(uri, true,contentObserver =  new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                log_i("onChange selfChange:"+selfChange+",uri:"+uri);
                super.onChange(selfChange, uri);
            }
        });
```
（注意，ContentObserver生命周期与Activity这些无关，所以必须自己手动调用etContentResolver().unregisterContentObserver(contentObserver);）

这样在当ContentProvider里面有数据发生改变时，ContentProvider可以调用：
```
getContext().getContentResolver().notifyChange(uri,null);
```

来通知那些观察者




## 启动流程源码分析

https://www.jianshu.com/p/74014e1b18b0


图片: ![Alt](https://upload-images.jianshu.io/upload_images/1973320-af2bfc6289111249.png)
