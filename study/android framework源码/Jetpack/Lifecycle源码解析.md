# Lifecycle过程涉及到的类： - LifecycleOwner
 - LifecycleRegistry（extends Lifecycle）
 - ObserverWithState
 - LifecycleObserver（衍生：FullLifecycleObserver、FullLifecycleObserverAdapter、LifecycleEventObserver、GenericLifecycleObserver）
 - Lifecycling
 - ReportFragment

## LifecycleOwner
在ComponentActivity里（AndroidX）会实现LifecycleOwner接口。
包括fragment Dialog、Services这些具有生命周期的类都可以实现这个接口
这个接口就是得到一个Lifecycle对象，实际上就是LifecycleRegistry的实例。

## LifecycleRegistry
LifecycleOwner接口的方法返回的实例对象，用来转发生命周期事件的
它内部持有LifecycleOwner对象的弱引用。
当我们需要添加一个Observer的时候，在addObserver方法里
会把LifecycleObserver包装成一个ObserverWithState对象，之所以这么做是因为LifecycleObserver有几个不同的实现，
目前是有三种：
 - FullLifecycleObserver（activity全周期）提供一系列周期方法
 - GenericLifecycleObserver通过注解的方式（废弃）
 - LifecycleEventObserver通过onStateChange的方式

## ReportFragment
在ComponentActivity里的onCreate会执行：
```
ReportFragment.injectIfNeededIn(this);
```
看ReportFragment内部，原来是用一个Fragment来跟Activity的生命周期进行同步，在fragemnt的各个生命周期里调用activity的handleLifecycleEvent方法
实际上就是LifecycleRegistry的handleLifecycleEvent方法
当 API 等级为 29+ 时，我们可以直接向 android.app.Activity 注册生命周期回调:activity.registerActivityLifecycleCallbacks
而就没有用到这个ReportFragment
所以在SDK<29时，默认的第一个fragment就是ReportFragment，要记得它是在getFragmentManager里的，不是在getSuportFragmentManager的

## Lifecycling
由于需要兼顾几种不同接口的obsever，所以这个会把不同的observer适配起来