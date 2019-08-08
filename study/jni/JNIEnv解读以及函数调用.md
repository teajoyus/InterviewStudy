参考博客：https://www.jianshu.com/p/67081d9b0a9c

JNIEnv
-----

每个函数都可以通过JNIEnv参数访问，JNIEnv类型是指向一个存放所有JNI接口指针的指针.

实际的类型定义是这个：
```
typedef const struct JNINativeInterface *JNIEnv;

```



获取JNI版本信息
-----

```
jint GetVersion(JNIEnv *env);
```