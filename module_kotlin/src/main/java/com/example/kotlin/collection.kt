package com.example.kotlin

/**
 * @Author linmh
 * @email 625321521@qq.com
 * @date 2020-04-16 18:37
 * @desc
 */

fun main() {

//    val numbers = setOf(1, 2, 3,3,3)
//    println(numbers.map { it.toString().length })
//    println(numbers.mapIndexed { idx, value -> value * idx })
//   println( numbers.mapTo(HashSet<Int>()){
//       it*2
//   })

//    val modelIdList = listOf("12", "45", "67")
//    val modelNameList = listOf("iphone X", "华为P30", "小米10")
//    //    println(modelIdList zip modelNameList)
////    println()
//    val zips = modelIdList.zip(modelNameList)
//    val pair = zips.unzip()
//    println(pair.first)
//    println(pair.second)


//    val numberSets = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(1, 2))
////    println(numberSets.flatten())
//    println(numberSets.flatMap {
//        it.filter {
//            it%2==0
//        }
//    })

//    val numbers = listOf("one", "two", "three", "four")
//    println(numbers.joinToString(separator = " | ", prefix = "start: ", postfix = ": end"))
//    val intNumbers = (1..100).toList()
//    println(intNumbers.joinToString(separator = " , ", prefix = "start: ", postfix = ",end",limit = 10,truncated = "..."))

//    val numbers = listOf("one", "two", "three", "four")
//    println(
//            numbers.filter {
//                it.startsWith("t")
//            }
//    )
//    println(
//            numbers.partition {
//                it.startsWith("t")
//            }
//    )

//    val numbers = listOf("one", "two", "three", "four")
//    println(numbers.any{it.startsWith("f")})
//    println(numbers.none{it.length==1})
//    println(numbers.all{ it.length==3})

//    println(listOf("one", "two", "three", "four", "five").groupBy(keySelector = { it.first() }, valueTransform = { it.toUpperCase() }))

//    val numbers = listOf("one", "two", "three", "four", "five", "six")
////    numbers.slice(1..3)
////    numbers.take()
//    println(numbers.takeWhile { !it.startsWith('f') })

//    val numbers = listOf("one", "two", "three", "four", "five")
//    println(numbers.windowed(3))
//    //partialWindows：是否包含最后一个不完整的块
//    println(numbers.windowed(size = 3,step = 2,partialWindows = true))
//    numbers.elementAt()

    val nums =  (1..100).toList()
    println(nums.reduce { acc, e ->
        //acc是之前的累积值， e是当前迭代的元素
        acc + e
    })
    //初始累积值从1000开始
    println(nums.fold(1000) { acc, e ->
        acc + e
    })
}