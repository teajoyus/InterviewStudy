热重载 hot reload 
---- 
不会改变状态，那些全局变量和静态变量并不会因为热重载而被重新修改
比如你声明了一个final int a = 1; 运行之后 代码又修改成a =2再热重载，这个时候读取的a还是1

一个问题（自己还不清楚）：

比如声明了个 const a = 1; 运行之后又改成： final a = 2;热重载发现a还是1
而反过来先定义final a = 1，再改成const a = 2热重载就能变成2




断言调试
----
Flutter推荐使用断言来调试，当要生成发布版本的apk时用：flutter run --release （个人认为发布版的话assert语句不会执行）




debugDumpApp()   &  debugDumpRenderTree()
---
调用这个方法可以输出widget树（有没有办法能给个视图模式）

debugDumpRenderTree方法可以更清楚的知道每个widget的状态


调试变量
---
https://flutterchina.club/debugging/

- debugPaintSizeEnabled 被设置为true后，就是有些边框颜色提醒

- debugPaintBaselinesEnabled

- debugPaintPointersEnabled


Dart 两种运行模式
---
- 检查模式（checked）：进行类型检查，如果发现实际类型与声明或期望的类型不匹配就报错。
- 生产模式（production）：不进行类型检查，忽略声明的类型信息，忽略 assert 语句。


