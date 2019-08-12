
# getContentResolver()
```
private ContextImpl( ... ) {
    ...
    mContentResolver = new ApplicationContentResolver(this, mainThread, user);
    ...
}
 @Override
    public ContentResolver getContentResolver() {
        return mBase.getContentResolver();
    }
   @Override
  public ContentResolver getContentResolver() {
      // We need to return the real resolver so that MailEngine.makeRight can get to the
      // subscribed feeds provider. TODO: mock out subscribed feeds too.
      return mResolver;
  }
```

当我们在Activity里面调用getContentResolver()实际拿到的是ContentResolver的派生类ApplicationContentResolver这个对象，并且可以确定，每个ContextImpl都持有一个ApplicationContentResolver对象
所以不同的Activity在调用getContentResolver()获取到的对象是不同的。

```
private static final class ApplicationContentResolver extends ContentResolver {

}
```

它在ContextImpl内部被定义成了一私有静态内部类，也被final修饰了。我们直接看下它的query方法

