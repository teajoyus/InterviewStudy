package com.example.recycleviewdemo;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author:mihon
 * Time: 2019\3\27 0027.18:34
 * Description:This is MyLayoutManager
 */
public class MyLayoutManager extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    /**
     * 故意写成了offestY+=height - 30; 会看到布局重叠的部分
     * 然后里面的getItemCount()应该优化才成可见部分，不然这样子就相当于绘制全部了,offestY也叠加到好几万
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if(getItemCount()==0){
            detachAndScrapAttachedViews(recycler);
            return;
        }
        //在动画
        if(state.isPreLayout()){
            return;
        }
        int offestY = getPaddingTop();
        for (int i = 0; i < getItemCount(); i++) {
            log("i = "+i+", offestY = "+offestY);
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view,0,0);
            int width = getDecoratedMeasuredWidth(view);
            int height =  getDecoratedMeasuredHeight(view);
            layoutDecorated(view,0,offestY,width,offestY +height);
            offestY+=height - 30;

        }
        totalHeight = offestY;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    private int getVerticalSpace() {
        //计算RecyclerView的可用高度，除去上下Padding值
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(getItemCount()==0)
        offsetChildrenVertical(-dy);
        return dy;
    }

    int verticalScrollOffset;
    public int totalHeight = 0;
    public int scrollVerticallyBy2(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        return super.scrollVerticallyBy(dy, recycler, state);
        log("scrollVerticallyBy dy:"+dy);
        //列表向下滚动dy为正，列表向上滚动dy为负，这点与Android坐标系保持一致。
        //实际要滑动的距离
        int travel = dy;

        log("dy = " + dy);
        //如果滑动到最顶部
        if (verticalScrollOffset + dy < 0) {
            travel = -verticalScrollOffset;
        } else if (verticalScrollOffset + dy > totalHeight - getVerticalSpace()) {//如果滑动到最底部
            travel = totalHeight - getVerticalSpace() - verticalScrollOffset;
    }

        //将竖直方向的偏移量+travel
        verticalScrollOffset += travel;

        // 调用该方法通知view在y方向上移动指定距离
        offsetChildrenVertical(-travel);

        return travel;
    }

    private void log(String str){
        Log.i("MyLayoutManager",str);
    }
}
