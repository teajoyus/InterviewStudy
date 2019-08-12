
# 前言
    ListView作为一个常用的列表控件，虽然现在基本被RecycleView取代了，但是它的内部思想还是很多值得我们学习的地方。既然新出了RecycleView来代替ListView，我们就要摸清他们两个之间的区别。
    鉴于篇幅关系，本篇先分析ListView的原理。

# 认识RecycleBin机制

解析ListView源码前，我们有必要先探究里面的RecycleBin机制，某种程度上讲，它正是ListView的精髓所在。

RecycleBin是在AbsListView里的一个内部类，SDK是这么注释的：
```
    /**
     * The RecycleBin facilitates reuse of views across layouts. The RecycleBin has two levels of
     * storage: ActiveViews and ScrapViews. ActiveViews are those views which were onscreen at the
     * start of a layout. By construction, they are displaying current information. At the end of
     * layout, all views in ActiveViews are demoted to ScrapViews. ScrapViews are old views that
     * could potentially be used by the adapter to avoid allocating views unnecessarily.
     **/
```

大致上就是说，RecycleBin会存储两种View，一种是ActiveViews，也就是目前正显示在屏幕上的，由我们自己创建的View。

另外一种是ScrapViews，也就是被废弃掉的View，它是当ActiveViews被移除之后就变成了ScrapViews，ScrapViews可以避免adapter去创建没必要的View

这个我们都理解，就是要记录当前屏幕上的View在ActiveViews里，当ListView向上滑动时，底部出现新的item会去ScrapViews中拿，而不重新创建。而顶部的ActiveView划出屏幕中，自动变化为ScrapView

它有以下几个重要的方法：
- fillActiveViews()
- getActiveView()
- addScrapView()
- getScrapView()
- setViewTypeCount()
- clear()

**fillActiveViews()**
```
   void fillActiveViews(int childCount, int firstActivePosition) {
            if (mActiveViews.length < childCount) {
                mActiveViews = new View[childCount];
            }
            mFirstActivePosition = firstActivePosition;

            //noinspection MismatchedReadAndWriteOfArray
            final View[] activeViews = mActiveViews;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                AbsListView.LayoutParams lp = (AbsListView.LayoutParams) child.getLayoutParams();
                // Don't put header or footer views into the scrap heap
                if (lp != null && lp.viewType != ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                    // Note:  We do place AdapterView.ITEM_VIEW_TYPE_IGNORE in active views.
                    //        However, we will NOT place them into scrap views.
                    activeViews[i] = child;
                    // Remember the position so that setupChild() doesn't reset state.
                    lp.scrappedFromPosition = firstActivePosition + i;
                }
            }
        }
```

上面的源码，方法接收两个参数
- childCount 要存储的View的数量，这里其实会传入进来ListView在屏幕上能显示的数量
- firstActivePosition 第一个可见View的postion

mActiveViews会保证储存的数量是ListView在屏幕上能显示的数量，然后把0到childCount的child view 赋值给mActiveViews

如果mActiveViews.length < childCount，比如ListView可以显示10个条目，但是只有5个数据，那么这里mActiveViews也是5

等下次假如ListView有100个条目了，那么mActiveViews就要扩容为10了。



**getActiveView()**
```
 View getActiveView(int position) {
            int index = position - mFirstActivePosition;
            final View[] activeViews = mActiveViews;
            if (index >=0 && index < activeViews.length) {
                final View match = activeViews[index];
                //被get完之后被置空
                activeViews[index] = null;
                return match;
            }
            return null;
        }
```
通过具体的位置得到activeViews数组中对应存储的View
特别注意的是被get完之后被置空，也就是说，在activeViews数组元素没有被更新之前，每个对应的位置只能被get一次拿到View。为什么呢？我们留着这个问题

**addScrapView()**
void addScrapView(View scrap, int position) {
    ...
    if (mViewTypeCount == 1) {
        mCurrentScrap.add(scrap);
    } else {
        mScrapViews[viewType].add(scrap);
    }
    ...
}
addScrapView方法有几十行代码，我们只抓这个核心代码就好了，可以看出。当mViewTypeCount是1的时候是被加到mCurrentScrap中，而mViewTypeCount大于1的时候则会加到mScrapViews中

