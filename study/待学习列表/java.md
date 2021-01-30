
## 集合相关
- **HashMap扩容** https://juejin.im/post/6844903983748743175
- **LinkedHashMap Lrucache的实现** https://blog.csdn.net/zhujiangtaotaise/article/details/107766548
- **ReentrantReadWriteLock源码解析** https://blog.csdn.net/zhujiangtaotaise/article/details/106859188


## 问题知识点相关

- **多线程wait时为什么要用while而不是if** https://blog.csdn.net/worldchinalee/article/details/83790790
（自己当时理解：用if的话，别的线程调用notify时，被wait的线程就可以接着往下走了，但是这个时候已经过了if判断了，会往下走逻辑。
- 如果用while的话，那么就会继续判断条件才允许往下走，这样既不错造成错误。
- 举例：一个生产者多个消费者的情况，消费者在判断不大于0时就wait，那么这个时候需要用while，因为生产者notify的时候两个消费者线程都能唤醒，但是都需要继续判断>0）

