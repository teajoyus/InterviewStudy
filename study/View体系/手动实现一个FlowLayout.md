# 前言
最近由于项目需求，需要呈现出流式布局的搜索关键词。虽然项目里早已有实现了FlowLayout，但是由于出现了一些计算上的bug，所以我又捋了一遍，这次写到博客上。
FlowLayout使用的场景还是挺多的，最常见的就是呈现一些搜索热词的时候了。下面将演示如何实现一个搜索热词的流式布局，最后面会贴出FlowLatyout的代码
下面先贴出一张效果图：

![效果图](https://img-blog.csdnimg.cn/2019031514555798.png)

# 思路分析

我们需要去自定义一个ViewGroup，然后在onMeasure方法中需要考虑当warp_content模式时需要自己计算FlowLayout所占的宽高，它所占的宽是最长的那一行的宽度，所占的高则就是所有行累加起来。
然后在onLayout方法中主要是确定好每个View要摆放的起始坐标（left,top）。
每摆放一个child后，累加已经占用行宽，如果下一个child占的宽度大于剩余的行宽，则另起一行。其次我们每起一行都要累加下当前高度，这样我们才好确定下一行的摆放位置应该从哪个top坐标开始，然后一一摆放

需要考虑的基本情况：
- FlowLayout应该支持warp_content
- FlowLayout应该支持padding
- child应该支持margin
- 当child的getVisibility() ==GONE的时候，不应该参与计算
- 支持设定行间距

## 继承自ViewGroup

我们知道，当继承ViewGroup的时候，它有一个抽象方法，就是onLayout。我们需要由它来确定我们这个布局的摆放规则。

然后，每个布局都有自己的一个LayoutParams，由于我们这里需要考虑到margin的情况，所以我们必须有一个MarginLayoutParams，而我们继承ViewGroup默认生成的是ViewGroup里面的LayoutParams，我们需要改变它。
我们创建一下：
```

    class FlowLayout extends ViewGroup {

        private int lineSpace;

        public FlowLayout(Context context) {
            super(context);
        }

        public FlowLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {

        }

        @Override
        protected LayoutParams generateDefaultLayoutParams() {
//        return super.generateDefaultLayoutParams();
            return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        @Override
        public LayoutParams generateLayoutParams(AttributeSet attrs) {
//            return super.generateLayoutParams(attrs);
            return new MarginLayoutParams(getContext(), attrs);
        }

        @Override
        protected LayoutParams generateLayoutParams(LayoutParams p) {
            return new MarginLayoutParams(p);
        }
        public void setLineSpace(int lineSpace) {
            this.lineSpace = lineSpace;
        }
    }
```

可以看到，我们分别重写了ViewGroup的generateDefaultLayoutParams、generateLayoutParams、generateLayoutParams方法。让她返回一个MarginLayoutParams。这样我们在里面用到计算child的margin时，才可以强转child的layotParams为MarginLayoutParams

其次，我们还定义了一个lineSpace变量用来参与总行高的计算，这样我们就可以动态的修改行间距。

## onMeasure

我们都知道View都会经历onMeasure、onLayout、onDraw三个流程。这里也不例外，我们除了得完成child的measure之外，还需要考虑当FlowLayout为warp_content时，也就是测量模式为MeasureSpec.AT_MOST的时候，我们需要计算出child总共占的宽高才能确定FlowLayout的宽高。

虽然代码已经写好，但是我们还是按照步骤来给代码。

首先：
```
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int width = 0, height = 0;

    //这里测量好child最终得出FlowLayout的width和height
    ...

    //这里判断如果不是精确模式则用我们计算出来的width和height，特别注意还要加上FlowLayout自身的Padding
     setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : (width + getPaddingLeft() + getPaddingRight()), heightMode == MeasureSpec.EXACTLY ? heightSize : (height + getPaddingTop() + getPaddingBottom()));

```

我们首先需要的是通过MeasureSpec.getMode来判断测量模式，以确定用哪个width height

然后我们要测量child的时候，ViewGroup有一个measureChild的方法可以帮我们测量child，measureChild方法我们可以传入父布局的宽高，表示父布局允许的最大测量的最大宽高。
这里考虑一种情况，当FlowLayout指定了padding的时候，我们需要把它宽高去掉这个padding后剩余的才是实际可用的最大宽高。
所以有：
```
    int parentwidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize - getPaddingLeft() - getPaddingRight(),widthMode);
    int parentheightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize - getPaddingTop() - getPaddingBottom(),heightMode);
    measureChild(child, parentwidthMeasureSpec, parentheightMeasureSpec);
```

这样我们就完成了child的测量，但是我们需要去遍历所有的child，来测量。并且要记录当前行宽才可以确定下一个View需不需要换行，也需要记录行高才能求出最终FlowLayout所占的高度
所以代码应该是（伪代码）：
```
for(遍历每个child){


    //需要换行
    if(当前行宽+child的宽度+margin > 父布局的宽度){

        //父布局的宽度替换成占据最宽的那一行的宽度
       父布局宽度 = max(行宽，父布局宽度+margin);

       //累加总高度
       父布局高度 += 行高

       //重置行宽为这个另起一行的child的宽
       行宽 = child的宽度+margin

    //不需要换行
    }else{

        //累加行宽
        行宽+=child的宽度+margin

        //每一行的行高应该是这一行里面高度最大的那个child的高度
        行高 = max(行高，child的高度)
    }

}
```

经过上面的伪代码分析的思路，这里贴上整个onMeasure方法的代码：
```
   @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int width = 0, height = 0;
            int childCount = getChildCount();
            int lineWidth = 0;
            int lineHeight = 0;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                //注意如果是GONE的话不参与运算
                if(child.getVisibility()==GONE){
                    continue;
                }
                //没这样算的话，不会计算padding
                int parentwidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize - getPaddingLeft() - getPaddingRight(),widthMode);
                int parentheightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize - getPaddingTop() - getPaddingBottom(),heightMode);
                measureChild(child, parentwidthMeasureSpec, parentheightMeasureSpec);

                MarginLayoutParams mp = (MarginLayoutParams) child.getLayoutParams();
                int childWidth = child.getMeasuredWidth() + mp.leftMargin + mp.rightMargin;
                int childHeight = child.getMeasuredHeight() + mp.topMargin + mp.bottomMargin;
                int parentWidth = widthSize - getPaddingLeft() - getPaddingRight();
                if (lineWidth + childWidth > parentWidth) {
                    width = Math.max(lineWidth, width);
                    lineWidth = childWidth;
                    //防止第一个view就已经占满一行，这样无端多计算了个lineSpace的高度
                    if(lineHeight>0){
                        height += lineHeight + lineSpace;
                    }
                    lineHeight = childHeight;

                } else {
                    lineWidth += childWidth;
                    lineHeight = Math.max(lineHeight, childHeight);
                }
                //注意这里最后一次的时候，上面的lineHeight还没有累积，width还没有重新计算
                if (i == childCount - 1) {
                    width = Math.max(width, lineWidth);
                    height += lineHeight;//注意这里不需要再加lineSpace了，因为最后一行下面没有行间距了
                }
            }
            setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), height + getPaddingTop() + getPaddingBottom());
        }
```

上面就是测量的过程了。


# onLayout

我们的重点方法来了，如何摆放的问题。有一种思路是我们可以用一个List来存放每一行的View，这样的好处是方便我们后面可以定位Child（比如我们想知道这个child在哪一行，我想知道哪一行有哪些child）

```
public class FlowLayout extends ViewGroup {
    private List<List<View>> linesViews = new ArrayList<>();
    private List<Integer> linesHeights = new ArrayList<>();


    ...
 }
```

我们定义一个linesViews和linesHeights。

接下来我们需要遍历child来把对应的child和line height放到linesViews和linesHeights里面。
这个遍历跟onMeausre遍历有点像，又有点不像。就直接贴代码了吧：
```
        linesHeights.clear();
        linesViews.clear();
        int lineWidth = 0;
        int lineHeight = 0;
        List<View> lineViews = new ArrayList<>();
        int parentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //如果为GONE 则也要记录这个坑位到list，但是不去计算它占的坑
            if (child.getVisibility() == GONE) {
                lineViews.add(child);
                continue;
            }
            MarginLayoutParams mp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + mp.leftMargin + mp.rightMargin;
            int childHeight = child.getMeasuredHeight() + mp.topMargin + mp.bottomMargin;
            if (lineWidth + childWidth > parentWidth) {
                //防止第一个view就已经大于一行，这样其实lineHeight、和lineViews是没数据的，不能添加
                if (lineViews.size() > 0) {
                    linesHeights.add(lineHeight);
                    linesViews.add(lineViews);
                }
                lineViews = new ArrayList<>();
                lineHeight = 0;
                lineWidth = 0;
            }
            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);
            lineViews.add(child);
            //最后一个lineHeight和lineViews没有被添加到列表
            if (i == childCount - 1) {
                linesHeights.add(lineHeight);
                linesViews.add(lineViews);
            }
        }

```


上面的思路就是遍历每个child，如果遇到的child是需要换行的，那么把这一行的lineViews（lineViews记录的就是某一行的view）放到linesViews，同时把这一行的高度也添加到linesHeights中。

然后重置lineViews，lineHeight、lineWidth，然后再计算lineWidth、lineHeight并把这个child放到lineViews中

如果遇到child为GONE的话，那么直接添加到lineViews不需要参与尺寸换行计算


这样子我们就知道了每一行有哪些View，并且每一行有多高，这样我们就可以确定每一个View要开始摆放的位置。
因为View的layout方法需要指定left、right、top、bottom四个参数
而left我们就得知道当前这一行已经摆放的宽度是多少了，才好从这里开始接着摆放
而right的话，只要知道left之后通过叠加自身的width即可确定
top的话我们就得知道经过上一行摆放后，总共摆放了多少高度了，这样我们才好确定下一行要从哪个高度开始摆
而bottom与right同理，只要知道top后通过叠加自身的height即可确定

这部分代码如下：
```
   //开始高度要从paddingTop开始
        int currentHeight = getPaddingTop();
        for (int i = 0; i < linesViews.size(); i++) {
            //开始宽度要从paddingTop开始
            lineWidth = getPaddingLeft();
            for (View child : linesViews.get(i)) {
                if (child.getVisibility() == GONE) {
                    continue;
                }
                MarginLayoutParams mp = (MarginLayoutParams) child.getLayoutParams();
                child.layout(lineWidth + mp.leftMargin, currentHeight + mp.topMargin, lineWidth + mp.leftMargin + child.getMeasuredWidth(), currentHeight + mp.topMargin + child.getMeasuredHeight());
                //得到摆放好的child的right坐标，再加上rightMargin就是下一个View开始计算的left位置
                lineWidth = child.getRight() + mp.rightMargin;
            }
            //每一行的View摆放好了，就取出这一行的高度，再加上行间距，这样就可以确定下一行要开始摆放的top了
            currentHeight += linesHeights.get(i) + lineSpace;
        }
```

这样onLayou方法就完成了，FlowLayout相对来说比较简单，所以遇见bug还比较好捋。




FlowLayout整体代码如下：
```
  public class FlowLayout extends ViewGroup {
        private List<List<View>> linesViews = new ArrayList<>();
        private List<Integer> linesHeights = new ArrayList<>();
        private int lineSpace;

        public FlowLayout(Context context) {
            super(context);
        }

        public FlowLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void setLineSpace(int lineSpace) {
            this.lineSpace = lineSpace;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int width = 0, height = 0;
            int childCount = getChildCount();
            int lineWidth = 0;
            int lineHeight = 0;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                //注意如果是GONE的话不参与运算
                if (child.getVisibility() == GONE) {
                    continue;
                }
                //没这样算的话，不会计算padding
                int parentwidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize - getPaddingLeft() - getPaddingRight(), widthMode);
                int parentheightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize - getPaddingTop() - getPaddingBottom(), heightMode);
//             注意这里，是measureChild，我自己写成了child.measure(widthMeasureSpec, heightMeasureSpec);找了半天bug，他们的区别
                measureChild(child, parentwidthMeasureSpec, parentheightMeasureSpec);

                MarginLayoutParams mp = (MarginLayoutParams) child.getLayoutParams();
                int childWidth = child.getMeasuredWidth() + mp.leftMargin + mp.rightMargin;
                int childHeight = child.getMeasuredHeight() + mp.topMargin + mp.bottomMargin;
                int parentWidth = widthSize - getPaddingLeft() - getPaddingRight();
                if (lineWidth + childWidth > parentWidth) {
                    width = Math.max(lineWidth, width);
                    lineWidth = childWidth;
                    //防止第一个view就已经占满一行，这样无端多计算了个lineSpace的高度
                    if (lineHeight > 0) {
                        height += lineHeight + lineSpace;
                    }
                    lineHeight = childHeight;

                } else {
                    lineWidth += childWidth;
                    lineHeight = Math.max(lineHeight, childHeight);
                }
                //注意这里最后一次的时候，上面的lineHeight还没有累积，width还没有重新计算
                if (i == childCount - 1) {
                    width = Math.max(width, lineWidth);
                    height += lineHeight;//注意这里不需要再加lineSpace了，因为最后一行下面没有行间距了
                }
            }
            setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), height + getPaddingTop() + getPaddingBottom());

        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int childCount = getChildCount();
            linesHeights.clear();
            linesViews.clear();
            int lineWidth = 0;
            int lineHeight = 0;
            List<View> lineViews = new ArrayList<>();
            int parentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                //如果为GONE 则也要记录这个坑位到list，但是不去计算它占的坑
                if (child.getVisibility() == GONE) {
                    lineViews.add(child);
                    continue;
                }
                MarginLayoutParams mp = (MarginLayoutParams) child.getLayoutParams();
                int childWidth = child.getMeasuredWidth() + mp.leftMargin + mp.rightMargin;
                int childHeight = child.getMeasuredHeight() + mp.topMargin + mp.bottomMargin;
                if (lineWidth + childWidth > parentWidth) {
                    //防止第一个view就已经大于一行，这样其实lineHeight、和lineViews是没数据的，不能添加
                    if (lineViews.size() > 0) {
                        linesHeights.add(lineHeight);
                        linesViews.add(lineViews);
                    }
                    lineViews = new ArrayList<>();
                    lineHeight = 0;
                    lineWidth = 0;
                }
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
                lineViews.add(child);
                if (i == childCount - 1) {
                    linesHeights.add(lineHeight);
                    linesViews.add(lineViews);
                }
            }
            //开始高度要从paddingTop开始
            int currentHeight = getPaddingTop();
            for (int i = 0; i < linesViews.size(); i++) {
                //开始宽度要从paddingTop开始
                lineWidth = getPaddingLeft();
                for (View child : linesViews.get(i)) {
                    if (child.getVisibility() == GONE) {
                        continue;
                    }
                    MarginLayoutParams mp = (MarginLayoutParams) child.getLayoutParams();
                    child.layout(lineWidth + mp.leftMargin, currentHeight + mp.topMargin, lineWidth + mp.leftMargin + child.getMeasuredWidth(), currentHeight + mp.topMargin + child.getMeasuredHeight());
                    //得到摆放好的child的right坐标，再加上rightMargin就是下一个View开始计算的left位置
                    lineWidth = child.getRight() + mp.rightMargin;
                }
                //每一行的View摆放好了，就取出这一行的高度，再加上行间距，这样就可以确定下一行要开始摆放的top了
                currentHeight += linesHeights.get(i) + lineSpace;
            }

        }

        @Override
        protected LayoutParams generateDefaultLayoutParams() {
            return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        @Override
        public LayoutParams generateLayoutParams(AttributeSet attrs) {
            return new MarginLayoutParams(getContext(), attrs);
        }

        @Override
        protected LayoutParams generateLayoutParams(LayoutParams p) {
            return new MarginLayoutParams(p);
        }

    }
```



# 结束语
上面演示的是FlowLayout的基本计算方法，实际项目中的FlowLayout还包含了其他各种各样的场景封装和方法调用，这里我们演示的时候略去了那些没必要的，只演示核心的onMeasure和onLayout过程，相信你再看完本篇后，也能手撸一个FlowLayout了