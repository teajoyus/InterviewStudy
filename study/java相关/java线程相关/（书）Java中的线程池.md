## ThreadPoolExecutor
- corePoolSize
- runnableTaskQueue 
- keepAliveTime 存活时间
- maximumPoolSize（线程池最大数量）
- ThreadFactory：用于设置创建线程的工厂
- RejectedExecutionHandler（饱和策略）：

## executor、submit
submit会返回一个Future对象，Future对象通过get可以阻塞的拿到返回值


## 合理分配线程池

思考的角度：
- 任务的性质：CPU密集型任务、IO密集型任务和混合型任务。
- 任务的优先级
- 任务执行时间长短
-  任务的依赖性

CPU密集型任务应配置尽可能小的线程，如配置N（cpu数量）+1个线程的线程池。因为每个线程可能都会有大量的计算一直在执行任务
而IO密集型的应该配置尽可能多一点的线程，如2倍的N。因为IO密集的就表示了不一定总在执行，可能阻塞了。
如果任务优先级有关系，那么应该采用一个优先级队列来存放任务

执行任务长短可以分配到不同规模的线程池里，或者是优先级队列 优先执行时间比较短的任务。

最好是不要用无界队列，虽然说安卓终端不像后台一般也不可能那么多任务。但是对内存也是有影响的

## 线程池的监控
线程池有一些状态参数，比如任务数量、已完成的任务数、创建过的最大线程数、线程池数量、存活数量


## Executor框架的成员
Executor框架的主要成员：ThreadPoolExecutor、ScheduledThreadPoolExecutor、
 Future接口、Runnable接口、Callable接口和Executors。
 
 ThreadPoolExecutor通常使用工厂类Executors来创建。
 Executors可以创建3种类型的 ThreadPoolExecutor（下面三种并不是类）：
 - SingleThreadExecutor 单个线程的线程池，可以让任务顺序串行的执行
 - FixedThreadPool 固定线程数，没有核心和非核心的差别。它适用于负载比较重的服务器。
 - CachedThreadPool 无边界的线程数。适用于执行很多的短期异步任务的小程序，或者 是负载较轻的服务器。
 
### ScheduledThreadPoolExecutor（一个类）
r适用于需要多个后台线程执行周期任务

### Runnable接口和Callable接口
Callable返回结果

### FutureTask
一个异步任务 同时实现了runnable和Future接口


