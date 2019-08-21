博客地址：https://www.cnblogs.com/fery/p/4709841.html


有两个特别注意的结论：
1、finally是在return后面的表达式运算后执行的（此时并没有返回运算后的值，而是先把要返回的值保存起来，不管finally中的代码怎么样，返回的值都不会改变，任然是之前保存的值），所以函数返回值是在finally执行前确定的；
比如返回了sb.toString() 这个时候再去修改sb，由于已经sb.toString()了，返回值已经被保存起来了是一个字符串形式，finally里面对sb添加就无效了
而如果返回的是StringBuffer这个对象就不同了，返回值是这个对象，那么finally修改这个对象的值后，返回值还是记录了finally修改后的


2、finally中有return的话会代替try catch里面的return，并且finally有return的话 外面就不能写return语句了，因为执行不到那里去



测试代码  一看就懂了：

```
public class FinallyDemo {

	public static void main(String[] args) {
		System.out.println(test1());
		System.out.println(test2());
		System.out.println(test3().toString());
		System.out.println(test4().toString());


	}

	private static int test1() {
		int i = 10;
		try {
			return i;
		} catch (Exception e) {
			// TODO: handle exception

		}finally {
			i = 20;
		}
		return 0;
	}

	private static String test2() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("123");
			return sb.toString();
		} catch (Exception e) {
			// TODO: handle exception

		}finally {
			sb.append("456");
		}
		return null;
	}
	private static StringBuffer test3() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("123");
			return sb;
		} catch (Exception e) {
			// TODO: handle exception

		}finally {
			sb.append("456");
		}
		return null;
	}
	private static StringBuffer test4() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("123");
			return sb;
		} catch (Exception e) {
			// TODO: handle exception

		}finally {
			sb.append("456");
			StringBuffer sb2 = new StringBuffer();
			sb2.append("111");
			return sb2;
		}
	}
}
```

运行结果：
```
10
123
123456
111
```
