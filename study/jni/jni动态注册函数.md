- 静态注册：
先由Java得到本地方法的声明，然后再通过JNI实现该声明方法

命名方式是：Java_包名_类名_函数名称

固定参数： 参数1JNIEnv+参数2jobject+其他参数


- 动态注册：
先通过JNI重载JNI_OnLoad()实现本地方法，然后直接在Java中调用本地方法。

当我们使用System.loadLibarary()方法加载so库的时候，Java虚拟机就会找到这个JNI_OnLoad函数并调用该函数

这个函数的作用是告诉Dalvik虚拟机此C库使用的是哪一个JNI版本

动态注册函数的步骤就是：

1、编写jint JNI_OnLoad(JavaVM* vm, void* reserved) 函数

2、定义一个字符串指针，它的字符串就是 对应的native方法的类（类名之间是用斜线，类似目录的形式）

3、定义一个注册函数的函数，比如jniRegisterNativeMethods函数

4、在niRegisterNativeMethods函数里面调用RegisterNatives函数 实现动态注册。


jniRegisterNativeMethods函数的步骤
---

需要的参数： 

- JNIEnv* env
- const char* className
- const JNINativeMethod* 
- int numMethods

需要这四个参数。

首先通过JNIEnv提供的FindClass函数来根据className找到具体的java class类（在这里属于jclass类型）

然后调用JNIEnv提供的RegisterNatives函数传入JNIEnv变量、jclass类、方法数组、方法数组个数。实现具体的方法关联注册

最后调用JNIEnv提供的RDeleteLocalRef来释放jclass