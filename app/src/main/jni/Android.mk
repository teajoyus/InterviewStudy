LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS    := -lm -llog

LOCAL_MODULE    := my_jni_call

LOCAL_SRC_FILES := my_jni_call.c

include $(BUILD_SHARED_LIBRARY)
