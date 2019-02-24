主app想要引用lib组件，只需要在dependencies里面依赖进来即可：
```
   //引用modulea 组件
     if(!rootProject.ext.isApplication){
            //引用modulea 组件
            implementation project(":modulea")
        }
```
在引用的时候判断一下modulea是不是application状态，是的话则不导入进来，否则当modulea是application状态的时候这里会报错找不到lib

特别注意modulea变成lib后没有Application，比如需要初始化一些第三方SDK的时候
那么只能把modulea上第三方SDK的初始化代码放到主app的Application中来

然后我们把config.gradle中切换isApplication为false，让modulea变成组件，然后编译主app即可
