# OOM图片问题处理

## 一、MarkView

### 原因
 -  bugly链接：[#2335023 java.lang.OutOfMemoryError](https://bugly.qq.com/v2/crash-reporting/crashes/2fb12c6c7d/2335023?pid=1).

 -  OOM相关代码：
```
public MaskView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ...
        mEraserBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mEraserCanvas = new Canvas(mEraserBitmap);
        ...
    }

```
 - 分析：

  MaskView在初始化的时候，为了蒙层效果而使用了Bitmap.createBitmap来创建一张全屏Bitmap表示画布的方式。
这种太吃内存了，屏幕像素越多Bitmap越大，在低系统高分辨率的手机会更容易出现这个问题。

 - 解决思路：

  去除这种创建全屏Bitmap后合成的方式，直接用一个Canvas就可以实现蒙层镂空的效果。
```
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ...
        //填充背景色（暗屏效果）
        canvas.drawColor(mFullingPaint.getColor());
        //挖空处理
        if (!mOverlayTarget) {
            if (mStyle == Component.CIRCLE) {
                canvas.drawCircle(mTargetRect.centerX(), mTargetRect.centerY(),
                        mTargetRect.width() / 2, mEraser);
            } else {
                canvas.drawRoundRect(mTargetRect, mCorner, mCorner, mEraser);
            }
        }
        ...
    }
```
 保留了原来的类，我复制了一份取名为：ZljMaskView。后面测试没问题再把MaskView剔除掉


## 二、LiveAdapterViewHolder
### 原因
 -  bugly链接：[#196540 java.lang.OutOfMemoryError](https://bugly.qq.com/v2/crash-reporting/crashes/2fb12c6c7d/196540?pid=1).

 - OOM相关代码：
```
    public void setCoverImage(String mCoverImg) {
        llLiveEndLayout.setVisibility(View.GONE);
        // FIXME: 2020/5/25 
//        showNoNetLayout(false);
        ImageLoaderV4.getInstance().downBitmapFromCache(mContext, mCoverImg, R.mipmap.live_bg, new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Blurry.with(mContext)
                        .radius(2)
                        .sampling(10)
                        .async()
                        .from(resource)
                        .into(mCover);
            }
        });


    }
```
 - 分析：
  LiveAdapterViewHolder里面在这个地方加载了一张本地全屏图：R.mipmap.live_bg，图片大小是：1125 x 2001。

 - 解决思路：

 如果一定要使用这个占位图，那么可以把图片弄小一点。然后尽量在内存不足时不要用这个图片


## 三、QRCodeEncoder#syncEncodeQRCode
### 原因
 -  bugly链接：[#726211 java.lang.OutOfMemoryError](https://bugly.qq.com/v2/crash-reporting/crashes/2fb12c6c7d/726211?pid=1).

 - OOM相关代码：

```
QRCodeEncoder.syncEncodeQRCode(strings[0], BGAQRCodeUtil.dp2px(mContext, 150), Color.BLACK, Color.WHITE, logoBitmap);
```
 - 分析：

  这是由于在创建二维码的时候内存资源不足，从内存角度上看这个的确占用不了太多内存，但是bugly却影响了一些低级系统的用户。

  其原因是：商详页一进来就会判断shareUrl不为空所以就默认创建好了二维码的bitmap，并且持有着。而在进来商详页的时候由于本身页面就需要加载很多图片，本身资源就不充足了，再经过二维码创建过程的一系列createBitmap会容易导致OOM了。

 - 解决思路：

 延迟二维码bitmap的创建时机，放在截图监听后再来执行。
 由于在合成截图二维码过程避免不了一系列bit拿着到onDestory才释放，感觉没必要吧 用时再创建，不用时销毁好了。


# 图片使用问题建议

 - 注意图片的释放时机
 - 有些图片不需要缓存的不要做缓存处理
 - 有些图片可以RGB_565就不要用ARGB_8888，如果需要gif必要时可以让后台返回字段 或自己判断后缀。
 - ImageView尽量不要用warp_content，不然Glide内部没法根据Imageview宽高做调整，浪费资源。
 - 。。。
未完~

## onLowMemory()&onTrimMemory()
本来想自己手动实现下对这两个状态的管理，了解了下Glide源码发现他们已经处理过了。
既然如此，这里就顺便记录下Glide中的管理方式。
在Glide这个类它实现了ComponentCallbacks2接口（ComponentCallbacks2就是定义了这两个方法）
然后再Glide创建实例的时候，会绑定这个关系，看代码：
```
private static void initializeGlide(
      @NonNull Context context,
      @NonNull GlideBuilder builder,
      @Nullable GeneratedAppGlideModule annotationGeneratedModule) {
      ...
      
      Glide glide = builder.build(applicationContext);
      
      ...
       if (annotationGeneratedModule != null) {
      annotationGeneratedModule.registerComponents(applicationContext, glide, glide.registry);
    }
    applicationContext.registerComponentCallbacks(glide);
      
      }
```
上面代码在创建Glide实例的时候就顺便往Application中registerComponents了。所以能接收onLowMemory()&onTrimMemory()
继续追踪源码，在onLowMemory()中是Glide执行了clearMemory()
而在onTrimMemory()中是分别对memoryCache和bitmapPool执行了处理
根据level的层次，来决定用clearMemory()还是trimToSize(getMaxSize() / 2);

