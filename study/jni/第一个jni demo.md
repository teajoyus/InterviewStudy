jni配置
---------
在经过第一个jni配置后。
就有了jni的环境

编写native方法
----

```
public class MyJniCall {

    public native int getInt(String str);

    public native String getString(int i);
}

```
根据native方法生成头文件
---
右键MyJniCall类，然后点击NDK-javaH 生成了头文件。
头文件默认生成在main文件夹下的jni目录（由于配置的快捷方式才有）

生成的头文件名称遵循 包名+类名的方式

编写C代码，实现头文件的函数
----
在jni目录下新建个c文件。编写：
```

#include <jni.h>
#include <com_example_interviewstudy_jni_MyJniCall.h>

JNIEXPORT jint JNICALL Java_com_example_interviewstudy_jni_MyJniCall_getInt
  (JNIEnv * env, jobject obj, jstring str){
  return 100;
}
JNIEXPORT jstring JNICALL Java_com_example_interviewstudy_jni_MyJniCall_getString
  (JNIEnv * env, jobject obj, jint i){

    return (*env)->NewStringUTF(env,"my first jni");

  }
```


编写 Android.mk
----
在右键点击我们配置的快捷方式 NDK-> ndk-build之前，我们还需要编写Android.mk

Android.mk配置如下内容：

```
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ndkdemotest-jni

LOCAL_SRC_FILES := ndkdemotest.c

include $(BUILD_SHARED_LIBRARY)

```

生成so文件
-----
在gradle的android下配置：
```
      ndk {

            moduleName "my_jni_call"
            abiFilters "armeabi-v7a","arm64-v8a", "x86","x86_64"

        }
```

在buildType里配置：
```
   sourceSets.main {
            //Gradle默认目录为src/main/jniLibs，但ndk-build的默认目录为src/main/libs
            jniLibs.srcDirs = ['src/main/libs']
            jni.srcDirs = [] //屏蔽掉默认的jni编译生成过程
        }
```

现在就可以右键 快捷方式 NDK-> ndk-build

然后会在main目录下生成一个obj文件夹，一直点进去可以看到生成了各个平台下的so。

调用so
------
有了so后就可以调用了

```
public class MyJniCall {

    static{
        System.loadLibrary("my_jni_call");
    }
    public native int getInt(String str);

    public native String getString(int i);
}
```