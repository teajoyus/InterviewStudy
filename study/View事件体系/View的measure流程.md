刘望舒：http://liuwangshu.cn/application/view/7-measure-sourcecode.html

# View的measure过程
------------
从OnMeasure来说：
```
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }
```

而getDefaultSize就是用来把大小和测量模式合成一个变量的
specMode是测量模式，specSize是测量大小

注意到测量宽度高度的时候调用了getSuggestedMinimumWidth、getSuggestedMinimumHeight

```
    protected int getSuggestedMinimumHeight() {
        return (mBackground == null) ? mMinHeight : max(mMinHeight, mBackground.getMinimumHeight());

    }
```
里面就是有mMinHeight,这个对应在xml中指定的minHeight，然后如果有设置背景的话则判断背景的尺寸，哪个大返回哪个

接下来，setMeasuredDimension：
```
    protected final void setMeasuredDimension(int measuredWidth, int measuredHeight) {
        boolean optical = isLayoutModeOptical(this);
        if (optical != isLayoutModeOptical(mParent)) {
            Insets insets = getOpticalInsets();
            int opticalWidth  = insets.left + insets.right;
            int opticalHeight = insets.top  + insets.bottom;

            measuredWidth  += optical ? opticalWidth  : -opticalWidth;
            measuredHeight += optical ? opticalHeight : -opticalHeight;
        }
        setMeasuredDimensionRaw(measuredWidth, measuredHeight);
    }
```

上面的isLayoutModeOptical是表示光学边界、比如阴影、发光。判断本身是不是有这个边界和父布局是否有这个，
以确定需要加上这个边界还是减去。
到上面就得到了measuredWidth，之后我们才可以调用getMeasuredWidth才能得到测量后的大小


以上就是View的测量过程，ViewGroup的测量过程不同在于需要遍历子View的测量后才最终确定自身的大小

# ViewGroup 没有onMeasure
----
而ViewGroup并没有提供onMeasure方法，而是measureChild方法。然后遍历每个child去调用他们的measure方法
而有个主意的地方就是getChildMeasureSpec方法
其中当ViewGroup是AT_MOST的时候，这时候childView都是AT_MOST, 也就是child设置WRAP_CONTENT等于设置MATCH_PARENT
```
case MeasureSpec.AT_MOST:
            if (childDimension >= 0) {
                // Child wants a specific size... so be it
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size, but our size is not fixed.
                // Constrain child to not be bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size. It can't be
                // bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;
```

所以这个也是为什么继承View的时候需要我们指定当为WRAP_CONTENT时默认的宽和高


至于ViewGroup的onMeasure则交给了每个具体的父布局，因为测量过程很不一样，没办法统一

# linearLayout的onMeasure
---------
比如在LinearLayout中，就判断是横向还是纵向，有不同的测量方法：
```

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }
```
就拿measureVertical来说，会先measure一遍后再测量那些带weight的，LinearLayout中有个记录totalHeight的（而FrameLayout则都是0，因为是可重叠的），所以子view会线性排下来

