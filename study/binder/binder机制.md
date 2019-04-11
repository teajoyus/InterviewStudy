参考博客：https://www.cnblogs.com/qingchen1984/p/5212755.html

# 简单说下Binder机制
在安卓启动的时候，Zygote进程会孵化出第一个进程叫SystemServer，在里面提供了很多服务，比如AMS、PMS

而在我们的app中会需要到跟这些服务或者其他进程通信的时候，Android也是基于Linux，在Linux上有很多IPC的方式，但是一方面性能问题需要两次拷贝、一方面是安全问题。处于安全考虑，Android为每个进程分配了一个UID，通过Binder机制解决上面两个问题


# IPC方式
Linux已经拥有的进程间通信IPC手段包括(Internet Process Connection)： 管道（Pipe）、信号（Signal）和跟踪（Trace）、插口（Socket）、报文队列（Message）、共享内存（Share Memory）和信号量（Semaphore）。

# 拷贝次数


# 安全性

传统IPC没有任何安全措施，完全依赖上层协议来确保。

传统IPC的接收方无法获得对方进程可靠的UID和PID（用户ID进程ID），从而无法鉴别对方身份。

传统IPC访问接入点是开放的，无法建立私有通道。

# binder
Binder基于Client-Server通信模式，传输过程只需一次拷贝，为发送方添加UID/PID身份，既支持实名Binder也支持匿名Binder，安全性高。

# 面向对象
面向对象思想的引入将进程间通信转化为通过对某个Binder对象的引用调用该对象的方法，而其独特之处在于Binder对象是一个可以跨进程引用的对象，它的实体位于一个进程中，而它的引用却遍布于系统的各个进程之中。

Binder模糊了进程边界，淡化了进程间通信过程，整个系统仿佛运行于同一个面向对象的程序之中。形形色色的Binder对象以及星罗棋布的引用仿佛粘接各个应用程序的胶水，这也是Binder在英文里的原意。

# 内核空间与用户空间

Kernel space 可以执行任意命令，调用系统的一切资源；
 User space 只能执行简单的运算，不能直接调用系统资源，必须通过系统接口（又称 system call），才能向内核发出指令。

当用户程序通过系统调用而进入到系统内核的代码执行时，这个时候就叫做内核态。
在用户自己的代码上就是用户态

# 角色

Binder框架定义了四个角色：Server，Client，ServiceManager（以后简称SMgr）以及Binder驱动。

其中Server，Client，SMgr运行于用户空间，驱动运行于内核空间。

这四个角色的关系和互联网类似：Server是服务器，Client是客户终端，SMgr是域名服务器（DNS），驱动是路由器。

## Binder驱动

工作于内核态，负责进程之间Binder通信的建立，Binder在进程之间的传递，Binder引用计数管理，数据包在进程之间的传递和交互等一系列底层支持。

它并不同于硬件驱动，它只是运行在内核空间的一些代码，通过一个叫”/dev/binder”的文件在内核空间和用户空间来回搬数据。

或者说inder驱动，它用”/dev/binder”文件来模拟一个硬件设备

在四个角色中，只有Binder驱动运行在内核空间，所以在传输数据的时候就需要Binder驱动来把用户空间的数据拷贝到这里来


## ServiceManager
用来维护一个Service列表

作用是将字符形式的Binder名字转化成Client中对该Binder的引用，使得Client能够通过Binder名字获得对Server中Binder实体的引用。
（也就是Client只要通过一个名称就可以去ServiceMangaer拿到某个Binder实体的引用）

在启动ServiceManager进程的时候它只是一个普通进程，也没有Binder实体可以通讯，因为此时还没有建立好Binder机制。
当它在打开/dev/binder文件，将BINDER_SET_CONTEXT_MGR命令传给Binder驱动的时候，Binder驱动就会为其在内核空间中创建一个节点（binder_node），这个结点就是ServiceManager的Binder实体。

然后Bindre驱动有了为这个ServiceManager所建立的Binder结点外，其它Clicnt还不知道ServiceManger怎么访问，所以系统中定义了一个句柄为0，只要是想找
ServiceManager进程的话，只要跟Binder驱动说想访问句柄为0的进程就好了，这样Client就能获取到ServiceManager了（就是一种约定，约定了句柄为0就是ServiceManager）
（句柄与指针区别：句柄正对于不同进程或系统的，相当于进程中的一个标识，拿到这个标识可以被系统定位到某个内存地址上，而指针是进程内的）
## Server与ServiceManager

Server创建了Binder实体，为其取一个字符形式，可读易记的名字，将这个Binder连同名字以数据包的形式通过Binder驱动发送给ServiceManager
而这里为了实现通信又要用到通信，就是先有鸡还是蛋的问题。

ServiceManager自己也有一个Binder实体，它是靠自己Binder实体来实现对Binder的注册、获取、查询等

为了解决这个在建立Binder通信之前，需要通信的问题。
ServiceManager的Binder的Binder比较特殊，既没有名字也需要注册。

所以会通过“BINDER_SET_CONTEXT_MGR”这个命令让这个进程注册成为ServiceManager这个角色，这时候Binder驱动会为它创建一个Binder实体

而且这个Binder的引用号为0，也就是其他进程要向ServiceManager注册自己的Binder的话则需要通过引用号为0的这个Binder（0号理解为域名服务器地址，需要手动先配置好）

Server向SMgr注册了Binder实体及其名字后，Client就可以通过名字获得该Binder的引用了。

Client也利用保留的0号引用向SMgr请求访问某个Binder

# 匿名Binder
Server端可以通过已经建立的Binder连接将创建的Binder实体传给Client，当然这条已经建立的Binder连接必须是通过实名Binder实现。
但是这个Binder并没有先跟ServiceManager注册，所以其他Client是无法通过ServiceMangaer获取到这个Binder的
Client将会收到这个匿名Binder的引用，通过这个引用向位于Server中的实体发送请求。

# IPC过程的数据拷贝
传统的IPC过程怎么拷贝数据的呢？通常做法是在发送方自己要发送的数据放在自己的缓存区，然后通过API切换到系统调用进入内核态，把这个缓存区的东西拷贝到内核区的空间，然后内核区再把这个内核空间的数据拷贝到接收方的缓存区

就是一种转储的方式：存储-转发。都是需要两次拷贝，也就是用户空间->内核空间->用户空间
Linux内核中提供了一个copy_from_user()和copy_to_user()函数来实现跨空间的拷贝
（题外话：除了两次拷贝性能低下之外，也有个问题，接收方不知道发送方的数据有多大自己应该开辟多少内存。所以要么自己确保开辟足够的内存要么先探测发送方的消息头数据量大小然后自己再开辟）

## mmap函数的作用
mmap将一个文件或者其它对象映射进内存。
mmap操作提供了一种机制，让用户程序直接访问设备内存，这种机制，相比较在用户空间和内核空间互相拷贝数据，效率更高。
面向流的设备不能进行mmap，mmap的实现和硬件有关。（mmap()通常用在有物理存储介质的文件系统上）




