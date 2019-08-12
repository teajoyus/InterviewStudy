
# 前言
  前面我们已经写过一篇RecycleView的用法，这里再更新一篇关于RecycleView的源码解析类的文章。
RecycleView与ListView同样作为列表控件，他们之间有什么共同点和不同点呢？
前面已经分析过ListView的源码：https://blog.csdn.net/lc_miao/article/details/88356511

它们之间孰好孰坏，你觉得呢？看了下面的分析，对比起ListView大概就有答案了。

# RecycleView#onMeasure

与ListView不同，RecycleView在onMeasure中做了大量的工作，onMeasure中的重要方法如下：
```
 @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        // 注释（1）
        if (mLayout == null) {
            defaultOnMeasure(widthSpec, heightSpec);
            return;
        }
      //三个实现类这个方法都是默认返回true，表示要不要把绘制流程交给LayoutManager来搞定
     if (mLayout.isAutoMeasureEnabled()) {
        ...
        //这里也是走了下defaultOnMeasure(widthSpec, heightSpec);
        mLayout.onMeasure(mRecycler, mState, widthSpec, heightSpec);
        //如果width和height都是精确模式的话，或者没有adapter的话，那么就直接返回了
        final boolean measureSpecModeIsExactly =
        widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY;
        if (measureSpecModeIsExactly || mAdapter == null) {
            return;
        }
        //所以下面都是正对于warp_content的情况

        if (mState.mLayoutStep == State.STEP_START) {
                //注释（2）
               dispatchLayoutStep1();
        }
        ...
        //注释（3）
         dispatchLayoutStep2();
         ...
         //宽高不确定的话，会导致绘制两次
        if (mLayout.shouldMeasureTwice()) {
                ...
                dispatchLayoutStep2();
                ...
            }
        ...
     ｝else{
        //如果不自动测量的话，最终是走LayoutManager的onMeasure方法，这个情况这里不做分析
        ...
     }
```

**注释（1）：**

这里的mLayout就是我们通过setLayoutManager中传入的，如果我们在setAdapter之前忘记去设置一个LayoutManager的话，则这里就走defaultOnMeasure方法，源码如下：
```
   void defaultOnMeasure(int widthSpec, int heightSpec) {
        // calling LayoutManager here is not pretty but that API is already public and it is better
        // than creating another method since this is internal.
        final int width = LayoutManager.chooseSize(widthSpec,
                getPaddingLeft() + getPaddingRight(),
                ViewCompat.getMinimumWidth(this));
        final int height = LayoutManager.chooseSize(heightSpec,
                getPaddingTop() + getPaddingBottom(),
                ViewCompat.getMinimumHeight(this));

        setMeasuredDimension(width, height);
    }
```
可以发现，没设置LayoutManager的话，这里并没有什么逻辑，只是简单计算了下宽高然后就结束了。（在后面的onLayout中还会再说明没设置LayoutManager的情况）

**注释（2）**

 在进行dispatchLayout的时候会分三步走，这是第一步。这其中涉及到RecycleView的另外一个秘密：preLayout和postLayout的概念。我们知道，RecycleView比起ListView多了动画的支持，那么动画如何被支持的，就跟这里有关。这里会保存动画前的信息，也就是预布局（preLayout），
 在dispatchLayoutStep3()，也就是第三步的时候，也就是postLayout，会结合此处保留的动画前的信息，这样对于RecycleView来说，它就知道发生动画前列表是什么样子，发生动画后列表会变化成什么样子，这样它就可以去执行动画。
 当然，三言两语说不清楚，篇幅原因也不打算重点放在这里。所以找了一篇感觉讲得特别好的关于RecycleView动画过程分析的文章： https://www.jianshu.com/p/3be9a519fe79

有需要的话可自行脑补。

**注释（3）**
dispatchLayoutStep2()正是第二步，第二步是measure和layout过程的关键。


```
  private void dispatchLayoutStep2() {
        ...
        //重写的getItemCount方法
        mState.mItemCount = mAdapter.getItemCount();
        ...
        // Step 2: Run layout
        mState.mInPreLayout = false;
        mLayout.onLayoutChildren(mRecycler, mState);
        ...
        mState.mLayoutStep = State.STEP_ANIMATIONS;
        ...
    }

```

