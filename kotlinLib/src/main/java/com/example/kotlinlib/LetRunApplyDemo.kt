package com.example.kotlinlib

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 *  author : linmh
 *  date : 2021/1/26 14:00
 *  description :
 */
class LetRunApplyDemo {
    fun demo1(){
        "22".also {
            it.length
        }
    }
    fun demo2(){
        "22"?.run {
            length
        }
    }
    fun demo3(): String {
        return "22".apply {
            length
        }
    }
    fun demo4(): Int {
        return "22".let {
            it.length
        }
    }

}