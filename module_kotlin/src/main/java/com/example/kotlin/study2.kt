package com.example.kotlin

import java.lang.Exception
import java.lang.NullPointerException

/**
 * Author:mihon
 * Time: 2019\3\25 0025.14:20
 *Description:This is study2
 */

/**
 * 这样可以用来定义实体类，里面自动帮我们定义好了属性，get和set，并且重写了copy、equals、hashcode这些方法
 */
data class Customer(var name: String, var email: String = "") {

}

/**
 * 单例
 */
object SingletonObject {

    val str = "我是单例"
    override fun toString(): String {
        return str;
    }
    //这个operator暂时不知道咋用
//    operator fun invoke(): String {
//        return str;
//    }
}


/**
 * 用问号来防止空指针
 */
fun demoNotNullCall(str: String): Int {

    //相当于java的if(str!=null)
    str?.let {

        println("str is not null")
    }

    return str?.length
}

/**
 * Try..Catch
 */
fun demoTryCatch(str: String) {

    //可以这么写  把它当做一个表达式
    var size = try {
        str.length
    } catch (e: NullPointerException) {
        e.printStackTrace()
    }

    try {
        size = str[2]
    } catch (e: Exception) {

    }
}

/**
 * if 表达式
 */


fun demoIf() {

    var i = 3
    var str = if (i == 1) {
        "One"
    } else if (i == 2) {
        "Two"
    } else {
        "Other"
    }
    println("demoIf():" + str)


}

fun check(c: Char) {
//    if (c == 1) { // 错误：类型不兼容
//        // ……
//    }
    if (c in 's'..'z' || c in 'A'..'Z') {
        println("$c 是一个字母")
    } else {
        println("$c 不是一个字母")
    }
}

/**
 *关于基本类型那么判断相等性
 */
fun demoEqualBaseType() {
    val a = 1000
    val b: Int? = a
    val c: Int? = a
    println("a==b?${a == b}")
    println("a===b?${a === b}")

    //TODO 这个目前不知道为啥，上面的加个Int后面的问号之后===就不等于了，个人理解是一种装箱作用后，对象地址变了
    //而没有问号的话则只是一个引用
    val d: Int = a
    val e: Int = a
    println("d==e?${d == e}")
    println("d===e?${d === e}")

    val byte: Byte = 1
    //可以这么转
    val int_b: Int = byte.toInt()
    val char_b: Char = byte.toChar()


}
//标签处返回
fun foo(){
    //这种是直接是foo函数的返回
    listOf<Int>(1,2,3,4,5).forEach{
        if(it==3){
            return
        }

    }

    //这种是直接是返回到forEach
    listOf<Int>(1,2,3,4,5).forEach{
        if(it==3){
            return@forEach
        }
    }

    //不采用lambda表达式 话就是这样：
    listOf<Int>(1,2,3,4,5).forEach(fun(value:Int){
        if(value==3){
            return
        }
    })
}

fun main(args: Array<String>) {

    var customer: Customer = Customer("张三", "234627@qq.com")
    println(customer)
    println(customer.hashCode())
    println("name = ${customer.name} ,email = ${customer.email}")
    var customer2 = customer.copy()
    customer2.name = "李四"
//    customer2.email = "1234542@qq.com"
    println("customer2:$customer2")
    //第二个参数已经有默认值，可以不传，这样子定义类的时候并不需要定义太多构造方法
    var customer3 = Customer("王五")

    //无法创建
//    var single = SingletonObject()

    println("SingletonObject:${SingletonObject}")
    demoTryCatch("123")
    demoIf()
    check('C')


    //Break 与 Continue 标签,
    //使用一个标识符后面加个@的符号，就可以使得break跳出或者continue继续到指定的那个循环体
    //比如这个break就会跳出最外一层循环
    loop@ for (i in 1..5) {
        println("i = $i")
        for (j in 1..100) {
            if (i == 3 && j == 20) {
                break@loop
            }
        }
        //而默认的话就是跳出最近的那个循环
        for (i in 1..5) {
            println("i = $i")
            for (j in 1..100) {
                if (i == 3 && j == 20) {
                    break
                }
            }
        }

        //标识符可以自己命名
        for (i in 1..5) {
            println("i = $i")
            yourdefind@ for (j in 1..100) {
                if (i == 3 && j == 20) {
                    break@yourdefind
                }
            }
        }


        //标签处返回

    }
}