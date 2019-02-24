
配置modulea在lib组件的时候是release，在debug的时候是一个apk状态，我们可以单独运行侧四modulea工程

在modulea工程下面的src/main文件夹下新建个debug和release文件夹，用于配置两个状态下的AndroidManifest.xml

然后在debug下面的AndroidManifest.xml配置正常的apk的清单文件，也编写一个LoginActivity

然后重点是在modulea工程里的build.gradle加入sourceSets节点：
```
 sourceSets{
        main{
            //budug时和发布时不同的清单文件
            if(rootProject.ext.isApplication){
                manifest.srcFile 'src/main/debug/AndroidManifest.xml'
            }else{
                manifest.srcFile 'src/main/release/AndroidManifest.xml'
                //发布版的时候移除无用的资源
                java{
                    exclude "debug/**"
                }
            }
        }
    }
```

目前modulea工程里的build.gradle如下：
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
    sourceSets{
        main{
            if(rootProject.ext.isApplication){
                manifest.srcFile 'src/main/debug/AndroidManifest.xml'
            }else{
                manifest.srcFile 'src/main/release/AndroidManifest.xml'
                //发布版的时候移除无用的资源
                java{
                    exclude "debug/**"
                }
            }
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

这样的话，只要我们在config.gradle里面配置isApplication为true的话，我们就可以编译modulea成为apk来单独测试，而设置isApplication为false时作为一个lib组件去给主app使用
