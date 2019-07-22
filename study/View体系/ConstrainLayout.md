https://www.jianshu.com/p/502127a493fb

# 用法
## 相对定位（layout_constraintXXX_toYYYOf）
### 语句
- layout_constraintLeft_toLeftOf
- layout_constraintLeft_toRightOf
- layout_constraintTop_toTopOf
- layout_constraintTop_toBottomOf
- layout_constraintRight_toLeftOf
- layout_constraintRight_toRightOf
- layout_constraintBottom_toTopOf
- layout_constraintBottom_toBottomOf
- layout_constraintBaseline_toBaselineOf
- layout_constraintStart_toEndOf
- layout_constraintStart_toStartOf
- layout_constraintEnd_toStartOf
- layout_constraintEnd_toEndOf

### 解释
google为我们提供了这么多个这种xxx to yyy of 的格式，
这里的xxx就是使用这条约束语句的View的某个位置（Left、Top、Right、Bottom、Start、End、BaseLine）
这里的yyy就是被用来做锚点的View的某个位置（Left、Top、Right、Bottom、Start、End、BaseLine）


整句的意思就是：约束这个View的左上右下某条边相对于某个View的左上右下某条边。效果类似于RelativeLayout的layout_toLeftOf、layout_alignParentLeft这些。

更直白：你想这个View的哪条边去对齐另外一个View的哪一条边的时候，就可以用这个。

### 运用举例

举个栗子，我们写个Button A，它居中父布局（水平和垂直），然后写第二个Button B，让它处于第一个Button下方。

代码如下：
```
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >

    <Button
        android:id="@+id/btn1"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:background="@color/colorAccent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        android:text="A"
        android:textSize="50sp"
        />
    
    <Button
        android:id="@+id/btn2"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:background="@color/colorPrimary"
        app:layout_constraintTop_toBottomOf="@+id/btn1"
        android:text="B"
        android:textSize="50sp"
        />
</android.support.constraint.ConstraintLayout>
```
运行结果如图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190722155208727.png)

（注：如果被拿来做约束参考的View是它的父布局的话，那么就不是写id，而是写parent）

View的（Left、Top、Right、Bottom、Start、End、BaseLine）:

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190722160144976.png)

这个东西特别好用，使得我们可以代替RelativeLayout来做相对位置的处理

## 边距（Margin）

### 语句
- android:layout_marginStart
- android:layout_marginEnd
- android:layout_marginLeft
- android:layout_marginTop
- android:layout_marginRight
- android:layout_marginBottom

### 解释
在ConstraintLayout里，也支持layout_marginLeft这种格式。只是区分的一点是，在使用这句代码时比必须先指定下Left是相对于哪个View哪个位置

比如我们想让上面的bButton B距离左边50dp，那么必须要先声明它的left相对于父布局的left：
```
app:layout_constraintLeft_toLeftOf="parent"
        android:layout_marginLeft="50dp"
```

这样才能起作用。

值得注意的是，**margin确定的边距并不会因为做为锚点的View被设置成GONE了而失效。**

比如有以下代码：
```
   <Button
        android:id="@+id/btn2"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:background="@color/colorPrimary"
        android:text="B"
        android:textSize="50sp"
        app:layout_constraintTop_toBottomOf="@+id/btn1"
        app:layout_constraintLeft_toLeftOf="parent"
        android:visibility="gone"
        />
    <Button
        android:id="@+id/btn3"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_marginLeft="150dp"
        android:background="@color/colorAccent"
        android:text="C"
        android:textSize="50sp"
        app:layout_constraintLeft_toLeftOf="@+id/btn2"
        app:layout_constraintTop_toBottomOf="@id/btn1"
        />
```

结果：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190722170143670.png)

可以看出Button C还是可以以Button B作为锚点，它的constrain依旧生效。

那如果我们想根据锚点View是否GONE而来确定margin生不生效，要怎么做呢。
google提供了另外一套方案：
- layout_goneMarginStart
- layout_goneMarginEnd
- layout_goneMarginLeft
- layout_goneMarginTop
- layout_goneMarginRight
- layout_goneMarginBottom

这样我们可以设置layout_goneMarginLeft="0dp"，从而当锚点View被设置GONE的时候，marginLeft属性失效。
当然，我们也可以设置另外一个数值，来表示当锚点View被GONE之后，margin才生效。



## bias与居中处理

### 语句
- layout_constraintHorizontal_bias
- layout_constraintVertical_bias

### 解释

在文章一开头，我们有实现过了居中的效果。一开始接触的时候其实有点不习惯，ConstraintLayout并没有像RelativeLayout的居中效果的语句
而是使用（水平居中）：
```
  app:layout_constraintLeft_toLeftOf="parent"
  app:layout_constraintRight_toRightOf="parent"
```
设置了这个属性的View，会被左右两边“拉”着，结果大家用力都一样，那么就水平居中了
在水平居中了后，我们同样可以使用margin来调整位置，而往往我们又需要根据比例来调整这个位置关系，这是bias属性就用上了

在使用constrain属性使得View水平居中后，比如想让View水平开始位于屏幕宽度20%的位置，那么可以：

``` 
 app:layout_constraintLeft_toLeftOf="parent"
 app:layout_constraintRight_toRightOf="parent"
 app:layout_constraintHorizontal_bias="0.2"

```

## 圆弧定位

### 语句

- layout_constraintCircle
- layout_constraintCircleAngle
- layout_constraintCircleRadius


### 解释

这个属性就高大上了，毕竟是其它布局所不能直接支持的。

它的作用就是你可以相对于锚点View的中心位置，声明一个角度和距离（半径）来确定View的位置



