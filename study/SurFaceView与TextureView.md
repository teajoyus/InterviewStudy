刘望舒：http://liuwangshu.cn/application/view/2-sliding.html


SurFaceView在继承上跟普通View一样，一般会实现SurfaceHolder.Callback

SurFaceView通过getHolder持有一个SurfaceHolder实例

在SurFaceView显示时会回调surfaceCreated，影藏时会回调surfaceDestroyed，

通过 Canvas canvas = getHolder().lockCanvas();来获取画布

在画完后通过 getHolder().unlockCanvasAndPost(canvas); 释放锁，然后将绘制内容post到Surface中


SurFaceView的疑难杂症：https://blog.csdn.net/gfg156196/article/details/72899287/

1、多层嵌套被遮挡的问题
setZOrderOnTop(boolean onTop) 会遮挡view，吧surfaceView调到最上层来

setZOrderMediaOverlay(boolean isMediaOverlay)// 如已绘制SurfaceView则在surfaceView上一层绘制。

注意：setZOrderMediaOverlay必须layout.addView之后使用，必须动态调用。

（SurfaceView是用Zorder排序的，他默认在宿主Window的后面，SurfaceView通过在Window上面“挖洞”（设置透明区域）进行显示）

2、如何旋转、透明？
View本身可以支持旋转，但是SurFaceView是不支持旋转的（好比如在墙上开了个窗户 窗户外面的世界并不会随着墙的旋转而旋转）

让视频横竖旋转只能改变Activity自身的横屏 竖屏模式。然后通过onConfigurationChanged监听到横竖屏改变的时候改变SurfaceView的宽高参数

3、SurfaceView背景问题

我们看到默认SurfaceView是黑色的，有时候也想修改成透明的，这个跟Activity的主题有关


4、SurfaceView双缓冲区

双缓冲：在运用时可以理解为：SurfaceView在更新视图时用到了两张 Canvas，一张 frontCanvas 和一张 backCanvas ，每次实际显示的是 frontCanvas ，backCanvas 存储的是上一次更改前的视图。当你在播放这一帧的时候，它已经提前帮你加载好后面一帧了，所以播放起视频很流畅。
当使用lockCanvas（）获取画布时，得到的实际上是backCanvas 而不是正在显示的 frontCanvas ，之后你在获取到的 backCanvas 上绘制新视图，再 unlockCanvasAndPost（canvas）此视图，那么上传的这张 canvas 将替换原来的 frontCanvas 作为新的frontCanvas ，原来的 frontCanvas 将切换到后台作为 backCanvas 。例如，如果你已经先后两次绘制了视图A和B，那么你再调用 lockCanvas（）获取视图，获得的将是A而不是正在显示的B，之后你将重绘的 A 视图上传，那么 A 将取代 B 作为新的 frontCanvas 显示在SurfaceView 上，原来的B则转换为backCanvas。

（双缓冲会增加内存开销，是所以不可见时要即时销毁SurfaceHolder）

5、SurfaceView的生命周期，注意surfaceChanged的调用时机
看博客：https://blog.csdn.net/gfg156196/article/details/72899287/



SurfaceView的优缺点

优点

在一个子线程中对自己进行绘制，避免造成UI线程阻塞。

高效复杂的UI效果。

独立Surface，独立的Window。

使用双缓冲机制，播放视频时画面更流畅。

缺点

每次绘制都会优先绘制黑色背景，更新不及时会出现黑边现象。

Surface不在View hierachy中，它的显示也不受View的属性控制，平移，缩放等变换。

不支持UI同步缓冲


什么时候用SurfaceView？（自己总结的 需要百度）
1、需要进行复制绘制UI效果时，绘制的时候需要长时间，这时候就要用SurfaceView来代替 因为可以在子线程完成，不会造成UI线程阻塞











TextureView
-----------
博客：https://blog.csdn.net/pgg_cold/article/details/79483731

TextureView 适用于 Android 4.0 和之后的版本，作为 SurfaceView 的替代品来使用

TextureView 的行为更像传统的 View，可以对绘制在它上面的内容实现动画和变换。

环境必须是硬件加速的（应用程序在 SDK 为 11或以上的版本时，默认启动了硬件加速。）

TextureView则可以通过TextureView.setSurfaceTextureListener在子线程中更新UI（而SurfaceView是通过SurfaceHolder.addCallback方法）

一些类似于坦克大战等需要不断告诉更新画布的游戏来说，SurfaceView绝对是极好的选择。（为什么）

比如视频播放器或相机应用的开发，TextureView则更加适合。（为什么）



