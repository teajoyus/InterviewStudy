如果不同组件之间的资源名字出现重复，则引用到主app的时候会报错

解决方法是避免组件之间出现重复的资源名字，
可以在build.gradle中加入一个resourcePrefix来强制让资源名称以组件名开头
resourcePrefix放在defaultConfig节点中：
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

这样子之后，所有的资源都会被强制以这个名称开头，如果没有则会编译报错：
```
Resource named 'app_name' does not start with the project's resource prefix 'modulea_'; rename to 'modulea_app_naame'?
```
这时候就要手动改为modulea_开头了，所以建议在一开始建立组件的时候就是用这个resourcePrefix属性，按照这个来命名就可以避免资源重复造成冲突
