## Lock
### 与synchronized的区别

- Lock是手动调用lock和unlock的，而synchronized是隐式获取和释放锁的。
  所以Lock的话相对来说就更灵活一点，我们可以更自由的控制锁的获取和释放时机
  
Lock具备了synchronized没有的特性：

- 尝试非阻塞性的获取锁。 它可以用tryLock来判断锁的获得
- 能被中断的获取锁。 当持有锁后能响应中断信息然后释放锁。
- 超时机制

### Lock接口的方法

#### lock()和lockInterruptibly()方法
 前者优先获取锁，后者优先响应中断
（也就是即使调用了thread.interrupt()的话，lock也不会有响应，直到他获取锁。而lockInterruptibly方法就可以抛出异常）

#### tryLock()
 非阻塞性的获取锁，如果获取不到返回false
 
#### tryLock(long time, TimeUtil unit) throws InterruptedException
超时性的获取锁，如果超时内获取锁返回true，超时没获取返回false。被中断则抛出异常

#### newCondition()
返回一个Condition对象。这个类有两个重要方法：
- await() 等待、释放锁
- signal() 唤醒其它线程
- signalAll() 
这个就跟Object里的等待/唤醒方法一样。 wait()/notify()


## AbstractQueuedSynchronizer(简称AQS)

 内部维护了一个int型的state变量，来表示同步状态。用volatile修饰的
 然后提供了对这个同步状态操作的方法：
 - getState()
 - setState()
 - compareAndSetState()  使用CAS设置当前状态，该方法能够保证状态 设置的原子性
 
 通过内置的一个同步队列来对那些等待锁的线程进行排队。
 
 

### 使用方式
 使用方式是继承这个AQS（它本身是一个抽象类）,它本身没有实现任何同步接口，只是定义了一组操作让我们来自定义同步状态。
 它可以重写：
 独占：
 - tryAcquire(int)
 - tryRelease(int)
共享：
 - tryAcquireShared(int)
 - tryReleaseShared(int)
 - isHeldExclusively() 是否独占
 
### AQS队列结构
 
 - int waitStatus  CANCELED 超时或中断 、SIGNAL(当前节点被唤醒)、CONDITION(等待状态，等待condition.signal())
 - prev
 - next
 头结点是获取了同步状态的结点。
 头结点释放同步状态后，会通知后继结点。
 后继结点被唤醒后也需要判断前驱结点是不是头结点（因为可能是其它地方唤醒了）
 
### 独占式&共享式 
 // ReentrantLock、 ReentrantReadWriteLock和CountDownLatch等
 独占式就是只能一个线程访问修改。
 共享式就是允许多个线程访问
 场景：比如读操作和写操作。如果当前有线程获取到了同步状态，它是读操作。那么这时候写操作的线程需要被阻塞
 但是还能允许读操作的线程。这时候就可以把这些读操作的线程弄成共享式的
 
### 独占式获取同步状态与释放
通过调用AQS的acquire(int)方法。
内部会调用tryAcquire方法来判断同步状态，这个方法需要我们自己自定义。
我们可以自定义tryAcquire方法，然后尝试修改同步状态。
如果直接获取到锁，那么就完成了，如果没拿到那么AQS会把它送入到同步队列（插入过程也是通过CAS实现来确保原子性）

通过调用AQS的release(int)方法
内部会调用tryRelease方法来修改同步状态，这个也需要我们自定义
如果尝试释放成功，那么就会通知后继结点在等待的线程

### 共享式获取同步状态与释放
与独占式的区别在于同一时刻是否允许多个线程访问同步状态。
所以共享式的是通过tryAcuirSharded判断获取结果是否大于0


## 重入锁

ReentrantLock。构造方法中它可以选择是公平锁还是非公平锁
公平锁的话就是靠同步队列来排队，非公平锁就是一起竞争。
### ReentrantLock是怎么实现可重入的
它内部也是用了AQS来实现同步，在实现tryAcquire这些方法的时候，当判断同步状态已经是被修改了（持有锁的状态）
那么它还会在判断持有这个状态的线程是不是自己，如果是的话就可以继续走下去，然后会把同步状态累加1
后面释放锁的时候也会依次减1 知道减到0时才能返回true让别的线程来

而Mutex就是非重入锁，因为它在实现tryAcquire的时候并没有判断是不是当前线程修改了状态。


##  ReadWriteLock 读写锁
我们看到的ReentrantLock这些都是排它锁，也就是只能同一个时刻只允许一个线程访问。
而读写锁可以同一时刻下允许多个读线程来访问。
如果写线程在访问时，所有读线程和写线程必须阻塞。
而如果读线程在访问时，那么所有读线程都允许访问，写线程就不行。
所以读写锁维护了一对锁，就是读锁和写锁。

ReadWriteLock接口维护了一个readLock()和一个writeLock方法对应了两把锁。
而他的一个实现就是ReentrantReadWriteLock（可重入的读写锁）
### ReentrantReadWriteLock
内部也是维护了一个AQS，所以通过AQS可以实现它公平与非公平性。
还有对应了Read和Write两个锁的实现，
**对应读锁的实现它是通过调用AQS的tryAcquireShared方法来实现共享式访问**
**对应写锁的实现它是tryAcquire方法来实现独占式访问**

### 锁降级的定义
只有在持有写锁时，再去获取读锁后再释放写锁，才是锁降级。
如果说先释放写锁再拿读锁这不叫锁降级。
### 为什么要锁降级（感觉是重难点，自己要再继续搞清楚）
主要是为了保证线程的可见性和提高并发性能。
假如说不使用锁降级。
我也可以一直持有写锁，但是这个时候可能我已经不需要修改数据了，但是其它读线程还是只能阻塞。这就影响了性能。
或者我先释放锁，然后再获取锁。这样也不是不可以。但是你不能保证你修改的数据跟你读出来的是一样的。
所以如果进行锁降级，第一方面你不需要再去竞争读锁，第二你能确定读取到的值是你修改后的。

### 不支持锁升级
如果读锁直接升级成写锁，但是这个时候可能还有其它读线程在访问，那就有问题了。


## LockSupport
LockSupport是一个线程工具类。提供了一系列park开头的方法表示阻塞当前线程
unpark(Thread thread)方法来表示唤醒其它线程。
### park与unpark和 wait/notify的区别
park方法和wait都可以让当前线程阻塞，但是wait是Object的方法，也就是必须持有锁对象才去调用，而park不需要锁对象
而unpark方法和nofity一样是唤醒，不同的是notify唤醒是随机的，而unpark通过传入Thread变量能唤醒具体的某个线程
注意park和unpark底层是通过UNSALE类对应的方法来实现的，native方法




 
 


  

