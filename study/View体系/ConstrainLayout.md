@[toc]

# 前言

&ensp;&ensp;在ConstraintLayout出现之前，我们编写布局往往少不了多层嵌套，很多效果需要结合Relativelayout、LinerLayout等容器的相互嵌套来完成，虽然页面的效果实现了，但却带来很大的性能消耗，而往往还因为适配问题而带来更多的麻烦。
而ConstraintLayout神奇的地方在于，它不仅能够实现Relativelayout的相对定位，也能实现像LinerLayout一样的比例分配，而且比它们还更优秀。除此之外，ConstraintLayout还提供了很多属性和辅助类，让我们更轻松的实现布局效果。
使用ConstraintLayout之后往往能把之前嵌套好几层的布局干掉。从而大大减少了布局嵌套层次，提高了性能。

&ensp;&ensp;ConstraintLayout也不是个什么新鲜的东西了，google最早在16年I/O大会上就发布了这个全新的布局，而实际上据我在各个技术群上的了解，貌似实际把ConstraintLayout用在项目里的人相对较少，也可能是受项目限制，不方便重构布局。也有一部分因为不熟悉这个布局的使用，从而不敢轻易用在项目中，笔者在一开始使用这个布局的时候，就被它的灵活性惊艳到了。而且容易用以前布局的思维来用在了ConstraintLayout中，这是不可取的。所以深感ConstraintLayout需要适应一段时间后就会慢慢的适应这种布局方式，在适应后也会很不想用其它的布局了。

&ensp;&ensp;为此我总结了以下用法，希望能够帮助到你们，也为自己知识做一个总结。

# 用法

本篇文章讲解的是ConstraintLayout的基础用法，基本上ConstraintLayout的要点已经在这里了。

## 相对定位
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


效果类似于RelativeLayout的layout_toLeftOf、layout_alignParentLeft这些。

直白的理解就是：你想这个View的哪条边去对齐另外一个View的哪一条边的时候，就可以用这个。

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

&ensp;&ensp;在ConstraintLayout里，也支持layout_marginLeft这种格式。只是区分的一点是，在使用这句代码时比必须先指定下Left是相对于哪个View哪个位置

比如我们想让上面的bButton B距离左边50dp，那么必须要先声明它的left相对于父布局的left：
```
app:layout_constraintLeft_toLeftOf="parent"
        android:layout_marginLeft="50dp"
```

这样才能起作用。

值得注意的是，**margin确定的边距并不会因为做为锚点的View被设置成GONE了而失效。**

比如有以下代码：
```
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
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
   </android.support.constraint.ConstraintLayout>
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

&ensp;&ensp;这样我们可以设置layout_goneMarginLeft="0dp"，从而当锚点View被设置GONE的时候，marginLeft属性失效。
当然，我们也可以设置另外一个数值，来表示当锚点View被GONE之后，margin才生效。



## bias与居中处理

### 语句

- layout_constraintHorizontal_bias
- layout_constraintVertical_bias

### 解释

&ensp;&ensp;在文章一开头，我们有实现过了居中的效果。一开始接触的时候其实有点不习惯，ConstraintLayout并没有像RelativeLayout的居中效果的语句
而是使用（水平居中）：
```
  app:layout_constraintLeft_toLeftOf="parent"
  app:layout_constraintRight_toRightOf="parent"
