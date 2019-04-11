# 前言
本篇讲解的是LayoutInflater的装载过程，其中会涉及到include、merge、ViewStub标签的源码解析。
我们对LayoutInflater的使用是再熟悉不过了，日常操作都是先调用from方法然后调用inflate方法。而对LayoutInflater是如何把一个xml布局文件加载到界面上的过程可能不是很了解。

# LayoutInflater的创建过程

说起LayoutInflater，我们最熟悉的写法是：
```
LayoutInflater.from(context).inflate(R.layout.xx,null);

```

LayoutInflater的from方法源码是：
```
public static LayoutInflater from(Context context) {
        LayoutInflater LayoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (LayoutInflater == null) {
            throw new AssertionError("LayoutInflater not found.");
        }
        return LayoutInflater;
    }
```
可以看出实际上是调用context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

而LayoutInflater本身是一个抽象类，它的具体实现类是在ContextImpl的getSystemService方法中被实例化。

通常在getSystemService方法得到的Object都是来自于SystemServiceRegistry中的static代码块中去registerService的：
```
   registerService(Context.LAYOUT_INFLATER_SERVICE, LayoutInflater.class,
                new CachedServiceFetcher<LayoutInflater>() {
            @Override
            public LayoutInflater createService(ContextImpl ctx) {
                return new PhoneLayoutInflater(ctx.getOuterContext());
            }});
```

由此可以知道，我们用的LayoutInflater实例都是来自于PhoneLayoutInflater。

但是一看PhoneLayoutInflater并没有几行代码，由此判断基本逻辑都是在LayoutInflater这个抽象类中，我们只要关注LayoutInflater就行了

# LayoutInflater的装载过程

那它是怎么装载布局的呢？我们调用inflate方法，经过重载后会到它是三个参数的重载方法:

```
public View inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot) {
        final Resources res = getContext().getResources();
        if (DEBUG) {
            Log.d(TAG, "INFLATING from resource: \"" + res.getResourceName(resource) + "\" ("
                    + Integer.toHexString(resource) + ")");
        }
        //载入布局文件
        final XmlResourceParser parser = res.getLayout(resource);
        try {
            return inflate(parser, root, attachToRoot);
        } finally {
            parser.close();
        }
    }
```

经过这个方法把xml文件载入进来，XmlResourceParser继承了XmlPullParser，可以看出解析xml布局是基于Pull解析的（不了解XML几个解析方式的最好先自行百度下），最终重载调用到这个inflate方法（只举例部分重要代码）：

```
  public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
    ...
    final Context inflaterContext = mContext;
    //XmlResourceParser也是实现了AttributeSet接口，转成AttributeSet后相当于对属性的一种封装，方便属性的获取。
    final AttributeSet attrs = Xml.asAttributeSet(parser);
    View result = root;
    ...
    final String name = parser.getName();
    //根标签是否是merge标签
    if (TAG_MERGE.equals(name)) {
    //merge标签的布局必须传入父view，并且attachToRoot=true，不然xml中零零散散的view不知道要被添加到哪里
    if (root == null || !attachToRoot) {
        throw new InflateException("<merge /> can be used only with a valid "
                + "ViewGroup root and attachToRoot=true");
    }
    //解析带merge标签的布局，注意最后一个参数是传递false，传的Context是inflaterContext，也就是LayoutInflater的mConext，实际上就是LayoutInflater.from(context)时传的context
    rInflate(parser, root, inflaterContext, attrs, false);
    } else{
        //创建布局的根结点，createViewFromTag方法里会解析出对应的类名，然后调用createView方法反射创建出来
         final View temp = createViewFromTag(root, name, inflaterContext, attrs);
         //生成根布局的布局参数对象
         params = root.generateLayoutParams(attrs);
         //解析子View布局，注意最后一个参数是传递true
          rInflateChildren(parser, temp, attrs, true);
         //如果我们传入了一个parent View，那么就把根结点附着到这个parent上
        if (root != null && attachToRoot) {
               root.addView(temp, params);
        }
    }
    ...
  }

```

