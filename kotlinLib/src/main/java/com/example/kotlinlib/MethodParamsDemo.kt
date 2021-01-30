package com.example.kotlinlib

/**
 *  author : linmh
 *  date : 2021/1/26 11:13
 *  description :
 */
public class MethodParamsDemo {
    interface OnClickListener {
        fun onClick(view: MethodParamsDemo)
    }

    fun setOnClickListener(listener: (MethodParamsDemo) -> Unit) {
    }

    fun haha(hah: () -> Unit) {

    }

    fun hehe(hah: () -> String) {

    }

}