在第二步中，调用到了mAdapter.getItemCount();方法，不过最最重要的这是这一句抛坑的代码：
```
mLayout.onLayoutChildren(mRecycler, mState);
```
把measure和layout的过程交给LayoutManager去做，RecycleView自己不干。
RecycleView把自己持有的mRecycler（负责缓存模型的类）和mState（它的状态类）转交给了LayoutManager。

我们知道，系统给我们三个关于LayoutManager实现类，这里我们特别俗气的也用LinearLayoutManager
所以接下来要讲的就是LinearLayoutManager了

# LayoutManager

我们直接从LinearLayoutManager的onLayoutChildren方法开始。

```
 @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // layout algorithm:
        //寻找一个锚点
        // 1) by checking children and other variables, find an anchor coordinate and an anchor
        //  item position.
        //锚点往上填充
        // 2) fill towards start, stacking from bottom
        //锚点往下填充
        // 3) fill towards end, stacking from top
        //滚动填充
        // 4) scroll to fulfill requirements like stack from bottom.
        // create layout state

        ...
        //判断绘制的方向,默认是正向绘制
        resolveShouldLayoutReverse();

    if (!mAnchorInfo.mValid || mPendingScrollPosition != NO_POSITION
                || mPendingSavedState != null) {
            mAnchorInfo.reset();
            //确定锚点的方向
            mAnchorInfo.mLayoutFromEnd = mShouldReverseLayout ^ mStackFromEnd;
            // 计算锚点的位置、偏移量
            updateAnchorInfoForLayout(recycler, state, mAnchorInfo);
            mAnchorInfo.mValid = true;
        }

        ...
        //注释（1）
        detachAndScrapAttachedViews(recycler);
        ...
        //倒着绘制
        if (mAnchorInfo.mLayoutFromEnd) {
             // fill towards start 从锚点往上
             updateLayoutStateToFillStart(mAnchorInfo);
             ...
             //注释（2）
            //填充
            fill(recycler, mLayoutState, state, false);
            ...

            // fill towards end 从锚点往下
           updateLayoutStateToFillEnd(mAnchorInfo);
           ...
           //填充
            fill(recycler, mLayoutState, state, false);
            ...
             if (mLayoutState.mAvailable > 0) {
                 // end could not consume all. add more items towards start
                 ...
                 //还有空间的话再填充一次
                 fill(recycler, mLayoutState, state, false);
                 ...
             }
        }else{
            //默认的绘制则是正向的
            // fill towards end 从锚点往下
            updateLayoutStateToFillEnd(mAnchorInfo);
            ....
            //填充
            fill(recycler, mLayoutState, state, false);
             ....
            // fill towards start 从锚点往上
            updateLayoutStateToFillStart(mAnchorInfo);
            ....
            //填充
            fill(recycler, mLayoutState, state, false);
             ....
            if (mLayoutState.mAvailable > 0) {
                // start could not consume all it should. add more items towards end
               //还有空间的话再填充一次
                fill(recycler, mLayoutState, state, false);
            }
        }
}
```

**注释（1）**

调用detachAndScrapAttachedViews方法，去detach掉或者是remove掉RecycleView的child，这里跟缓存机制有关。
如果了解过ListView的原理的话，这里类似于它的fillActiveViews方法
我们这里先不做大讨论，后面统一说下缓存机制的。

**注释（2）**
从上面列出的终点代码流程可以初步猜测，fill方法就是用来填充View的，有点像ListView的fillDown、fillUp。具体是不是，我们往下一步看看：

