https://blog.csdn.net/niubitianping/article/details/72617864

# CPU
已经写了 《如何检测方法耗时》

# 内存

### Shallow size
Shallow size就是对象本身占用内存的大小，不包含其引用的对象。
于String对象实例来说，它有三个int成员（3*4=12字节）、一个char[]成员（1*4=4字节）以及一个对象头（8字节），总共3*4 +1*4+8=24字节。
根据这一原则，对String a=”rosen jiang”来说，实例a的shallow size也是24字节。

### Retained size
Retained size是该对象自己的shallow size，加上从该对象能直接或间接访问到对象的shallow size之和。
换句话说，retained size是该对象被GC之后所能回收到内存的总和。为了更好的理解retained size，不妨看个例子。
比如A持有B、C、D的引用，其中C还被E所引用，只有B和D只被A引用，那么A的Retained size就是B和D的Shallow size之和

看博客：https://blog.csdn.net/kingzone_2008/article/details/9083327

## 内存泄漏检测

在FlowActifvity上加个Handler延迟消息，操作一番后点击GC一下 在点击堆转储：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190322120807216.png)


搜索FlowActifvity，可以看到：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190322120710841.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xjX21pYW8=,size_16,color_FFFFFF,t_70)


可以看得FlowActivity还有5个实例没回收，

要判断是因为什么对象导致不能回收的，主要是看左边每个实例种，它持有的这个引用的Retained size最大的那个，因为Retained size最大的那个如果能释放了，Activity就释放了

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019032212094782.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xjX21pYW8=,size_16,color_FFFFFF,t_70)

由这里可以看出是因为Handler泄漏导致


在FlowActifvity上再加个Thread内部类，来sleep15秒

能搜索出FlowActifvity$4好多个实例，实例里面都有FlowActivity的引用，可以看出这个实例导致泄漏


## 导致泄漏的地方
Context被其他地方引用
非静态的内部类：比如Handler、Timer
集合中的对象没有清理
资源未释放：如Cursor、Stream、Socket，Bitmap等在使用后要及时关闭。
没有取消注册：比如Service、broadcast、eventbus等等好多需要手动解注册的


## 内存