JNI_OnLoad函数
------

当我们使用System.loadLibarary()方法加载so库的时候，Java虚拟机就会找到这个JNI_OnLoad函数并调用该函数，

这个函数的作用是告诉Dalvik虚拟机此C库使用的是哪一个JNI版本，

如果你的库里面没有写明JNI_OnLoad()函数，VM会默认该库使用**最老的JNI 1.1版本**。

如果需要使用JNI新版本的功能，就必须在JNI_OnLoad()函数声明JNI的版本。

同时也可以在该函数中做一些初始化的动作，其实这个函数有点类似于Android中的Activity中的onCreate()方法。

JNI_OnUnload()函数
-----

与JNI_OnLoad()函数相对应的有JNI_OnUnload()函数，

当虚拟机释放的该C库的时候，则会调用JNI_OnUnload()函数来进行善后清除工作。


JNIEXPORT
----



JNICALL
----