```
int fill(RecyclerView.Recycler recycler, LayoutState layoutState,
            RecyclerView.State state, boolean stopOnFocusable) {
        // max offset we should set is mFastScroll + available
        final int start = layoutState.mAvailable;
        if (layoutState.mScrollingOffset != LayoutState.SCROLLING_OFFSET_NaN) {
            // TODO ugly bug fix. should not happen
            if (layoutState.mAvailable < 0) {
                layoutState.mScrollingOffset += layoutState.mAvailable;
            }
            //有滚动的话做一下回收，后面再细讲
            recycleByLayoutState(recycler, layoutState);
        }
        //需要填充的空间
        int remainingSpace = layoutState.mAvailable + layoutState.mExtra;
        //LayoutChunkResult就是维护几个变量，用来记录每次的填充结果
        LayoutChunkResult layoutChunkResult = mLayoutChunkResult;
        //while循环去填充：如果还有可用空间并且还有更多的item的话
        while ((layoutState.mInfinite || remainingSpace > 0) && layoutState.hasMore(state)) {
            //先重置下结果记录
            layoutChunkResult.resetInternal();
            ...
            //填充一个块，在这里也就是填充一个item
            //注释（1）
            layoutChunk(recycler, state, layoutState, layoutChunkResult);
            ...
            //如果填充完成，那么就不用继续填充
            if (layoutChunkResult.mFinished) {
                break;
            }
            //累加当前的偏移量，layoutChunkResult.mConsumed相当于一个item的大小
            layoutState.mOffset += layoutChunkResult.mConsumed * layoutState.mLayoutDirection;
            /**
             * Consume the available space if:
             * * layoutChunk did not request to be ignored
             * * OR we are laying out scrap children
             * * OR we are not doing pre-layout
             */
            if (!layoutChunkResult.mIgnoreConsumed || mLayoutState.mScrapList != null
                    || !state.isPreLayout()) {
                //每次填充完，可用空间就相应减少
                layoutState.mAvailable -= layoutChunkResult.mConsumed;
                remainingSpace -= layoutChunkResult.mConsumed;
            }
            //有产生滚动的话做一下回收
            if (layoutState.mScrollingOffset != LayoutState.SCROLLING_OFFSET_NaN) {
                layoutState.mScrollingOffset += layoutChunkResult.mConsumed;
                if (layoutState.mAvailable < 0) {
                    layoutState.mScrollingOffset += layoutState.mAvailable;
                }
                recycleByLayoutState(recycler, layoutState);
            }
            ...
        }
        ...
        //返回填充的像素数量，后面滚动的话才可以根据这个来
        return start - layoutState.mAvailable;
    }
```

**注释（1）**
在fill方法里面，最重要的就是layoutChunk方法，它是用于填充一个item，其后完成测量、布局的工作。我们来看看：
```
void layoutChunk(RecyclerView.Recycler recycler, RecyclerView.State state,
            LayoutState layoutState, LayoutChunkResult result) {
         //从缓存获取View，或者是通过adapter创建一个
         //注释（1）
        View view = layoutState.next(recycler);
        ...
         LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (layoutState.mScrapList == null) {
            //到这里看到熟悉的addView，说明确实进行填充了。
            //到最后都会调到ViewGroup的addView
            if (mShouldReverseLayout == (layoutState.mLayoutDirection
                    == LayoutState.LAYOUT_START)) {
                addView(view);
            } else {
                //反向填充
                addView(view, 0);
            }
        }else {
             if (mShouldReverseLayout == (layoutState.mLayoutDirection
                     == LayoutState.LAYOUT_START)) {
                 addDisappearingView(view);
             } else {
                 addDisappearingView(view, 0);
             }
         }
         //测量这个item的大小，包括它的margin
        easureChildWithMargins(view, 0, 0);
        //这里点进去看getDecoratedMeasurement方法可以知道，
        //mConsumed变量就是记录这个item的height+margin
        result.mConsumed = mOrientationHelper.getDecoratedMeasurement(view);

        //计算这个child view的left, top, right, bottom值
         int left, top, right, bottom;
        if (mOrientation == VERTICAL) {
            if (isLayoutRTL()) {
                right = getWidth() - getPaddingRight();
                left = right - mOrientationHelper.getDecoratedMeasurementInOther(view);
            } else {
                left = getPaddingLeft();
                right = left + mOrientationHelper.getDecoratedMeasurementInOther(view);
            }
            if (layoutState.mLayoutDirection == LayoutState.LAYOUT_START) {
                bottom = layoutState.mOffset;
                top = layoutState.mOffset - result.mConsumed;
            } else {
                top = layoutState.mOffset;
                bottom = layoutState.mOffset + result.mConsumed;
            }
        } else {
            //横向的同理 代码略去
            ....
        }
        // We calculate everything with View's bounding box (which includes decor and margins)
        // To calculate correct layout position, we subtract margins.
        //这里完成child view的layout
        layoutDecoratedWithMargins(view, left, top, right, bottom);
}
```

