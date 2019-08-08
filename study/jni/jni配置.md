1、下载NDK

在线：右键项目打开module settings，在sdk下的ndk配置这里可以点击download

离线：自己下载zip包（我的做法是用谷歌的链接到迅雷下载，比较快）
然后到sdk目录下新建个ndk-bundle文件夹，解压这个zip包。

配置快捷方式：

头文件生成命令 - javaH  
---------
在Settings的Tools->extent tools里面新建个javaH，所属分组可以命名成NDK

然后填写：

program：C:\Program Files\Java\jdk1.8.0_91\bin\javah.exe

Arugments：-v -classpath $ModuleFileDir$\src\main\java  -d $ModuleFileDir$\src\main\jni  $FileClass$

Working Diectory ： $ModuleFileDir$\src


生成so命令 - ndk-build
-------
在Settings的Tools->extent tools里面新建个ndk-build，所属分组可以命名成NDK

然后填写：

program：E:\sdk\ndk-bundle\ndk-build.cmd

Arugments：空

Working Diectory ： $ModuleFileDir$\src\main\jni


这样就配置好了。 右键类就可以选择NDK，用这两个来编译