接下来rInflateChildren，用来装载子布局的，实际是调用了rInflate方法：
```
  final void rInflateChildren(XmlPullParser parser, View parent, AttributeSet attrs,
            boolean finishInflate) throws XmlPullParserException, IOException {
        //注意这里的context有区别于merge标签时传的context了
        rInflate(parser, parent, parent.getContext(), attrs, finishInflate);
    }
```

到这里有个重要的发现，就是在inflate中判断了是不是merge布局，最终都会走向rInflate方法，

不同的是 如果是merge布局，那么rInflate方法中的第二个参数传递的是root，也就是来自我们调用的inflate(R.layout.xx,root,true);，然后最后一个参数finishInflate是false，而且context是来自于from(context)

如果不是merge布局。那么的第二个参数传递的是temp，也就是调用createViewFromTag后解析出根布局的view，最后一个参数finishInflate传的是true，Context传的是View的getContext()


那么rInflate方法如下：
```
    void rInflate(XmlPullParser parser, View parent, Context context,
            AttributeSet attrs, boolean finishInflate) throws XmlPullParserException, IOException {
        //获取xml标签的层级
        final int depth = parser.getDepth();
        int type;
        //pull解析结束条件
        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            final String name = parser.getName();
            if (TAG_REQUEST_FOCUS.equals(name)) {
                parseRequestFocus(parser, parent);
            } else if (TAG_TAG.equals(name)) {
                parseViewTag(parser, parent, attrs);
            } else if (TAG_INCLUDE.equals(name)) {
                if (parser.getDepth() == 0) {
                    throw new InflateException("<include /> cannot be the root element");
                }
                parseInclude(parser, context, parent, attrs);
            } else if (TAG_MERGE.equals(name)) {
                //merge标签只能作为xml的根标签，也就是在另外一个文件中
                throw new InflateException("<merge /> must be the root element");
            } else {
                //递归解析创建View or ViewGroup
                final View view = createViewFromTag(parent, name, context, attrs);
                final ViewGroup viewGroup = (ViewGroup) parent;
                final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
                rInflateChildren(parser, view, attrs, true);
                viewGroup.addView(view, params);
            }
        }
        //xml装载完成后每个view都会收到onFinishInflate方法的回调
        if (finishInflate) {
            parent.onFinishInflate();
        }
    }
```

我们关注else分支的代码，这里面也就是一个递归，不断的调用createViewFromTag反射创建出View，然后把创建的View添加到传进来的Parent View上，
在前面我们说的如果是merge布局的话则需要传一个root，因为merge布局文件中是没有父布局的，需要有个父布局可以来装载View

而如果不是merge的话，则传进来的是xml布局的根布局view。

## include标签解析
在里面中，判断遇到include标签，则判断parser.getDepth() >0 就调用parseInclude方法

parser.getDepth()方法说明一下，指的也就是标签的层级(深度)
比如有下面的XML片段：
```
<root>
    <parent>
            <child>
            <child/>
    <parent/>
<root/>
```
那么root就是第一层 parent就是第二层 child就是第三层

这里代码判断parser.getDepth()==0要抛出异常，说明 include标签是不能作为XML中的根标签的，换一种意思就是，include标签外面必须有包含的ViewGroup（当然这里还没体现出来一定得是ViewGroup，但在parseInclude方法里就判断了这个）

不过这里有点废话了 ，我们使用include一定是嵌在某个父布局里，要是XML要想就单单使用include的内容的话，那还不如直接引用那个xml文件了 何必新增个xml文件

在parseInclude方法与inflate方法有很大类似的逻辑，因为本身include标签也是去加载另外一个xml布局

