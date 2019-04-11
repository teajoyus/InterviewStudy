注意静态方法和非静态方法，不要混淆

每个synchronized方法都必须获得调用该方法的类实例的”锁“方能执行，否则所属线程阻塞。

比如有两个非静态方法，都用synchronized修饰

那么访问了其中一个方法时，也就相当于得到了这个实例的锁，同时想访问另外一个方法时是阻塞的，除非拿到这个实例的锁（也就是那个方法执行完）


如果是静态方法的话，则锁就是类锁。


必须注意！！！必须注意！！！必须注意！！！必须注意！！！必须注意！！！必须注意！！！

synchronized修饰的方法或者代码快究竟能不能执行还是阻塞，是要看**它有没有拿到锁**

而不是看被synchronized包围的区域，

比如：

...
public void mehted1(){

    synchronized(this){
        sleep(3000);

    }
}

public synchronized void mehted2(){

}

...


这样的话访问了mehted1的时候，mehted2就会阻塞了，因为他们两个用了同一个锁，就是这个对象实例锁。

而：
```
public synchronized static void mehted1(){

        sleep(3000);

}

public synchronized void mehted2(){

}
```
这样执行mehted1方法同时执行mehted2方法是不会被锁的，因为mehted1用的是类锁，也就是xxx.class这个类，mehted2用的是对象锁


之前自己还因为synchronized方法锁，锁的只是这个方法。千万要记得！！！，看的是synchronized持有了什么锁，只要跟他是同个锁的都不能访问

相信集合里面的线程安全的，访问add的时候肯定不能同时delete啊
