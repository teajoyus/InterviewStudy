rxjava基础学习来自于：https://www.jianshu.com/u/383970bef0a0

# 用法
## 创建操作符
- create
- just()  快速创建事件，最多10个
- fromArray() 数组形式的事件
- fromIterable() 集合形式的事件
- empty()直接发送complete事件、error()
- defer() 直到有观察者（Observer ）订阅时，才动态创建被观察者对象（Observable）
- timer() 延迟发布
- interval() 间隔发布
- intervalRange
- range
- rangeLong

## 变换操作符

- map 转换事件
- flatMap 拆分、再转为ObservableSource重新发送事件  注意它是无序的
- concatMap 与flatMap一样，但是它是有序的
-buffer 获取N个事件放到缓存区中，再一起发送

## 组合 / 合并操作符

- concat() / concatArray()  组合多个被观察者一起发送数据，合并后 按发送顺序串行执行
(二者区别：组合被观察者的数量，即concat（）组合被观察者数量≤4个，而concatArray（）则可＞4个)


- merge（） / mergeArray（）组合多个被观察者一起发送数据，合并后 按时间线并行执行
（二者区别：组合被观察者的数量，即merge（）组合被观察者数量≤4个，而mergeArray（）则可＞4个）
是按照时间性并行执行，配合interval来用，而concat是串行执行

### concatDelayError mergeDelayError
如果直接用concat和merge，如果前面的被观察者出现了error，那么后续的被观察者就不会发出事件了
而使用这个的话则不会影响

### zip
可以和并多个被观察者 Observable。 会根据两者的事件序列来进行和并，1发出一个事件，即使再发出一个事件，等2发出一个事件后，1的第一个事件才会开始和2的事件和并
注意：如果有一个提前onComplete了的话，则zip就会失败
可用于和并网络请求


### combineLatest
当两个Observables中的任何一个发送了数据后，将先发送了数据的Observables 的最新（最后）一个数据 与 另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据

与zip的差别是，zip是1对1和并。
而combineLatest是跟着时间线走的。
每个被观察者都发出事件后，和并起来的都是他们最后或者说最新的一次事件
比如被观察者1发了个A和B，被观察者2再之后才发了个事件1，那么和并事件就是B和1

### combineLatestDelayError

包含了错误处理

### reduce（）

聚合 聚合的逻辑根据需求撰写，但本质都是前2个数据聚合，然后与后1个数据继续进行聚合，依次类推
比如Observable.just(1,2,3,4)。 那就是1和2聚合，结果再合3聚合，结果再和4聚合

### collect（）
把事件集成在一起变成一个集合

### startWith（） / startWithArray（）
在事件发送之前追加发送某个 或 某组事件

### count（）
统计发送数量

## 功能性操作符

### delay（）
与timer的区别是，delay是发送事件后才延迟发送事件。 而timer是延迟后才开始事件发送

### do操作符
doOneach 当Obsevable每发送一次事件就会调用一次
doOnNext、doAfterNext、doOnComplete、doOnError、doOnSubscribe、doAfterTerminate（事件发送完毕调用）、doFinally
一大堆都是在事件发送的整个生命周期来做执行

### onErrorReturn（）
遇到错误时，发送1个特殊事件 & 正常终止。可捕获在它之前发生的异常

### onErrorResumeNext（）
遇到错误时，发送1个新的Observable

### onExceptionResumeNext（）
遇到错误时，发送1个新的Observable。这是拦截Exception的，如果是Throwable的用onErrorResumeNext（）

### retry（）
重试，即当出现错误时，让被观察者（Observable）重新发射数据. 可以传入重试次数

### retryUntil（）

出现错误后，判断是否需要重新发送数据

### retryWhen（）
遇到错误时，将发生的错误传递给一个新的被观察者（Observable），并决定是否需要重新订阅原始被观察者（Observable）& 发送事件

### repeat（）
重复发送、可设置重复创建次数

### repeatWhen（）

## 线程调度 / 切换

subscribeOn（） & observeOn（）实现

- Schedulers.immediate() 当前线程 默认
- AndroidSchedulers.mainThread() 主线程
- Schedulers.newThread() 新线程
- Schedulers.io() IO操作线程
- Schedulers.computation() cpu密集计算线程



