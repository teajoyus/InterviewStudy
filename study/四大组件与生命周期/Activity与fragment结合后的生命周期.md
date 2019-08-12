参考博客：https://www.jianshu.com/p/6fb2936d2d3c
参考博客：https://www.cnblogs.com/lrcaoxiang/p/9288798.html

注意看Actvitiy各个生命周期的时候，frgament对应的生命周期

xml布局有fragment的话，
在activity的onCreated的时候

fragment完成了：
- onAttach()
- onCreate()
- onCreateView()
- onActivityCreated()


然后onStart、onResume、onPause、OnStop时在FragmentActivity里面