parseInclude方法中比较长，我们挑几个重要的流程代码：
```
   private void parseInclude(XmlPullParser parser, Context context, View parent,
            AttributeSet attrs) throws XmlPullParserException, IOException {

     //当然 能用inclde标签的一定是ViewGroup
     if (parent instanceof ViewGroup) {
     ...
     int layout = attrs.getAttributeResourceValue(null, ATTR_LAYOUT, 0);
       if (layout == 0) {
            final String value = attrs.getAttributeValue(null, ATTR_LAYOUT);
            //如果include中没有带layout标签，则抛出异常。没有layout标签的include是没有灵魂的，连肉体都没有
            if (value == null || value.length() <= 0) {
                throw new InflateException("You must specify a layout in the"
                        + " include tag: <include layout=\"@layout/layoutID\" />");
            }
               ...
     }else{
        //跟inflate方法类似，会吧layout读取进来
         final XmlResourceParser childParser = context.getResources().getLayout(layout);

        //剩下的代码与inflate过程有惊人的相似，这里不再说明
          ...
     }

     ｝
 ｝
```


另外注意rInflate方法最下面的：
```
 if (finishInflate) {
            parent.onFinishInflate();
        }
```

如果finishInflate为true，则会调用View的onFinishInflate方法。

因为rInflate方法是递归调用的，**由此得知onFinishInflate方法的调用时机就是：在整个XML布局解析完毕之后会回调每个View的onFinishInflate方法**

通常我们自定义View的时候时就可以用到onFinishInflate方法来监听布局是否已经加载完成，以确保我们可以读取到View和属性，比如在XML使用自定义ViewGroup时，它的getChildAt()、findViewById()一定是在onFinishInflate方法之后使用


## merge标签解析

merge标签解析并没有太多与其他xml不同的地方。

上面代码注释说了如果是带merge标签的xml则在进入rInflate方法时传了finishInflate为false，这不是不让merge标签里的View回调onFinishInflate
因为递归调用rInflateChildren时都一样是传true

这里传的false只是针对于载入merge标签的父布局，也就是root。为什么要为false呢？

因为使用merge标签有两种情况：

第一种是我们自定义组合视图时，比如LinearLayout。在里面进行inflate时一般会采用merge标签的xml，因为本身就是一个父布局了

另外一种就是XML中与include标签组合使用，比如我们定义一个merge标签的视图（注意此时是没有根布局的），然后在其它xml里面要引用这个merge的话
可以使用include把该xml包含进来，这样就达到了复用带merge标签里的布局内容，又不会因为要include一个xml进来时而多了一层父布局。

在第一种情况下，我们其实用不到finishInflate这个回调，因为调用完inflate方法后我们就可以使用findViewById了

但是第二种情况下，对于merge的父布局来讲本身就会调用被一次finishInflate方法了，那么在遇到merge时，对rInflate传入这个父布局要是再调用就重复了。

## attachToRoot参数解析

到这里，还有个东西没说，就是我们的inflate方法：
```
LayoutInflater.from(context).inflate(R.layout.root,false);

```
第三个参数是啥作用？或许你已经明白，但是我们还是从源码的角度来解释它的作用

在上面，我并没有贴出inflate方法中关于第三个参数的相关代码，是因为想单独出来说，下面我们抽出这个方法涉及到这个参数的代码：
```
//这里我们讲过，merge标签必须要attachToRoot为true，为什么呢
if (TAG_MERGE.equals(name)) {
        if (root == null || !attachToRoot) {
            throw new InflateException("<merge /> can be used only with a valid "
                    + "ViewGroup root and attachToRoot=true");
        }

        rInflate(parser, root, inflaterContext, attrs, false);
}


 params = root.generateLayoutParams(attrs);
 //如果attachToRoot不为tue，则xml根布局才会设置LayoutParams
    if (!attachToRoot) {
        // Set the layout params for temp if we are not
        // attaching. (If we are, we use addView, below)
        temp.setLayoutParams(params);
    }



   //如果我们有指定传入一个父布局，并且attachToRoot=true ，那么会把xml根布局添加到这个我们指定的父布局中
  if (root != null && attachToRoot) {
        root.addView(temp, params);
    }

    //如果我们没指定一个父布局 或者attachToRoot=false，则inflate方法解析完成后返回的view就是这个xml的根布局
    if (root == null || !attachToRoot) {
        result = temp;
    }
```

