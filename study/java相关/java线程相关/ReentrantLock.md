ReentrantLock就是可重入锁，如何理解这个可重入的概念很重要。

这篇博客讲了可重入和不可重入的差别：https://blog.csdn.net/qq_29519041/article/details/86583945

简单的说不可重入就是：只判断这个锁有没有被锁上，只要被锁上，那么来申请锁的线程都会被要求等待。实现简单
可重入锁：不仅判断锁有没有被锁上，还会判断锁是谁锁上的，当就是自己锁上的时候，那么他依旧可以再次访问临界资源，并把加锁次数加一。

所以这个不可重入是对同一个线程而言，能否第二次获取锁

synchronized是可重入锁，写一个程序也可以验证了。就是一个比如一个synchronized修饰的方法里面又调用了一个synchronized修饰的方法，就重入了。


博客地址：https://blog.csdn.net/zhousenshan/article/details/53026785


一个关于Android的用处：
（点击事件）用在界面交互时点击执行较长时间请求操作时，防止多次点击导致后台重复执行（忽略重复触发）




关于使用，博客：https://www.cnblogs.com/-new/p/7256297.html


效果和synchronized一样，都可以同步执行，lock方法获得锁，unlock方法释放锁。




```
private Condition condition=lock.newCondition();
```
通过创建Condition对象来使线程wait，必须先执行lock.lock方法获得锁

condition对象的signal方法可以唤醒wait线程







公平锁机制
---------
它实现了Lock接口，具有lock方法和unlock方法，可以对某一代码段进行加锁。相比起synchronized要比较灵活一点

在new出一个ReentrantLock实例的时候，可以传入一个参数，true表示是公平锁机制，false表示竞争锁

而synchronized永远是非公平的。

可响应中断
-----
与synchronized相比，ReentrantLock支持响应中断。它有提供中断方法

限时等待
----
ReentrantLock提供了tryLock方法可以传入一个时间，设置tryLock的超时等待时间tryLock(long timeout,TimeUnit unit)，也就是说一个线程在指定的时间内没有获取锁，那就会返回false，就可以再去做其他事了。
这种方法用来解决死锁问题。比如这个线程在等待资源，如果超过5秒就不继续等待了，那么就可以用：
```
try {
    if (lock.tryLock(5, TimeUnit.SECONDS)) {  //如果已经被lock，尝试等待5s，看是否可以获得锁，如果5s后仍然无法获得锁则返回false继续执行
        try {
        //操作
        } finally {
          lock.unlock();
        }
     }
} catch (InterruptedException e) {
   e.printStackTrace(); //当前线程被中断时(interrupt)，会抛InterruptedException                 
}
```


同时，tryLock()也可以用来判断是否获得锁，可用于防止任务重复执行：
```
if (lock.tryLock()) {  //如果已经被lock，则立即返回false不会等待，达到忽略操作的效果 
    try {
    //操作
    } finally {
      lock.unlock();
    }
}
```

日期| 版本信息
---| ---
2020/7/9  | 8.1.10->282
2020/7/9  | 8.1.10->282