现在看这个layoutChunk方法，还真是重中之重，它完成了child View的填充、测量、布局。

**注释（1）**
这是layoutChunk方法的第一句代码也是一句似乎看上去很不起眼的代码，实际这句代码抽象了对child view的获取
在layoutState这个的next方法中，则回到由RecycleView中的Recycle定义的缓存规则，由它的规则决定怎么从缓存获取，或者是创建一个新的item
我们后面统一说这个缓存规则


# RecycleView#onLayout()

上面我们大致对完了onMeasure流程，除了缓存的机制外，我们大概知道了RecycleView中itemView填充过程，而在layoutChunk里面不仅完成了item view的measure还完成了layout，那么RecycleView的onLayout流程还有什么用呢？
会到最上面讲onMeasure方法的时候，代码里面判断如果RecycleView是精确模式，也就是宽高是固定的话，则直接返回了，并没有走item view的填充流程，但是我们知道设置一个固定宽高后还是可以看得见那些item View的。这就是onLayout的工作了。

我们接下来看看onLayout流程：
```
 @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        TraceCompat.beginSection(TRACE_ON_LAYOUT_TAG);
        dispatchLayout();
        TraceCompat.endSection();
        mFirstLayoutComplete = true;
    }
```

转移到dispatchLayout方法来：
```
   void dispatchLayout() {
        ...
        mState.mIsMeasuring = false;
        //如果进行过dispatchLayoutStep1方法的话，那么mState.mLayoutStep就不是State.STEP_START了
        if (mState.mLayoutStep == State.STEP_START) {
            dispatchLayoutStep1();
            mLayout.setExactMeasureSpecsFrom(this);
            dispatchLayoutStep2();
        } else if (mAdapterHelper.hasUpdates() || mLayout.getWidth() != getWidth()
                || mLayout.getHeight() != getHeight()) {
            // First 2 steps are done in onMeasure but looks like we have to run again due to
            // changed size.
            mLayout.setExactMeasureSpecsFrom(this);
            dispatchLayoutStep2();
        } else {
            // always make sure we sync them (to ensure mode is exact)
            mLayout.setExactMeasureSpecsFrom(this);
        }
        dispatchLayoutStep3();
    }
```

可以发现，dispatchLayout最主要的也是保证走了这三个过程。如果宽高都是MeasureSpec.EXACTLY的话，则什么都没走就返回了，到这里mState.mLayoutStep记录的步骤 就依然还是State.STEP_START

所以可以得出结论：**如果RecycleView的宽高是MeasureSpec.EXACTLY的话，那么关于item View的填充 测量 布局都会延迟到onLayout阶段**

除此之外，onLayout并没有其他重要的流程了

# RecycleView#onDraw()

首先，每个item view肯定都是自己负责自己的绘制。RecycleView额外要做的就是分割线的处理所以onDraw方法只是增加了对分割线的绘制：
```
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        final int count = mItemDecorations.size();
        //绘制分割线
        for (int i = 0; i < count; i++) {
            mItemDecorations.get(i).onDraw(c, this, mState);
        }
    }
```


# RecycleView缓存机制

到这里，重头戏才来了。不管是ListView还是RecycleView，最特别之处就是能够缓存复用View了。

经过我们上面的分析，在：
```
recycleByLayoutState(recycler, layoutState);
```

```
View view = layoutState.next(recycler);
```

还有我们还没提到的滑动过程，都出现了缓存的影子。这里先讲解下它的缓存机制的模型，再来分析源码。


## RecycleView四级缓存
与ListView不同，RecycleView除了拥有ListView的activeView、scrapView的缓存模型，还支持自定义缓存和共用缓存池
ListView中，封装的缓存类是它的内部类RecycleBin，而RecycleView则是它的内部类Recycle

### mAttachedScrap

缓存当前在屏幕上的ViewHolder，因为每个ViewGroup都至少会onLayout两次，所以在第一次onLayout的时候记录在mAttachedScrap上，第二次onLayout的时候就可以从mAttachedScrap上直接获取，避免重复的创建，而在第二次layout过程完之后它也就被清空了