经过上面对几种情况的注释，相信已经差不多了解attachToRoot的影响了吧。

attachToRoot的作用也就是**要不要附属到指定的父布局上**

如果我们传入的root不为空，并且attachToRoot为true，则会把布局内容添加到root容器里

如果我们传入的root不为空，并且attachToRoot为false，那么同样不会添加到root容器里，但是也不会设置自身的LayoutParams

如果我们传入的root为空，那么attachToRoot为true，布局内容不会被添加到其他地方去，但是会设置自身的LayoutParams

如果我们传入的root为空，那么attachToRoot为false，布局内容不会被添加到其他地方去，同时也不会设置自身的LayoutParams


# View创建过程


到这里我们已经对LayoutInflater在装载View的过程有个大概的了解了。

我们在其中一个叫做createViewFromTag的方法在上面都是一笔带过，这里挑出来专门大概讲一下。


createViewFromTag方法最终调用它的5个参数的重载方法：
```
View createViewFromTag(View parent, String name, Context context, AttributeSet attrs,
            boolean ignoreThemeAttr) {
         // 1
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }

        // 2
        if (!ignoreThemeAttr) {
            final TypedArray ta = context.obtainStyledAttributes(attrs, ATTRS_THEME);
            final int themeResId = ta.getResourceId(0, 0);
            if (themeResId != 0) {
                context = new ContextThemeWrapper(context, themeResId);
            }
            ta.recycle();
        }
        //3
        if (name.equals(TAG_1995)) {
            // Let's party like it's 1995!
            return new BlinkLayout(context, attrs);
        }

        try {
            View view;
            //4
            if (mFactory2 != null) {
                view = mFactory2.onCreateView(parent, name, context, attrs);
            } else if (mFactory != null) {
                view = mFactory.onCreateView(name, context, attrs);
            } else {
                view = null;
            }
            //5
            if (view == null && mPrivateFactory != null) {
                view = mPrivateFactory.onCreateView(parent, name, context, attrs);
            }
            //6
            if (view == null) {
                final Object lastContext = mConstructorArgs[0];
                mConstructorArgs[0] = context;
                try {
                    if (-1 == name.indexOf('.')) {
                        view = onCreateView(parent, name, attrs);
                    } else {
                        view = createView(name, null, attrs);
                    }
                } finally {
                    mConstructorArgs[0] = lastContext;
                }
            }

            return view;
        } catch (InflateException e) {
            throw e;

        } catch (ClassNotFoundException e) {
            final InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name, e);
            ie.setStackTrace(EMPTY_STACK_TRACE);
            throw ie;

        } catch (Exception e) {
            final InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name, e);
            ie.setStackTrace(EMPTY_STACK_TRACE);
            throw ie;
        }
    }
```


我在源码中标注了6个地方，一个一个描述

## （1）判断view标签

从源码上看，当标签是view的话，就获取它的一个class属性，然后重新赋值给name

由此我们可以知道，这个view标签的用法应该是这样的：
```
  <view
        class="LinearLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

想这样的话，name本身是view，而解析了class属性后，name就变成了LinearLayout，与我们定义：
```
<LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

作用是一样的。这个view标签我们几乎不会用到这样的写法，所以可能很多人还不知道view标签的作用


## （2） 主题相关判断

从源码逻辑上看，是获取了theme的相关属性，如果有的话则重新包装一下Context，变成一个带主题的Context，也就是ContextThemeWrapper

这个主题会影响后面关于View的属性样式相关

