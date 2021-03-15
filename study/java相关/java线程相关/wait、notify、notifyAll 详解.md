
## wait方法

wait()使当前线程阻塞，前提是 必须先获得锁，一般配合synchronized 关键字使用.
即，一般在synchronized 同步代码块里使用 wait()、notify/notifyAll() 方法。
（理解：没有获得锁，谈何让自己阻塞？而且，线程阻塞也就是因为锁的原因）

## notify/notifyAll() 
调用wait方法时需要在synchronized ，也就是要获得锁后才能够让自己释放当前锁，进入阻塞队列
而调用notify/notifyAll() 也需要在synchronized，要唤醒其他线程首先得知道他们被哪个锁锁了。
（理解：要唤醒别的线程也得知道别人是被哪个锁锁住了吧）

## wait调用后线程的状态位置
被调用wait方法的线程是被放在阻塞队列中的，正常竞争但没拿到CPU资源的才是放在准备队列中，调用notify后就是把阻塞的队列放到准备队列去



当线程执行wait()方法时候，会释放当前的锁，然后让出CPU，进入等待状态。
只有当 notify/notifyAll() 被执行时候，才会唤醒一个或多个正处于等待状态的线程，然后继续往下执行，直到执行完synchronized 代码块的代码或是中途遇到wait() ，再次释放锁。




线程的interrupt()并不会直接中断线程，需要自己判断中断标志Thread.interrupted（）主动结束




博客：https://www.cnblogs.com/moongeek/p/7631447.html


wait()、notify/notifyAll() 方法是Object的本地final方法，无法被重写

注意这个几个方法是针对于锁对象来说的，不是直接调用的。同步块用的是哪个锁对象，就用那个对象来调用wait方法或者notify方法


wait()是使当前线程（calling thread）阻塞，前提是必须要先获得锁，一般是放在synchronized同步块里面使用
为什么需要先获得锁： 如果没有锁的话 你有什么好阻塞的？
所以在synchronized同步快中，调用了wait的话。会释放这个对象的锁，让出CPU，进入等待状态。处于这个状态的时候不会去抢占锁


notify/notifyAll() 会唤醒一个/多个正处在等待状态中的线程（调用了wait的线程），但是并不会马上释放锁，它只是去唤醒那些处于wait状态的线程进入抢占锁的状态

往往是在同步快的临界处调用notify/notifyAll()，这样才能确保让出锁后让其它等待的线程开始抢占