它不用重新onBindViewHolder()的，因为本来就是屏幕上对应好的位置

### mChangedScrap
mChangedScrap的功能与mAttachedScrap是差不多的，他们都属于Scrap缓存，与mAttachedScrap不同的是，mChangedScrap保存的是那些将要做改变的View。
我们知道RecyclerView提供了局部刷新的功能，比如我们通过调用getAdapter().notifyItemChanged(int postion);就能修改某个位置
所以那个位置上在经过Scrap缓存时会到mChangedScrap，其余的就到mAttachedScrap


### mCachedViews

在我们滑动过程中，优先复用的就是来自这个mCachedViews中，里面会暂存在RecycleView滑出的ViewHoder，而暂存的数量在RecycleView里面默认是2

由这个mCachedViews缓存的ViewHoder的位置信息都在而且也是对应的，所以它也是不需要重新onBindViewHolder()的



### mRecyclerPool

看名字带有Pool，显然它是一个缓存池。通过这个缓存池可以把移出屏幕的，并且没有在CacheViews中的（达到最大缓存数）缓存下来，不同的是，它会清空ViewHoder的信息，而且内部也会记录ViewHoder对应的viewType

所以当要复用的时候，需要通过指定的viewType来缓存池中获取一个ViewHolder，而此时这个ViewHoder的数据是被清空的，所以需要调用onBindViewHolder()来重新绑定数据

### mViewCacheExtension

看名字是一个拓展用的缓存机制，在系统的缓存机制里，并没有去实现它。（也就是默认mViewCacheExtension=null）

要使用它的话我们必须继承ViewCacheExtension这个抽象类重写它的抽象方法：getViewForPositionAndType来完成自定义的缓存机制

## RecycleView获取缓存

在RecycleView的设计中，往往是LayoutManager需要拿到View来做布局，LayoutManager会调用Recycler的getViewForPosition方法来获取一个View

所以getViewForPosition便是作为了取得缓存View的入口。这个方法经过重载后最终调用tryGetViewHolderForPositionByDeadline方法。
我们来看看这个方法：
```
ViewHolder tryGetViewHolderForPositionByDeadline(int position,
                boolean dryRun, long deadlineNs) {
        ...
        boolean fromScrapOrHiddenOrCache = false;
        ViewHolder holder = null;
        //注释（1）
        // 0) If there is a changed scrap, try to find from there
        if (mState.isPreLayout()) {
            holder = getChangedScrapViewForPosition(position);
            fromScrapOrHiddenOrCache = holder != null;
        }
        //注释（2）
        // 1) Find by position from scrap/hidden list/cache
        if (holder == null) {
            holder = getScrapOrHiddenOrCachedHolderForPosition(position, dryRun);
         ｝


        //通过position还是没找到的话
        if (holder == null) {

                ...
                final int type = mAdapter.getItemViewType(offsetPosition);
                //通过id来找
                // 2) Find from scrap/cache via stable ids, if exists
                if (mAdapter.hasStableIds()) {
                    //去Scrap或者Cached中找，逻辑跟上面是差不多的，区别于这里是通过id去找
                    holder = getScrapOrCachedViewForId(mAdapter.getItemId(offsetPosition),
                                            type, dryRun);
                }
                //注释（3）
                if (holder == null && mViewCacheExtension != null) {
                    final View view = mViewCacheExtension
                                .getViewForPositionAndType(this, position, type);
                    if (view != null) {
                              holder = getChildViewHolder(view);
                              ...
                       ｝
                       ...
                ｝
         //在Scrap和Cached、CacheExtension中都没找到的话，只能去pool中看看了
        if (holder == null) { // fallback to pool
                    if (DEBUG) {
                        Log.d(TAG, "tryGetViewHolderForPositionByDeadline("
                                + position + ") fetching from shared pool");
                    }
                    //注释（4）
                    holder = getRecycledViewPool().getRecycledView(type);
                    ...
            }
          if (holder == null) {

            ...
            //注释（5）
            holder = mAdapter.createViewHolder(RecyclerView.this, type);
            ...
          }

        ｝
}
```