我们知道，在Adapter中允许我们使用多个布局类型，我们可以用getItemViewType()方法区分，这里的mViewTypeCount会对应这个数字。

当有多种布局样式的时候，这个时候要回收这些View肯定是需要分类的，不然复用的时候，不同样式的view就乱了。所以用mScrapViews用来记录有多个布局类型的时候

而mCurrentScrap来记录只有一种类型的时候。

**getScrapView()**

```
   View getScrapView(int position) {
        final int whichScrap = mAdapter.getItemViewType(position);
        if (whichScrap < 0) {
            return null;
        }
        if (mViewTypeCount == 1) {
            return retrieveFromScrap(mCurrentScrap, position);
        } else if (whichScrap < mScrapViews.length) {
            return retrieveFromScrap(mScrapViews[whichScrap], position);
        }
        return null;
    }
```

可以看到getScrapView方法中会判断mViewTypeCount是否等于1，来选择用mCurrentScrap还是mScrapViews，最终都会调用retrieveFromScrap方法来获得View

这个retrieveFromScrap方法如下：
```
private View retrieveFromScrap(ArrayList<View> scrapViews, int position) {
            final int size = scrapViews.size();
            if (size > 0) {
                // See if we still have a view for this position or ID.
                // Traverse backwards to find the most recently used scrap view
                for (int i = size - 1; i >= 0; i--) {
                    final View view = scrapViews.get(i);
                    final AbsListView.LayoutParams params =
                            (AbsListView.LayoutParams) view.getLayoutParams();
                    //优先匹配stable id
                    if (mAdapterHasStableIds) {
                        final long id = mAdapter.getItemId(position);
                        if (id == params.itemId) {
                            return scrapViews.remove(i);
                        }
                     //其次再匹配有没有回收过的相同位置
                    } else if (params.scrappedFromPosition == position) {
                        final View scrap = scrapViews.remove(i);
                        clearScrapForRebind(scrap);
                        return scrap;
                    }
                }
                //实在不行再取最后面一个
                final View scrap = scrapViews.remove(size - 1);
                clearScrapForRebind(scrap);
                return scrap;
            } else {
                return null;
            }
        }
```
retrieveFromScrap方法源码可以发现，再获取之前scrap View的时候，会经历几个优先级判断：
- 先匹配是否有存在itemId相同的scrap View
- 如果不存在itemId相同的，则判断存不存在相同位置的scrap View
- 如果相同位置的scrap View也没有，那么只能去取列表中的最后一个了

**setViewTypeCount()**

```
  public void setViewTypeCount(int viewTypeCount) {
        if (viewTypeCount < 1) {
            throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
        }
        //noinspection unchecked
        ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
        for (int i = 0; i < viewTypeCount; i++) {
            scrapViews[i] = new ArrayList<View>();
        }
        mViewTypeCount = viewTypeCount;
        mCurrentScrap = scrapViews[0];
        mScrapViews = scrapViews;
    }
```

可以发现setViewTypeCount其实是对scrapViews和mCurrentScrap的初始化，初始化后在其他地方会根据viewTypeCount是否等于1来判断是否用mCurrentScrap还是mScrapViews

从mCurrentScrap = scrapViews[0];上看，可以看出本质上mCurrentScrap只是指向scrapViews数组的第0个而已，并没有自己单独new ArrayList

**clear()**
```
  void clear() {
    if (mViewTypeCount == 1) {
        final ArrayList<View> scrap = mCurrentScrap;
        clearScrap(scrap);
    } else {
        final int typeCount = mViewTypeCount;
        for (int i = 0; i < typeCount; i++) {
            final ArrayList<View> scrap = mScrapViews[i];
            clearScrap(scrap);
        }
    }

    clearTransientStateViews();
 }
```
clear方法不难看出，它就是一个清除所有Scrap View的方法。当我们重新setAdapter的时候会就应该clear掉原先的View。