## （3）BlinkLayout判断
<blink>标签是一个会自动闪烁的布局容器，会被转化成BlinkLayout。
如果你有兴趣的话可以写个例子：
```
    <blink
        android:layout_width="wrap_content"
        android:layout_height="wrap_content">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="我会一直闪烁的文字" />

    </blink>
```
BlinkLayout是LayoutInflater里面的一个私有内部类，我们并不能直接使用它，而且我们日常也几乎用不到这个blink标签
它的内部也没有几句代码，点击进去看一下只是继承了下FrameLayout

内部封装了一个Handler用于定时调用invalidate、然后用一个不断交替的mBlinkState变量判断是否调用dispatchDraw来完成绘制

## （4）Factory接口自定义View的创建规则

这个从代码看上去貌似是用来生产View的工厂，那么mFactory和mFactory2是什么时候被赋值的

在AS上输入查找关键词： mFactory =

可以看到他们分别都有一个set方法：setFactory(Factory factory) 和 setFactory2(Factory2 factory)

setFactory如下：
```
   public void setFactory(Factory factory) {
        if (mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        mFactorySet = true;
        if (mFactory == null) {
            mFactory = factory;
        } else {
            mFactory = new FactoryMerger(factory, null, mFactory, mFactory2);
        }
    }
```
可以发现setFactory方法并不允许被重复调用的，不然会抛出异常

然后mFactory如果不为空的话，并没有直接赋值传进来的factory变量，而是创建了一个FactoryMerger

看setFactory2方法如下：
```
public void setFactory2(Factory2 factory) {
        if (mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        mFactorySet = true;
        if (mFactory == null) {
            mFactory = mFactory2 = factory;
        } else {
            mFactory = mFactory2 = new FactoryMerger(factory, factory, mFactory, mFactory2);
        }
    }
```

可以发现setFactory2方法也是不允许被重复调用的，不然会抛出异常

而且逻辑也一样，mFactory如果不为空的话，并没有直接赋值传进来的factory变量，而是创建了一个FactoryMerger。

那么FactoryMerger是啥呢？从名字上看待了merge的字眼，看上去不难猜测出是合并这两个工厂的作用。

根据Factory接口的介绍我们明白了，Factory接口和Factory2接口都是用来给开发者自定义View的创建规则的。

而**Factory2接口相比Factory接口，则重载了下接口方法，多传了个parent View进来**

**Factory2接口是在API 11的时候被加入进来的，因为Factory接口造成的问题是我们无法得知它的父 view是谁 从而限制了一些操作。**

所以官方已经废弃了Factory接口，我们一般要采用的就是Factory2接口

关于这个Factory接口，谷歌是这么介绍的：
```
 /**
         * Hook you can supply that is called when inflating from a LayoutInflater.
         * You can use this to customize the tag names available in your XML
         * layout files.
         *
 **/
```

原来是做了个hook方便我们来自定义View的创建规则，比如说我故意要把该xml布局文件里面的TextView控件都换成Button
那么我们可以在Activity内这么写：
```
 LayoutInflater.from(this).setFactory2(new LayoutInflater.Factory2() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                return null;
            }

            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                if(name.equals("TextView")){
                    Button button = new Button(context,attrs);
                    return button;
                }
                return null;
            }
        });

```

既然是留给开发者自定义的，那么正常来讲这里factory和factory2都会为空，所以view还是空的

## （5）View的默认创建规则

正常来说的话则会走第5步这个流程：
```
//5
    if (view == null && mPrivateFactory != null) {
        view = mPrivateFactory.onCreateView(parent, name, context, attrs);
    }
```

mPrivateFactory一看就是私有的工厂 不是给开发者用的。那么这个mPrivateFactory是什么时候被赋值的呢，搜索一下发现：
```
 /**
     * @hide for use by framework
     */
    public void setPrivateFactory(Factory2 factory) {
        if (mPrivateFactory == null) {
            mPrivateFactory = factory;
        } else {
            mPrivateFactory = new FactoryMerger(factory, factory, mPrivateFactory, mPrivateFactory);
        }
    }
```

