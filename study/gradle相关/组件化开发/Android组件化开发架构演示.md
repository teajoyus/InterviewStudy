# 组件化前言

项目总会越来越庞大，代码总会越来越臃肿，模块与模块之间的耦合，功能模块相互依赖，在不断的迭代修改和新增功能后，造成项目越来越难维护。就好比如一锅粥，不断往里面扔进各种食材然后搅和在一块。

组件化的几个特点：

**组件分离**
如何把一个庞大的项目分离成若干个组件，这确实是一个架构上的难题。
一般组件会分离成业务组件和基础组件。基础组件始终被当做library供其它业务组件使用，它允许与其它组件有代码耦合。

**组件调试**
业务组件应该能独立运行，它并不依赖其余任何组件。当我们被分配到某个业务组件的开发时，我们应该能独立编译、运行调试这个组件（apk）。

**模块通信**
既然要组件之间能够独立运行，而肯定又会涉及到模块之间如何通信的问题，由于业务组件之间已经解耦出来，你没办法在A组件显示调用B组件的代码，所以在涉及到通信问题的的时候，我们都要利用一种隐式的方式来传递数据。

# 集中配置版本控制

组件化之后，我们必须统一好组件之间的各个版本号，比如targetSdkVersion这些。
在之前，可能我们在gradle的写法是这样的：
```
   defaultConfig {
        applicationId "com.example.demo"
        minSdkVersion 16
        targetSdkVersion 27
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters "armeabi-v7a", "x86"
        }
        multiDexEnabled true
    }
```

这样造成在整合组件之后造成版本不统一的问题，所以我们要有个配置文件来统一管理。
在项目根目录创建一个 config.gradle文件，然后我们可以定义各种变量如下：
```
ext{
    android=[
            compileSdkVersion:27,
            minSdkVersion:15,
            targetSdkVersion:27,
            versionCode :1,
            versionName :"1.0",
            applicationId:"com.example.interviewstudy",

    ]
    dependencies=[
            supportV7:'com.android.support:appcompat-v7:27.1.1',
            constraintLayout:'com.android.support.constraint:constraint-layout:1.1.3',
            recyclerview: 'com.android.support:recyclerview-v7:27.1.1',
    ]
}
```

然后在根目录下的build.gralde中把这个config.gradle配置文件引入进来,只需要在开头声明一句：
```
apply from :'config.gradle'
```

然后我们就可以使用config.gradle中定义的变量了：
```
 defaultConfig {
        applicationId rootProject.ext.android.applicationId
        minSdkVersion rootProject.ext.android.minSdkVersion
        targetSdkVersion rootProject.ext.android.targetSdkVersion
        versionCode rootProject.ext.android.versionCode
        versionName rootProject.ext.android.versionName
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [ moduleName : project.getName() ]
            }
        }
    }
```

# app与 lib模式切换
我们通过File>>New>>new Module创建一个Module，选择Library 如图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190225120932387.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xjX21pYW8=,size_16,color_FFFFFF,t_70)

这里我为了演示就把module的命名直接写成modulea了，创建好了之后可以在Project模式下看到这个module：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190225121145174.png)

我们在config.gradle中新增一个变量isApp，用来切换这个modulea是app模式还是lib模式
```
ext{
    //切换modulea工程是app还是lib
    isApp=false
    android=[
            compileSdkVersion:27,
            minSdkVersion:15,
            targetSdkVersion:27,
            versionCode :1,
            versionName :"1.0",
            applicationId:"com.example.interviewstudy",

    ]
    dependencies=[
            supportV7:'com.android.support:appcompat-v7:27.1.1',
            constraintLayout:'com.android.support.constraint:constraint-layout:1.1.3',
            recyclerview: 'com.android.support:recyclerview-v7:27.1.1',
    ]
}
```

然后我们刚创建modulea的build.gradle里面的各个版本号改成引用config.gradle的方式。

其中，由于我们需要切换app和lib模式，由isApp这个变量来控制：
```
if (rootProject.ext.isApp) {
    apply plugin: 'com.android.application'
} else {
    apply plugin: 'com.android.library'
}
```

并且修改为在 'com.android.application'时才有applicationId，不然**lib模式下声明时applicationId会报错**：

```
 defaultConfig {
        //app模式下声明时applicationId
        if(rootProject.ext.isApp){
            applicationId 'com.example.modulea'
        }

        minSdkVersion rootProject.ext.android.minSdkVersion
        targetSdkVersion rootProject.ext.android.targetSdkVersion
        versionCode rootProject.ext.android.versionCode
        versionName rootProject.ext.android.versionName
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
```

好了，这样子。我们让isApp设置为true，发现modulea工程图标变成了app模式：

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019022513464562.png)

并且运行配置里多了个选项：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019022513500534.png)

