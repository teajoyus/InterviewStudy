下载安装NDK

用java编写几个native方法
然后用命令把native方法转换成头文件
创建jni目录
有了头文件后，就可以根据头文件声明的方法来写函数的实现

通过 System.loadLibrary("cppUtils"); 加载so库

然后java的native方法对应C C++的方法

java到C语言的过程的话 就是类型的名称不同

修改mk文件可以生成自动CPU平台的so库

C语言调用java方法：
------------
写法有点类似于反射，NDK提供了那些FindClass，GetMethodID、GetFieldID、CallObjectMethod之类的

java调用C语言的方法：
编写native方法，在调用之前需要先加载so文件，system.load("");

