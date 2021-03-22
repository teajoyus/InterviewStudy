博客：https://blog.csdn.net/yanbober/article/details/49314255

gradle的所以任务：
菜单栏上的View-》Tool Window -》Gradle 之后可以列出所有task

gradle打印日志：
```
println 'This is executed during the initialization phase.'+new File(settingsDir.getParent()).getParent()

def lib_modules = new File(settingsDir.getParent()).getParent()
println lib_modules
```
使用println xx即可

然后在下面的Build窗口:

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190312144640614.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xjX21pYW8=,size_16,color_FFFFFF,t_70)



自己写了个settings.gradle
```

def lib_modules_dir = new File(settingsDir.getParent()).getParent() + '\\lib_modules'

println lib_modules_dir
def lib_modules = [
        'JoinUILibrary',
        'JoinProtocolLibrary',
        'AndroidTelevision',
        'iPanelTVLibrary',
        'iPanelICLibrary'
]

for (def i = 0; i<lib_modules.size();i++){

   include ':'+lib_modules[i]
}

for (def i = 0; i<lib_modules.size();i++){

    project(':'+lib_modules[i]).projectDir = new File(lib_modules_dir, '../'+lib_modules[i])
}
```