那我们现在虽然这只成了app模式。但不能运行，看这个运行项也是带了叉号的，提示没有找到default Activity

我们要单独运行的时候，肯定需要配置一份AndroidManifest.xml文件，声明application和要launcher的activity，而在作为lib模式的时候，并不能声明自己的application模式和默认的launcher，否则在**整合的时候会出现AndroidManifest Marge faild的提示**，所以又要声明另外一个AndroidManifest.xml文件

那么能不能让gradle自动帮我们选择运行哪份AndroidManifest.xml文件呢？答案是可以的。
我们在modulea的src/main里面增加个debug文件夹，然后拷贝一份进去：AndroidManifest.xml

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190225140843954.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xjX21pYW8=,size_16,color_FFFFFF,t_70)

然后在build.gradle中利用sourceSets（注意sourceSets是在android节点里面）来声明：
```
 sourceSets{
        main{
            if(rootProject.ext.isApp){
                manifest.srcFile 'src/main/debug/AndroidManifest.xml'
            }else{
                manifest.srcFile 'src/main/AndroidManifest.xml'
                //发布版的时候移除无用的资源
                java{
                    exclude "debug/**"
                }
            }
        }
    }
```
注意就可以控制当isApp为true，则加载的是src/main/debug/AndroidManifest.xml。但要集成到主app去的时候则使用src/main/AndroidManifest.xml

我们创建个DemoActivity吧

然后我们开始编写src/main/debug/AndroidManifest.xml的内容：
```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.modulea">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".DemoActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```
注意，默认我们在new 出一个Module的时候并不像new Project会给我们一份默认的图标，样式、主题这些，所以这里引用android:icon="@mipmap/ic_launcher"这些资源的时候会提示找不到，请读者自己拷贝一下吧

然后我们再编写src/main/AndroidManifest.xml的内容：（注意与编写src/main/debug/AndroidManifest.xml的内容：的差异）
```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.base">

    <application >
        <activity
            android:name=".DemoActivity"
            />

    </application>

</manifest>
```
可以看到application节点并没有任何的属性声明，因为这个往往都是放在主app里面的，**一个项目不可能每个module都配置各自的application配置，这样会造成合并时的冲突**。

好了，基于以上。我们就可以轻松的通过修改isApp变量，来达到切换的效果。在这种模式下，我们就可以通过单独运行这个组件用于调试

# 整合lib进主app里
在调试好module之后，需要组装到主app里面，只需要在主app的build.gradle添加对这个module的依赖即可：

```
  if(!rootProject.ext.isApp){
        //引用modulea 组件
        implementation project(":modulea")
    }else{
        ...
    }
```
注意，我们在添加的时候同样判断了 isApp，这是因为当modulea设置为app模式的时候，那么这里就会报错。所以我们根据这个变量修改一下，只有当isApp为false才引入进来

## 解决多个Application问题
值得注意的是，通常来讲，我们的组件可能会进行一些第三方SDK的初始化，放在了Application里面。而项目中只能启动一个Application类
解决这种冲突也有几种方法，第一种不推荐的写法就是把组件的SDK初始化方法的代码拷贝到主App中来，这样有点违和感。因为对主app来说，我不一定出版本都需要使用这个组件

另外一种比较好的方法是，**在基础组件里面编写一个记录Application生命周期的接口**，各个组件只需要实现这个接口就可以完成对Application生命周期的调用
另外基础组件也可以提供一个记录了实现这些接口的类，在主app里面反射调用出来。这里我们就不演示了


## 解决module之间的资源冲突

其次，如果没有经过规范的资源命名的话，那么很可能整合后出现出现module之间的资源冲突，比如有两个组件都声明了一个叫做activity_main.xml
那整合进来之后，要使用哪个呢？

所以在组件分离之初，就应该有一个命名的规范，比如所有的资源都必须按照组件名开头，这样的话就极大的避免了资源冲突了

尽管团队有这个规范，但是不小心的同学还是没按照这个来命名怎么办?
可以在build.gradle里面添加： resourcePrefix 'modulea_'  具体位置如下：
```
 defaultConfig {
         if(rootProject.ext.isApplication){
             applicationId 'com.example.modulea'
         }

         minSdkVersion rootProject.ext.android.minSdkVersion
         targetSdkVersion rootProject.ext.android.targetSdkVersion
         versionCode rootProject.ext.android.versionCode
         versionName rootProject.ext.android.versionName
         testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
         //强制让资源名称以组件名开头
         resourcePrefix 'modulea_'
     }
```

这样后，AS会提示我们那些没按照modulea_开头命名的资源，强制让我们修改。

**为了后期不产生资源命名冲突，这一句 resourcePrefix 'modulea_'显然是必不可少的**

# 组件之间的跳转与通信方式

