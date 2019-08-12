其实可以试下直接在AS里面new module，里面有Flutter Plugger


参考的博文：

https://juejin.im/post/5d4002b4f265da03925a2165 



# Flutter集成到现有项目

主要的步骤就是

- AS关联好flutter和Dart的SDK路径
- 在根目录下命令创建flutter项目
- cd到my_flutter/.android ，执行：gradlew flutter:assembleDebug （暂时不知道这个是干嘛用）
- 在settings.gradle文件里编写脚本执行include_flutter.groovy这个文件
- 在项目的build.gradle中引入这个module
- 注意build.grade的版本必须引用JDK1.8以上版本
- 使用Flutter.createView方法即可调用flutter，特别注意的是第而个参数LifeCycle参数可以直接getLifeCycle()，第三个参数是传入dart文件的入口


遇到的问题：
Gradle sync failed: Already disposed: Module: 'Flutter-flutter' 

大概是因为自己新建了一个然后又强制删掉，又在新建导致出现这个错误。
自己的解决方法：根据settings.config指定的脚本去include_flutter.groovy里面修改flutter为my_flutter（也就是修改项目名）


# Flutter调用Android代码方法

主要的步骤只有两步：

- 在Activity利用MethodChannel设置方法调用的回调

    - 在Activity中通过创建MethodChannel对象，传入flutterView和自己定义的一个标识字符串
    - 在MethodChanel对象调用setMethodCallHandler方法，当Flutter调用方法时会回调，可通过methodCall.method来指定调用具体的方法
    
- 在Flutter中利用MethodChannel对象来调用java方法
   - 创建MethodChannel对象，传入刚才定义的一个标识字符串
   - 只需要 ： await platform.invokeMethod("xxxxx");就可以通过xxx字符串来调用对应的java的方法
  