滤清了RecycleBin这几个重要方法后，我们心中对这个机制就有个大概的框架了

- 猜想是会调用fillActiveViews()方法来记录当前在屏幕上的View，之后可以用getActiveView来获取。
这样做可能是为了避免两次onLayout过程使屏幕上的View重复创建，而在getActiveView方法之后置空，这样才可以回收内存。

- 在顶(底)部的View被滑出屏幕的时候，会调用addScrapView来回收一个View

- 同时有新元素被滑进屏幕时通过getScrapView方法来获取一个废弃过的View，达到复用的效果。

究竟是不是这样？我们分析完源码就知道

# ListView的布局方式
我们知道 每个显示在界面上的View，都会经历onMeasure、onLayout、onDraw三个过程。


看ListView的onMeasure流程，并没有啥值得深挖的地方

onDraw方面，ListView本身除了绘制下分割线这些，并不负责每个item的绘制，所以它的重点和强大之处应该是在于如何布局，也就是onLayout

ListView并没有直接实现onlayout，而它的基类AbsListView实现了，并且在onLayout里面调用了一个layoutChildren的空方法

这个空方法交由ListView来实现，我们看下layoutChildren方法：

```
    @Override
    protected void layoutChildren() {

     ....
    final int childCount = getChildCount();
    final RecycleBin recycleBin = mRecycler;
    // 1
    if (dataChanged) {
        for (int i = 0; i < childCount; i++) {
            recycleBin.addScrapView(getChildAt(i), firstPosition+i);
        }
    } else {
        recycleBin.fillActiveViews(childCount, firstPosition);
    }
    // Clear out old views
    detachAllViewsFromParent();
     ...

     default:
        if (childCount == 0) {
            if (!mStackFromBottom) {
                final int position = lookForSelectablePosition(0, true);
                setSelectedPositionInt(position);
                // 2
                sel = fillFromTop(childrenTop);
            } else {
                final int position = lookForSelectablePosition(mItemCount - 1, false);
                setSelectedPositionInt(position);
                sel = fillUp(mItemCount - 1, childrenBottom);
            }
        } else {
            if (mSelectedPosition >= 0 && mSelectedPosition < mItemCount) {
                sel = fillSpecific(mSelectedPosition,
                        oldSel == null ? childrenTop : oldSel.getTop());
            } else if (mFirstPosition < mItemCount) {
                sel = fillSpecific(mFirstPosition,
                        oldFirst == null ? childrenTop : oldFirst.getTop());
            } else {
                sel = fillSpecific(0, childrenTop);
            }
        }
        break;
    }
    // Flush any cached views that did not get reused above
      recycleBin.scrapActiveViews();
    ...

    }
```

这里面会列出几个layoutMode，比如setSelection的时候的layoutMode，这里正常我们setAdapter的话就会走这个LAYOUT_NORMAL，

我在代码里面只贴出两个重点的地方，直接看这个流程

首先第一个重点是：
```
    if (dataChanged) {
        for (int i = 0; i < childCount; i++) {
            recycleBin.addScrapView(getChildAt(i), firstPosition+i);
        }
    } else {
        recycleBin.fillActiveViews(childCount, firstPosition);
    }
  // Clear out old views
    detachAllViewsFromParent();
```


mDataChanged就是只有在数据要发生改变的时候才会设置成true（mDataChanged在AbsListView被多处地方设置标记,这里不深究）

第一次执行的话，View还没有被加载到ListView上，所以这里dataChanged为false，会执行recycleBin.fillActiveViews(childCount, firstPosition);

childCount是从getChildCount中来的，第一次是0，所以这里即使调用了recycleBin.fillActiveViews也没啥用

但是下一次有初始数据了的话，fillActiveViews就有作用了，我们知道它记录了当前ListView上显示的View的引用。

然后注意接下来会调用detachAllViewsFromParent方法移除掉ListView所有的View。
要保证这次layout的过程不会被上一次所影响，所以会移除掉所有View。
这个就跟getActiveView挂钩了，因为ListView上的view被移除了，但是却被记录在了RecycleBin里面，等下第二次布局的时候要用到View，就可以直接拿这个View来做改动，而没必要重新new一个


