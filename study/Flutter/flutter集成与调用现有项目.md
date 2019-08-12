其实可以试下直接在AS里面new module，里面有Flutter Plugger


参考的博文：

https://juejin.im/post/5d4002b4f265da03925a2165 


主要的步骤就是

- AS关联好flutter和Dart的SDK路径
- 在根目录下命令创建flutter项目
- cd到my_flutter/.android ，执行：gradlew flutter:assembleDebug （暂时不知道这个是干嘛用）
- 在settings.gradle文件里编写脚本执行include_flutter.groovy这个文件
- 在项目的build.gradle中引入这个module
- 注意build.grade的版本必须引用JDK1.8以上版本
- 使用Flutter.createView方法即可调用flutter，特别注意的是第而个参数LifeCycle参数可以直接getLifeCycle()，第三个参数是传入dart文件的入口