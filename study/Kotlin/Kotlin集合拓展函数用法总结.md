@[TOC](Kotlin集合拓展函数用法总结)

# 关联映射
## map、mapIndex
映射转换把一个集合通过函数结果映射为另外一个集合。最基本的就是**map()、mapIndexed()**

- 场景：我们需要对集合的每个元素或者某一些元素进行一些更新的时候

- 示例：
```
  val numbers = setOf(1, 2, 3)
  println(numbers.map { it * 3 })
  println(numbers.mapIndexed { idx, value -> value * idx })

```
- 输出：
```
[3, 6, 9]
[0, 2, 6]
```
转换在某些元素上产⽣ null 值，所以衍生的还有 mapNotNull() 、mapIndexedNotNull()

## mapTo

map内部也是通过mapTo()函数传入一个新创建的集合作为参数，我们也可以直接调用mapTo来确定具体的集合类型：
- 场景：我们以前可能会因为某些需求而创建一个新集合再去通过某些条件add进来，比如去重。
- 运用：
```
val numbers = setOf(1, 2, 3,3,3)
//去除重复元素，并把元素结果乘以2
println( numbers.mapTo(HashSet<Int>()){
       it*2
   })
```
- 输出：
```
[2, 4, 6]
```

## 衍生：mapNotNull()、mapIndexedNotNull()
从结果集中过滤掉null值

# 合并
把两个集合中具有相同位置的元素进行配对。

## zip()

- 场景：需要关联两个集合的映射关系的情况
- 示例：
```
    val modelIdList = listOf("12", "45", "67")
    val modelNameList = listOf("iphone X", "华为P30", "小米10")
    // println(modelIdList zip modelNameList)
    println(modelIdList.zip(modelNameList))
```
- 输出：
```
[(12, iphone X), (45, 华为P30), (67, 小米10)]
```
(注意：看zip()的源码，它的操作结果是并不是Map，而是List<Pair<T, R>>， 也就是你的两个集合元素被Pair对象关联了起来)

## unzip()
通过zip函数关联起来的集合，也可以通过unzip把他们分离出来。
- 示例：
```
    val zips = modelIdList.zip(modelNameList)
    val pair = zips.unzip()
    println(pair.first)
    println(pair.second)
```
- 输出
```
[12, 45, 67]
[iphone X, 华为P30, 小米10]
```
（注意：unzip函数结果是Pair<List<T>, List<R>>，一个Pair对象）

## associateWith()
associateWith函数通过对一个集合关联一个函数结果形成一个Map
- 示例
```
    val numbers = listOf("one", "two", "three", "four")
    //生成一个key为List的元素，value为元素长度的Map
    println(numbers.associateWith { it.length })
```
- 输出
```
{one=3, two=3, three=5, four=4}
```
- 衍生
 相关的还有associateBy()函数，这个相比associateWith()函数多了一个定义key生成规则的参数.
 还有associate()函数,通过接收函数参数，该函数结果返回一个Pair

# 打平

## flatten()
- 场景  
有时候我们会在一个集合内又有嵌套集合，然后又需要拿出所有元素的嵌套集合的元素的时候就有用。
- 示例
```
  val numberSets = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(1, 2))
  println(numberSets.flatten())
```
- 输出
```
[1, 2, 3, 4, 5, 6, 1, 2]
```

## flatMap()

flatten()只是把嵌套集合平铺开来，如果需要在铺平时在定义嵌套集合元素的规则的时候，就得用flatMap()函数

- 场景：当我们想把嵌套集合变成一维集合时，又要对嵌套集合做一些筛选过滤或转换操作的时候
- 示例
```
    val numberSets = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(1, 2))
//    println(numberSets.flatten())
    //平铺嵌套集合，并且对嵌套集合进行过滤
    println(numberSets.flatMap {
        it.filter {
            it%2==0
        }
    })
```

- 输出
```
[2, 4, 6, 2]
```

