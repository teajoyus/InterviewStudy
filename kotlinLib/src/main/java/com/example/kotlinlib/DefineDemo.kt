package com.example.kotlinlib

/**
 *  author : linmh
 *  date : 2021/1/26 11:31
 *  description :
 */
class DefineDemo {
        init {
        print("init")
    }
    lateinit var lateinitString: String
    val haha: String? by lazy {
        "22222"
    }
}