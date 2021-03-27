# 启动优化
## 获取启动时间和方法耗时
adb加个 -W可以看启动时间
Debug.startMethodTracing() 与stopMethodTracing()可以生成一个trace文件，里面包含了这个区域所执行的方法与耗时
或者profiler也可以看到

## 做法- 启动器task、线程池并发
初始化的有一些SDK可以延迟的、或者可以不必要放在主线程的都需要把它们分类出来
每个SDK的初始化看作是一个task 一个任务，这个task里面会指定是否允许在子线程加载、是否需要等到同意隐私权限、依赖性等等
也用到了CountDownLatch，就是针对一些必须在进首页之前初始化完成的。

## idleHandler
原则上也可以用一下idleHandler来处理一些问题
在进入首页一些可以延迟的任务就等空闲再来做。比如启动service之类的、或者是一些可以延迟渲染的UI
不过要是有动画之类的 就不太合适了，idleHandler执行不到



