博客：https://blog.csdn.net/qq_27570955/article/details/55046934


设备配置的更改会导致Acitivity销毁重建，而设置android:configChanges则避免Activity销毁重建，系统会回调onConfigurationChanged方法。