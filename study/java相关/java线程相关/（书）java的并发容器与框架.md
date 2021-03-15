## ConcurrentHashMap
 因为Hashmap不是线程安全的，并发过程可能导致死循环（链表形成了环）。
 HashTable虽然是线程安全的，但是效率比较低。
 直接在方法加synchronized关键字的，所有同步的方法都必须竞争这一把锁。如果一个线程在put数据，另外一个线程在get的话就得阻塞了。
  而ConcurrentHashMap里采用的是锁分段的实现，内部有多把锁。
  把每一段数据分配一把锁
## 1.7的实现 采用Segment分段数组+hashEntry数组来实现
 每个Segment又继承了ReentrantLock，就拥有了锁功能。
 在put操作的时候，根据key算出的hash值对应到了不痛的Segment。
 然后再通过加锁的方式来插入。
 而get的话因为数据都用voltile修饰了，所以不需要加锁。
 而size方法是先计算两次结果，如果结果不同则在对每个segment加锁获取
 
## 1.8的实现去除了Segment，采用了Node，利用synchronized和CAS操作来实现 
对于put方法，如果对应的node为初始化，也就是没有被占坑，那么通过CAS来插入
如果已经有了，那么通过synchronized来同步操作


## ConcurrentLinkedQueue
CAS

## java的阻塞队列
- ArrayBlockingQueue：内部用ReentrantLock加锁（可以公平也可不公平）入队
- LinkedBlockingQueue 内部用ReentrantLock加锁，count是原子类 AtomicInteger
- PriorityBlockingQueue 支持优先级的无界阻塞队列
- SynchronousQueue 不存储元素的阻塞队列。每一个put操作必须等待一个take操作， 否则不能继续添加元素
- LinkedTransferQueue：无界
- LinkedBlockingDeque 双向


## fork/join框架
把任务分成小任务并发执行后，再汇总起来。

 
 