第二个重点我们看下

第一次的话，childCount肯定是0,

mStackFromBottom变量是表示列表要从头开始显示，还是从尾部开始。
mStackFromBottom貌似不常用，如果我们想从尾部开始显示，对应的方法是：
```
lv.setStackFromBottom(true);
```
默认是false，所以会执行这个分支
```
 if (childCount == 0) {
            if (!mStackFromBottom) {
                final int position = lookForSelectablePosition(0, true);
                setSelectedPositionInt(position);
                // 2
                sel = fillFromTop(childrenTop);
            }
        }
```



重点的方法就来到了fillFromTop方法，这里先插播一下接下来调用的这句代码recycleBin.scrapActiveViews();

在上面我们讲RecycleBin的时候没讲这个方法，scrapActiveViews()方法就是把多余的ActiveView转化成ScrapView，因为到这一步已经是布局完成了，没必要去记录ActiveView了

回到上面来，那么fillFromTop方法的源码如下：
```
  private View fillFromTop(int nextTop) {
        mFirstPosition = Math.min(mFirstPosition, mSelectedPosition);
        mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
        if (mFirstPosition < 0) {
            mFirstPosition = 0;
        }
        return fillDown(mFirstPosition, nextTop);
    }
```

注意传递的nextTop的值是来自：
```
final int childrenTop = mListPadding.top;
```
通常我们不设置ListView的paddingTop的话，那么这里传进来的nextTop就是0，

fillFromTop方法简单判断了下起始位置的逻辑后，调用了fillDown方法：
```
   private View fillDown(int pos, int nextTop) {
        View selectedView = null;

        int end = (mBottom - mTop);
        if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
            end -= mListPadding.bottom;
        }

        while (nextTop < end && pos < mItemCount) {
            // is this the selected item?
            boolean selected = pos == mSelectedPosition;
            View child = makeAndAddView(pos, nextTop, true, mListPadding.left, selected);

            nextTop = child.getBottom() + mDividerHeight;
            if (selected) {
                selectedView = child;
            }
            pos++;
        }

        setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount() - 1);
        return selectedView;
    }
```

源码不多，都看看下。

首先是end变量，取的是mBottom - mTop，相当于getHeight()，如果我们让ListView铺满屏幕，那么就是一个屏幕的高度了

而在while循环里面有： nextTop = child.getBottom() + mDividerHeight;

然后进来一个循环：while (nextTop < end && pos < mItemCount)

判断nextTop < end ，nextTop 是我们传进来的值，通常来说一开始的话 也没设置paddingTop，它就是0

可以发现**nextTop就是记录了下一个元素要布局的位置**。

第二个条件是pos < mItemCount 这个是要确保小标一定得小于长度 没毛病。

因为可能没那么多元素，还不能布满listView的高度。

到这里我们也知道了ListView的布局规则：**不管ListView有多少个元素，总是只布局当前能显示在ListView上的元素，其余的元素不会被布局**

# ListView的元素创建流程
接下来的重点就是这一句了：
```
 View child = makeAndAddView(pos, nextTop, true, mListPadding.left, selected);
```

它决定了ListView是如何获取View的，makeAndAddView方法如下：

```
    private View makeAndAddView(int position, int y, boolean flow, int childrenLeft,
            boolean selected) {
        if (!mDataChanged) {
            // Try to use an existing view for this position.
            final View activeView = mRecycler.getActiveView(position);
            if (activeView != null) {
                // Found it. We're reusing an existing child, so it just needs
                // to be positioned like a scrap view.
                setupChild(activeView, position, y, flow, childrenLeft, selected, true);
                return activeView;
            }
        }

        // Make a new view for this position, or convert an unused view if
        // possible.
        final View child = obtainView(position, mIsScrap);

        // This needs to be positioned and measured.
        setupChild(child, position, y, flow, childrenLeft, selected, mIsScrap[0]);

        return child;
    }
```

这里判断mDataChanged，一开始是false，所以会走if里面的逻辑，但是第一次layout时 mRecycler.getActiveView(position)肯定是为null

