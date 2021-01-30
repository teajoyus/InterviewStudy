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
    val a = 1
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
    for (x in s until e) {
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
        val length: Double
) : Shape(listOf(height, length, height, length)), RectangleProperties {
    override val isSquare: Boolean get() = length == height
    override fun calcArea(): Double {
        return length * height
    }

}


//===============================
//扩展函数
fun String.myDemo() {
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
    var rectangle: Rectangle = Rectangle(4.0, 5.0)
    println("rectangle demo:" + rectangle.demo)
    println("rectangle isSquare:" + rectangle.isSquare)
    println("rectangle calc area:" + rectangle.calcArea())
    var v = "扩展"
    v.myDemo()

    var numbers = listOf("one", "two", "three", "three","four","five")
    //list 转 map
    println(numbers.associateWith { it.length })
    //可以定义key和生成规则和value的生成规则
    println(
            numbers.associateBy(keySelector ={
                it.first().toUpperCase()
            } ,valueTransform = {
                it.length
            })

    )
    //可以定义key和生成规则和value的生成规则
    println(
            numbers.associate {
                    it to it.first()
            }
    )

    val numberSets = listOf(setOf(1, 2, 3), setOf(4, 5, 6), setOf(1, 2))
    println(numberSets.flatten())

    val containers = listOf(listOf("one", "two", "three"),listOf("four", "five", "six"),listOf("seven", "eight"))
    val containers2 = containers.flatMap {
        it
    }
    val containers3 = numberSets.flatMap {
        it
    }
    println("containers2$containers2")
    println("containers3$containers3")


     numbers = listOf("one", "two", "three", "four")
    println(numbers)
    println(numbers.joinToString())
    val listString = StringBuffer("The list of numbers: ")
    numbers.joinTo(listString)
    println(listString)
    println(numbers.joinToString(separator = " | ", prefix = "start: ", postfix = ": end"))
    println(numbers.joinToString(separator = ""))


    val intNumbers = (1..100).toList()
    println(intNumbers.joinToString(limit = 10, truncated = "<...>",transform = {entry ->
        (entry*1000).toString()
    }))

    val list = listOf(null, 1, "two", 3.0, "four")
    println("All String elements in upper case:")
    list.filterIsInstance<String>().forEach {
        println(it.toUpperCase())
    }

    numbers = listOf("one", "two", "three", "four")
    val (match, rest) = numbers.partition { it.length > 3 }
    println(match)
    println(rest)


    numbers = listOf("one", "two", "three", "four", "five")
    println(numbers.groupBy { it.first().toUpperCase() })
    println(numbers.groupBy(keySelector = { it.first() }, valueTransform = { it.toUpperCase()
    }))

    numbers = listOf("one", "two", "three", "four", "five", "six")
    println(numbers.groupingBy { it.first() }.eachCount())

    numbers = listOf("one", "two", "three", "four", "five", "six")
    println(numbers.slice(1..3))
    println(numbers.slice(0..4 step 2))
    println(numbers.slice(setOf(3, 5, 0)))

    println("take and drop")
    numbers = listOf("one", "two", "three", "four", "five", "six")
    println(numbers.take(3))
    println(numbers.takeLast(3))
    println(numbers.drop(1))
    println(numbers.dropLast(5))

    println("takeWhile and dropWhile")
    numbers = listOf("one", "two", "three", "four", "five", "six","seven")
    println(numbers.takeWhile { !it.startsWith('f') })
    println(numbers.takeLastWhile { it != "three" })
    println(numbers.dropWhile { it.length == 3 })
    println(numbers.dropLastWhile { it.contains('i') })

    println("chunkeds")
    val chunkeds = (0..13).toList()
    println(chunkeds.chunked(3) { it }) // `it` 为原始集合的⼀个块
    println("windowed")
    println(numbers.windowed(3))
    println(numbers.windowed(size = 3,step = 2,partialWindows = false))
    println(numbers.windowed(size = 3,step = 2,partialWindows = true))

    println("zipWithNext")
    println(numbers.zipWithNext() { s1, s2 -> s1.length > s2.length})

    println("elementAtOrNull() ")
    println(numbers.elementAtOrNull(5))
    println(numbers.elementAtOrNull(15))

    println("find() ")
    println(numbers.find {
        it.startsWith("t")
    })
    println(numbers.findLast {
        it.startsWith("t")
    })

    println("random() ")
    println(numbers.random())

    println("sort() ")
    println(numbers.sortedBy {
        it.length
    })
    println(numbers.sortedByDescending {
        it.length
    })
    println("sortedWith() ")
    numbers = listOf("one", "two", "three", "four")
    println(numbers.sortedWith(compareBy {
        if(it.length>2){
            it.first()
        }else{
            it.first()
        }
    }))
    println("Sorted by length ascending: ${numbers.sortedWith(compareBy { it.length })}")

    println("reversed() ")
    println(numbers.reversed())
    var numberss = (1..10).toMutableList()
    val asReversedNumberss = numberss.asReversed()
    val reversedNumberss = numberss.reversed()
    println(asReversedNumberss)
    numberss.add(11)
    println(reversedNumberss)
    println(asReversedNumberss)
    println("reduce and fold ")
//    val numbersReduce = listOf(5, 2, 10, 4)
//    val sum = numbersReduce.reduce { sum, element -> sum + element }
//    println(sum)
//    val sumDoubled = numbersReduce.fold(100) { sum, element -> sum + element * 2 }
//    println(sumDoubled)
   println( numbers.reduce { acc, s ->
       println(acc)
       println(s)
       acc+s
   })
   println( numbers.fold("i am fold:") { acc, s ->
       println(acc)
       println(s)
       acc+s
   })
   println( numbers.foldIndexed("i am fold2:") { index,acc, s ->
       println(index)
       println(acc)
       println(s)
       acc+s
   })
    println((numbers union  numberss))


}