# 字符串表示
这个估计比较少会需要用到，当我们想对一个集合进行输出，又需要定义输出格式的时候可以用

- 示例
```
    val numbers = listOf("one", "two", "three", "four")
    println(numbers.joinToString(separator = " | ", prefix = "start: ", postfix = ": end"))
    val intNumbers = (1..100).toList()
    println(intNumbers.joinToString(separator = " , ", prefix = "start: ", postfix = ",end",limit = 10,truncated = "..."))
```
- 输出
```
start: one | two | three | four: end
start: 1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10 , ...,end
```

# 过滤
对一个集合通过某些规则过滤出想要的子集

## filter()
- 示例
```
    val numbers = listOf("one", "two", "three", "four")
    //筛选由t开头的元素的集合
    println(
            numbers.filter {
                it.startsWith("t")
            }
    )
```
- 输出
```
[two, three]
```
- 衍生
  - **filterNot()函数**，返回过滤不满足结果条件的子集
  - 如果有需要到index的话还可以用**filerIndexed()函数**
  - **filterNotNull()函数**可以返回所有非空元素
  - 当一个集合里面有不同类型的元素时，可以通过**filterIsInstance()函数**来过滤元素的类型

## partition()

当我们在使用filter()函数过滤集合的时候，假如除了过滤得到的子集外，还需要被过滤出来的子集。那么可以把filter()函数替换成partition()函数

partition()函数的操作结果是一个Pair，就对应了过滤得到的子集和过滤掉的子集。从而去划分一个集合中的东西

# 检验谓词

## 谓词

- any()：如果集合里面有一个匹配给定谓词，那么返回true
- none()：如果没有元素匹配，那么返回true
- all()：所有元素都匹配，那么返回true

-  示例
```
    val numbers = listOf("one", "two", "three", "four")
    //是否存在f开头的元素
    println(numbers.any{it.startsWith("f")})
    //是否没有长度=1的元素
    println(numbers.none{it.length==1})
    //是否所有元素长度都是3
    println(numbers.all{ it.length==3})
```
- 输出

```
true
true
false
```

- 场景
 如果我们需要了解一个集合里面是否包含某个东西（比如元素的某个字段值），那么我们可以用any()来判断。
 或者说我们需要判断集合里面不存在、或者都存在某某东西。这个就可以考虑用none()和all()来代替

# 分组

## groupBy
感觉这个可能用得也少吧。类似于sql语句的groupBy，根据某些规则对一个集合进行分组
- 示例
```
//根据首字母进行分组，并且对元素结果进行大写转换
println(listOf("one", "two", "three", "four", "five").groupBy(keySelector = { it.first() }, valueTransform = { it.toUpperCase() }))
```
- 输出
```
{o=[ONE], t=[TWO, THREE], f=[FOUR, FIVE]}
```
- 衍生
 使⽤ groupingBy() 函数。它返回⼀个Grouping类型的实例。使得我们可以通过Grouping对象去获取分组结果的一些信息

## chunked()
使用chunked可以对集合进行分块，分为多个List，最后一个List元素可能会比较少
比如chunked(3)分为3块， 然后对分块list进行操作得到最后一个list.
chunked()的操作结果是一个嵌套集合：List<List<T>>

## windowed()
可以检索给定⼤⼩的集合元素中所有可能区间。这个估计是用不到了
具体什么作用看下示例结果就知道了
- 示例
```
    val numbers = listOf("one", "two", "three", "four", "five")
    println(numbers.windowed(3))
    //partialWindows：是否包含最后一个不完整的块
    println(numbers.windowed(size = 3,step = 2,partialWindows = true))
```
- 输出
```
[[one, two, three], [two, three, four], [three, four, five]]
[[one, two, three], [three, four, five], [five]]
```
- 衍生
 构建两个元素的窗口，有一个单独的函数：zipWithNext()，

# 取集合范围

