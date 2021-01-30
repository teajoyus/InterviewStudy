博客地址：http://www.importnew.com/21889.html
都来自于java.util.concurrent包

CountDownLatch
---------
个人快速理解：创建一个final CountDownLatch latch = new CountDownLatch(2);

它有个countDown()方法可以来减1

当线程调用了latch.await();后只有等到latch减到0了才能继续执行

https://www.jianshu.com/p/f17692e9114f

CountDownLatch是一次性的

CyclicBarrier
-------------

个人快速理解：就是一个Barrier作用，当构造的时候传了N个任务的时候，每个线程调用了cyclicBarrier.await();后只有等到所有的任务都调用完了这个await，大家才再一起执行后面的

只有所有线程都完成写数据操作之后，这些线程才能继续做后面的事情，此时就可以利用CyclicBarrier了

CyclicBarrier是可以重用的，用完之后可以继续使用


CountDownLatch和CyclicBarrier的使用区别
-----
CountDownLatch的适用场景是一个线程如果需要等待n个线程做完后它再来做的时候，也就是await到count = 0的时候再停止阻塞

而CyclicBarrier的适用场景是每个线程都需要相互等待，只有所有的线程都处理好之后，大家才会一起进去下一步。

而且CountDownLatch是一次性的，用完之后不能再用了。
CyclicBarrier还可以复用


自己区别：

CyclicBarrier在构造的时候会传入一个count数量，和一个Runnable回调。
而这个Runnable回调正是其他线程都调用了await()之后就会回调。
然后其它线程再开始处理await接下来的代码流程。

而CountDownLatch则是一个线程（也可以多个线程）调用await后就会等到CountDownLatch调用countDown到0后才会恢复阻塞状态


原理自己简单理解：

CountDownLatch是基于AOS，一个同步队列的，当有线程调用await方法之后该线程会被送入到这个同步队列
之后count=0的时候，在按照FIFO的顺序去唤醒这些线程

而CyclicBarrier是基于重入锁ReentrantLock 的基础上实现的，每个线程调用await方法时需要获得锁才能继续执行下去、
当最后一个线程调用await方法时会唤醒是所有等待的线程，并且重置CyclicBarrier的状态


---------
 信号量，Semaphore可以控同时访问的线程个数，通过 acquire() 获取一个许可，如果没有就等待，而 release() 释放一个许可。

 比如8个Threaad，Semaphore设置了5个，那么在Thread中调用acquire() 最多能同时有5个，除非调用了release() 后才能继续接受一个许可


个人理解：
就是一个信号量吧，获得资源和释放资源

 三者的使用区别
 ---
 CountDownLatch侧重于当其他线程执行完后 才执行

 CyclicBarrier则侧重于等到所有线程都执行到某个操作后，才再一起执行下一个操作

 CountDownLatch是不能够重用的，而CyclicBarrier是可以重用的。

 Semaphore其实和锁有点类似，它一般用于控制对某组资源的访问权限。

 Semaphore侧重于控制资源，有点类似锁。能控制同时允许多少个任务在执行
