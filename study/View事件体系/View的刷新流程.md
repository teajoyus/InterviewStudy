玩安卓：https://www.cnblogs.com/dasusu/p/8311324.html

信息量比较大，还需要再去看一遍原文章。

笔记：


1、先通过CPU计算好数据，然后交给GPU，GPU计算数据后放在buffer里，然后屏幕每隔16.6s取一次buffer的数据显示到屏幕上
丢帧就是说CPU计算时间长了，没办法在这一帧内处理到数据提交给GPU，那么下一帧信号来的时候还是只能取之前的buffer的数据，有就还是上一帧的内容
所以比如CPU计算需要20ms，那么就会丢了一帧，等到32ms第二个帧信号来才能拿到新的buffer数据

2、invalidate()的时候并不会马上刷新，而是计算数据，然后等下个VSync信号来的时候才会绘制到屏幕
同一个VSync信号内 多次invalidate()并不会导致刷新多次；


3 invalidate流程：
---------------
初始化界面：
ViewRootImpl 是在**addView方法**里面（这个方法又是什么时候被调用的？）被new出来的
然后new完之后就有**root.setView方法**吧DecorView 传进来，之后会把ViewRootImpl设置为DecorView 的parent（因为ViewRootImpl也实现了ViewParent接口）

之后会调用**requestLayout方法**

在这个方法里面调用了**scheduleTraversals()方法**

当我们调用invalidate的时候后面也是走到scheduleTraversals()方法


# 4、scheduleTraversals()
------------------
这个方法里面有个**mTraversalScheduled**来控制**SyncBarrier**的，

当mTraversalScheduled为false的时候则在消息队列里插入一个同步屏障，然后置这变量为true

然后调用**mChoreographer.postCallback()**把一个**mTraversalRunnable**任务放进来，

而这个Runnable的run方法里面是执行**doTraversal()**方法

并且mTraversalScheduled为true的时候，再去移除同步屏障，然后设置为false

之后调用了performTraversals()方法

# 5、performTraversals()方法
-----

这个方法里面判断执行了performMeasure()、performLayout()、performDraw()三个流程，并不是一定会去走这个，看判断是否需要

回到上面来，也就是那个mTraversalRunnable任务最终会执行doTraversal()-》performTraversals()方法最终执行了measure、layout、draw三个流程

那么这个rannable被post到哪里去在什么时候执行是很重要的流程

在上面说的是Choreographer这个类去执行个postCallback方法

而postCallback方法里面执行的是**postCallbackDelayed()方法**

传入的delayed是0，所以接下来走的是**scheduleFrameLocked方法**

这个scheduleFrameLocked方法会判断不在主线程的话则调用Handler的sendMessageAtFrontOfQueue插入到消息队头去，并且设置消息未异步消息（这样就不会因为同步消息被同步屏障信号给阻塞了）
然后最终两者都是走到**scheduleVsyncLocked方法**里

这个方法里则调用了**mDisplayEventReceiver.scheduleVsync**方法

这个方法里面走了个native方法，吧一个receiver注册到监听中去（就是因为这样，每次界面需要刷新的时候就去注册一次Vsync信号监听，不需要的时候则不会老是计算，而底层的16.6ms则还是同样会刷新页面）

那么到这里就是去注册了一个监听，这个监听的时候底层会回调FrameDisplayEventReceiver的onVsync方法

然后这个方法就会在消息队列里发出一条异步消息，这个消息最后执行了**doFrame()方法**

然后doFrame方法会去进行CPU-》GPU到刷新屏幕的操作（个人猜测）


# 这个过程的同步屏障
-----
在Choreographer的post方法前会设置个同步屏障，在doTraversal方法的时候才移除

Handler的消息屏障，在Queue的next里面如果遇到了屏障信号，会直接绕过同步消息，在队列里面寻找异步消息，只有这个同步消息被移出队列的时候才能处理同步消息

# ViewRootImpl
----、
分析之后发现，界面的刷新这些工作都是ViewRootImpl引起的