既然业务组件之间已经完全解耦出来，每个业务组件都没办法显示调用另外一个业务组件的代码。那么我们组件之间要通信怎么办呢？

## Activity路由跳转
比如最常见的Activity跳转。

如此之下，既然不能直接调用目标Activity类，我们只能这样了：
```
  try{
            Class clazz = Class.forName("com.example.modulea.DemoActivity");
            Intent intent = new Intent(this,clazz);
            startActivity(intent);
        }catch (ClassNotFoundException e){

        }
```
这样也不是不可以，但是由于硬编码在了各个组件之间，看似组件分离，其实还是避免了出现一个耦合状态，也就是组件中使用的字符串来表示类路径，对于另外一个组件来说 并不总是这个类，如果其他组件换了Activity的话，则另外一个组件也必须得换

所以一般我们也不会这么去写，在市面上。由于组件化开发的热门，所以诞生了一个叫做Activity路由的东西

框架就是类似于一个路由器，所有的activity在里面注册成路由表，然后通过查表的方式来做跳转

至于页面路由，市面上非常多的框架，比如阿里的Arouter，美团的WMRouter，聚美Router、等等
还有个人维护的github上面也挺火的ActivtityRouter（github：https://github.com/mzule/ActivityRouter）

这里演示下，就用了ActivtityRouter这个吧，具体的使用方法在README.md里面也介绍了，这里就不多说了


根据它的教程提示，我们导入依赖：
```
//ActivityRouter 注意要用api才能被其他module使用到，用implementation只在本module内部能被的调用，在外部隐藏
    api 'com.github.mzule.activityrouter:activityrouter:' + rootProject.ext.dependencies.activityrouter
    annotationProcessor 'com.github.mzule.activityrouter:compiler:' + rootProject.ext.dependencies.annotationActivityrouter
```

对了，忘了说，像上面的页面路由的依赖，肯定是放到基础组件中去给每个业务组件进行使用。

（这里我们需要创建一个新的module，叫做basemodule。用来提供一些基础性（包括比如BaseActivity、BaseFragment）的东西给其他组件使用）
凡事共有的东西，不涉及业务逻辑的，我们都尽量抽离出来放到基础组件中。

并且犹如上面的 注释，需要用api命令才能被其他module使用到，用implementation只在本module内部能被调用，在外部会隐藏

然后在业务组件的AndroidManifest.xml中配置：
```
<activity
    android:name="com.github.mzule.activityrouter.router.RouterActivity"
    android:theme="@android:style/Theme.NoDisplay">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="demo" /><!--改成自己的scheme-->
    </intent-filter>
</activity>
```

在需要配置的Activity上添加注解
```
@Router("router_demo")
public class DemoActivity extends BaseActivity {
	...
}
```

最后：
```
Routers.open(this,Uri.parse("demo://router_demo"));
```
即可跳转activity了，这种跳转方式有点像web页面的url，使得跟具体的Activity分离开来
ActivityRouter还有更多玩法，比如传值、回调等等，请参考github介绍，还有其他的路由框架也可以了解一下

## 组件之间的数据通信

像上面只是说了跳转方面，而往往我们在各个组件之间还需要有一些数据通信，要怎么传递呢。

这里方法也有很多，不唯一。个人觉得重点在于选取一个避免耦合的方法即可

1、利用基础组件为桥梁，用面向接口的方式实现通信

2、基础组件维护一些全局共享数据，其他的业务组件操作全部来自于对基础组件的调用。比如用户信息相关，其他业务组件总避免不了需要使用到。这个需要权衡好什么数据适合下沉到基础组件里，因为从某种程度上讲，这种方式都是跟基础组件有强耦合的，后期想要分离开来就不是一件小事了。

3、利用 EventBus通信也是一种方式，可以通过在基础组件中定义各种事件类，达到一个通信的效果

4、基于观察者模式的设计思想，让另外的业务组件做出反应。

# 总结
以上演示的就是一种最基础的组件化开发架构，当然我们实际的项目上往往会有更多的规范和约束，和独特的组件化架构。

总结下几个要点：
利用config.gradle统一集中管理好各个组件的版本号，便于管理。
组件化分为基础组件和业务组件。对于一些共用的第三方依赖、还有Base类都可以放到基础组件里面。
基础组件允许跟业务组件强耦合，事实上业务组件本来就需要基于基础组件来调用一些基础包和第三方库
为了避免整合冲突，组件需要有合格的命名规范
为了避免整合冲突，要注意AndroidManifest.xml的写法和Application的封装
可以单独建立某个路径下的AndroidManifest.xml文件，通过声明sourseSet来区分，这样我们就可以写一份独立运行的AndroidManifest.xml文件
选择好一套路由跳转框架，所有的组件通过路由的方式来跳转
注意通信方式要避免耦合依赖，通过对基础组件作为桥梁，或者使用诸如EventBus的方式等等



