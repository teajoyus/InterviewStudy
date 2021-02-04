package com.example.kotlinlib.thread

import kotlinx.coroutines.*

/**
 *  author : linmh
 *  date : 2021/1/26 14:38
 *  description :
 *  协程：https://juejin.cn/post/6919709428295925768
 *  协程作用域构造器中，除了 coroutineScope 函数之外，
 *  其它函数都是可以指定这样一个线程参数的，只不过 withContext 函数是强制要求指定的。
 */
class GlobalScopeDemo {
    fun demo() {
        //会在新的线程中
        GlobalScope.launch {
            //thread name:DefaultDispatcher-worker-1
            println("GlobalScope launch execute current thread:${Thread.currentThread()}")
            delay(2000)
            println("GlobalScope launch execute end thread:${Thread.currentThread()}")
        }
    }
}

fun main() {
    println("current thread:${Thread.currentThread()}")
    GlobalScopeDemo().demo()
    GlobalScopeDemo().demo()
    // 如果代码块中的代码不能在 1 秒钟内运行结束，那么就会被强制中断。
//    Thread.sleep(3000)
    //阻塞的是当前线程 要注意
    //由于每次创建的都是顶层协程
    runBlocking {
        //当前线程
        println("runBlocking execute current thread:${Thread.currentThread()}")
        delay(1000)
        println("runBlocking delay end")
        launch {
            println("runBlocking launch execute ")
            delay(1000)
            println("runBlocking launch execute end")
            printSuspend()
            printSuspend2()
            printAsync()
        }
    }
}

/**
 * launch函数里有协程的作用域，但是抽离成方法之后就没有了
 * 在方法前面加suspend关键字就可以
 * 将任意函数声明成挂起函数，而挂起函数之间都是可以相互调用的。
 * suspend只是一种“提醒”，说明这个方法是阻塞的 耗时的
 * 让调用者知道这个方法是要在协程里面去调用
 */
suspend fun printSuspend() {
    println("printSuspend launch execute")
    delay(1000)
    println("printSuspend launch execute end")
}

/**
 * 直接suspend修饰是没有协程作用域的，如果需要的话可以直接使用coroutineScope函数
 */
suspend fun printSuspend2() {
    println("printSuspend2 launch call")
    //coroutineScope 函数只会阻塞当前协程，既不影响其它协程，也不影响任何线程，因此是不会造成任何性能上的问题的
    //执行的是在当前线程中，但是不会阻塞当前线程
    //coroutineScope 函数可以在协程作用域或挂起函数中调用
    coroutineScope {
        println("printSuspend2 launch execute :${Thread.currentThread()}")
        launch {
            delay(1000)
            println("printSuspend2 launch execute end :${Thread.currentThread()}")
        }
    }
}

/**
 * async方法加上await 可以同步获取
 * 不过系统提示改成withContext的形式
 */
fun printAsync(){
    print("printAsync call")
    runBlocking {
//        val result = async {
//            delay(1000)
//            5 + 5
//        }.await()
        val result = withContext(Dispatchers.IO) {
            delay(1000)
            5 + 5
        }
        print("printAsync result:$result")
    }
}

