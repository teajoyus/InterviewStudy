//
   // Created by Administrator on 2019\8\8 0008.
   //

#include <jni.h>
#include <com_example_interviewstudy_jni_MyJniCall.h>
#include <stdio.h>
#include <stdlib.h>
#include  <android/log.h>
// for native window JNI
//#include <android/native_window_jni.h>
// log标签

#define  LOG_TAG    "MY_JNI"

#define LOGD(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGI(fmt, ...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGE(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGV(fmt, ...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, fmt, ##__VA_ARGS__)

JNIEXPORT jint JNICALL Java_com_example_interviewstudy_jni_MyJniCall_getInt
 (JNIEnv * env, jobject obj, jstring str){
 return 100;
}
JNIEXPORT jstring JNICALL Java_com_example_interviewstudy_jni_MyJniCall_getString
 (JNIEnv * env, jobject obj, jint i){

   return (*env)->NewStringUTF(env,"my first jni");

 }

static const char *className = "com/example/interviewstudy/jni/MyJniCall";


static jstring sayHello(JNIEnv *env, jobject obj,jlong handle) {
    return (*env)->NewStringUTF(env,"我的动态注册函数");
}


static JNINativeMethod gJni_Methods_table[] = {
    {"sayHello", "(J)Ljava/lang/String;", (void*)sayHello}
};

static int jniRegisterNativeMethods(JNIEnv* env, const char* className,
    const JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    LOGI("Registering %s natives\n", className);
    LOGI("Registering natives\n");
    clazz = (*env)->FindClass( env,className);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class '%s'\n", className);
        return -1;
    }

    int result = 0;
    if ((*env)->RegisterNatives(env,clazz, gJni_Methods_table, numMethods) < 0) {
        LOGE("RegisterNatives failed for '%s'\n", className);
        result = -1;
    }

    (*env)->DeleteLocalRef(env,clazz);
    return result;
}



jint JNI_OnLoad(JavaVM* vm, void* reserved){

    JNIEnv* env = NULL;
    jint result = -1;

    if ((*vm)->GetEnv(vm,(void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    jniRegisterNativeMethods(env, className, gJni_Methods_table, sizeof(gJni_Methods_table) / sizeof(JNINativeMethod));

    return JNI_VERSION_1_4;
}
