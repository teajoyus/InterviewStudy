
# 对比


# ListView回收机制
之前已经写过一篇源码解析类的，这里就略过了



# RecycleView回收机制

比起ListView中的RecycleBin，RecycleView中使用的缓存类则就是Recycle，Recycle里面比起RecycleBin会多了一些用于缓存的集合容器，实际上，RecycleView正是采用了多级缓存的原理，下面我们分别对这几个缓存进行说明

## mAttachedScrap

缓存在屏幕上面的，因为每个ViewGroup都至少会onLayout两次，所以要避免重复的创建，因为它只是避免这样的情况而所存在的，在第二次layout过程完之后它也就被清空了。

它也是不用rebind的，因为本来就是屏幕上对应好的位置





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
























缓存机制对比：https://mp.weixin.qq.com/s/-CzDkEur-iIX0lPMsIS0aA
源码解析：https://blog.csdn.net/qq_23012315/article/details/50807224


RecycleView的onMeasure里面会进入LayoutManager的onLayoutChildren方法
从而让测量流程转交给LayoutManager

onMeasure-》dispatchLayoutStep2-》LayoutManager的onLayoutChildren


LinearLayoutManager的onLayoutChildren
fill方法是主要的
   layoutChunk()   -》之后调用measureChildWithMargins和layoutDecoratedWithMargins完成child的measure和layout
       里面调用LayoutState的next方法
            调用了RecyclerView.Recycler的getViewForPosition方法
              调用tryGetViewHolderForPositionByDeadline方法
                     缓存策略拿到view，拿不到缓存则就是Adapter的createViewHolder方法


这部分的总结：

- 测量流程是移交给LayoutManager的，没有设置LayoutManagr则不会测量子view

- 测量是分为正向和逆向的

//TODO 这里还不是很理解锚点和向上向下
- 测量就是先确定锚点，然后向上(fill)，向下(fill)，还有剩余空间的话则再(fill)
- LayoutManager获取view的方法是从RecyclerView的Recycle.next()方法中获取的,方法里面涉及到RecycleView的缓存策略，没有的话拿不到缓存则就是Adapter的createViewHolder方

- 如果RecyclerView宽高没有写死，onMeasure流程就会执行完子View的measure和Layout方法，onLayout仅仅是重置一些参数，如果写死，子View的measure和layout会延后到onLayout中执行。

- layoutChunk方法里面在进行next方法拿到view后，会先后调用measureChildWithMargins和layoutDecoratedWithMargins完成child的measure和layout

-在measureChildWithMargins方法中会调用RecyclerView.getItemDecorInsetsForChild，来完成child的四周边距，其中会调用到**RecyclerView.ItemDecoration.getItemOffsets**



# 四级缓存

## mAttachedScrap
缓存在屏幕上边的，因为每个ViewGroup都至少会onLayout两次，所以要避免重复的创建，因为它只是避免这样的情况而所存在的，在第二次layout过程完之后它也就被清空了

它也是不用rebind的，因为本来就是屏幕上对应好的位置



## CacheViews





