如果是第二次layout了话，这里有值了也就拿得到了。

而ListView在布局的时候需要先清理掉旧的View，再重新布局。

这种情况下已经调用过mRecycler.fillActiveViews方法缓存了当前屏幕上的View了，在这里做恢复就OK了，而不用重复创建

如果获取不到ActiveView，通常就是第一次的时候。则只能到obtainView(position, mIsScrap);这里拿了。

我们看下obtainView方法：
```
View obtainView(int position, boolean[] outMetadata) {

        ...

        final View scrapView = mRecycler.getScrapView(position);
        final View child = mAdapter.getView(position, scrapView, this);
        if (scrapView != null) {
            if (child != scrapView) {
                // Failed to re-bind the data, return scrap to the heap.
                mRecycler.addScrapView(scrapView, position);
            } else if (child.isTemporarilyDetached()) {
                outMetadata[0] = true;

                // Finish the temporary detach started in addScrapView().
                child.dispatchFinishTemporaryDetach();
            }
        }

       ...

        return child;
    }
```

我们挑出了以上的重点方法，看看这两句：
```
        final View scrapView = mRecycler.getScrapView(position);
        final View child = mAdapter.getView(position, scrapView, this);
```

先从RecyclerBin中获取一个scrapView，然后调用Adapter中我们熟悉的getView方法。
我们平时写getView方法的时候会都判断convertView==null来复用，这里就是会去复用一个已经废弃过的View，让我们在getView方法里面不需要再去创建
当然 一开始也拿不到废弃的View，所以ListView能显示几个条目，这里就会创建几个View了

然后下面再做了一层判断：
```
 if (child != scrapView) {
        // Failed to re-bind the data, return scrap to the heap.
        mRecycler.addScrapView(scrapView, position);
 }
```
尽管把废弃的View给我们调出来用，但是我们不一定会用它，如果没用它的话而去创建个新的View，那么这里就把新的加入进来

不管是走mRecycler.getActiveView拿到activeView，还是走了obtainView方法，接下来都会调用setupChild方法，该方法也比较长。也不是我们的重点，所以不再说了。

里面最主要的就是调用了addViewInLayout方法，最终调用addViewInner方法完成child的添加（我们熟悉的addView方法最终也是调用addViewInner方法）

# ListView滑动加载过程

上面我们只是讲了ListView在初始化数据的过程，那如果ListView有100条数据，我们在滑动的过程中，是怎么样的呢？

我们找下滑动事件，发现是放在了AbsListView的onTouchEvent里面，由于我们关心的是滑动的过程，也就是ACTION_MOVE。
（注意低版本的SDK这里好像对各个事件单独封装方法）
所以会调用onTouchMove()方法，在里面判断了mTouchMode。


正常来讲，我们手指滑动的时候mTouchMode是TOUCH_MODE_SCROLL,那么会执行scrollIfNeeded方法

这个方法也很长，我们没办法去仔细分析它的过程。我们要抓的是重点
```
 private void scrollIfNeeded(int x, int y, MotionEvent vtev){
    ...
    int rawDeltaY = y - mMotionY;
    final int deltaY = rawDeltaY;
    ...
    int incrementalDeltaY =
         mLastY != Integer.MIN_VALUE ? y - mLastY + scrollConsumedCorrection : deltaY;
    if (incrementalDeltaY != 0) {
         atEdge = trackMotionScroll(deltaY, incrementalDeltaY);
    }
    ...
    mLastY = y + lastYCorrection + scrollOffsetCorrection;
 }
```

scrollIfNeeded的重点在这里，当然它还有其他各种复杂的滑动情况我也分析不了。

看代码mMotionY是按下的时候记录的y坐标，那么deltaY就是手指滑动的距离

mLastY是记录上一次滑动的距离

所以incrementalDeltaY也就是一个增量，表名这个move事件在y轴上偏移了多少

然后会把eltaY（手指滑动的距离）和incrementalDeltaY（增量）传递给trackMotionScroll方法，剩下的逻辑在里面完成

