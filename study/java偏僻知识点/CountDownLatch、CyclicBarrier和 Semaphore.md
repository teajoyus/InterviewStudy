博客地址：http://www.importnew.com/21889.html
都来自于java.util.concurrent包

CountDownLatch
---------
个人快速理解：创建一个final CountDownLatch latch = new CountDownLatch(2);

它有个countDown()方法可以来减1

当线程调用了latch.await();后只有等到latch减到0了才能继续执行


CyclicBarrier
-------------

个人快速理解：就是一个Barrier作用，当构造的时候传了N个任务的时候，每个线程调用了cyclicBarrier.await();后只有等到所有的任务都调用完了这个await，大家才再一起执行后面的

只有所有线程都完成写数据操作之后，这些线程才能继续做后面的事情，此时就可以利用CyclicBarrier了

CyclicBarrier是可以重用的，用完之后可以继续使用

Semaphore
---------
 信号量，Semaphore可以控同时访问的线程个数，通过 acquire() 获取一个许可，如果没有就等待，而 release() 释放一个许可。

 比如8个Threaad，Semaphore设置了5个，那么在Thread中调用acquire() 最多能同时有5个，除非调用了release() 后才能继续接受一个许可

 三者的使用区别
 ---
 CountDownLatch侧重于当其他线程执行完后 才执行

 CyclicBarrier则侧重于等到所有线程都执行到某个操作后，才再一起执行下一个操作

 CountDownLatch是不能够重用的，而CyclicBarrier是可以重用的。

 Semaphore其实和锁有点类似，它一般用于控制对某组资源的访问权限。

 Semaphore侧重于控制资源，有点类似锁。能控制同时允许多少个任务在执行