framework会自己调用这个共有方法，而对开发者是隐藏的。

它在Activity的attach方法中被用到：
```
 final void attach(...){
  mWindow.getLayoutInflater().setPrivateFactory(this);
 }
```
设置了Activity自己，看来Activity是实现了这个接口了，找了下发现Activity实现了LayoutInflater.Factory2接口

我们看Activity是如何实现这个接口的：
```
   public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (!"fragment".equals(name)) {
            return onCreateView(name, context, attrs);
        }

        return mFragments.onCreateView(parent, name, context, attrs);
    }
@Nullable
public View onCreateView(String name, Context context, AttributeSet attrs) {
    return null;
}
  public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
      return mHost.mFragmentManager.onCreateView(parent, name, context, attrs);
  }
```

这里一不小心引出了Fragment。

我们可以发现Activity并没有自己根据标签去创建View，而是判断如果是fragment标签的话则应用是走了Fragment的onCreateView生命周期方法

所以我们就可以知道，如果是fragment标签的话，则是Activity自己创建的

如果不是的话，Activity调用3个参数的onCreateView方法，直接返回null

所以默认情况下，还是会教给LayoutInflater自己来创建

## （6）View的创建过程

既然我们得出了上面的结论，那么下面这个第6步的代码，默认情况下还是会走
```
// 6
if (view == null) {
    final Object lastContext = mConstructorArgs[0];
    mConstructorArgs[0] = context;
    try {
        if (-1 == name.indexOf('.')) {
            view = onCreateView(parent, name, attrs);
        } else {
            view = createView(name, null, attrs);
        }
    } finally {
        mConstructorArgs[0] = lastContext;
    }
```
这里做了个判断，判断标签名字是否有带个小数点，我们不难猜测出，是因为我们往往自定义View的时候，在xml中布局的时候需要写出这个View的全称

所以这里判断是否带了个小数点，判断是否是自定义View。 如果不是自定义的话，走了onCreateView方法后最终也是到createView方法：
```
 protected View onCreateView(String name, AttributeSet attrs)
            throws ClassNotFoundException {
        return createView(name, "android.view.", attrs);
    }
```

可以发现，与自定义view调用createView方法不同的是，这里多传了个前缀"android.view."

足以看出，我们在XML中声明的自带的控件，会被系统自己加上个类名的前缀以反射出类名创建对象。

我们看一下createView方法：

```
  public final View createView(String name, String prefix, AttributeSet attrs)
            throws ClassNotFoundException, InflateException {
       //取得缓存中已经加载过的class
       Constructor<? extends View> constructor = sConstructorMap.get(name);
       ...
        if (constructor == null) {
           // 注意这里，默认prefix则会传进来android.view以构成控件类名全称
           clazz = mContext.getClassLoader().loadClass(
                   prefix != null ? (prefix + name) : name).asSubclass(View.class);
            ...
            constructor = clazz.getConstructor(mConstructorSignature);
            constructor.setAccessible(true);
            //缓存已经加载过的类
            sConstructorMap.put(name, constructor);
        ｝
        ...
       //反射创建View实例
        final View view = constructor.newInstance(args);
       if (view instanceof ViewStub) {
           // Use the same context when inflating ViewStub later.
           final ViewStub viewStub = (ViewStub) view;
           viewStub.setLayoutInflater(cloneInContext((Context) args[0]));
       }
       ...
       return view;
 }
```
createView方法被我精简了大量代码，因为那些反射代码流程感觉没有太多需要说明的，这里只有一个重点，就是ViewStub登场了

# ViewStub源码解析
我们可以看到，如果判断是ViewStub类的话，也只是克隆了一个LayoutInflater实例，并没有做其他操作。

我们知道ViewStub是可以用来懒加载视图的，当我们在xml布局中不需要一开始就显示的布局，我们可以采用ViewStub，当需要用到的时候再inflate进来

