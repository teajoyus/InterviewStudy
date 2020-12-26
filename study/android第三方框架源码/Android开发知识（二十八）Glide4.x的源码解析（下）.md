@[TOC]
# 前言
 &nbsp;&nbsp;&nbsp;&nbsp;这是Glide源码分析的下篇，如果你还没看过上篇的分析，最好能先去看我的上篇分析：[Glide4.x的源码解析（上）](https://blog.csdn.net/lc_miao/article/details/106442804).
 本篇我们来重点讨论加下来的网络请求过程与解码过程。
 
# 网络请求过程
## 优先磁盘缓存
&nbsp;&nbsp;&nbsp;&nbsp;在上篇我们分析到了EngineJob#start()方法这里，我们接着说，execute方法接收的是一个Runnable参数，按照常理我们也不难去找下一步应该就是要去找DecodeJob的run方法了
在run方法里又进去到runWrapped方法：
```
  private void runWrapped() {
    switch (runReason) {
      case INITIALIZE:
      //获取下一个状态
        stage = getNextStage(Stage.INITIALIZE);
        //获取下一个状态的执行指令
        currentGenerator = getNextGenerator();
        //执行
        runGenerators();
        break;
      case SWITCH_TO_SOURCE_SERVICE:
        runGenerators();
        break;
      case DECODE_DATA:
        decodeFromRetrievedData();
        break;
      default:
        throw new IllegalStateException("Unrecognized run reason: " + runReason);
    }
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;这里判断了runReason，我们这里的DecodeJob也是才刚创建的，创建的时候赋值了 RunReason.INITIALIZE
getNextStage(Stage.INITIALIZE);获取下一个状态，那正常就是Stage.RESOURCE_CACHE（允许缓存）
然后getNextGenerator方法根据这个状态获取到一个ResourceCacheGenerator类赋值给currentGenerator。
继续看runGenerators方法：
```
  private void runGenerators() {
	...
	    while (!isCancelled
        && currentGenerator != null
        && !(isStarted = currentGenerator.startNext())) {
      stage = getNextStage(stage);
      currentGenerator = getNextGenerator();

      if (stage == Stage.SOURCE) {
        reschedule();
        return;
      }
    }
    ...
}
```
&nbsp;&nbsp;&nbsp;&nbsp;我省略了其它代码，这里主要的就是while循环，循环去获取下一个状态直到Stage.SOURCE才退出，这个怎么理解呢。
这里结合上面提到的getNextStage方法，首先我们一开始是Stage.INITIALIZE状态，它的下一个状态是RESOURCE_CACHE，RESOURCE_CACHE的下一个状态是DATA_CACHE，DATA_CACHE下一个状态才是SOURCE。这三个分别在getNextGenerator方法中对应了ResourceCacheGenerator、DataCacheGenerator、SourceGenerator。
&nbsp;&nbsp;&nbsp;&nbsp;此时想一下我们的磁盘缓存策略可以设置DiskCacheStrategy.RESOURCE、DiskCacheStrategy.DATA，或者是ALL、NONE之类的，默认是AUTOMATIC
那么这里其实就是命中关系。一开始先用RESOURCE_CACHE状态如果没命中（缓存文件），那么就接着DATA_CACHE还是没命中（原始缓存文件），最后就只能Stage.SOURCE去网络获取了。

&nbsp;&nbsp;&nbsp;&nbsp;值得注意的就是 currentGenerator.startNext()方法了，因为其它方法对于主流程来说没有过多逻辑了。

## 获取网络请求加载器
&nbsp;&nbsp;&nbsp;&nbsp;我们这里先不考虑命中本地的，直接先看没命中到然后进行网络获取的，也就是currentGenerator是SourceGenerator实例。
startNext方法最主要的代码是：
```
 @Override
  public boolean startNext() {
	...
    loadData = null;
    boolean started = false;
    while (!started && hasNextModelLoader()) {
      loadData = helper.getLoadData().get(loadDataListIndex++);
      if (loadData != null
          && (helper.getDiskCacheStrategy().isDataCacheable(loadData.fetcher.getDataSource())
              || helper.hasLoadPath(loadData.fetcher.getDataClass()))) {
        started = true;
        startNextLoad(loadData);
      }
    }
    return started;

}
```
&nbsp;&nbsp;&nbsp;&nbsp;它是通过读遍历 helper.getLoadData()这个集合找一个合适的 ModelLoader.LoadData
而在 helper.getLoadData()里面是获取了ModelLoader一个集合，那么这个是怎么来的

&nbsp;&nbsp;&nbsp;&nbsp;这个ModelLoader一看吓一跳有十来个子类。我们简单来追踪一下：
DecodeHelper#getModelLoaders ->Registry#getModelLoaders() ->ModelLoaderRegistry#getModelLoaders()
可以看到获取来源是Registry的modelLoaderRegistry变量，那么这个集合的数据来自哪里，我们追踪下它的引用发现了appand方法
再看下appand的调用来源看到了Glide这个类，在构造Glide的时候通过Registry注册了一大堆诸如以下的东西：
```
Glide(...){
...
	registry.
	.append(File.class, InputStream.class, new FileLoader.StreamFactory())
	.append(GlideUrl.class, InputStream.class, new HttpGlideUrlLoader.Factory())
	.append(Uri.class, File.class, new MediaStoreFileLoader.Factory(context))
	...
}
```
超级多，这里面其实就是做了一个注册映射，什么类型的获取用哪个Fetcher来加载。
Glide也允许我们自己去replace一个自定义的加载器
知道了这个来源后，那我们这种情况属于那种类型呢？也就是我们helper.getLoadData()返回的哪个LoadData才是我们需要的呢
我们刚才上面没有贴代码只是用一个调用链来表示一下关于ModelLoader集合的获取，是来自Glide构造时append的，而在ModelLoaderRegistry#getModelLoaders()中并不是返回了集合的全部内容，它是有一个过滤的操作的，相关代码如下：
```
@NonNull
  public <A> List<ModelLoader<A, ?>> getModelLoaders(@NonNull A model) {
		...
		 List<ModelLoader<A, ?>> filteredLoaders = Collections.emptyList();
		 for (int i = 0; i < size; i++) {
		ModelLoader<A, ?> loader = modelLoaders.get(i);
		//通过handlers来过滤是否满足条件
      if (loader.handles(model)) {
        if (isEmpty) {
          filteredLoaders = new ArrayList<>(size - i);
          isEmpty = false;
        }
        filteredLoaders.add(loader);
      }
      ...
       return filteredLoaders;
}
```
&nbsp;&nbsp;&nbsp;&nbsp;我们发现了这个handles尤为重要，当你去看那些的ModelLoader的子类重写的这个handles方法，就能意识到这个方法是用来决定这个ModelLoader能不能处理我们传入的model来源，在这里我们一开始用的是一个网络图片地址，自然model就是String.class了。
那么我们回去看Glide的构造方法中注册了哪些String.class相关的，可以发现是下面这四个：
···
        .append(String.class, InputStream.class, new DataUrlLoader.StreamFactory<String>())
        .append(String.class, InputStream.class, new StringLoader.StreamFactory())
        .append(String.class, ParcelFileDescriptor.class, new StringLoader.FileDescriptorFactory())
        .append(String.class, AssetFileDescriptor.class, new StringLoader.AssetFileDescriptorFactory())
···
&nbsp;&nbsp;&nbsp;&nbsp;那么我们通过ModelLoaderRegistry#getModelLoaders()方法中首先根据model的类型拿到的modelLoaders集合就应该是这4个内容了，
其次再通过handlers来做过滤。
首先第一个是DataUrlLoader，它的handles方法是匹配我们的model字符串是不是data:image开头，很显然这不是。
第二个StringLoader返回了true，那么一定是它了。
但是这里用的StringLoader内部的三个factory：StreamFactory、FileDescriptorFactory、AssetFileDescriptorFactory
在ModelLoaderRegistry#getModelLoaders()方法中会调用到build方法对应到这几个factory中来。
而在DecodeHelper#getLoadData()中通过buildLoadData构造LoadData
所以这里我们可以得知DecodeHelper#getLoadData()中返回的modelLoaders集合就有三个StringLoader
然后getLoadData()方法中再通过buildLoadData方法构造出这几个LoadData。我们看StringLoader的buildLoadData方法：
```
 @Override
  public LoadData<Data> buildLoadData(
      @NonNull String model, int width, int height, @NonNull Options options) {
    Uri uri = parseUri(model);
    if (uri == null || !uriLoader.handles(uri)) {
      return null;
    }
    return uriLoader.buildLoadData(uri, width, height, options);
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;StringLoader#buildLoadData只是对uriLoader做个转发，而uriLoader是来自于StringLoader的构造方法，是来自于上面提到的StringLoader中的三个factory构造出来的。但是他们三个在构造StringLoader的时候传递的uriLoader都不一样：
```
StreamFactory:
  public ModelLoader<String, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
      return new StringLoader<>(multiFactory.build(Uri.class, InputStream.class));
    }
FileDescriptorFactory:
    public ModelLoader<String, ParcelFileDescriptor> build(
        @NonNull MultiModelLoaderFactory multiFactory) {
      return new StringLoader<>(multiFactory.build(Uri.class, ParcelFileDescriptor.class));
    }

AssetFileDescriptorFactory:
    public ModelLoader<String, AssetFileDescriptor> build(
        @NonNull MultiModelLoaderFactory multiFactory) {
      return new StringLoader<>(multiFactory.build(Uri.class, AssetFileDescriptor.class));
    }

```
&nbsp;&nbsp;&nbsp;&nbsp;这里有点绕，它又是去通过MultiModelLoaderFactory的build构造出来的，又是去拿对应的ModelLoader。
我们去查一下Glide注册的这三个，可以发现对应的三个：
```
append(Uri.class, InputStream.class, new HttpUriLoader.Factory())
append(Uri.class, InputStream.class, new AssetUriLoader.StreamFactory(context.getAssets()))
append(
            Uri.class,
            ParcelFileDescriptor.class,
            new AssetUriLoader.FileDescriptorFactory(context.getAssets()))
```
&nbsp;&nbsp;&nbsp;&nbsp;我们这里才对应出了这三个modelLoader分别是HttpUriLoader、AssetUriLoader、AssetUriLoader。
然后这三个又因为StringBuilder里面buildLoadData又继续去判断handles，如果handles返回false则buildLoadData方法返回null。
否则就继续调用uriLoader这个变量的buildLoadData方法。
对比HttpUriLoader、AssetUriLoader、AssetUriLoader发现只有HttpUriLoader这个的handler满足，他判断了我们传入的model字符串是否是http或https开头，很显然是HttpUriLoader。
但是问题还不止，我们会发现这个过程非常的绕，但这其实等同于是一个递归。
&nbsp;&nbsp;&nbsp;&nbsp;因为HttpUriLoader的buildLoadData里又继续去调uriLoader.buildLoadData,而他的这个urlLoader来自于HttpUriLoader构建的时候是通过它的内部类Factory构建的：
```
 public static class Factory implements ModelLoaderFactory<Uri, InputStream> {
     @NonNull
    @Override
    public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new HttpUriLoader(multiFactory.build(GlideUrl.class, InputStream.class));
    }
}
```
&nbsp;&nbsp;&nbsp;&nbsp;所以我们又得去查表了，找GlideUrl.class, InputStream.class对应的那个类，就是HttpGlideUrlLoader。
而这个HttpGlideUrlLoader的buildLoadData就没有再继续递归下去了，它构造了LoadData返回来了。
&nbsp;&nbsp;&nbsp;&nbsp;**这才终于让这一个递归结束了，定位到最终一个ModelLoader了
它算是一种责任链设计模式，只有符合对应可以处理的类型才会做出处理，否则就又是一层层build。**

&nbsp;&nbsp;&nbsp;&nbsp;那返回去再看SourceGenerator的startNext方法里面的while，我们就知道处理这个的就是HttpGlideUrlLoader类了。
而这时不知道你发现了没，我们的传入的model类型从String.class变成了GlideUrl.class了，这是因为HttpUriLoader在调用buildLoadData：
```
  @Override
  public LoadData<InputStream> buildLoadData(
      @NonNull Uri model, int width, int height, @NonNull Options options) {
    return urlLoader.buildLoadData(new GlideUrl(model.toString()), width, height, options);
  }
```

它的urlLoader就是HttpGlideUrlLoader实例，这里就把String.class的model转成了GlideUrl类型了，来形容一个url类型的模型封装类。
而HttpGlideUrlLoader的buildLoadData在构造LoadData时是：
```
  @Override
  public LoadData<InputStream> buildLoadData(
      @NonNull GlideUrl model, int width, int height, @NonNull Options options) {
   	...
    return new LoadData<>(url, new HttpUrlFetcher(url, timeout));
  }
```
它构建的LoadData类中包含了一个HttpUrlFetcher类对象。

## 网络请求

&nbsp;&nbsp;&nbsp;&nbsp;分析这一步实在太累了，我们快完成这一步了，紧接着继续看SourceGenerator#startNext()方法
在接下来有这么一句：helper.getDiskCacheStrategy().isDataCacheable(loadData.fetcher.getDataSource()
helper.getDiskCacheStrategy()对应的就是我们磁盘缓存策略，默认是DiskCacheStrategy.AUTOMATIC。
DiskCacheStrategy.AUTOMATIC里的isDataCacheable方法判断了：
```
        public boolean isDataCacheable(DataSource dataSource) {
          return dataSource == DataSource.REMOTE;
        }
```
&nbsp;&nbsp;&nbsp;&nbsp;只有等于DataSource.REMOTE那么才是true。而loadData.fetcher.getDataSource()也就是HttpUrlFetcher的getDataSource返回的就是DataSource.REMOTE。

在接下来是调用了startNextLoad方法：
```
  private void startNextLoad(final LoadData<?> toStart) {
    loadData.fetcher.loadData(
        helper.getPriority(),
        new DataCallback<Object>() {
          @Override
          public void onDataReady(@Nullable Object data) {
            if (isCurrentRequest(toStart)) {
              onDataReadyInternal(toStart, data);
            }
          }

          @Override
          public void onLoadFailed(@NonNull Exception e) {
            if (isCurrentRequest(toStart)) {
              onLoadFailedInternal(toStart, e);
            }
          }
        });
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;startNextLoad方法调用了我们刚才分析得到的LoadData的fetcher变量的loadData方法,注意它创建了一个DataCallback回调监听，在后面获取到InputStream会回调回来。
这个fetcher变量是来自于HttpGlideUrlLoader#buildLoadData构造出来的HttpUrlFetcher类对象。
&nbsp;&nbsp;&nbsp;&nbsp;所以接下来就是转到了HttpUrlFetcher#loadData()方法了,内部就是用HttpURLConnection这种方式获取到了InputStream了
简单贴下主要的代码，loadData方法：
```
@Override
  public void loadData(
      @NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
	...
	InputStream result = loadDataWithRedirects(glideUrl.toURL(), 0, null, glideUrl.getHeaders());      
	callback.onDataReady(result);
	...
}

```
loadDataWithRedirects方法
```
  private InputStream loadDataWithRedirects(
      URL url, int redirects, URL lastUrl, Map<String, String> headers) throws IOException {
      //最多5次重定向
      if (redirects >= MAXIMUM_REDIRECTS) {
	 throw new HttpException("Too many (> " + MAXIMUM_REDIRECTS + ") redirects!");
  }
			...
		  urlConnection = connectionFactory.build(url);
		  ...
		  urlConnection.connect();
		  ...
		 stream = urlConnection.getInputStream();
		  ...
		  return getStreamForSuccessfulRequest(urlConnection);
		  ...
}
```
&nbsp;&nbsp;&nbsp;&nbsp;我们发现了它内部默认是会做5次重定向，判断了状态吗是3开头的话就会去这么做。
这里我们了解到是用HttpURLConnection这种方式获取到了InputStream就好了，不细说这个连接过程了，不是重点。

# 图片解码过程 
## 获取图片解码器
&nbsp;&nbsp;&nbsp;&nbsp;接下来我们要知道如何把InputStream转成我们需要的Drawable
上面我们贴的loadData方法，在哪到inputStream后回调了onDataReady这个方法，这个回调是给谁呢。
&nbsp;&nbsp;&nbsp;&nbsp;我们上面提到在SourceGenerator#startNextLoad方法里创建了这个回调监听，
&nbsp;&nbsp;&nbsp;&nbsp;然后通过这个回调链：DataCallback#onDataReady() -> SourceGenerator#onDataReadyInternal() ->DecodeJob#onDataFetcherReady()
又回到了DecodeJob类来，我们上面分析了那么多就是从DecodeJob类的run方法执行后的一系列流程最后又回到DecodeJob来。
&nbsp;&nbsp;&nbsp;&nbsp;接下来我们直接通过调用链追踪，不贴出一个一个方法了
- DecodeJob#decodeFromRetrievedData()  调用链顶层，拿到Resource<R>结果,回调onResourceReady与通知释放。
- DecodeJob#decodeFromData() 拿到Resource<R>结果，DataFetcher做出cleanup操作（也就是HttpUrlFetcher关闭流并且断开连接）
-  DecodeJob#decodeFromFetcher() 获取一个解码的途径来拿到Resource<R>结果
- DecodeJob#runLoadPath() 获取图片宽高、options，包裹InputStream。然后通过解码途径来拿到Resource<R>结果
-  LoadPath#load()  通过解码途径来拿到Resource<R>结果
- LoadPath#loadWithExceptionList() 包裹错误对应的异常列表，再来拿到Resource<R>结果
- DecodePath#decode()  通过DecodePath#decodeResource() 解码Resource<R>,并且拿对应的transform再做一层包裹形成新的Resource<R>
- DecodePath#decodeResource()  
- DecodePath#decodeResourceWithList()
经历过了这9个方法，最终进入到了DecodePath类来。我们在DecodePath的decodeResourceWithList()方法中看到了有一点熟悉的代码：
```
  @NonNull
  private Resource<ResourceType> decodeResourceWithList(
      DataRewinder<DataType> rewinder,
      int width,
      int height,
      @NonNull Options options,
      List<Throwable> exceptions){
	  ...
	  //遍历所有解码器
	     for (int i = 0, size = decoders.size(); i < size; i++) {
      ResourceDecoder<DataType, ResourceType> decoder = decoders.get(i);
      try {
        DataType data = rewinder.rewindAndGet();
        //如果支持这种类型的解码，在这里我们的来源是InputStream.class
        if (decoder.handles(data, options)) {
          data = rewinder.rewindAndGet();
          result = decoder.decode(data, width, height, options);
        }
        // Some decoders throw unexpectedly. If they do, we shouldn't fail the entire load path, but
        // instead log and continue. See #2406 for an example.
      } catch (IOException | RuntimeException | OutOfMemoryError e) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
          Log.v(TAG, "Failed to decode data for " + decoder, e);
        }
        exceptions.add(e);
      }

      if (result != null) {
        break;
      }
    }
    ...

}
```

&nbsp;&nbsp;&nbsp;&nbsp;又看到了一个handles方法，这根网络请求那块是一样的套路，都是通过Glide构造时也注册了不同来源类型对应的解码器。
这里我们的来源是Inpstream.class，我们就不再去绕一圈了，因为远离跟网络请求所用的ModelLoader几乎是相同的。
我们直接去Glide找Inpstream.class对应的解码器。找到了：
```
append(
            Registry.BUCKET_BITMAP_DRAWABLE,
            InputStream.class,
            BitmapDrawable.class,
            new BitmapDrawableDecoder<>(resources, streamBitmapDecoder))
```
&nbsp;&nbsp;&nbsp;&nbsp;这个找到关联到这里来的代码又是特别的绕，它本身也是一个责任链的设计模式。
而BitmapDrawableDecoder本身又传入了streamBitmapDecoder变量，这个变量做了版本判断：
```
   ResourceDecoder<InputStream, Bitmap> streamBitmapDecoder;
    if (isImageDecoderEnabledForBitmaps && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      streamBitmapDecoder = new InputStreamBitmapImageDecoderResourceDecoder();
      ...
    } else {
      ...
      streamBitmapDecoder = new StreamBitmapDecoder(downsampler, arrayPool);
    }
```
&nbsp;&nbsp;&nbsp;&nbsp;看BitmapDrawableDecoder的decode方法，实际上就不出乎我们的意料是走这个streamBitmapDecoder变量的decode方法
当SDK<28时用的StreamBitmapDecoder，不然就是用InputStreamBitmapImageDecoderResourceDecoder
它们有什么差别呢，**其实就是因为Android.P时引入了一个ImageDecoder类，用于解码图像。使用该类取代 BitmapFactory 和 BitmapFactory.Options API。**
## 图片解码
&nbsp;&nbsp;&nbsp;&nbsp;我们重点就来看StreamBitmapDecoder吧。看看它是怎么一步一步最终生成Bitmap的。
StreamBitmapDecoder#decode()方法：
```
 @Override
  public Resource<Bitmap> decode(
      @NonNull InputStream source, int width, int height, @NonNull Options options)
      throws IOException {
	...
	try {
      return downsampler.decode(invalidatingStream, width, height, options, callbacks);
    } finally {
      exceptionStream.release();
      if (ownsBufferedStream) {
        bufferedStream.release();
      }
    }
}
```
&nbsp;&nbsp;&nbsp;&nbsp;它是调用了Downsampler的decode方法，然后Downsampler的deode方法一步步调用到了decodeFromWrappedStreams方法来：
```
  private Bitmap decodeFromWrappedStreams(...){
	...
	//获取图片类型
	ImageType imageType = imageReader.getImageType();
	...
	 calculateScaling(...);
	 calculateConfig(...);
	 ...
	  Bitmap downsampled = decodeStream(imageReader, options, callbacks, bitmapPool);
	  ...
	  rotated = TransformationUtils.rotateImageExif(bitmapPool, downsampled, orientation);
	  ...
	  return rotated;
}
```
&nbsp;&nbsp;&nbsp;&nbsp;decodeFromWrappedStreams主要完成的就是对图片解码的一些配置计算，包括他的大小规格定义。这些都比较细，就不细说了。
其中一个值得我们注意的是mageReader.getImageType(); ，点进去看ImageType类是一个枚举类，枚举出了各种图片格式。
Glide是怎么知道我们的图片类型的，然后是利用url后缀？
我们来揭开imageReader#getImageType()，
&nbsp;&nbsp;&nbsp;&nbsp;首先这个imageReader看调用链，它是在DownSample#decode方法中传入的：new ImageReader.InputStreamImageReader(is, parsers, byteArrayPool)
重载的decode方法把inputstream流包装成了Reader流。

&nbsp;&nbsp;&nbsp;&nbsp;那我们来看InputStreamImageReader的getImageType()方法，点进去看这个方法是调用到了ImageHeaderParserUtils#getType()方法
并对ImageHeaderParserUtils这个方法传入了三个参数，后面两个还好理解，一个是输入流，一个是字节池。
&nbsp;&nbsp;&nbsp;&nbsp;那么第一个parsers参数是怎么来的呢，看名字它是一个Header的解析器。我们看它的调用链，找到了它是在DownSampler构造方法中传入的，
&nbsp;&nbsp;&nbsp;&nbsp;而DownSampler是在Glide构造方法中创建的，我们在Glide类中找到了这个来源是：
```
  Downsampler downsampler =
        new Downsampler(
            registry.getImageHeaderParsers(), resources.getDisplayMetrics(), bitmapPool, arrayPool);
```
```
    registry.register(new DefaultImageHeaderParser());
    // Right now we're only using this parser for HEIF images, which are only supported on OMR1+.
    // If we need this for other file types, we should consider removing this restriction.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      registry.register(new ExifInterfaceImageHeaderParser());
    }
```
&nbsp;&nbsp;&nbsp;&nbsp;又是通过注册器来提前注册这两个解析器。
&nbsp;&nbsp;&nbsp;&nbsp;这里又做了一下SDK版本判断，先注册一个DefaultImageHeaderParser类，如果大于等于O_MR1时就再注册一个ExifInterfaceImageHeaderParser类。
&nbsp;&nbsp;&nbsp;&nbsp;我们简单看一下ExifInterfaceImageHeaderParser这个类的注释就能明白一点：
```
/**
 * Uses {@link ExifInterface} to parse orientation data.
 *
 * <p>ExifInterface supports the HEIF format on OMR1+. Glide's {@link DefaultImageHeaderParser}
 * doesn't currently support HEIF. In the future we should reconcile these two classes, but for now
 * this is a simple way to ensure that HEIF files are oriented correctly on platforms where they're
 * supported.
 */
@RequiresApi(Build.VERSION_CODES.O_MR1)
public final class ExifInterfaceImageHeaderParser implements ImageHeaderParser {
```
&nbsp;&nbsp;&nbsp;&nbsp;它说，OMR1+开始才支持HEIF格式的，但是Glide之前用的DefaultImageHeaderParser并不支持，在未来可能会去整合这两个类，而现在就是确保支持HEIF格式的一种做法。
&nbsp;&nbsp;&nbsp;&nbsp;我们也不管那么多，在这里我们就知道parsers总共有这么两个。
回到ImageHeaderParserUtils#getType()方法：
```
  @NonNull
  public static ImageType getType(
      @NonNull List<ImageHeaderParser> parsers,
      @Nullable InputStream is,
      @NonNull ArrayPool byteArrayPool)
      throws IOException {
		...
		    return getTypeInternal(
        parsers,
        new TypeReader() {
          @Override
          public ImageType getType(ImageHeaderParser parser) throws IOException {
            try {
              return parser.getType(finalIs);
            } finally {
              finalIs.reset();
            }
          }
        });
}
```
&nbsp;&nbsp;&nbsp;&nbsp;调用了getTypeInternal()方法，并把这个parsers传进去，还构造了一个TypeReader内部类，确保在getType后能让流重置。
在getTypeInternal()方法也就是调用了这个TypeReader内部类的getType()方法，
&nbsp;&nbsp;&nbsp;&nbsp;那么接下来就是进去了parser.getType(finalIs);，我们已经知道，第一个parser是DefaultImageHeaderParser。
我们进去看DefaultImageHeaderParser#getType()方法：
```
@NonNull
  private ImageType getType(Reader reader) throws IOException {
    try {
      final int firstTwoBytes = reader.getUInt16();
      // JPEG.
      if (firstTwoBytes == EXIF_MAGIC_NUMBER) {
        return JPEG;
      }

      final int firstThreeBytes = (firstTwoBytes << 8) | reader.getUInt8();
      if (firstThreeBytes == GIF_HEADER) {
        return GIF;
      }

      final int firstFourBytes = (firstThreeBytes << 8) | reader.getUInt8();
      // PNG.
      if (firstFourBytes == PNG_HEADER) {
        // See: http://stackoverflow.com/questions/2057923/how-to-check-a-png-for-grayscale-alpha
        // -color-type
        reader.skip(25 - 4);
        try {
          int alpha = reader.getUInt8();
          // A RGB indexed PNG can also have transparency. Better safe than sorry!
          return alpha >= 3 ? PNG_A : PNG;
        } catch (Reader.EndOfFileException e) {
          // TODO(b/143917798): Re-enable this logging when dependent tests are fixed.
          // if (Log.isLoggable(TAG, Log.ERROR)) {
          //   Log.e(TAG, "Unexpected EOF, assuming no alpha", e);
          // }
          return PNG;
        }
      }

      // WebP (reads up to 21 bytes).
      // See https://developers.google.com/speed/webp/docs/riff_container for details.
      if (firstFourBytes != RIFF_HEADER) {
        return UNKNOWN;
      }

      // Bytes 4 - 7 contain length information. Skip these.
      reader.skip(4);
      final int thirdFourBytes = (reader.getUInt16() << 16) | reader.getUInt16();
      if (thirdFourBytes != WEBP_HEADER) {
        return UNKNOWN;
      }
      final int fourthFourBytes = (reader.getUInt16() << 16) | reader.getUInt16();
      if ((fourthFourBytes & VP8_HEADER_MASK) != VP8_HEADER) {
        return UNKNOWN;
      }
      if ((fourthFourBytes & VP8_HEADER_TYPE_MASK) == VP8_HEADER_TYPE_EXTENDED) {
        // Skip some more length bytes and check for transparency/alpha flag.
        reader.skip(4);
        short flags = reader.getUInt8();
        return (flags & WEBP_EXTENDED_ALPHA_FLAG) != 0 ? ImageType.WEBP_A : ImageType.WEBP;
      }
      if ((fourthFourBytes & VP8_HEADER_TYPE_MASK) == VP8_HEADER_TYPE_LOSSLESS) {
        // See chromium.googlesource.com/webm/libwebp/+/master/doc/webp-lossless-bitstream-spec.txt
        // for more info.
        reader.skip(4);
        short flags = reader.getUInt8();
        return (flags & WEBP_LOSSLESS_ALPHA_FLAG) != 0 ? ImageType.WEBP_A : ImageType.WEBP;
      }
      return ImageType.WEBP;
    } catch (Reader.EndOfFileException e) {
      // TODO(b/143917798): Re-enable this logging when dependent tests are fixed.
      // if (Log.isLoggable(TAG, Log.ERROR)) {
      //   Log.e(TAG, "Unexpected EOF", e);
      // }
      return UNKNOWN;
    }
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;原来Glide就是利用这种原理来识别出图像类型的，并不是我们以为的rul后缀名判断，本身url就不一定有后缀名。
它利用流里的前一些描述字节可以用来区分是什么图片格式，这种又涉及到另外一种图片流协议的东西了，我们这里知道就好了。

&nbsp;&nbsp;&nbsp;&nbsp;这里我们了解到了这个原理后，继续回到Downsampler的decodeFromWrappedStreams方法来，在这个方法后面就调用了decodeStream方法：
```
 private static Bitmap decodeStream(...){
	...
	 try {
	 ...
      result = imageReader.decodeBitmap(options);
    } catch (IllegalArgumentException e) {
		...
	}
	...
	  return result;
}
```
&nbsp;&nbsp;&nbsp;&nbsp;可以看到调用了imageReader的decodeBitmap方法，前面我们说imageReader在这里就是InputStreamImageReader，那么它的decodeBitmap()方法：
```
    @Override
    public Bitmap decodeBitmap(BitmapFactory.Options options) throws IOException {
      return BitmapFactory.decodeStream(dataRewinder.rewindAndGet(), null, options);
    }
```
&nbsp;&nbsp;&nbsp;&nbsp;**到这里就揭晓了图片的解码了，是通过BitmapFactory来完成的。
而我们上面也提到如果SDK>=28的话，注册的解码器是InputStreamBitmapImageDecoderResourceDecoder。
它内部不同的就是用ImageDecoder#decodeBitmap()方法来得到的，SDK28后引入的ImageDecoder类应该是对BitmapFactory的一种优化吧，这里就不细究代码了。**
&nbsp;&nbsp;&nbsp;&nbsp;那我们知道了Bitmap已经生成了，接下来就是如何绑定到View上了。

# 绑定图片到Target
&nbsp;&nbsp;&nbsp;&nbsp;我们知道，关于网络请求和解码这个开始都是发生在DecodeJob方法里，当解码完成后会对onResourceReady进行回调（这不是瞎说啊，我们解码阶段开始是发生在DecodeJob#decodeFromRetrievedData()方法里去调用decodeFromData拿到图片的，然后再通过notifyEncodeAndRelease()方法去会onResourceReady()进行回调）
最后回调到EngineJob#notifyCallbacksOfResult()中来：
```
  @Synthetic
  void notifyCallbacksOfResult() {
	...
	    for (final ResourceCallbackAndExecutor entry : copy) {
      entry.executor.execute(new CallResourceReady(entry.cb));
    }
    ...
}
```
&nbsp;&nbsp;&nbsp;&nbsp;这里又把这个回调传入给CallResourceReady类对象，去调用一个线程池去excute。
&nbsp;&nbsp;&nbsp;&nbsp;那么这个线程池是哪个呢？为啥又要经历一次线程池的操作？
我们去追踪这个executor的由来发现这个是在Engine类里创建一个EngineJob任务时就传进来的：current.addCallback(cb, callbackExecutor);
&nbsp;&nbsp;&nbsp;&nbsp;而Engine的这个callbackExecutor则是从SingleRequest哪里传来的，而SingleRequest的这个参数又是从RequestBuilder的into方法传进来的：
```
  @NonNull
  public ViewTarget<ImageView, TranscodeType> into(@NonNull ImageView view) {
	...
	    return into(
        glideContext.buildImageViewTarget(view, transcodeClass),
        /*targetListener=*/ null,
        requestOptions,
        Executors.mainThreadExecutor());
}
```
&nbsp;&nbsp;&nbsp;&nbsp;来源就是在这里了，Executors.mainThreadExecutor()) 可以看出这个是主线程的执行器。
&nbsp;&nbsp;&nbsp;&nbsp;因为图片的网络请求和编码都是在DecodeJob这个任务中开启的，而这个任务是运行在线程池里，当我们准备好了Bitmap之后自然而然就需要切到主线程来更新View了。
&nbsp;&nbsp;&nbsp;&nbsp;而这个onResourceReady会回调到SingleRequest来，而SingleRequest再回调到target的onResourceReady()方法中去。
我们知道，我们用的into(imageview)内部实际是帮我们创建了一个VIewTarget，它的实例是DrawableImageViewTarget（默认Drawable形式加载），DrawableImageViewTarget这个类我们一看也没什么代码，就是重写了一下setResource方法：
```
  @Override
  protected void setResource(@Nullable Drawable resource) {
    view.setImageDrawable(resource);
  }
```
&nbsp;&nbsp;&nbsp;&nbsp;可以看出这个就是最终设置到ImageView的方法，调用的是ImageView#setImageDrawable()
也不难看出，这个setResource就是父类ImageViewTarget在回调onResourceReady方法时调用的：
```
  @Override
  public void onResourceReady(@NonNull Z resource, @Nullable Transition<? super Z> transition) {
    if (transition == null || !transition.transition(resource, this)) {
      setResourceInternal(resource);
    } else {
      maybeUpdateAnimatable(resource);
    }
  }
  
  private void setResourceInternal(@Nullable Z resource) {
    // Order matters here. Set the resource first to make sure that the Drawable has a valid and
    // non-null Callback before starting it.
    setResource(resource);
    maybeUpdateAnimatable(resource);
  }
```

&nbsp;&nbsp;&nbsp;&nbsp;我们知道，我们不仅可以往into方法传入ImageView对象，还可以是一个自定义的Target，然后通过监听onResourceReady的回调来确定加载资源成功。
而传入ImageView对象的话它也是帮我们转换成Target对象，在Target对象里帮我们监听了onResourceReady()方法然后去调用ImageView#setImageDrawable()

&nbsp;&nbsp;&nbsp;&nbsp;到这一步我们就解析完成了Glide是如何加载图片到ImageView上的。

# 尾声
&nbsp;&nbsp;&nbsp;&nbsp;当然上面我们也只是举例了其中一个流程，在Glide中我们看到注册了那么多个类型对应的处理器，每一个都有它对应的处理方式，比如对应GIF图的处理就那就是ByteBufferGifDecoder这些了，又是另外一个故事了。还有很多配置、图片转换这种说来真的话长了，所以在本篇我们就是一起捋清了Glide.with(context).load("http://xxx.png").into(iv)这条线索。
感谢你能坚持看到这里来，文章可能有些地方表达得不好，欢迎勘误。如果你有关于Glide的问题，请在评论区与我一起探讨吧。
