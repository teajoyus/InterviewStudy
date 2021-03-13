## ReentrantLock可重入锁
ReentrantLock就是可重入锁，如何理解这个可重入的概念很重要。

这篇博客讲了可重入和不可重入的差别：https://blog.csdn.net/qq_29519041/article/details/86583945

简单的说不可重入就是：只判断这个锁有没有被锁上，只要被锁上，那么来申请锁的线程都会被要求等待。实现简单
可重入锁：不仅判断锁有没有被锁上，还会判断锁是谁锁上的，当就是自己锁上的时候，那么他依旧可以再次访问临界资源，并把加锁次数加一（后面说到FairSync验证这个）。

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







## 公平锁机制
---------
它实现了Lock接口，具有lock方法和unlock方法，可以对某一代码段进行加锁。相比起synchronized要比较灵活一点

在new出一个ReentrantLock实例的时候，可以传入一个参数，true表示是公平锁机制，false表示竞争锁

而**synchronized永远是非公平的**（因为没有谁去维护这个公平队列）。

## 可响应中断
-----
与synchronized相比，ReentrantLock支持响应中断。它有提供中断方法

## 限时等待
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

## ReentrantLock原理

ReentrantLock内部是用AQS来实现的，就是AbstractQueuedSynchronizer。 而AQS内部维护一个volatile修饰的state变量。
这 个volatile变量是ReentrantLock内存语义实现的关键
ReentrantLock可以指定公平锁（FairSync）和非公平锁（NonFairSync）。
### FairSync 与  NonFairSync 对公平与不公平的实现
FairSync 与  NonFairSync都是继承了AQS
内部都是一样的，FairSync在判断上会比NonFairSync多一个对AQS的判断（hasQueuedPredecessors方法），判断队列上有没有其它线程在等待锁
有的话，那就还轮不到自己

首先看ReentrantLock的lock方法，实际就是FairSync和NonFairSync的lock方法实现

看FairSync的lock：
```
final void lock() {
            acquire(1);
 }
```
调用了AQS的acquire方法传入1：
```
 public final void acquire(int arg) {
      //经过tryAcquire(arg)看是否拿到锁，如果没拿到那么就用addWaiter方法把自己放到队列中去等。
     //如果插入队列成功则打断自己。
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```

所以重点是方在tryAcquire如何获取锁。

FairSync的tryAcquire源码：

```

protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            //获取同步状态 
            int c = getState();
            //如果是0表示为加锁
            if (c == 0) {
               //hasQueuedPredecessors()是判断队列上是否有元素，这是实现公平锁的关键。如果队列上有其它线程再等待，那么就得排队等锁
              //compareAndSetState内部是调用了Unsafe类进行CAS操作
              //setExclusiveOwnerThread 如果修改成功，那么设置当前线程为独占线程
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            //如果当前线程已经是独占线程了，那么就把锁的状态次数+1（+1有什么用？）
            //这里说明了可重入性，如果当前线程已经持有锁了，那么直接就允许继续执行了，把锁状态+1
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }

```
NonFairSync在lock的时候会先进行一次CAS（CAS内部用了Unsafe类来进行CAS，本质上也是修改state变量），如果成功就直接拿到锁了
```
  final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
}

```
NonFairSync的tryAcquire源码与FairdSync几乎一样，就少了一个判断AQS队列，因为它是非公平的。

而FairSync 与  NonFairSync在释放锁的原理上一模一样。
tryRelease:
```
   protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            //如果不等于0，说明重入锁多次了，还没有真正释放锁。
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
```

而release方法里：
```
   public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
             //这个方法会修改线程状态，唤醒下一个线程
            //unparkSuccessor方法
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
```

## setState() & compareAndSetState() 区别
当当前线程持有锁，比如重入锁时，或者要释放锁时，就可以直接修改，减少执行CAS的成本。
而当需要参与竞争获取锁的时候，就需要用 compareAndSetState() ，因为会有多个线程参与，所以必须CAS

## 双重检查锁不嘛安全问题
对象初始化：
```
memory = allocate(); // 1：分配对象的内存空间 
ctorInstance(memory); // 2：初始化对象 
instance = memory; // 3：设置instance指向刚分配的内存地址
```
如果instance没有加volatile的话，那么第二三步可能会发生重排序

另外一个方式是不用同步语句块，而是利用类的延迟加载。也极速把instance放在另外一个类里面作为一个静态变量。
当执行getInstance类的时候会触发另外一个类的初始化，JVM能保证这个初始化是同步的。