刘望舒：http://liuwangshu.cn/application/view/8-layout-sourcecode.html


从layout来说
```
public void layout(int l, int t, int r, int b)
```

接受四个属性，分别都是对应getLedt、getTop这些，是相对于父布局的距离

然后：
```
  boolean changed = isLayoutModeOptical(mParent) ?
                setOpticalFrame(l, t, r, b) : setFrame(l, t, r, b);
```
判断是不是LayoutModeOptical（光学 发光阴影这些）
一般来说就是走setFrame(l, t, r, b)

之后再调用onLayout方法。而View和ViewGroup的onLayout方法都是没任何东西的，需要具体的控件去实现具体的布局

而ViewGroup的layout方法是一个final方法，最终也是调用了View的layout方法

RelativeLayout的onLayout方法：
可以看到是遍历子View，然后获取他们的LayoutParams然后调用子View的layout方法
```

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //  The layout has actually already been performed and the positions
        //  cached.  Apply the cached values to the children.
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                RelativeLayout.LayoutParams st =
                        (RelativeLayout.LayoutParams) child.getLayoutParams();
                child.layout(st.mLeft, st.mTop, st.mRight, st.mBottom);
            }
        }
    }
```