联系源码，只要LinearLayout不是Match_parent也不是MeasureSpec.EXACTLY ，
是wrap_content的话，那么会整体测量一次View后，再单独测量多一次带weight的View
```
2019-01-26 16:35:38.161 8999-8999/? I/2222222222222: onMeasure com.example.interviewstudy.view.LinearMeasureActivity$MyView{9de322f V.ED..... ......I. 0,0-0,0 #7f070050 app:id/mv_0}
2019-01-26 16:35:38.161 8999-8999/? I/2222222222222: onMeasure com.example.interviewstudy.view.LinearMeasureActivity$MyView{63fe03c V.ED..... ......I. 0,0-0,0 #7f070051 app:id/mv_1}
2019-01-26 16:35:38.161 8999-8999/? I/2222222222222: onMeasure com.example.interviewstudy.view.LinearMeasureActivity$MyView{9ecf1c5 V.ED..... ......I. 0,0-0,0 #7f070052 app:id/mv_2}
2019-01-26 16:35:38.161 8999-8999/? I/2222222222222: onMeasure com.example.interviewstudy.view.LinearMeasureActivity$MyView{63fe03c V.ED..... ......I. 0,0-0,0 #7f070051 app:id/mv_1}
2019-01-26 16:35:38.225 8999-8999/? I/2222222222222: onMeasure com.example.interviewstudy.view.LinearMeasureActivity$MyView{9de322f V.ED..... ......I. 0,0-0,0 #7f070050 app:id/mv_0}
2019-01-26 16:35:38.225 8999-8999/? I/2222222222222: onMeasure com.example.interviewstudy.view.LinearMeasureActivity$MyView{63fe03c V.ED..... ......I. 0,0-0,0 #7f070051 app:id/mv_1}
2019-01-26 16:35:38.225 8999-8999/? I/2222222222222: onMeasure com.example.interviewstudy.view.LinearMeasureActivity$MyView{9ecf1c5 V.ED..... ......I. 0,0-0,0 #7f070052 app:id/mv_2}
2019-01-26 16:35:38.226 8999-8999/? I/2222222222222: onMeasure com.example.interviewstudy.view.LinearMeasureActivity$MyView{63fe03c V.ED..... ......I. 0,0-0,0 #7f070051 app:id/mv_1}`
```

上面的mv_1这个view就是带weight的放在最后了，至于总体绘制了两次，说是ViewRootImpl的performTraversals会调用两次
如果是Match_parent或者是高度是确切的数值的话，则先测量不带weight的再测量带weight的


# ReleativeLayout 的onMeaure
-------------
而ReleativeLayout则都会measure两次，因为不同于linerLayout只有一个方向，ReleativeLayout有水平和垂直的：
第一次是measureChildHorizontal 水平方向上的
第二次是measureChild 垂直方向的

由于View的measure做了优化，如果前后高度宽度是一样的话那就没必要再测量一次，
而ReleativeLayout第一次测量时，如果子View不是MeasureSpec.EXACTLY的话，那么高度是不确定的，只能暂时用自身的高度来做测量高度，
那么进行第二次测量的时候，发现高度不一样，则需要重新测量。
所以要**要尽量使用padding，而不要margin，因为margin会让宽高不确定，而padding则不会**
# FrameLayout 的onMeaure
-------------
FrameLayout的话第一次是调用measureChildWithMargins来测量child设置的margin
利用一个mMatchParentChildren列表来记录那些MATCH_PARENT的View，这些View需要再被测量一次

思考：为什么是mMatchParentChildren的count>1才会重新测量那些MATCH_PARENT的View，而不是count>0






# 总结：
-------------


1.RelativeLayout会让子View调用2次onMeasure，LinearLayout 在有weight时，也会调用子View2次onMeasure
2.RelativeLayout的子View如果高度和RelativeLayout不同，则会引发效率问题，当子View很复杂时，这个问题会更加严重。如果可以，尽量使用padding代替margin。
3.在不影响层级深度的情况下,使用LinearLayout和FrameLayout而不是RelativeLayout。


最后再思考一下文章开头那个矛盾的问题，为什么Google给开发者默认新建了个RelativeLayout，而自己却在DecorView中用了个LinearLayout。
因为DecorView的层级深度是已知而且固定的，上面一个标题栏，下面一个内容栏。
采用RelativeLayout并不会降低层级深度，所以此时在根节点上用LinearLayout是效率最高的。
而之所以给开发者默认新建了个RelativeLayout是希望开发者能采用尽量少的View层级来表达布局以实现性能最优，因为复杂的View嵌套对性能的影响会更大一些。

4.能用两层LinearLayout，尽量用一个RelativeLayout，在时间上此时RelativeLayout耗时更小。另外LinearLayout慎用layout_weight,也将会增加一倍耗时操作。由于使用LinearLayout的layout_weight,大多数时间是不一样的，这会降低测量的速度。这只是一个如何合理使用Layout的案例，必要的时候，你要小心考虑是否用layout weight。总之减少层级结构，才是王道，让onMeasure做延迟加载，用viewStub，include等一些技巧。






第个布局的分析：https://www.cnblogs.com/chenlong-50954265/p/5942182.html