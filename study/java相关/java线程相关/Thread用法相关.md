## 线程状态
Java中线程中状态可分为五种：New（新建状态），Runnable（就绪状态），Running（运行状态），Blocked（阻塞状态），Dead（死亡状态）。


## Thread类的方法

### join()

join() :会阻塞调用此方法的线程（calling thread）去等待这个线程执行完再继续执行
（join是Thread类的非静态方法，看的是哪个线程调用这个方法，就阻塞哪个进程，而不是哪个Thread类实例调用。）

比如:
```
 void  test(){
	
	Thread t = new Thread(r);
	t.start();
	t.join();
	...
}
```
理解运用场景举例：
创建一个需要工作5秒的子线程，主线程需要在子线程执行完之后打印出结束。 这时候就可以用join来让主线程阻塞，而让join方法的Thread类实例去执行完之后解锁才会唤醒被阻塞的主线程


对于上面的例子,调用t.join()这个方法的那个线程就是calling thread（在这个例子中，被阻塞的线程就是主线程），，调用后它会阻塞等到t这个线程执行完毕了才继续执行

join()还能传个long型参数，表示等待多少ms

### join()方法的原理是以线程对象的锁来调用wait的
注意calling thread 在调用t.join()时是获取t这个对象的锁，假如在t的run里面使用了synchronized，用的锁是t这个对象（因为join源码里面直接调用wait方法，那么就是Thread对象锁）

那么calling thread调用join并没有持有t对象的锁，那么即使在join传入个毫秒参数，也是要等到synchronized同步快执行完才能往下执行

(需要再仔细理解 打叉)
（自己再次理解：也就是join这个方法里面是获取t这个对象锁（join方法声明了一个方法级的synchronized），那么如果线程t如果刚好获得它自身的锁，
这时候对于call thread来说，它要调用join的时候没获得锁就阻塞了，尽管在join传入了个时间也没用，需要等待t释放锁了才轮到它）


在理解了Object的wait和notify方法之后，看懂了join的源码：
```
 public final synchronized void join(long millis)
    throws InterruptedException {
        long base = System.currentTimeMillis();
        long now = 0;

        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (millis == 0) {
            while (isAlive()) {
                wait(0);
            }
        } else {
            while (isAlive()) {
                long delay = millis - now;
                if (delay <= 0) {
                    break;
                }
                wait(delay);
                now = System.currentTimeMillis() - base;
            }
        }
    }
```

首先join这个方法是有个synchronized关键字的，代表calling thread调用这个方法的时候会获得锁（当然如果被其他线程获得去了也只能等待了）
接下来判断millis == 0，在调用join不传参数的时候默认是调用join(0),0就是直到该线程执行完毕

用了：
   while (isAlive()) {
                wait(0);
            }

因为calling thread一直卡在这个while死循环，而导致它阻塞状态，直到isAlive()是false，也就是线程执行结束，为什么一直要死循环调用wait(0),不直接死循环判断isAlive()呢

本身调用wait(0);之后，calling thread会释放该线程锁，进入等待状态
这个时候假如被其他线程notify的话 那么它就又活过来了，活过来的话判断isAlive()是true，则调用wait继续等待
如果直接while (isAlive())的话会特别耗资源，是一种轮询的方式
也就是说：用wait会去释放锁 进行等待，不会一直轮询下去

### 调用join方法的那个线程自己wait了后谁notify它的？
我的思考：
而calling thread调用了wait方法释放了线程t这个对象锁进入等待的时候，它为什么就能够在线程t执行完毕后它能继续执行判断到 isAlive()是false呢，又没有主动notify它

看百度后：是因为本地方法里面做了操作：After run() finishes, notify() is called by the Thread subsystem.
当monitor（锁）是线程对象的时候，那么在该线程执行完毕的时候底层会自动调用notify通知calling thread


那么如果两个线程同时在等这个线程锁呢 都会唤醒，百度看本地方法的C语言的notify_all，博客：https://blog.csdn.net/qq_26975307/article/details/81666471




## yield()方法

Java线程中yield()的用法

Thread.yield()方法的作用：暂停当前正在执行的线程，并执行其他线程. 
这个是静态方法，**哪个线程执行了哪个线程就暂停**
这个是针对于CPU调度的，跟锁没有关系了

调用了它后会让线程Running **运行状态 变成 Runnable 可运行状态**

### 不一定成功
但是不确保一定会做出让步，因为有可能调用了之后又被调度程序选中了