**注释（1）**
如果现在是isPreLayout(),也就是预布局的话则调用getChangedScrapViewForPosition方法。getChangedScrapViewForPosition方法的内部也就是去遍历mChangedScrap了
前面我们讲解mChangedScrap的作用时说到，如果局部刷新改变了某个item那么在Scrap缓存时会到mChangedScrap中。所以这里也就是取这个


**注释（2）**
如果第一种情况拿不到，则在这里去getScrapOrHiddenOrCachedHolderForPosition()方法中拿
这个方法名字很长，理解上是从Scrap、Hidden、Cached这三个中去拿，我们过一下这个方法的代码：
```
getScrapOrHiddenOrCachedHolderForPosition(int position, boolean dryRun){
    final int scrapCount = mAttachedScrap.size();
    //首先优先去找mAttachedScrap的
    // Try first for an exact, non-invalid match from scrap.
    for (int i = 0; i < scrapCount; i++) {
        final ViewHolder holder = mAttachedScrap.get(i);
        //当然也需要满足一些条件才是有效的
        if (!holder.wasReturnedFromScrap() && holder.getLayoutPosition() == position
                && !holder.isInvalid() && (mState.mInPreLayout || !holder.isRemoved())) {
            holder.addFlags(ViewHolder.FLAG_RETURNED_FROM_SCRAP);
            return holder;
        }
    }


  if (!dryRun) {
        //注释（2）<1>
        //获取对应位置上的一个不可见但是没有被移除的View
        View view = mChildHelper.findHiddenNonRemovedView(position);
        if (view != null) {
            // This View is good to be used. We just need to unhide, detach and move to the
            // scrap list.
            final ViewHolder vh = getChildViewHolderInt(view);
            mChildHelper.unhide(view);
            int layoutIndex = mChildHelper.indexOfChild(view);
            if (layoutIndex == RecyclerView.NO_POSITION) {
                throw new IllegalStateException("layout index should not be -1 after "
                        + "unhiding a view:" + vh + exceptionLabel());
            }
            //detach掉View
            mChildHelper.detachViewFromParent(layoutIndex);
            //放入到Scrap缓存中
            scrapView(view);
            vh.addFlags(ViewHolder.FLAG_RETURNED_FROM_SCRAP
                    | ViewHolder.FLAG_BOUNCED_FROM_HIDDEN_LIST);
            return vh;
        }
    }
    //注释（2）<2>
    // Search in our first-level recycled view cache.
        final int cacheSize = mCachedViews.size();
        for (int i = 0; i < cacheSize; i++) {
            final ViewHolder holder = mCachedViews.get(i);
            if (!holder.isInvalid() && holder.getLayoutPosition() == position) {
                ...
                return holder;
            }
        }


}
```

**注释（2）<1>**
这里我们看到了一个较为陌生的对象mChildHelper，在RecyclerView的机制中，由于动画原因（比如删除）可能会造成RecyclerView的View与实际的Data不一致，
所以在RecyclerView中维护了一个ChildHelper类，用来协调这两者的关系。
在这里就是那些动画过程后被hidden的，会被detach出RecycleView然后放入到scrap缓存中（也就是mAttachedScrap或mChangedScrap中），其后返回这个ViewHolder
关于ChildHelper这里推荐一篇专门讲ChildHelper的博客，有兴趣的请看下：
https://blog.csdn.net/fyfcauc/article/details/54175072


**注释（2）<2>**

这里就是到了使用mCacheView这一步了，也就是复用那些滑动移出屏幕的View。

所以getScrapOrHiddenOrCachedHolderForPosition方法就是先去Scrap缓存在中找，再去HiddenView找，最后再去CachedView中找
我们再继续回到tryGetViewHolderForPositionByDeadline方法接着说

**注释（3）**
 还是没找到的话，如果用户有实现ViewCacheExtension，自定义缓存机制的话，这里就去ViewCacheExtension中取，默认不走这里
 前面我们介绍过ViewCacheExtension，它可以让我们自己拓展缓存机制，默认我们不实现的话就不走这个逻辑了

 **注释（4）**

 在Scrap和Cached、CacheExtension中都没找到的话，只能去pool中看看了。
 到这里出现了我们前面介绍过的RecycledViewPool，通常来说，我们在快速滑动的过程，毕竟mCachedView默认只是缓存2个，如果我们没设置的话。那么这个过程大量的复用还是来自RecycledViewPool
 而RecycledViewPool需要传入viewType后才能用，因为不同的viewType它有不同的ViewHoder、View。这里必须要做下区分。

 **注释（5）**

 当所有的缓存手段都没办法拿到View的情况下，那么只能通过Adapter的createViewHolder方法来创建一个ViewHolder了


