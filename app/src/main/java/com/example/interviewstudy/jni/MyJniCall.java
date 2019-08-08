package com.example.interviewstudy.jni;

/**
 * Author:linmh
 * Time: 2019\8\8 0008.10:40
 * Description:This is MyJniCall
 */
public class MyJniCall {

    static {
        System.loadLibrary("my_jni_call");
    }

    public native int getInt(String str);

    public native String getString(int i);

    public static native String sayHello(long l);
}