ViewStub类的代码也不多，它同样继承了View,所有也可以有View的特性，可以被其他父布局添加成一个子View

ViewStub类定义的一些重点源码如下：
```
public final class ViewStub extends View {

private WeakReference<View> mInflatedViewRef;
private LayoutInflater mInflater;

 public ViewStub(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        ...
        setVisibility(GONE);
        setWillNotDraw(true);
    }
  public void setLayoutInflater(LayoutInflater inflater) {
        mInflater = inflater;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    @Override
    public void draw(Canvas canvas) {
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
    }

    public void setVisibility(int visibility) {
        //mInflatedViewRef不为空则说明加载过了，直接调用view自己的setVisibility
        if (mInflatedViewRef != null) {
            View view = mInflatedViewRef.get();
            if (view != null) {
                view.setVisibility(visibility);
            } else {
                throw new IllegalStateException("setVisibility called on un-referenced view");
            }
        } else {
            super.setVisibility(visibility);
            //可见时装载布局
            if (visibility == VISIBLE || visibility == INVISIBLE) {
                inflate();
            }
        }
    }
    public View inflate() {
        final ViewParent viewParent = getParent();

        if (viewParent != null && viewParent instanceof ViewGroup) {
            if (mLayoutResource != 0) {
                final ViewGroup parent = (ViewGroup) viewParent;
                final View view = inflateViewNoAdd(parent);
                replaceSelfWithView(view, parent);

                mInflatedViewRef = new WeakReference<>(view);
                if (mInflateListener != null) {
                    mInflateListener.onInflate(this, view);
                }

                return view;
            } else {
                throw new IllegalArgumentException("ViewStub must have a valid layoutResource");
            }
        } else {
            throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
        }
    }
  //替换坑位
  private void replaceSelfWithView(View view, ViewGroup parent) {
        final int index = parent.indexOfChild(this);
        parent.removeViewInLayout(this);

        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            parent.addView(view, index, layoutParams);
        } else {
            parent.addView(view, index);
        }
    }

 //装载资源布局界面
 private View inflateViewNoAdd(ViewGroup parent) {
        final LayoutInflater factory;
        if (mInflater != null) {
            factory = mInflater;
        } else {
            factory = LayoutInflater.from(mContext);
        }
        final View view = factory.inflate(mLayoutResource, parent, false);

        if (mInflatedId != NO_ID) {
            view.setId(mInflatedId);
        }
        return view;
    }
}
```

ViewStub的代码很少，我们调了其中最重要的方法，我们可以知道，在创建的时候为ViewStub克隆了一个LayoutInflater实例赋值给了mInflater

我们发现在构建的时候，ViewStub自己便设置了setVisibility(GONE);和setWillNotDraw(true);

然后还在onMeasure里面传了宽高都是0的数值，并且draw和dispathDraw都是空方法。

这一切足以证明，ViewStub像是一个空袋子，与世无争什么都不干，就只在布局中占着个位置。

我们知道，我们要显示ViewStub内容的时候可以用inflate方法或者是setVisibility方法


我们看他的inflate方法流程，它会先调用inflateViewNoAdd方法来把指定的资源布局加载进来，然后用replaceSelfWithView方法来把自己从原先的父布局中的坑位中替换出来，给这个新加载的布局替换进去。

然后setVisibility方法其实也只是做了个判断，如果未装载，则在VISIBLE or INVISIBLE时调用inflate方法。

由此我们可以得知，原来ViewStub的懒加载原理是这样的，先不加载自己指定的xml到布局中，而是在布局中占了个坑位，没宽高也什么都不显示。
当需要加载的时候再加载布局资源进来，替换自己占的坑位

# 结束语

 相信你在读完本篇后，一定会LayoutInfalter的装载过程有了一定的了解。

并且对布局优化的三个标签（include、merge、ViewStub）的原理理解更加深入。



