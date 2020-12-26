@[TOC]
# 前言
&nbsp;&nbsp;&nbsp;&nbsp;目前来说项目里用到的图片加载框架，都是Glide、Picasso、Fresco这三大图片框架其中的一种，记得以前还有个Universal-Image-Loader，现在一些老项目可能还有在使用。这几个框架都有各自的优缺点，而本篇要探索的重点是对Glide的源码解析，毕竟是google的亲儿子，Glide还是值得我们去使用和探索的。接下来我们不会赘述Glide的用法，重点放在源码分析上，由于源码也不少，所以分析的流程会沿着Glide.with(context).load("http://xxx.png").into(imageview)这条链路去分析。
&nbsp;&nbsp;&nbsp;&nbsp;由于篇幅较长，这里我拆分成了上篇和下篇，下篇是：[Glide4.x的源码解析（下）](https://blog.csdn.net/lc_miao/article/details/106803478).


# 获取RequestManager过程
&nbsp;&nbsp;&nbsp;&nbsp;使用glide来加载图片那第一个方法我们就是Glide.with()了，它支持传入Activity、Context、Fragment、View这几种对象，来得到一个RequestManager对象。那么参数传入的类型不同，都有什么差别呢？
我们展开来说：
先从Glide.with(activity)入手，有两个方法：
```
 @NonNull
  public static RequestManager with(@NonNull Activity activity) {
    return getRetriever(activity).get(activity);
  }
    @NonNull
  public static RequestManager with(@NonNull FragmentActivity activity) {
    return getRetriever(activity).get(activity);
  }
```
（其实这两个方法本质上是一样的，只不过FragmentActivity是来自support的，后面就我们以FragmentActivity对象参数为例。）
&nbsp;&nbsp;&nbsp;&nbsp;先看看getRetriever(activity)得到了啥：
```
  @NonNull
  private static RequestManagerRetriever getRetriever(@Nullable Context context) {
    // Context could be null for other reasons (ie the user passes in null), but in practice it will
    // only occur due to errors with the Fragment lifecycle.
    Preconditions.checkNotNull(
        context,
        "You cannot start a load on a not yet attached View or a Fragment where getActivity() "
            + "returns null (which usually occurs when getActivity() is called before the Fragment "
            + "is attached or after the Fragment is destroyed).");
    return Glide.get(context).getRequestManagerRetriever();
  }
```
可以看到是得到了一个RequestManagerRetriever对象，来看下这个对象怎么拿到的：
Glide.get(context)代码：
```
 @NonNull
  public static Glide get(@NonNull Context context) {
    if (glide == null) {
      GeneratedAppGlideModule annotationGeneratedModule =
          getAnnotationGeneratedGlideModules(context.getApplicationContext());
      synchronized (Glide.class) {
        if (glide == null) {
          checkAndInitializeGlide(context, annotationGeneratedModule);
        }
      }
    }

    return glide;
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;这里可以看出，这里用了单例设计模式-双重检查锁来得到一个全局的Glide对象，并不会创建多个。最终是通过Glide#initializeGlide方法来完成初始化的：
```
  private static void initializeGlide(
      @NonNull Context context,
      @NonNull GlideBuilder builder,
      @Nullable GeneratedAppGlideModule annotationGeneratedModule) {
	  ...
	  //如果开启了AndroidManifest配置方式
	 if (annotationGeneratedModule == null || annotationGeneratedModule.isManifestParsingEnabled()) {
      manifestModules = new ManifestParser(applicationContext).parse();
    }
      ...
      builder.setRequestManagerFactory(factory);
      //解析在AndroidManifest中配置的module
      for (com.bumptech.glide.module.GlideModule module : manifestModules) {
      module.applyOptions(applicationContext, builder);
    }
    //在注解类中回调applyOptions，我们可以在这里做default options的处理
     if (annotationGeneratedModule != null) {
      annotationGeneratedModule.applyOptions(applicationContext, builder);
    }
    //创建了Glide对象，初始化
     Glide glide = builder.build(applicationContext);
	 ..	 
	 //向application注册了callback，glide类本身实现了ComponentCallbacks2接口，由于在内存不足时做操作，这个后面再细讲
 	applicationContext.registerComponentCallbacks(glide);
    Glide.glide = glide;
}
```
&nbsp;&nbsp;&nbsp;&nbsp;在4.0以前我们在AndroidManifest中配置glide，而4.0后通过@GlideModule注解简化了这个，不过同时也保留了这个方式。并提供了一个isManifestParsingEnabled()方法来让我们决定用哪种方式。最后通过 builder.build(applicationContext)创建了Glide对象，也就是在GlideBuilder的build方法中，初始化各种池、加载引擎等，这个我们不展开讲了。值得注意的是在build方法中也创建了一个RequestManagerRetriever对象传入给Glide对象持有。
草草的说完Glide对象的创建，回到getRetriever方法可以看出就是从Glide对象中获取在Glide创建时也创建的RequestManagerRetriever对象。
那么RequestManagerRetriever这个对象是干嘛用的呢？我感觉要随着后面的分析才能逐渐说明它的用途。
&nbsp;&nbsp;&nbsp;&nbsp;好，回到最上面的Glide#with方法，我们得知 getRetriever(activity)就是拿到RequestManagerRetriever对象（而如果是第一次拿则会初始化一个单例的Glide对象）。
而getRetriever(activity).get(activity)，我们来看下这个get方法：
```
  @NonNull
  public RequestManager get(@NonNull FragmentActivity activity) {
    if (Util.isOnBackgroundThread()) {
      return get(activity.getApplicationContext());
    } else {
      assertNotDestroyed(activity);
      FragmentManager fm = activity.getSupportFragmentManager();
      return supportFragmentGet(activity, fm, /*parentHint=*/ null, isActivityVisible(activity));
    }
  }

```
&nbsp;&nbsp;&nbsp;&nbsp;首先通过Util.isOnBackgroundThread()判断是否处于子线程，如果是子线程则调用get方法的另外一个重载方法：
```
 @NonNull
  public RequestManager get(@NonNull Context context) {
    if (context == null) {
      throw new IllegalArgumentException("You cannot start a load on a null Context");
    } else if (Util.isOnMainThread() && !(context instanceof Application)) {
      if (context instanceof FragmentActivity) {
        return get((FragmentActivity) context);
      } else if (context instanceof Activity) {
        return get((Activity) context);
      } else if (context instanceof ContextWrapper
          // Only unwrap a ContextWrapper if the baseContext has a non-null application context.
          // Context#createPackageContext may return a Context without an Application instance,
          // in which case a ContextWrapper may be used to attach one.
          && ((ContextWrapper) context).getBaseContext().getApplicationContext() != null) {
        return get(((ContextWrapper) context).getBaseContext());
      }
    }

    return getApplicationManager(context);
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;这个方法逻辑很清晰，前面我们说了Glide#with方法能接收Context对象，那么实际上Context对象就在这里做了类型区分。
而如果是子线程的话，容易看出就是走了最后那句代码： return getApplicationManager(context);，代码如下：
```
 private RequestManager getApplicationManager(@NonNull Context context) {
    // Either an application context or we're on a background thread.
    if (applicationManager == null) {
      synchronized (this) {
        if (applicationManager == null) {
         	...
          applicationManager =
              factory.build(
                  glide,
                  //绑定的生命周期是application
                  new ApplicationLifecycle(),
                  new EmptyRequestManagerTreeNode(),
                  context.getApplicationContext());
        }
      }
    }
    return applicationManager;
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;到此我们可以确认，如果你是在子线程调用Glide.with的话，那不管你是传入activity对象、Context对象，都是走了这个getApplicationManager方法拿到一个applicationManager，注意它是一个单例的RequestManager对象。
&nbsp;&nbsp;&nbsp;&nbsp;它在构造RequestManager对象时传入的第二个参数是new ApplicationLifecycle()，其实这里不难看出，这是绑定了Application级别的声明周期。
&nbsp;&nbsp;&nbsp;&nbsp;那如果在主线程，则get(activity)方法最后调用了supportFragmentGet方法：
```
  @NonNull
  private RequestManager supportFragmentGet(
      @NonNull Context context,
      @NonNull FragmentManager fm,
      @Nullable Fragment parentHint,
      boolean isParentVisible) {
      //创建一个SupportRequestManagerFragment
    SupportRequestManagerFragment current =
        getSupportRequestManagerFragment(fm, parentHint, isParentVisible);
    RequestManager requestManager = current.getRequestManager();
    if (requestManager == null) {
      // TODO(b/27524013): Factor out this Glide.get() call.
      Glide glide = Glide.get(context);
      //创建一个RequestManager
      requestManager =
          factory.build(
              glide, current.getGlideLifecycle(), current.getRequestManagerTreeNode(), context);
      //绑定
      current.setRequestManager(requestManager);
    }
    return requestManager;
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;方法里首先出现了一个SupportRequestManagerFragment，点进去先不分析它的源码，只看他是继承了Fragment。
通过getSupportRequestManagerFragment方法来创建或者拿到SupportRequestManagerFragment。然后再创建一个RequestManager对象，注意这里创建RequestManager对象时传入的第二个参数是current.getGlideLifecycle(),也就是这个Fragment的生命周期。
到这里我们发现了，原来Glide在请求图片之前，它会创建一个SupportRequestManagerFragment对象，其实很多SDK都是这么做，通过创建一个空不可见的Fragment来同步Activity生命周期。
然后再对SupportRequestManagerFragment绑定一个ReuqestManager对象。
&nbsp;&nbsp;&nbsp;&nbsp;**至此我们先总结一下，使用Glide#with(activity)方法会在FragmentActivity里会创造一个SupportRequestManagerFragment，并且创建一个RequestManager。而且它们是1对1的，也就是一个Activity里只会有最多有一个SupportRequestManagerFragment对象和一个RequestManager对象，不会重复创建的。
而如果Glide#with(activity)方法是在子线程环境下调用的则绑定的是Application生命周期级别的一个RequestManager，它就跟Activity就没什么关系了。它跟Applcation也是1比1的。**
&nbsp;&nbsp;&nbsp;&nbsp;为什么如果是子线程就需要这么处理呢？其实涉及到Activity处理Fragment所以不能放在子线程，如果要Post到主线程的话又变成异步操作加大了获取难度。所以干脆就去拿applcation级别的RequestManager。只不过不同的是图片加载的生命周期就变成了application级别了，所以我们需要注意这一点，尽量不要在子线程去加载图片。

&nbsp;&nbsp;&nbsp;&nbsp;那Glide#with方法除此之外还能接收View参数，其实本身也就是通过View获取Context对象，所以是一样的道理。
剩下最后一种是接收Fragment对象，这个也是同理，只不过如果传入的是Activity级别的，那么SupportRequestManagerFragment是附着在Activity里的，而如果传入的是Fragment对象，那么SupportRequestManagerFragment是作为child fragment附着到这个Fragment上的。
&nbsp;&nbsp;&nbsp;&nbsp;**我们容易看出他们最大的差别其实就是生命周期不同，因为生命周期的不同，后面加载过程的清除处理时机也就不同。**
&nbsp;&nbsp;&nbsp;&nbsp;所以到这里我们也建议了如果你是在Fragment里加载图片，那最好就不要使用Context，而是传入Fragment对象，这样图片加载的生命周期就会更短一些。

&nbsp;&nbsp;&nbsp;&nbsp;所以噼里啪啦到这里才讲完了Glide#with的大致过程，其实我们根本都没讲Glide如何初始化的，如何读取AndroidMenfiest的和注解的GlideModule配置的。

# RequestManager生命周期绑定
&nbsp;&nbsp;&nbsp;&nbsp;上面我们知道了Glide是利用一个不可见的Fragment来实现生命周期绑定的，这里我们就来了解下具体是如何做绑定的。
我们来看看SupportRequestManagerFragment内部，在构造的时候：
```
  public SupportRequestManagerFragment() {
    this(new ActivityFragmentLifecycle());
  }
  @VisibleForTesting
  @SuppressLint("ValidFragment")
  public SupportRequestManagerFragment(@NonNull ActivityFragmentLifecycle lifecycle) {
    this.lifecycle = lifecycle;
  }
```
可以看到创造了一个ActivityFragmentLifecycle对象，并且在几个周期方法里都转发给这个lifecycle：
```
  @Override
  public void onStart() {
    super.onStart();
    lifecycle.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();
    lifecycle.onStop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    lifecycle.onDestroy();
    unregisterFragmentWithRoot();
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;可以看出SupportRequestManagerFragment其实自身做的生命周期处理就是分发，它本身并没有处理啥逻辑。重点自然而然就放在了ActivityFragmentLifecycle上，点进去看ActivityFragmentLifecycle代码，很欣慰，因为他只是一个Lifecycle的实现。内部维护了一个集合。
通过addListener方法来添加监听和removeListener移除，然后实现Lifecycle的方法就是做这个生命周期回调。
那不用想了，看下addListener方法谁在用，点出来就发现了RequestManager，还记得在构造RequestManager的时候传入的Lifecycle对象吗，是通过调用SupportRequestManagerFragment#getGlideLifecycle()方法传入的，然后又对这个Lifecycle对象调用addListener(this)把自己加入监听。
**所以不难看出，这一层层的转发和回调，实际上就是为了让RequestManager具有生命周期的能力。**


# RequestManager内部加载图片过程
## 构造SingleRequest
上面我们获取到的RequestManager对象，如果我们是通过注解的方式，那么它其实是GlideRequests的实例，GlideRequests继承了RequestManager对象。原因是在上面我们看到获取这个对象都是用factory.build()，而这个factory实际就是GeneratedRequestManagerFactory
对象。在Glide初始化时initializeGlide方法里会通过GeneratedAppGlideModuleImpl这个实现类去创建这个factory。而这个factory在创建RequestManager实例的时候new出的是GlideRequests对象。初看GlideRequests也没啥差别，只是做了一层包装，利用后期拓展。而毕竟GlideRequests本身暂时也没做其它什么操作，所以主要还是在RequestManager对象内部。包括我们后续要讲的RequestBuilder对象，它实际也是GlideRequest的实例。
我们继续按着加载流程去走，经过Glide#with()之后我们通常接下来就是调用load方法了，那么我们直接看load方法好了。
load方法有太多个重载个了，它支持传入多钟类型的来源，因为我们通常也是用于加载网络图片的。在这里我们举一个重要的load(String string)，它调用了父类RequestManager方法：
```
   public RequestBuilder<Drawable> load(@Nullable String string) {
    return asDrawable().load(string);
  }
```
先调用了一下asDrawable()，我们也很清楚，假如我们想得到Bitmap类型的，我们就可以在Glide.with(context).asBitmap()。
点进去asDrawable()就是调用了as方法并传入Drawable.class这个参数，通过as方法再创建了一个GlideRequest对象（GlideRequests重写了as方法，返回的是GlideRequest实例）记住这个Drawable.class参数最后是传给了GlideRequest里的transcodeClass变量。
接下来看GlideRequest的load方法，实现在RequestBuilder里：
```
  public RequestBuilder<TranscodeType> load(@Nullable String string) {
    return loadGeneric(string);
  }
    private RequestBuilder<TranscodeType> loadGeneric(@Nullable Object model) {
    this.model = model;
    isModelSet = true;
    return this;
  }
```
这样就完了，这就是一个Builder模式，设置了model这个参数，注意model这个参数类型是Object，它就是各个load方法传入的参数。
然后其它的比如format方法、override方法、placeholder方法等我们暂时不管它，反正他们就是Builder构建，都是属于选项设置。
我们直接就来重头戏：into方法。into也有好多个重载方法，这里我们先看常用的into(ImageView)方法：
```
  public ViewTarget<ImageView, TranscodeType> into(@NonNull ImageView view) {
  		...
      return into(
        glideContext.buildImageViewTarget(view, transcodeClass),
        /*targetListener=*/ null,
        requestOptions,
        Executors.mainThreadExecutor());
  }
```
源码上看，首先看到了熟悉的getScaleType()，看上去这是Glide在为每个不同的scaleType做不同的处理，我们先不去细挖它。
到最后又调用了into的重载方法，注意第一个参数是glideContext.buildImageViewTarget(view, transcodeClass)
transcodeClass记得么，就是我们上面传入的那个Drawable.class，点进去看buildImageViewTarget最终调用到了ImageViewTargetFactory#buildTarget方法：
```
  public <Z> ViewTarget<ImageView, Z> buildTarget(
      @NonNull ImageView view, @NonNull Class<Z> clazz) {
    if (Bitmap.class.equals(clazz)) {
      return (ViewTarget<ImageView, Z>) new BitmapImageViewTarget(view);
    } else if (Drawable.class.isAssignableFrom(clazz)) {
      return (ViewTarget<ImageView, Z>) new DrawableImageViewTarget(view);
    } else {
      throw new IllegalArgumentException(
          "Unhandled class: " + clazz + ", try .as*(Class).transcode(ResourceTranscoder)");
    }
  }
```
这里就根据transcodeClass来创建对应的ViewTarget，比如我们上面用的是asDrawbale()，那这例创建的就是DrawableImageViewTarget实例了，我们继续看主流程，先不看DrawableImageViewTarget里的源码。
继续看接下来调用的另外一个into重载方法：
```
  private <Y extends Target<TranscodeType>> Y into(
      @NonNull Y target,
      @Nullable RequestListener<TranscodeType> targetListener,
      BaseRequestOptions<?> options,
      Executor callbackExecutor) {
    Preconditions.checkNotNull(target);
    //必须设置过来源
    if (!isModelSet) {
      throw new IllegalArgumentException("You must call #load() before calling #into()");
    }
	//构造一个请求，后文再细说
    Request request = buildRequest(target, targetListener, options, callbackExecutor);
    Request previous = target.getRequest();
    //如果当前的Reuqest和上一个request相等，并且满足情况那么直接重新再执行一遍，确保刷新。
    if (request.isEquivalentTo(previous)
        && !isSkipMemoryCacheWithCompletePreviousRequest(options, previous)) {
      // If the request is completed, beginning again will ensure the result is re-delivered,
      // triggering RequestListeners and Targets. If the request is failed, beginning again will
      // restart the request, giving it another chance to complete. If the request is already
      // running, we can let it continue running without interruption.
      if (!Preconditions.checkNotNull(previous).isRunning()) {
        // Use the previous request rather than the new one to allow for optimizations like skipping
        // setting placeholders, tracking and un-tracking Targets, and obtaining View dimensions
        // that are done in the individual Request.
      
        previous.begin();
      }
      return target;
    }
	//先做清除，避免重复添加
    requestManager.clear(target);
    //绑定request
    target.setRequest(request);
    //开始执行
    requestManager.track(target, request);

    return target;
  }
```
先判断isModelSet，这个变量就是当我们在调用load的时候就会设置为true的。然后通过buildRequest方法构建了一个我们还不清楚的Request对象
然后判断这个Target是不是已经绑定过Request了，并且满足情况的话就直接重新刷一遍。
具体满足什么情况呢，下面的源码注释解释了：如果请求已完成，则重新开始将确保重新；如果请求失败，将重新开始启动请求；如果请求已经在运行，我们可以让它继续运行而不中断。
有点拗口，反正这里也不重要，就是Glide做了一层确保。
然后源码往下来，先clear一遍，因为可能存在相同的target了，然后再 调用target.setRequest(request);把Reuqest绑定到这个target上。

```
  synchronized void track(@NonNull Target<?> target, @NonNull Request request) {
    targetTracker.track(target);
    requestTracker.runRequest(request);
  }
    /** Starts tracking the given request. */
  public void runRequest(@NonNull Request request) {
  	//记录该request
    requests.add(request);
    //如果不是暂停状态，那么就可以开始执行
    if (!isPaused) {
      request.begin();
    } else {
    //如果是暂停的，那么就先做清除处理，然后再把request加到pendingRequests里，等下次是非pause状态时再从pendingRequests取出来
      request.clear();
      if (Log.isLoggable(TAG, Log.VERBOSE)) {
        Log.v(TAG, "Paused, delaying request");
      }
      pendingRequests.add(request);
    }
  }
```
可以看出实际上requestManager.track(target, request);这句话就是分别对targetTracker变量和requests进行一次add，然后又一样调用request.begin()开启请求。这个targetTracker和requests我们姑且可以认为是两个容器，分别来存储target对象和request对象。
所以不难看出，接下来的操作就在request.begin()身上了。我们在开始begin之前得先了解这个request是什么来历。
在前面的into方法里，有这一句：
```
 Request request = buildRequest(target, targetListener, options, callbackExecutor);
```
我们追踪buildRequest方法，最终调用到buildRequestRecursive方法：
```
private Request buildRequestRecursive(
      Object requestLock,
      Target<TranscodeType> target,
      @Nullable RequestListener<TranscodeType> targetListener,
      @Nullable RequestCoordinator parentCoordinator,
      TransitionOptions<?, ? super TranscodeType> transitionOptions,
      Priority priority,
      int overrideWidth,
      int overrideHeight,
      BaseRequestOptions<?> requestOptions,
      Executor callbackExecutor) {
      ...
       ErrorRequestCoordinator errorRequestCoordinator = null;
      //如果通过调用error方法配置过，那么就不为空，构建的是一个ErrorRequestCoordinator类型的Request
    if (errorBuilder != null) {
      errorRequestCoordinator = new ErrorRequestCoordinator(requestLock, parentCoordinator);
      parentCoordinator = errorRequestCoordinator;
    }
      构造正常请求的Request
       Request mainRequest =
        buildThumbnailRequestRecursive(
            requestLock,
            target,
            targetListener,
            parentCoordinator,
            transitionOptions,
            priority,
            overrideWidth,
            overrideHeight,
            requestOptions,
            callbackExecutor);
	//如果没有定义error，那么正常就是使用SingleRequest
    if (errorRequestCoordinator == null) {
      return mainRequest;
    }
    ...
    //不然就是用这个ErrorRequestCoordinator
  errorRequestCoordinator.setRequests(mainRequest, errorRequest);
    return errorRequestCoordinator;
    }
```
这个调用链的源码实在太长了，我们全部贴出来就很难看，在buildRequestRecursive完成的工作就是判断需不需要构造error类型的请求（ErrorRequestCoordinator），
如果不需要那么就直接构造正常的Request（SingleRequest）。什么意思呢，就是在RequelstBuidler里面有个error方法，它支持我们当请求的url时产生错误则用另外另外一个备用的url来做代替。通常我们也比较少需要用到这个功能。所以这里我把buildRequestRecursive方法其它代码省略掉，逻辑会清晰一些，，而其中的buildThumbnailRequestRecursive方法，我们继续看：
```
  private Request buildThumbnailRequestRecursive(
      Object requestLock,
      Target<TranscodeType> target,
      RequestListener<TranscodeType> targetListener,
      @Nullable RequestCoordinator parentCoordinator,
      TransitionOptions<?, ? super TranscodeType> transitionOptions,
      Priority priority,
      int overrideWidth,
      int overrideHeight,
      BaseRequestOptions<?> requestOptions,
      Executor callbackExecutor) {
       if (thumbnailBuilder != null) {
       ...
       ThumbnailRequestCoordinator coordinator =
          new ThumbnailRequestCoordinator(requestLock, parentCoordinator);
		...
		 coordinator.setRequests(fullRequest, thumbRequest);
      return coordinator;
.
       }else if(thumbSizeMultiplier != null){
		...
		return coordinator;
}
else{
  return obtainRequest(
          requestLock,
          target,
          targetListener,
          requestOptions,
          parentCoordinator,
          transitionOptions,
          priority,
          overrideWidth,
          overrideHeight,
          callbackExecutor);
    }
}
}
```
&nbsp;&nbsp;&nbsp;&nbsp;其实在buildThumbnailRequestRecursive里面又判断了我们有有没有配置缩略图。如果配置了缩略图那么就是ThumbnailRequestCoordinator实例,如果没有的话才是SingleRequest实例（obtainRequest方法里new出了一个SingleRequest）。

&nbsp;&nbsp;&nbsp;&nbsp;所以这里我们总结一下这个Request的生成过程：它通过判断我们在Builder构造的时候有没有用error(@Nullable RequestBuilder<TranscodeType> errorBuilder)，或者是有没有用到 thumbnail(
      @Nullable RequestBuilder<TranscodeType> thumbnailRequest) 
   
  如果都没有，那么这个Request就是SingleRequest；
  如果有构造Error时的处理，那么就是ErrorRequestCoordinator（它包裹了一个正常的Request和和error时请求的Request）
  如果有构造thumbnail的处理，那么这个正常的Request就是ThumbnailRequestCoordinator类型（它包裹了一个正常的Request和一个缩略图的Request）。
&nbsp;&nbsp;&nbsp;&nbsp;这里我们就摸索这个SingleRequest的情况好不好，不然太麻烦了。只要我们知道，这个调用链通过buildRequest()方法构造Request  ->buildRequestRecursive()方法构造可能包含error处理的包裹Request  - > buildThumbnailRequestRecursive()方法构造可能包含缩略图处理的包裹Request。

## onSizeReady计算
  &nbsp;&nbsp;&nbsp;&nbsp;那接下来我们就可以进入SingleRequest#begin()方法来一探究竟了。begin方法代码不算特别多，全部贴上算了：
  ```
  Override
  public void begin() {
    synchronized (requestLock) {
      assertNotCallingCallbacks();
      stateVerifier.throwIfRecycled();
      startTime = LogTime.getLogTime();
      //检查来源是否为空，为空的话直接回调onLoadFailed方法
      if (model == null) {
        if (Util.isValidDimensions(overrideWidth, overrideHeight)) {
          width = overrideWidth;
          height = overrideHeight;
        }
        // Only log at more verbose log levels if the user has set a fallback drawable, because
        // fallback Drawables indicate the user expects null models occasionally.
        int logLevel = getFallbackDrawable() == null ? Log.WARN : Log.DEBUG;
        onLoadFailed(new GlideException("Received null model"), logLevel);
        return;
      }
	//检查请求是否已经处于运行状态，不能重复运行。
      if (status == Status.RUNNING) {
        throw new IllegalArgumentException("Cannot restart a running request");
      }

      // If we're restarted after we're complete (usually via something like a notifyDataSetChanged
      // that starts an identical request into the same Target or View), we can simply use the
      // resource and size we retrieved the last time around and skip obtaining a new size, starting
      // a new load etc. This does mean that users who want to restart a load because they expect
      // that the view size has changed will need to explicitly clear the View or Target before
      // starting the new load.
      //源码也已经注释过了，如果说状态已经是加载完成了，那么直接就会回调onResourceReady方法了。
      if (status == Status.COMPLETE) {
        onResourceReady(resource, DataSource.MEMORY_CACHE);
        return;
      }

      // Restarts for requests that are neither complete nor running can be treated as new requests
      // and can run again from the beginning.
	//先假设status处于需要等待target测量宽高的状态
      status = Status.WAITING_FOR_SIZE;
      //如果宽高都是有效的，那么调用onSizeReady方法，在该方法里会把status置为Status.RUNNING状态
      if (Util.isValidDimensions(overrideWidth, overrideHeight)) {
        onSizeReady(overrideWidth, overrideHeight);
      } else {
      //如果还不确定宽高，那么就交给target自己去处理
        target.getSize(this);
      }
	//如果是进入运行状态了或者是等待布局大小的状态，那么就是正常的开始状态，会在这里回调target的onLoadStarted方法
      if ((status == Status.RUNNING || status == Status.WAITING_FOR_SIZE)
          && canNotifyStatusChanged()) {
        target.onLoadStarted(getPlaceholderDrawable());
      }
      if (IS_VERBOSE_LOGGABLE) {
        logV("finished run method in " + LogTime.getElapsedMillis(startTime));
      }
    }
  }

  ```
&nbsp;&nbsp;&nbsp;&nbsp;上面的注释已经写挺多了，在begin方法里主要做的工作就是除了做一下状态的校验，另外一个重点是规划图片的宽高，如果我们没调用过override方法去传入固定的宽高值，那么这里就会进入target.getSize(this);，那Glide是怎么确定宽高的呢，我们先摸清楚一下target.getSize(this)执行的流程，首先我们前面就讲到DrawableImageViewTarget的来源，也就是如果通过into(imageview)的话这里target要么就是DrawableImageViewTarget要么就是BitmapImageViewTarget，我们就先相当于是DrawableImageViewTarget，看getSize()方法是父类ViewTarget中的：
```
   void getSize(@NonNull SizeReadyCallback cb) {
      int currentWidth = getTargetWidth();
      int currentHeight = getTargetHeight();
      //如果宽高已经是有效了，那么就可以回调onSizeReady了
      if (isViewStateAndSizeValid(currentWidth, currentHeight)) {
        cb.onSizeReady(currentWidth, currentHeight);
        return;
      }
	//否则下面就是对宽高获取的处理了
      // We want to notify callbacks in the order they were added and we only expect one or two
      // callbacks to be added a time, so a List is a reasonable choice.
      //把回调监听加入进来，等下获取到宽高后才可以回调出去
      if (!cbs.contains(cb)) {
        cbs.add(cb);
      }
      if (layoutListener == null) {
      //可以看到是利用ViewTreeObserver的形式，通过addOnPreDrawListener来监听View在draw阶段之前，如果view接下来要到draw阶段，那么它一定是已经知道宽高了。
        ViewTreeObserver observer = view.getViewTreeObserver();
        layoutListener = new SizeDeterminerLayoutListener(this);
        observer.addOnPreDrawListener(layoutListener);
      }
    }

//SizeDeterminerLayoutListener就是OnPreDrawListener，用来监听View在draw之前，把宽高回调出去
    private static final class SizeDeterminerLayoutListener
        implements ViewTreeObserver.OnPreDrawListener {
      private final WeakReference<SizeDeterminer> sizeDeterminerRef;

      SizeDeterminerLayoutListener(@NonNull SizeDeterminer sizeDeterminer) {
        sizeDeterminerRef = new WeakReference<>(sizeDeterminer);
      }

      @Override
      public boolean onPreDraw() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
          Log.v(TAG, "OnGlobalLayoutListener called attachStateListener=" + this);
        }
        SizeDeterminer sizeDeterminer = sizeDeterminerRef.get();
        if (sizeDeterminer != null) {
        //只有这句代码有价值了 追踪这句代码
          sizeDeterminer.checkCurrentDimens();
        }
        return true;
      }
    }
    @Synthetic
    void checkCurrentDimens() {
      if (cbs.isEmpty()) {
        return;
      }
	//通过getTargetWidth()和getTargetHeight()来获取宽高
      int currentWidth = getTargetWidth();
      int currentHeight = getTargetHeight();
      //如果宽高还是无效的，那么就不会布局了
      if (!isViewStateAndSizeValid(currentWidth, currentHeight)) {
        return;
      }
	//通知回调
      notifyCbs(currentWidth, currentHeight);
      //事情已干完，对监听器做清除处理
      clearCallbacksAndListener();
    }
    //高度计算方式
       private int getTargetHeight() {
      int verticalPadding = view.getPaddingTop() + view.getPaddingBottom();
      LayoutParams layoutParams = view.getLayoutParams();
      int layoutParamSize = layoutParams != null ? layoutParams.height : PENDING_SIZE;
      return getTargetDimen(view.getHeight(), layoutParamSize, verticalPadding);
    }
 //宽度计算方式
    private int getTargetWidth() {
      int horizontalPadding = view.getPaddingLeft() + view.getPaddingRight();
      LayoutParams layoutParams = view.getLayoutParams();
      int layoutParamSize = layoutParams != null ? layoutParams.width : PENDING_SIZE;
      return getTargetDimen(view.getWidth(), layoutParamSize, horizontalPadding);
    }
      private void notifyCbs(int width, int height) {
      // One or more callbacks may trigger the removal of one or more additional callbacks, so we
      // need a copy of the list to avoid a concurrent modification exception. One place this
      // happens is when a full request completes from the in memory cache while its thumbnail is
      // still being loaded asynchronously. See #2237.
      for (SizeReadyCallback cb : new ArrayList<>(cbs)) {
        cb.onSizeReady(width, height);
      }
    }