## slice()
slice() 就是对List的subList方法进行了封装
- 示例
```
val numbers = listOf("one", "two", "three", "four", "five", "six")
println(numbers.slice(1..3))
println(numbers.slice(0..4 step 2))
println(numbers.slice(setOf(3, 5, 0)))
```
## take() & takeLast()
从头开始取3个元素，就可以用take(3)，从末尾取3个元素，就可以用takeLast(3)

## drop()&dropLast()
从头开始丢弃3个元素，就可以用drop(3)，从末尾丢掉3个元素，就可以用dropLast(3)

## takeWhile() & takeLastWihle()
使用takeWhile()函数可以不停的获取元素直到与函数结果不匹配的首个元素为止。同理takeLastWihle()就是末端操作
- 示例
```
    val numbers = listOf("one", "two", "three", "four", "five", "six")
    //从开头开始取元素，条件是元素不以f开头，否则就结束
    println(numbers.takeWhile { !it.startsWith('f') })
```
- 输出
```
[one, two, three]
```
## dropWhile() & dropLastWihle()
使用dropWhile()函数可以不停的获丢弃元素直到与函数结果不匹配的首个元素为止。同理dropLastWihle()就是末端操作
用法就是和takeWhile() & takeLastWihle()一样，只不过takeWhile是取元素，而dropWhile是丢弃元素。

# 元素定位

## first() & last()
作用不用讲，不过需要注意这个只是帮你封装了下list[index]这个操作。所以它可能会越界。
所以取而代之的是用**firstOrNull，lastOrNull(）**（抛弃你喜欢用的list[list.size() - 1]）

## elementAt()
 elementAt()可能会越界，取而代之用用**elementAtOrNull()**来定位具体位置的元素，如果不存在则会返回null
 如果你需要越界后得到一个默认值，那么可以用**elementAtOrElse()**来用lambda表示得到结果
 相对的，get方法也是有**getOrNull()、getOrElse()**

## find()
这个也不用说，通过find()可以根据lambda表达式的条件来查找第一个满足条件的元素（注意是返回第一个元素，不是集合）
相反的findLast查找最后一个。

# 其它
contains() = in
sortedBy()、sortedByDescending()反序、reversed()得到反转集合副本、asReversed()得到反转集合视图（区别于reversed()）


# 聚合
## 数字集合操作
跟sql语句类似的功能
maxBy minBy maxWith、minWith、sumBy、average() 、count() 、sum()

## fold() & reduce()

这个我们貌似也很少用到 ，当有需要递推累积元素的时候可以用到。

它们依次将所提供的操作应⽤于集合元素并返回累
积的结果。  

reduce()函数有两个参数，第一个参数是上一次的累积结果，第二个是下个元素
一开始从第一个元素、第二个参数作为第二个元素开始。

如果想有个初始值，然后从第一个元素开始。那么可以使用fold()

- 示例
```
    val nums =  (1..100).toList()
    println(nums.reduce { acc, e ->
        //acc是之前的累积值， e是当前迭代的元素
        acc + e
    })
    //初始累积值从1000开始
    println(nums.fold(1000) { acc, e ->
        acc + e
    })
```
- 输出
```
5050
6050
```

衍生的还有 reduceRight() 和 foldRight()，他们会从反过来从最后一个元素往前递推

如果需要到index，那么也可以用reduceIndexed、foldIndexed。
相应衍生的也有reduceRightIndexed、foldRightIndexed

# Set集合操作

- 并集：使用union()或者直接：a union b

- 交集：intersect() 或者直接：a intersect b

- 差集：subtract() 或者直接： a subtract b

注意：上面操作的结果是Set集合

场景：当我们有几个集合，需要对这集合有并集、交集、差集的数学模型需求的时候，可以用这个来直接实现功能

# 结束语
之前kotlin这些语法半身不遂，老是要去查。这次索性写成个文档总结一下。
还有其它一些函数后面还需要完善。