```
&ensp;&ensp;设置了这个属性的View，会被左右两边“拉”着，结果大家用力都一样，那么就水平居中了
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
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
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
      </android.support.constraint.ConstraintLayout>
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

&ensp;&ensp;当直接指定宽度为wrap_content时，可以指定layout_constrainedWidth="true"则不会超过约束大小。
但是这样看来感觉有点多此一举了，感觉wrap属性没大用处了


&ensp;&ensp;由上，ConstraintLayout的强大之处真不是一点两大点。我们现在可以轻松自由的根据比例来指定View的宽高了。
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



&ensp;&ensp;可以看出，这个效果其实是相当于Linearlayout的链。它也一样和LinearLayout一样用weight来表示权重
比如在水平方向上使用layout_constraintHorizontal_weight来分配剩余的空间。
但是它不是和layout_weight属性全部一样，因为在LinearLayout中，layout_weight属性是不管宽高怎么设置它都会生效的

而layout_constraintHorizontal_weight使用的前提下必须chainStyle是spread的形式（默认就是了），还有需要设置0dp后才会有效果的

## 辅助布局

### GuideLine


GuideLine就是指导线，参考线的意思，有水平参考线和竖直参考线两种。在ConstraintLayout里面View的定位往往需要找到对应参考的锚点View，而有时候我们并不好找到这个View，或者说一定要先建一个参考的锚点View出来后才行，在这种情况下，GuideLine就有很大用途了。

GuideLine就是一个虚拟的辅助线，方便其它的View以此作为锚点，而它自身并不会参与绘制。

GuideLine有以下几个重要的属性：

- orientation
- layout_constraintGuide_percent
- layout_constraintGuide_begin
- layout_constraintGuide_end

orientation用法则和在linearLayout中一样，当作为水平参考线时需指定：android:orientation="horizontal"，想做为垂直参考线时指定：android:orientation="vertical"

layout_constraintGuide_percent则是指定参考线的百分比位置，根据orientation指定的方向调整位置。

layout_constraintGuide_begin和 layout_constraintGuide_end则是参考线距离开始或结束的具体数值

举个例子，我们的定义一条垂直居中的参考线，再定义一个Button A贴在这条参考线的下方：
```
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
  <android.support.constraint.Guideline
        android:id="@+id/guideline1"
        android:layout_width="22dp"
        android:layout_height="11dp"
        android:orientation="horizontal"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintGuide_percent="0.5"
       />

    <Button
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_marginTop="4dp"
        android:layout_marginBottom="8dp"
        android:background="@color/colorAccent"
        android:text="A"
        android:textSize="50sp"
        app:layout_constraintTop_toBottomOf="@+id/guideline1"
       />
       </android.support.constraint.ConstraintLayout>
```

效果截图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019072310204473.png)


### Group

&ensp;&ensp;Group同样是一个虚拟的View，并不参与实际绘制。它可以用来控制多个View同时hide or show，
之前我们往往对多个View需要同时显示和隐藏的时候往往需要一个一个去控制。而Group则提供了这个便利，它可以通过指定一组View，来达到控制这一组View的显示状态。

Group有两个重要属性：

- android:visibility
- app:constraint_referenced_ids

比如同时控制Button A 和 B 为GONE状态。（当然，这里只是举例了XML，但一般是写在代码里）

```
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
    <android.support.constraint.Group
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:visibility="invisible"
    app:constraint_referenced_ids="btn1,btn2"
    />
    <Button
        android:id="@+id/btn1"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_marginTop="4dp"
        android:layout_marginBottom="8dp"
        android:background="@color/colorAccent"
        android:text="A"
        android:textSize="50sp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        />

    <Button
        android:id="@+id/btn2"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_marginTop="4dp"
        android:layout_marginBottom="8dp"
        android:background="@color/colorPrimary"
        android:text="B"
        android:textSize="50sp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/btn1" />
</android.support.constraint.ConstraintLayout>
```

上面这样就什么也没看到了，因为Button A 和B被同时隐藏了。

需要注意的是，Group在写一组控件id的时候是逗号隔开，然后只写id的名字。

### Placeholder

Placeholder是一个占位布局，同样它本身不会参与绘制.
它有一个app:content属性，可以通过绑定一个View，当绑定了一个View之后，被绑定的View会相当于被GONE掉，而显示到Placeholder中来。
因此，它适合用来编写一些模版布局，在适当的情况下利用Placeholder先提前占位，然后再来替换成目标View。

Placeholder有一个重要属性：

- app:content

比如我们来写一个模版布局，这个布局分成上下两个部分。
```
<?xml version="1.0" encoding="utf-8"?>
<merge xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:layout_editor_absoluteX="0dp"
    tools:layout_editor_absoluteY="81dp"
    tools:parentTag="android.support.constraint.ConstraintLayout">

    <android.support.constraint.Placeholder
        android:id="@+id/template_top"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintHeight_percent="0.5"
        app:layout_constraintHeight_default="percent"
        app:content="@+id/top"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent" />
    <android.support.constraint.Placeholder
        android:id="@+id/template_bottom"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintHeight_percent="0.5"
        app:content="@+id/bottom"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent" />


