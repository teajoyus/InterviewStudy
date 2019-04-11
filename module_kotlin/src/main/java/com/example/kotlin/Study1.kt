package com.example.kotlin

import java.util.*
import kotlin.collections.ArrayList

fun sum(a: Int, b: Int): Int {
    return a + b
}

fun sum2(a: Int, b: Int) = a + b

fun printSum(a: Int, b: Int) = println("$a + $b = ${a + b}")

//使用val定义的是常量
val a: Int = 1
val b = 2
val c = "string"

//使用var定义的是变量
var d: Float = 1.2f
var e: String = "jack"
var f = true

fun printFormat() {
    var a = 1;
    //这个时候字符串已经被解析了
    var str = "a is $a"
    a = 2
    //这时候str的内容已经是 a is 1
    println("${str.replace("is", "was")}, but now is $a")
}

fun findMax(a: Int, b: Int) = if (a > b) a else b

fun getStringLength(str: Any): Int? {
    //is就相当于 instanceof
    if (str is String) {
        return str.length
    }
    //可以在返回值后面加个问号说明可以允许返回null
    return null
}

fun getStringLength2(str: Any): Int? {
    //还可以用!is
    if (str !is String) {
        return null
    }
    return str.length
}

fun printLoopWithList() {
    var a = 1
    //采用listOf来生成一个列表
    var list = listOf<String>("abc", "def", "a is $a", "xyz")
//    var list:List<String> = ArrayList<String>()
    //可以使用这样子的方式来遍历列表
    println("遍历item：")
    for (item in list) {
        print("${item}\t")
    }
    println("\nitem是自己起的名字，也可以写成其他的：")
    //item是自己起的名字，也可以写成其他的
    for (v in list) {
        print("${v}\t")
    }
    println("\n遍历索引：")
    for (index in list.indices) {
        //注意 不一定需要list.get(index)，也可以是数组使用
        print("${list[index]}\t")
    }
}

//使用when关键字
fun printWhen(obj: Any): String {
    val str: String
    //语法跟switch差不多，注意语法样式
    when (obj) {
        1 -> str = "One"
        is Long -> str = "is Long"
        !is String -> str = "isn't String"
        else -> str = "is String"
    }
    val s = "abc"
    var list = listOf<String>("abc", "def")
    when (obj) {
        s in list -> println("$s in list")
    }
    return str
}

/**
 * 使用区间来做范围
 */

fun printCheckRange() {
    val a = 1.5f
    val b = 8
    //暂时测试 可以是Int Float Long，不能是Boolean String等其它类型
    if (a in 1..b) {
        println("$a in [1,$b]")
    } else {
        println("$a !in [1,$b]")
    }
    val s = 1
    val e = 10
    println("使用$s..$e 输出1到10")
    for (x in s..e) {
        print("$x\t")
    }
    var step = 2;
    println("\n使用$s..$e 输出1到10,step = $step")
    for (x in s..e step step) {
        print("$x\t")
    }
    step = 3
    println("\n使用$s..$e 输出1到10,step = $step 使用downTo来递减")
    for (x in e downTo s step step) {
        print("$x\t")
    }
    println("")
    println("\n使用$s..$e 输出1到10,用until：")
    for (x in s until e){
        print("$x\t")
    }
    println("")
}

fun printFilterList() {
    val fruits = listOf("banana", "avocado", "apple", "kiwifruit", "abc")
    fruits.subList(0, 4)
//            .filter { it.contains("a") }
            .filter { x -> x.contains("a") }
            .sortedBy { it }
            .map { it.toUpperCase() }
            .forEach { print(it + "\t") }
    println()
}

//下面是定义接口、抽象类和类

interface RectangleProperties {
    //接口可以定义常量，由实现类来赋值这个常量
    val isSquare: Boolean
    //不能去初始化
//    val demo:String = "I am a Interface"
    //常量要这么写
    val demo: String
        get() = "I am a Interface"
}

abstract class Shape(val sides: List<Double>) {
    val side: Double get() = sides.sum()
    abstract fun calcArea(): Double
}

/**
 * 用括号来表示构造方法，用冒号来继承
 */
class Rectangle(
        val height: Double,
        val length:Double
):Shape(listOf(height,length,height,length)),RectangleProperties{
    override val isSquare: Boolean get() = length==height
    override fun calcArea(): Double {
        return length * height
    }

}


//===============================
//扩展函数
fun String.myDemo(){
    println("我是String类的扩展方法")
}
fun main(array: Array<String>) {
    println("a + b = " + sum2(2, 3))
    printSum(a, b)
    //d是用var修饰，所以是正常的变量
    d = 2.3f
    //a是被val定义。所以是一个常量，不能再赋值
//    a = 2
    printFormat()
    println("findMax($a,$b):${findMax(a, b)}")
    println("getStringLength(2):${getStringLength("2")}")
    //虽然返回值是Int 但是可以在Int后面加问号，这样就可以返回null
    println("getStringLength2(1.2f):${getStringLength2(1.2f)}")
    printLoopWithList()
    println("printWhen(1):${printWhen(1)}")
    println("printWhen(1L):${printWhen(1L)}")
    println("printWhen(true):${printWhen(true)}")
    println("printWhen(str):${printWhen("str")}")
    printCheckRange()
    printFilterList()
    var rectangle:Rectangle = Rectangle(4.0,5.0)
    println("rectangle demo:"+rectangle.demo)
    println("rectangle isSquare:"+rectangle.isSquare)
    println("rectangle calc area:"+rectangle.calcArea())
    var v = "扩展"
    v.myDemo()
}
