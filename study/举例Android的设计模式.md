博客地址：https://www.cnblogs.com/fery/p/4709841.html

单例设计模式、Builder模式、装饰者模式、观察者模式、责任链模式、代理模式

单例设计：LayoutInflater ，通过SystemServer那里获取
注意怎么防止被反射出现创建了多个实例，可以用枚举类型来实现单例


Builder模式：第三方框架用得比较多，比如EventBus啊、Glide啊
用于初始化参数很多，需要些很多构造方法，就用Builder来选择性的构建参数


装饰者模式：IO流
用于功能拓展
 通过继承被装饰者或者实现一样的接口，然后把被装饰者组合进来，自己编写相同的方法来实现方法拓展

观察者模式：EventBus框架、ListView这些notifyDataSetChange

责任链模式： 事件分发

代理模式：AIDL过程