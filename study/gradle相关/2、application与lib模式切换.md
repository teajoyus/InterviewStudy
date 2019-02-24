首先，新建一个modulea工程

把我们主工程的build.gradle内容复制过来（注意已经是用了config.gradle集中管理版本了）

可以在config.gradle中加个字段判断modulea工程是appliction状态还是lib状态
config.gradle改成了：
```
ext{
  //切换modulea工程是apk还是lib
    isApplication=false
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
            rxjava:'2.2.6',
            rxandroid:'2.1.0',
    ]
}
```

在build.gradle中application和lib的区别主要在于：
1、application是用apply plugin: 'com.android.application'
而lib是用apply plugin: 'com.android.library'

2、lib的话不能申明applicationId

接下来modulea工程把build.gradle改成：
```
if(rootProject.ext.isApplication){
    apply plugin: 'com.android.application'
}else{
    apply plugin: 'com.android.library'
}



android {
    compileSdkVersion rootProject.ext.android.compileSdkVersion
    defaultConfig {
        if(rootProject.ext.isApplication){
            applicationId 'com.example.modulea'
        }

        minSdkVersion rootProject.ext.android.minSdkVersion
        targetSdkVersion rootProject.ext.android.targetSdkVersion
        versionCode rootProject.ext.android.versionCode
        versionName rootProject.ext.android.versionName
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation rootProject.ext.dependencies.supportV7
    implementation  rootProject.ext.dependencies.constraintLayout
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    implementation rootProject.ext.dependencies.recyclerview
    implementation 'io.reactivex.rxjava2:rxjava:'+rootProject.ext.dependencies.rxjava
    implementation 'io.reactivex.rxjava2:rxandroid:'+rootProject.ext.dependencies.rxandroid
}

```

这样之后我们就可以通过修改isApplication变量来切换modulea工程的状态了