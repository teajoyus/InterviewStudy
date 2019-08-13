
var &  val
----
var 是可变的， val是只读的，相当于final

? 和 !!
----
对象调用方法时，在对象后面加问号表示可为空（也就是不会抛出空指针的异常）
而用两个感叹号的话则跟java一样，为空就会抛出异常


匿名内部类
----
在方法中如果有匿名内部类对象做为参数的话，那么这个参数的格式是：

object: className{
}

比如setOnclickListener，那么就是：

```
   btn.setOnClickListener(
                object :View.OnClickListener{
                    override fun onClick(v: View?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                }
        )

```


Any类
---
所有类的基类，对应了java的Object类