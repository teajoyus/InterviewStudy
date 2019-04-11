首先看一下跳转时生命周期
A跳转到B的时候：

- A onPause
- B onWindowFocusChanged(false)
- B onCreate
- B onStart
- B onResume
- B onAttachedToWindow
- B onWindowFocusChanged(true)
- A onStop

B按返回到A
- B onPause
- A onRestart
- A onStart
- A onResume
- A onWindowFocusChanged(true)
- B onStop
- B onDestroy

正常来讲把setResult方法放在finish方法调用之前，这时候B退出到A的生命周期：
- B onPause
- A onActivityResult
- A onRestart
- A onStart
- A onResume
- A onWindowFocusChanged(true)
- B onStop
- B onDestroy


可以发现，A的onActivityResult方法是在B的onPause调用之后，在A的onRestart之前调用的。

我们尝试在B的onPause方法里面增加setResult方法，代码放在super.onPause()之后
然后看A的onActivityResult方法能不能拿到数据。

实验打印出intent = null

那么放在放在super.onPause()之前呢？

也是打印出intent = null

所以一定要在finish之前了

因为setResult方法：
```
    public final void setResult(int resultCode, Intent data) {
        synchronized (this) {
            mResultCode = resultCode;
            mResultData = data;
        }
    }
```
finish方法：
```
   private void finish(int finishTask) {
        if (mParent == null) {
            int resultCode;
            Intent resultData;
            synchronized (this) {
                resultCode = mResultCode;
                resultData = mResultData;
            }
        ...
    }
```

可以看得出来，setReuslt后是在finish方法里面读取setResult的值，所以在onPause环节，已经过了读取的时候了。