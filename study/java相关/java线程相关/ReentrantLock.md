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