# Flowable & Observable
Observable是rxjava1.0里面的 Flowable是rxJava2.0的，用来代替Observable。 解决背压的问题。
实现 非阻塞式背压 策略

区别：

Observable 对应的观察者是 Observer
Flowable 对应的观察者是 Subscriber
2.0中Observable不再支持背压、适合小数据流传输，默认缓存大小16
Flowble支持背压，适合大数据流传输 默认缓存大小128个事件

## Flowable如何解决背压问题
背压问题：被观察者 发送事件速度太快，而观察者 来不及接收所有事件，从而导致观察者无法及时响应 / 处理所有发送过来事件的问题，最终导致缓存区溢出、事件丢失 & OOM
解决:在Rxjava2.0中采用Flowable来解决背压问题

## 为什么Observable不能解决背压

因为Observable中采用队列存储，默认是16个，如果超过16个就会有问题了
手动解决：减少被观察者发送的事件数量
降低被观察者发送事件的速度（比如延迟）
但是这些都不能很好的支持背压问题

## 控制 观察者接收事件 的速度
### 异步订阅的情况
默认观察者不接收事件，除非观察者（Subscriber）在onSubscribe方法传入的Subscription对象中，去指定接收事件个数
比如subscription.request(2);
由此来控制观察者接收的事件个数，多余的事件则会放在缓冲区。
这样观察者就可以自己定义自己能接收事件的时机，能处理多少个就接收多少个
而此时被观察者仍可以正常发送事件放在缓冲区里，如果缓冲区超过128则发生溢出

### 同步订阅的情况

同步订阅指的是被观察者和观察者处于同一个线程里，此时事件是同步的，就没有缓冲区的概念
被观察者发送一个事件后，只有观察者接收处理完成后，被观察者才可以发出第二个事件
而此时利用subscription.request()就不行了。
虽然控制了观察者接收事件的数量，但是在同步订阅的情况下，与被观察者发送的事件数量匹配不上就会有问题
比如观察者指定了subscription.request(3)，那么被观察者发送了第四个事件时就会产生MissBackPressureException的问题了
 
 还有一种特殊情况：同步订阅时，如果观察者没指定subscription.request()，那么被观察者一发出事件也会产生问题
 
 ## 控制被观察者发出事件的速度
 
 2.0中，用FlowableEmitter来拓展了Emitter接口，增加了个requested方法
 
 ### 同步订阅的情况
 被观察者 通过 FlowableEmitter.requested()获得了观察者自身接收事件能力
 从而根据该信息控制事件发送速度，从而达到了观察者反向控制被观察者的效果
 有三个特性需要注意：
 - 可叠加性 观察者可以多次调用request，会被叠加起来
 - 实时性 每次发出事件后，requested会马上更新事件可接收数量
 - 异常 ：如果requested=0后还继续发送，那么就会有异常，如果观察者没有设置可接收数量，那么requested方法返回0
 
 这样的话，通过观察者反向控制来约束被观察者发送事件的数量，来达到处理速度和数量匹配的效果
 
 ### 异步订阅的情况
 被观察者 无法通过 FlowableEmitter.requested()知道观察者自身接收事件能力
 
 那么在异步订阅关系中，反向控制的实现是rxjava内部实现的
 内部通过调用被观察者的request来确定可发送事件的数量，当事件发出来后，观察者根据自身的request来接收相应的事件数量。
 如果缓冲区小于32个事件，那么rxjava内部又继续调用被观察者的request来继续发出事件
 
 ## 背压的策略模式（缓冲区溢出的处理方案）
 
 - BackpressureStrategy.ERROR 直接抛异常
 - BackpressureStrategy.MISSING 提示缓冲区满了
 - BackpressureStrategy.BUFFER 缓冲区设置无限大
 - BackpressureStrategy.DROP 超过128个的则丢弃
 - BackpressureStrategy.LATEST 超过128个的则丢弃，但是会保留最后一个最新的事件
  
  模式指定可以通过在Flowable.create的时候传入，也可以调用对应的方法。
  
  
## Rxjava 1.0 与 2.0差别

- 最大差别是新增了Flowable来支持背压问题
- 修改了一些接口名的表示，比如Action啊 Function接口啊




