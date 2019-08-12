刘望舒：http://liuwangshu.cn/application/view/3-animation.html

AlphaAnimation，RotateAnimation,TranslateAnimation,ScaleAnimation四种动画方式，并提供了AnimationSet动画集合来混合使用多中动画。


# 自从android3.0后引入了ObjectAnimation
------------------
1、ObjectAnimation用静态工厂直接返回对象，通过ofFloat、ofInt等等方式

2、ObjectAnimation继承自ValueAnimation，ValueAnimation像是一个数值发生器，用来规则的改变数字

3、所改变的属性必须有set和get方法，因为ObjectAnimation构建的时候传入属性名通过反射来设置和获取的

4、ValueAnimator的AnimatorUpdateListener可以中监听数值的变化，每次数值产生变化则会回调

5、ObjectAnimator的addListener 也可以传入AnimatorListenterAdaper，这样就不用实现4个方法太冗余了


# AnimatorSet-属性动画集合
----------
它有before、with、after这些方法用来控制几个动画的前后顺序（方法返回的是它的Builder）
也可以playTogether(Animator... items);来让动画同时执行


# 组合动画-PropertyValuesHolder
------

这个动画可以让几个动画一起执行，但不能顺序执行
例子：
```
PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat('scaleX', 1.0f, 1.5f);
PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat('rotationX', 0.0f, 90.0f, 0.0F);
PropertyValuesHolder valuesHolder3 = PropertyValuesHolder.ofFloat('alpha', 1.0f, 0.3f, 1.0F);

ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(imageView,  valuesHolder1, valuesHolder2, valuesHolder3);
objectAnimator.setDuration(2000).start();
```

# XML中的属性动画
ObjectAnimator也可以配置在xml中，需要提供duration来控制动画时长、propertyName来传入属性名、valueFrom和valueTo对应开始结束的值
另外还有个valueType用来指定是float型、int型、double型这些
------------
在res文件中新建animator文件，在里面新建一个scale.xml,里面的内容如下：
```
<?xml version="1.0" encoding="utf-8"?>
<objectAnimator xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:duration="1000"
    android:propertyName="scaleX"
    android:valueFrom="1.0"
    android:valueTo="2.0"
    android:valueType="floatType"
    >
</objectAnimator>
```
之后在代码里面用动画装载器AnimatorInflater 加载进来 loadAnimator：

```
Animator animator=AnimatorInflater.loadAnimator(this,R.animator.scale);
animator.setTarget(view);
animator.start();
```