</merge>
```

(注：我们用一个merge标签来避免模版布局带来的冗余嵌套，利用tools:parentTag="android.support.constraint.ConstraintLayout"使之按照ConstraintLayout的约束规则来处理)

其后，我们有了模版布局之后，假如我们想把这两个模版布局替换成两个ImageView，则可以这么做：
```
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <include layout="@layout/template_layout" />
    <ImageView
        android:id="@+id/top"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@color/colorPrimary"
        />
    <ImageView
        android:id="@+id/bottom"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@color/colorAccent"
        />
</android.support.constraint.ConstraintLayout>
```

效果截图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190723151033979.png)

&ensp;&ensp;可以看到，我们利用include标签引入的模版布局，两个Placeholder实际上被替换成了ImageView，而原来定义的ImageView则不会显示。
需要注意的是include标签需要放在要替换Placeholder的View的前面，不然不会被替换。
而且定义的ImageView已经没必要再配置那些约束属性了，因为这些约束属性已经在Placeholder里面声明了。

有了这个东西之后，我们可以很轻松的根据模版布局填充不同的View。


### Barrier

#### 
没错，他就是一个屏障器。它可以阻止View越过自己，当某个View要越过自己的时候，Barrier会自动移动，从而避免被覆盖

它也是通过constraint_referenced_ids属性指定需要添加屏障的View，从而这些View就不会超越这个屏障（跟个准确来说这个屏障会随着View的扩张而移动，使之不会让View越过自己）

Barrier有两个重要属性：

- app:barrierDirection
- app:constraint_referenced_ids

描述的不够好，我们来举个例子。

假如我有这么一个需求，左边有两个TextView表示两个单行的文字，右边是一个Textview表示多行的。

代码如下：
```
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
  <TextView
        android:id="@+id/tv1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="内容："
        android:textSize="24sp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        />
    <TextView
        android:id="@+id/tv2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="12345678"
        android:textSize="24sp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/tv1"
        />
    <TextView
        android:id="@+id/tv3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="this is content this is content this is content this is content this is content this is content"
        android:textSize="24sp"
        app:layout_constraintLeft_toRightOf="@+id/tv1"
        />
</android.support.constraint.ConstraintLayout>
```

效果图大致是这样：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190723154035457.png)

&ensp;&ensp;由于ConstraintLayout的约束规则，一个View只可以指定另外一个View作为锚点。所以这里右边的TextView无论是指tv1还是tv2的时候，都有可能因为tv1或者tv2中文字太长而遮挡到tv3

从效果图可以看到，由于锚点View指定的是tv1，而tv2的文字更长，它便重叠在tv3。

这个时候Barrier的作用就来了，我们可以通过Barrier可以指定tv1、tv2使这两个View不会越过Barrier。

而tv3可以设置这个Barrier为锚点，约束在它的右边。 代码如下：
```
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
  <TextView
        android:id="@+id/tv1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="内容："
        android:textSize="24sp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        />
    <TextView
        android:id="@+id/tv2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="12345678"
        android:textSize="24sp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/tv1"
        />
<android.support.constraint.Barrier
        android:id="@+id/barrier"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:barrierDirection="right"
        app:constraint_referenced_ids="tv1,tv2" />

    <TextView
        android:id="@+id/tv3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="this is content this is content this is content this is content this is content this is content"
        android:textSize="24sp"
        app:layout_constraintLeft_toRightOf="@+id/barrier" />
</android.support.constraint.ConstraintLayout>
```

效果图如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190723154542405.png)

可以看到左边有一条黑边屏障，这里只是看设计效果，实际上并不会显示在屏幕上的。


# 结束语

 终于结束了，其实一直很懒得写博客，因为总结一篇实在太耗费精力和时间了。但是换来的是对知识的总结，所以还是有必要坚持写下去。
 
 如果你也看到了最后，请给我一个赞支持一下吧！ ^。^