在Android.mk文件里加入：

```
LOCAL_LDLIBS    := -lm -llog
```



然后在c代码里面引用头文件：
```
#include  <android/log.h>
```

指定宏定义，方便编写：

```
#define  LOG_TAG    "MY_JNI"

#define LOGD(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGI(fmt, ...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGE(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGV(fmt, ...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, fmt, ##__VA_ARGS__)
```

然后就可以用了，比如：

```
LOGI("Registering %s natives\n", className);
```