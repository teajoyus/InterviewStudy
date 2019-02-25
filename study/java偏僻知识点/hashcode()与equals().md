博客：https://blog.csdn.net/qq_38182963/article/details/78940047

我们知道，HashSet是不不存在重复元素的。
而下面的例子，却存在重复：
```
import java.util.HashSet;

public class Test {

	public static class KeyDemo{
		public int id;
		public String name;
		@Override
		public String toString() {
			return "KeyDemo [id=" + id + ", name=" + name + "]";
		}

	}

	public static void main(String[] args) {
			HashSet<KeyDemo> set = new HashSet<>();
			KeyDemo key = new KeyDemo();
			key.id = 20;
			key.name = "jack";
			set.add(key);
			KeyDemo key2 = new KeyDemo();
			key2.id = 20;
			key2.name = "jack";
			set.add(key2);
			System.out.println(set);

	}
}

```
运行结果：
```
[KeyDemo [id=20, name=jack], KeyDemo [id=20, name=jack]]
```


HashSet内部是由HashMap构成的，在调用add方法的时候实际上是调用HashMap的put方法，然后存的value是一个final的object

出现上面的原因就是因为KeyDemo没有重写hashcode的计算，导致equals计算有误


注：java中<<和<<<区别在于有无符号的计算，对于正数来说这两个没区别，对于负数来说
比如-2<<<