到这里整个getViewForPosition大概的框架就解析完了，我们可以回顾一下它：
当需要到去createViewHolder时，整个链下来就是：

- 预布局的话，先去mChangedScrap中取
- 在mAttachScrap中取
- 在HiddenView中取
- 在mCachedView中取
- 在mViewCacheExtension中取
- 在RecycledViewPool中取
- 调用Adapter的createViewHolder方法


返回到前面我们在分析onMeasure阶段时，在LinearLayoutManager中的layoutChunk的时候，第一句代码就是：
```
 View view = layoutState.next(recycler);
```

点击next方法：
```
 View next(RecyclerView.Recycler recycler) {
    if (mScrapList != null) {
        return nextViewFromScrapList();
    }
    final View view = recycler.getViewForPosition(mCurrentPosition);
    mCurrentPosition += mItemDirection;
    return view;
}
```
发现调用的就是我们分析的getViewForPosition方法拿到View的。


## RecycleView添加缓存

我们知道LayoutManager提供了一系列移除View并且加入缓存的方法，比如removeAndRecycleView、removeAndRecycleAllViews
我们来看看removeAndRecycleView方法：
```
 public void removeAndRecycleView(View child, Recycler recycler) {
            removeView(child);
            recycler.recycleView(child);
        }
```
只有两句代码，第一句是remove掉View，内部通过ChildHelper来代理这个逻辑，前面我们说明过ChildHelper了
第二句代码则就是缓存的了我们看看下Recycler的recycleView方法：
```
   public void recycleView(View view) {
        // This public recycle method tries to make view recycle-able since layout manager
        // intended to recycle this view (e.g. even if it is in scrap or change cache)
        ViewHolder holder = getChildViewHolderInt(view);
        if (holder.isTmpDetached()) {
            removeDetachedView(view, false);
        }
        if (holder.isScrap()) {
            holder.unScrap();
        } else if (holder.wasReturnedFromScrap()) {
            holder.clearReturnedFromScrapFlag();
        }
        recycleViewHolderInternal(holder);
    }
```
可以看到会先判断ViewHolder是否是attach状态（注意不是View），主要是区别于mAttachedScrap，mAttachedScrap是没有attach的，所以它并不需要bindViewHolder，
而这里回收的View不是Scrap缓存，所以要先dettach掉。然后主要就是调用了recycleViewHolderInternal方法，我们看看：
```
void recycleViewHolderInternal(ViewHolder holder) {

    //各种状态的检查
    ...
    boolean cached = false;
    boolean recycled = false;
    if (mViewCacheMax > 0&& ...) {
       if (cachedViewSize >= mViewCacheMax && cachedViewSize > 0) {
            recycleCachedViewAt(0);
            cachedViewSize--;
      }
      mCachedViews.add(targetCacheIndex, holder);
      cached = true;
    }
       if (!cached) {
          addViewHolderToRecycledViewPool(holder, true);
          recycled = true;
      }


}
```
本身的代码非常多，我们需要的是抓重点。所以上面给出了重点代码。
我们可以看得，这个方法主要判断了当前cachedViewSize的数量是否达到了mViewCacheMax
是的话就调用recycleCachedViewAt(0)方法，然后cachedViewSize再减1
在这里我们猜猜也不难发现是把mCachedView的第一个给移出来丢到mRecyclerPool中，这样子才能缓存当前要移除的这个View到mCacheView中
具体是不是，看看才知道。我们看下recycleCachedViewAt方法的源码：
```
   void recycleCachedViewAt(int cachedViewIndex) {
            if (DEBUG) {
                Log.d(TAG, "Recycling cached view at index " + cachedViewIndex);
            }
            ViewHolder viewHolder = mCachedViews.get(cachedViewIndex);
            if (DEBUG) {
                Log.d(TAG, "CachedViewHolder to be recycled: " + viewHolder);
            }
            addViewHolderToRecycledViewPool(viewHolder, true);
            mCachedViews.remove(cachedViewIndex);
        }
```
可以发现也就是取出来又移除，中间调用addViewHolderToRecycledViewPool添加到缓存池中


