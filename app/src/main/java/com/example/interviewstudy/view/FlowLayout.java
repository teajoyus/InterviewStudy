package com.example.interviewstudy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:mihon
 * Time: 2019\3\15 0015.9:22
 * Description:This is FlowLayout
 */
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
        log_i("onMeasure widthSize:" + widthSize);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = 0, height = 0;
        int childCount = getChildCount();
        int lineWidth = 0;
        int lineHeight = 0;
//        log_i("---------------------------------------------------------------------------------------------------------");
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
            log_i("lineWidth:" + lineWidth + ",lineHeight:" + lineHeight);
            log_i("MeasureSpec.getMode(widthMeasureSpec) == EXACTLY :" + (widthMode == MeasureSpec.AT_MOST));
            log_i("MeasureSpec.getMode(heightMeasureSpec) == EXACTLY :" + (heightMode == MeasureSpec.AT_MOST));
        }
        log_i("onMeasure height:" + height);
        //注意这里要带上padding来测量出真实的宽高，因为上面计算子View时的宽高是不包含padding的
//        log_i("width:" + width + ",height:" + height);
//        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : (width + getPaddingLeft() + getPaddingRight()), heightMode == MeasureSpec.EXACTLY ? heightSize : (height + getPaddingTop() + getPaddingBottom()));
        setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), height + getPaddingTop() + getPaddingBottom());

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        log_i("onLayout");
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
//            自己这么写 找了半天问题：child.layout(i * 100, i * 100,  child.getMeasuredWidth(), child.getMeasuredHeight());
            //原来是因为right和bottom只是用了getMeasuredWidth+getMeasuredHeight，而应该也要加上本身的left和top啊。。
//            child.layout(i * 100, i * 100, i * 100 + child.getMeasuredWidth(), i * 100 + child.getMeasuredHeight());
        }

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
            //TODO 这里还没有考虑第一个View就大于一行的情况
            //已经解决：在下面的 解决方法
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
            log_i("onLayout currentHeight:" + currentHeight);
        }

    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        log_i("onDraw");
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
//        log_i("requestLayout");
    }

    @Override
    public void invalidate() {
        super.invalidate();
//        log_i("invalidate");
    }

    @Override
    public void postInvalidate() {
        super.postInvalidate();
//        log_i("invalidate");
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        log_i("dispatchDraw");
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
//        return super.generateDefaultLayoutParams();
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * 每个ViewGroup都有自己的LayoutParams，一般重新ViewGroup的话，要注意这个默认生成的LayoutParams
     *
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
//            return super.generateLayoutParams(attrs);
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    protected static void log_i(String str) {
        Log.i("FlowLayout", str);
    }
}