- layout_constraintCircle  是关联的锚点View的id
- layout_constraintCircleRadius View的中心点与关联的锚点View的中心点的距离（圆弧半径）
- layout_constraintCircleAngle View的中心点与关联的锚点View的中心点的角度关系（0到360度）

关于角度和半径的说明，这里贴一张官方图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190722182610619.png)

### 举例运行

比如以下代码，让Button B位于Button A的45度角，并且距离Button A中心点为150dp
```
  <Button
    android:id="@+id/btn1"
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:background="@color/colorAccent"
    android:text="A"
    android:textSize="50sp"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/btn2"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:background="@color/colorPrimary"
        android:text="B"
        android:textSize="50sp"
        app:layout_constraintCircle="@id/btn1"
        app:layout_constraintCircleAngle="45"
        app:layout_constraintCircleRadius="150dp"
      ></Button>
```

效果：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190722182517439.png)


## View的尺寸大小

### 语句

- layout_constraintWidth_default
- layout_constraintWidth_percent
- layout_constraintHeight_percent

### 解释

ConstraintLayout也一样，可以用layout_width、layout_height来确定View大小。

然而值得注意的是，它额外的提供给了一个0dp的属性（MATCH_CONSTRAINT ），这不是说让宽或高位0dp，而是一种标记。

它标记的含义会随着不同的constrain设置而有不同的体现。

layout_constraintWidth_default语句有三个不同的值：

-  默认是spread，意思是占用所有的符合约束的空间

比如宽度铺满：
```
    ...
 android:layout_width="0dp"
  app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent" 
    ...
```
这样的话则就会铺满整个宽度。 （google不推荐使用match_parent，有这个高大上的属性来代替）

- percent， 顾名思义就是按照百分比来表示宽度

比如，水平居中后View的宽度为总宽度的50%，则可以这么写：
```
...
        android:layout_width="0dp"
        android:layout_height="100dp"
        app:layout_constraintWidth_default="percent"
        app:layout_constraintWidth_percent="0.5"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
  ...
```

运行：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190722184508558.png)


- wrap 

匹配内容大小但不会超过约束的限制。（和指定宽度为wrap_content的区别是不会超过约束限制）

这个跟直接指定宽度为wrap_content有相似之处，不同的是：

**指定layout_constraintWidth_default="wrap"时不会超过约束限制的大小，而直接指定宽度为wrap_content则可以超过约束限制的大小**

然而google又提供了一对属性
- app:layout_constrainedWidth=”true|false”
- app:layout_constrainedHeight=”true|false”

当直接指定宽度为wrap_content时，可以指定layout_constrainedWidth="true"则不会超过约束大小。
但是这样看来感觉有点多此一举了，感觉wrap属性没大用处了


由上，ConstraintLayout的强大之处真不是一点两大点。我们现在可以轻松自由的根据比例来指定View的宽高了。
虽然说LinearLayout也能做到一点，但是使用LinearLayout往往使得布局有更多的嵌套，而ConstraintLayout的出发点就是位了减少嵌套。


## View的尺寸比例

### 语句

- layout_constraintDimensionRatio

### 解释

ConstraintLayout不仅支持宽高比例的配置，还是配置宽高比例根据其中的一个而根据比例计算出另外一个的

当宽度高度有一个为0dp时，另外一个可以根据指定的ratio来确认自己的大小。


layout_constraintDimensionRatio指定的格式可以是"width:height"的形式，也可以是width/height的比例值

比如宽度为100dp radio指定了“2:1”，那么高度就是50dp。

代码如下：

```
    ...
     android:layout_width="0dp"
     android:layout_height="100dp"
    app:layout_constraintDimensionRatio="2:1"
    ...
```
上面的2:1 也可以写成 2， 也就是宽和 高的比值



如果两个都为0dp，那么尺寸就是满足约束限制的最大尺寸了。

比如我在根布局ConstraintLayout里想指定一个View，它居中屏幕，铺满宽度，并且高度很宽度一样大小，那么可以这么写：
```
  <Button
        android:id="@+id/btn1"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintDimensionRatio="1"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" 
        ...
        />
```

运行效果：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190722190957813.png)



如果还想单独约束宽度或者高度的话，layout_constraintDimensionRatio可以这么写：

在layout_constraintDimensionRatio写比例的时候前面加个W或H，来表示要约束的是宽还是高
比如高度铺满，而宽度是高度是二分之一：
```
 <Button
        android:id="@+id/btn1"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintDimensionRatio="W,1:2"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
         ...
         
         />

```
运行结果：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190722191408928.png)


## View的最大最小尺寸

### 语句

- layout_constraintWidth_min
- layout_constraintWidth_max
- layout_constraintHeight_max
- layout_constraintHeight_min

## View链

### 语句

- layout_constraintHorizontal_chainStyle
- layout_constraintHorizontal_weight
- layout_constraintVertical_chainStyle
- layout_constraintVertical_weight

### 解释

如果在一个水平或者垂直方向上，一排View之间两两相互约束，则为链

如图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190722191729500.png)


可以通过layout_constraintHorizontal_chainStyle来设置这条链

- spread

默认的方式，也就是上图的样子

- spread_inside

和spread的区别是没算两端的约束

- packed

所有元素挤在中间,可以使用bias来改变位置偏移


具体可以看下官方提供的图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190722191929491.png)



可以看出，这个效果其实是相当于Linearlayout的链。它也一样和LinearLayout一样用weight来表示权重
比如在水平方向上使用layout_constraintHorizontal_weight来分配剩余的空间。
但是它不是和layout_weight属性全部一样，因为在LinearLayout中，layout_weight属性是不管宽高怎么设置它都会生效的

而layout_constraintHorizontal_weight使用的前提下必须chainStyle是spread的形式（默认就是了），还有需要设置0dp后才会有效果的

## 辅助布局