另外，前面我们没有分析的recycleByLayoutState方法，最终也是调用到removeAndRecycleViewAt方法后调用Recycler的recycleView方法


而mAttachScrap的添加，则是在我们分析的LinearLayoutManager的onLayoutChilden中会调用detachAndScrapAttachedViews方法：
```
 public void detachAndScrapAttachedViews(Recycler recycler) {
            final int childCount = getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                final View v = getChildAt(i);
                scrapOrRecycleView(recycler, i, v);
            }
        }
```

而scrapOrRecycleView方法：
```
private void scrapOrRecycleView(Recycler recycler, int index, View view) {
            final ViewHolder viewHolder = getChildViewHolderInt(view);
            ...
            if (viewHolder.isInvalid() && !viewHolder.isRemoved()
                    && !mRecyclerView.mAdapter.hasStableIds()) {
                removeViewAt(index);
                recycler.recycleViewHolderInternal(viewHolder);
            } else {
                detachViewAt(index);
                recycler.scrapView(view);
                mRecyclerView.mViewInfoStore.onViewDetached(viewHolder);
            }
        }
```

在这个方法中会判断这个ViewHolder如果是要被移除掉的那就会调用Recycler的recycleViewHolderInternal方法添加到Cached或者Pool中
而如果不是的话，那么就会调用Recycler的scrapView方法，这个方法正是对mAttachScrap的添加。

# RecycleView的滑动复用

不同于ListView，RecycleView由于一切的View来源于Recycler自己封装的方法，这让其他调用者无需关心内部缓存细节
我们知道在自己实现LayoutManager的时候为了要让列表能滚动都会实现scrollVerticallyBy或者scrollHorizontallyBy
而要填充View的时候只需要调用fill方法就好了。并不需要在LayoutManager去手动实现这个过程的回收和复用

简单追踪一下RecycleView的滑动，从onTouchEvent中的ACTION_MOVE分支调用scrollByInternal方法：
```
@Override
    public boolean onTouchEvent(MotionEvent e) {

    ...

    case MotionEvent.ACTION_MOVE:
           ...
           if (scrollByInternal(
                   canScrollHorizontally ? dx : 0,
                   canScrollVertically ? dy : 0,
                   vtev)) {
               getParent().requestDisallowInterceptTouchEvent(true);
           }
           ...

    ...

    }
```

看下scrollByInternal方法,会根据不同的方向调用scrollHorizontallyBy或者scrollVerticallyBy：
```
boolean scrollByInternal(int x, int y, MotionEvent ev) {
        if (x != 0) {
            consumedX = mLayout.scrollHorizontallyBy(x, mRecycler, mState);
            unconsumedX = x - consumedX;
        }
        if (y != 0) {
            consumedY = mLayout.scrollVerticallyBy(y, mRecycler, mState);
            unconsumedY = y - consumedY;
        }
}
```

我们这里看下LinearLayoutManager的scrollVerticallyBy方法：
```
@Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler,
            RecyclerView.State state) {
        if (mOrientation == HORIZONTAL) {
            return 0;
        }
        return scrollBy(dy, recycler, state);
    }
```
scrollBy方法：
```
int scrollBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
    ...
    updateLayoutState(layoutDirection, absDy, true, state);
    final int consumed = mLayoutState.mScrollingOffset
                + fill(recycler, mLayoutState, state, false);
    ...
 }
```
也就是在滚动过程中，每一次的MOVE事件（一个滚动里面很多次MOVE事件）会根据滑动的距离计算需不需要填充布局，而fill方法则就是来填充的，并返回填充的多少像素
而具体是不是重新创建ViewHolder还是复用，都在fill方法里面的layoutChunk方法里的这句：
```
View view = layoutState.next(recycler);
```
被这句代码给抽象了，使得LayoutManager不用关心View从哪里获取。所以我们分析RecyclerView的滚动复用过程，跟前面分析的LayoutManager布局是差不多的
当然这样篇幅原因，分析得特别粗糙，并没有去仔细分析这个过程，包括Fling，还有填充像素的计算等等