```
boolean trackMotionScroll(int deltaY, int incrementalDeltaY) ｛

    ...
    final int height = getHeight() - mPaddingBottom - mPaddingTop;
   if (incrementalDeltaY < 0) {
            incrementalDeltaY = Math.max(-(height - 1), incrementalDeltaY);
        } else {
            incrementalDeltaY = Math.min(height - 1, incrementalDeltaY);
        }

    ...

    final boolean down = incrementalDeltaY < 0;

    if (down) {
        int top = -incrementalDeltaY;
        if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
            top += listPadding.top;
        }
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getBottom() >= top) {
                break;
            } else {
               ...
               int position = firstPosition + i;
                mRecycler.addScrapView(child, position);
            }
        }
    } else {
        int bottom = getHeight() - incrementalDeltaY;
        if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
            bottom -= listPadding.bottom;
        }
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getTop() <= bottom) {
                break;
            } else {
                ...
                int position = firstPosition + i;
                mRecycler.addScrapView(child, position);
            }
        }
    }
   final int absIncrementalDeltaY = Math.abs(incrementalDeltaY);
        if (spaceAbove < absIncrementalDeltaY || spaceBelow < absIncrementalDeltaY) {
            fillGap(down);
        }
｝
```

同样，我精简掉了N多代码，让我们抓重点来看。

我们知道传进来了incrementalDeltaY是个增量，在里面会判断incrementalDeltaY是否小于0

我们知道，**Android的坐标系是以左上角为原点的，从原点以右为x轴正方向，从原点向下为y轴的正方向（有区别与数学上的坐标系的第四象限）**

所以我们手指在往下滑的时候，y轴的数值会递增，那么他们之间的差值（end - start）就是大于0的

相反往上滑的话那么差值就是个负数。

在这里判断incrementalDeltaY小于0，那么就是手指往上滑，则down为true，这里的down是针对于ListView来说，对它来说就是往下滚动

这时候会遍历ListView中当前的view，如果直到遇到child.getBottom() >= top才跳出，怎么理解？

 top就是  top = -incrementalDeltaY;; 也就是这次到来的move事件滑动的距离

 而child.getBottom()就是与ListView顶部的距离，

 如果child距离顶部的距离比这次滑动事件的距离还小，说明顶部的这个View被划出了屏幕

 也就是child.getBottom() < top那就是有child被滑出顶部，需要回收

 这个时候就调用了mRecycler.addScrapView(child, position);来回收这个child

 当下个child的底部距离顶部比这次滑动事件的距离大，则说明还没滑出屏幕，那么后面的更不会滑出屏幕，所以就跳出循环


 讲完了手指上滑这个过程，那么手指下滑的时候 也就是down=false也是一样的道理

只不过判断的是从底部的child开始判断它的child.getTop()是否已经划出屏幕了，

在这个滑动的同时，有回收view，当然也有新的view进来啊，比如我们手指往上滑，ListView顶部的child被回收，同时底部也有child被添加进来，我们看下

逻辑就是在这个fillGap方法里：

```
   void fillGap(boolean down) {
        final int count = getChildCount();
        if (down) {
            int paddingTop = 0;
            if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
                paddingTop = getListPaddingTop();
            }
            final int startOffset = count > 0 ? getChildAt(count - 1).getBottom() + mDividerHeight :
                    paddingTop;
            fillDown(mFirstPosition + count, startOffset);
            correctTooHigh(getChildCount());
        } else {
            int paddingBottom = 0;
            if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
                paddingBottom = getListPaddingBottom();
            }
            final int startOffset = count > 0 ? getChildAt(0).getTop() - mDividerHeight :
                    getHeight() - paddingBottom;
            fillUp(mFirstPosition - 1, startOffset);
            correctTooLow(getChildCount());
        }
    }
```

这个方法里面会根据down的正负决定方向，从而决定调用fillDown还是fillUp，
而fillDown我们前面讲过，fillUp没讲但是也是同一个道理。

所以在这个move事件过程就是判断顶部或底部的child是否需要回收，然后再进行调用fillDown或fillUp来重新填充child