```
&nbsp;&nbsp;&nbsp;&nbsp;**看上面这代码的流程，也就是当宽高已知，那么就回调onSizeReady，如果View还没有确定的宽高，那么就会利用ViewTreeObserver通过监听preDraw的形式来得知宽高，再回调onSizeReady()。**
&nbsp;&nbsp;&nbsp;&nbsp;至此，我们知道了begin()这个方法完成的最主要的工作还是对宽高的确定，确定了之后就通过onSizeReady()方法进入下一个环节。

##  开启DecodeJob加载任务
我们来看onSizeReady()方法：
```
  @Override
  public void onSizeReady(int width, int height) {
	...
	status = Status.RUNNING;
	//如果配置了大小倍数
	float sizeMultiplier = requestOptions.getSizeMultiplier();
      this.width = maybeApplySizeMultiplier(width, sizeMultiplier);
      this.height = maybeApplySizeMultiplier(height, sizeMultiplier);
	...
	      loadStatus =
          engine.load(
              glideContext,
              model,
              requestOptions.getSignature(),
              this.width,
              this.height,
              requestOptions.getResourceClass(),
              transcodeClass,
              priority,
              requestOptions.getDiskCacheStrategy(),
              requestOptions.getTransformations(),
              requestOptions.isTransformationRequired(),
              requestOptions.isScaleOnlyOrNoTransform(),
              requestOptions.getOptions(),
              requestOptions.isMemoryCacheable(),
              requestOptions.getUseUnlimitedSourceGeneratorsPool(),
              requestOptions.getUseAnimationPool(),
              requestOptions.getOnlyRetrieveFromCache(),
              this,
              callbackExecutor);
              ...
}
```
&nbsp;&nbsp;&nbsp;&nbsp;可以看到onSizeReady方法里面主要就是engine.load()这个方法，这个看上去好像要进入加载阶段了。那这个engine是啥？，其实你有没有发现从开始构造Glide对象时到构造RequestManager、GlideRequest、SingleRequest这些实例时都传入了好多参数，都有传这个engine，
&nbsp;&nbsp;&nbsp;&nbsp;追踪这个engine的来源才知道是在构造构造Glide对象时，也就是Glide在初始化时就做了这个操作，在前面我们没有细说Glide的初始化过程，这里用到了可以了解一下了。Glide的构造我们知道是在GlideBuilder的build方法中：
```
  Glide build(@NonNull Context context) {
  。。。
    if (engine == null) {
      engine =
          new Engine(
              memoryCache,//LruResourceCache 内存缓存器
              diskCacheFactory,//InternalCacheDiskCacheFactory 磁盘缓存器的创建工厂
              diskCacheExecutor,//磁盘缓存执行器 
              sourceExecutor,//资源加载器 线程池
              GlideExecutor.newUnlimitedSourceExecutor(), //资源加载器 无数量限制无核心数的线程池。 这两个线程池都是加载资源的，具体怎么使用通过外部配置
              animationExecutor,//动画执行器
              isActiveResourceRetentionAllowed);
    }
。。。

}
```
&nbsp;&nbsp;&nbsp;&nbsp;在这里创建了一个加载引擎，就是Engine类的实例，并且把memoryCache、diskCacheExecutor、sourceExecutor等这些都创建一个实例传入进去。
&nbsp;&nbsp;&nbsp;&nbsp;注意我们只讲Glide的主流程，并不会过多的去介绍和分析这些线程池啊、工厂策略等。
接下来我们就看重要的Engine类的load方法。

```
  public <R> LoadStatus load(
      GlideContext glideContext,
      Object model,
      Key signature,
      int width,
      int height,
      Class<?> resourceClass,
      Class<R> transcodeClass,
      Priority priority,
      DiskCacheStrategy diskCacheStrategy,
      Map<Class<?>, Transformation<?>> transformations,
      boolean isTransformationRequired,
      boolean isScaleOnlyOrNoTransform,
      Options options,
      boolean isMemoryCacheable,
      boolean useUnlimitedSourceExecutorPool,
      boolean useAnimationPool,
      boolean onlyRetrieveFromCache,
      ResourceCallback cb,
      Executor callbackExecutor) {
    long startTime = VERBOSE_IS_LOGGABLE ? LogTime.getLogTime() : 0;
	//关于缓存key的生成
    EngineKey key =
        keyFactory.buildKey(
            model,
            signature,
            width,
            height,
            transformations,
            resourceClass,
            transcodeClass,
            options);

    EngineResource<?> memoryResource;
    synchronized (this) {
    //先尝试读取一遍内存
      memoryResource = loadFromMemory(key, isMemoryCacheable, startTime);

      if (memoryResource == null) {
      //读不到内存只能开启一个网络请求了
        return waitForExistingOrStartNewJob(
            glideContext,
            model,
            signature,
            width,
            height,
            resourceClass,
            transcodeClass,
            priority,
            diskCacheStrategy,
            transformations,
            isTransformationRequired,
            isScaleOnlyOrNoTransform,
            options,
            isMemoryCacheable,
            useUnlimitedSourceExecutorPool,
            useAnimationPool,
            onlyRetrieveFromCache,
            cb,
            callbackExecutor,
            key,
            startTime);
      }
    }

    // Avoid calling back while holding the engine lock, doing so makes it easier for callers to
    // deadlock.
    //如果加载到了缓存，么就直接回调onResourceReady了
    cb.onResourceReady(memoryResource, DataSource.MEMORY_CACHE);
    return null;
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;load方法非常清晰，首先构造了个EngineKey，通过这个key尝试去获取缓存，获取不到就调用waitForExistingOrStartNewJob方法开启一个新任务，我们先简单看一下获取缓存的：
```
  @Nullable
  private EngineResource<?> loadFromMemory(
      EngineKey key, boolean isMemoryCacheable, long startTime) {
    if (!isMemoryCacheable) {
      return null;
    }

    EngineResource<?> active = loadFromActiveResources(key);
    if (active != null) {
      if (VERBOSE_IS_LOGGABLE) {
        logWithTimeAndKey("Loaded resource from active resources", startTime, key);
      }
      return active;
    }

    EngineResource<?> cached = loadFromCache(key);
    if (cached != null) {
      if (VERBOSE_IS_LOGGABLE) {
        logWithTimeAndKey("Loaded resource from cache", startTime, key);
      }
      return cached;
    }

    return null;
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;可以看到先调用了loadFromActiveResources()方法，有一个ActiveResources类记录了这些“活跃”的缓存，当这个加载器正在运行，name 它就是活跃的，当它进行了释放处理那么就是不活跃了。
&nbsp;&nbsp;&nbsp;&nbsp;如果没命中这个活跃的缓存，那么就调用loadFromCache()方法，会去MemoryCache中去取了。MemoryCache就是在GlideBuilder中要构造Glide时创建的一个LruResourceCache。
&nbsp;&nbsp;&nbsp;&nbsp;如果那得到缓存那么就不会再去加载了，这里我们先假设获取不到缓存，等我们捋清了网络请求过程这一块后再来说缓存（因为也只有经过请求后才可能有缓存吧）

看waitForExistingOrStartNewJob方法：
```
  private <R> LoadStatus waitForExistingOrStartNewJob(...){
		...
		 EngineJob<?> current = jobs.get(key, onlyRetrieveFromCache);
		 //防止重复添加
		   if (current != null) {
		   ...
			return new LoadStatus(cb, current);
		｝
		 //构建一个加载任务
		    EngineJob<R> engineJob = engineJobFactory.build(...);
		    //构建一个解码任务
		     DecodeJob<R> decodeJob =  decodeJobFactory.build(...);
		     //记录这个任务
		      jobs.put(key, engineJob);
		      ...
		      //开启任务
 			engineJob.start(decodeJob);
 			...
 			return new LoadStatus(cb, engineJob);
}
```
&nbsp;&nbsp;&nbsp;&nbsp;这个方法语句不多，参数一堆，我省略了，只贴出重要的这几句代码。主要就是构造了一个EngineJob，通过EngineJobFactory构建出来的（内部维护了一个EngineJob的pool，防止频繁创建）
同时也创建了一个DecodeJob，通过DecodeJobFactory构建出来的（同理也是内部维护了一个DecodeJob的pool，重复利用对象）
那么任务就是由engineJob.start(decodeJob);这句代码开启了，点进去看start方法：
```
 public synchronized void start(DecodeJob<R> decodeJob) {
    this.decodeJob = decodeJob;
    GlideExecutor executor =
        decodeJob.willDecodeFromCache() ? diskCacheExecutor : getActiveSourceExecutor();
    executor.execute(decodeJob);
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;这里通过读取配置来决定是应该用哪个线程池，有diskCacheExecutor、sourceUnlimitedExecutor、animationExecutor、sourceExecutor
正常来说我们啥都没配置那就是sourceExecutor了。
然后执行到executor.execute(decodeJob); 就没了，发现是把decodeJob这个任务递交到线程池里执行。

# 尾声
&nbsp;&nbsp;&nbsp;&nbsp;由于篇幅太长，在本篇我们就讲到了关于Glide.with(context)的过程，讲到了生命周期、讲到了加载任务。那么具体图片怎么去做请求，怎么解码的呢，我们看下篇：[Glide4.x的源码解析（下）](https://blog.csdn.net/lc_miao/article/details/106803478).




