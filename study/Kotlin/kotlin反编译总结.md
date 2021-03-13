# 前言
 下面这些例子可以在kotlinLib中去找
 
# 拓展函数 原理
编译后的class路径：\build\classes\kotlin\main
kotlin会生成一个类，把拓展函数变成static final的形式，并且把对象变成形参
如果是java调用的话，那么就是这个类直接调用方法传入拓展对象
拓展类就是帮你完成一些工具类相关的操作

知识点：
1、生成新的类
2、在新的类里增加static final方法
3、拓展对象作为形参传入

# object class 原理

定义一个object class查看反编译：
```
public final class ObjectTest {
  public static final ObjectTest INSTANCE;
  
  static {
    ObjectTest objectTest = new ObjectTest();
  }
  
  public final int haha() {
    return 1;
  }
}
```
知识点：  
1、object class 不可继承  它是final class
2、私有化构造器，不可以new实例。会生成一个INSANCE静态实例引用，通过静态代码快来创建这个实例
3、定义的方法会变成final修饰

# data class 反编译

知识点：
1、在data class 中定义的变量有没有加问号，对应了生成后的代码的注解（@Nullable与@NotNull）
2、在data class在中如果是用val修饰变量的话，那么生成后的代码就是final修饰，全部都是private
3、data class生成后的代码完成了toString方法、hashCode方法、equals方法
4、在data class中使用的默认值，在java中是不生效的。在java里面只会生成一个带所有参数的构造方法，
java调用的话就需要把这些参数都传递进来才行。

重难点：
data class会生成get和set的。但是看反编译的代码却发现有component1()、component2()之类的方法
其实这个跟kotlin的解构有关系
（解构：在 Koltin 中可以把一个对象赋值给多个变量，这种操作叫做解构声明）
那么kotlin要怎么知道第一个参数对应的是data class中的第一个定义的参数呢？就通过componentN()方法来做对应
 var(ss,dd) = dataClassDemo
 比如这样ss就是component1()、dd就是component2()。我们来看这句代码的反编译：
 ```
DataClassDemo dataClassDemo1 = dataClassDemo;
String str1 = dataClassDemo1.component1(), dd = dataClassDemo1.component2();
```
可以看到创建了一个变量，然后分别调用了component1和component2方法

深入：
既然data class帮我们做了这些操作，其实我们还可以自定义这些。
对于一个普通的类自然不具备解构声明的功能，但我们可以手动声明类似operator fun component1()的方法，来实现解构的功能
具体查看DataClassDemo类里的EntryDemo类，注意operator 修饰符
如果在解构声明中不需要某个变量，那么可以用下划线_取代其名称

# 字符串用$符号引用 反编译
就是普通的String去拼接

# kotlin接口默认实现
```
public interface InterfaceDemo {
  int getMax();
  
  @Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 3)
  public static final class DefaultImpls {
    public static int getMax(InterfaceDemo $this) {
      return 1;
    }
  }
}

```
看一遍反编译后的代码就豁然开朗，它其实是创建了一个Impls实现类。

然后使用到接口对象的时候,如果这个方法没有去重写过 那么默认就是会调用DefaultImpls.getMax(this);

# companion object

会在类里面插入对应的静态变量，同时会生成一个Companion类，该类会生成对这个变量的get set方法
所以这也就是companion object在java里调用的话需要先引出Companion类后再get/set

# const修饰符
这个只能用于object class或者是在class的companion object中
差别是如果用const修饰的话，那么不会生成get方法，调用的时候是直接拿这个常量（在java中就看得出他们的差别）

# ==与===
==相当于java里的equals方法，是比较值。 ===是相当于java里的==方法，是比较两个变量的地址是否相等

# inline 内联函数
跟拓展函数差不多，是在该类里新增一个final类型的方法，传入的参数就是内联对象


# 函数参数作为形参

kotlin里有一个Functions.kt的文件，里面定义了Function0、Function1等二十多个。后面的数字对应的它们的invoke方法接收的参数个数
当我们用函数作为形参时，会根据函数参数的个数去去创建一个类来实现对应的Function接口，继承了Lambda类（Lambda类本身也是实现Function接口的一个抽象类）
而这个类实现的invoke方法就是我们写的函数体代码。
实际上也就是相当于kotlin编译后把函数参数变成一个Function接口，然后自己再去定义一个类来实现这个Function接口并且把我们写的函数体代码放在invoke实现方法里
所以我们看到在java中去调用的话会提示我们去实现一个Function0之类的接口

# init
反编译后就是java的无参构造函数

# lateinit var
反编译后生成setter和getter方法，和普通变量不同的是getter方法会判断是否为null，为null则抛出UninitializedPropertyAccessExceptio的异常
个人注意：只能是var类型的，不然后面没办法给他赋值呀
# by lazy 
声明了的变量，会创建一个类实现function接口，然后把by lazy的代码放在invoke方法里
然后具体声明的变量是一个Lazy类型的变量，使用了LazyKt.lazy(DefineDemo$haha$2.INSTANCE);来赋值
然后生成了这个变量对应的get方法，会从这个lazy里拿到这个变量的实例。（lazy里判断没有实例的时候再创建 所以就是懒加载）
个人注意：by lazy一定是val类型的，不然要是可以随意赋值的haul，懒加载也没有意义了

# let run apply also
这些都是放在kotlin的Standard.kt文件里，其实就是一个内联函数，我们自己也可以写.
区别就是最后是return block(this)还是 block(this);return this