刘望舒：http://liuwangshu.cn/application/view/2-sliding.html


# 第一种：layout（）
-----
我们只需要在onTouchEvent中通过getX和getY获取每次移动的偏移量，然后
调用一次layout（）
layout（）这个方法接收四个参数 对应的是getLeft、getButtom这些，是与父布局边距的距离
：
```

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                lastX = event.getX();
                lastY = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                int y_gap = (int) (event.getY() - lastY + 0.5f);
                int x_gap = (int) (event.getX() - lastX + 0.5f);
                //第一种
//                layout(getLeft() + x_gap, getTop() + y_gap, getRight() + x_gap, getBottom() + y_gap);
                //第二种
                offsetLeftAndRight(x_gap);
                offsetTopAndBottom(y_gap);
            }
            return super.onTouchEvent(event);
        }
    }

```


第二种 offsetLeftAndRight、offsetTopAndBottom

代码在上面给出了



第三种：通过改变layoutParams.leftMargin这种方式



以上都是通过改变Layout的形式来实现拖动



如果是自己滑动的话 可以用属性动画：

//ObjectAnimator中会去找mCustomView的setTranslationX方法然后设置值偏移值进去
ObjectAnimator.ofFloat(mCustomView,"translationX",0,300).setDuration(1000).start();


用scollTo与scollBy

scollTo(x,y)表示移动到一个具体的坐标点，而scollBy(dx,dy)则表示移动的增量为dx、dy。

这个过程是瞬间完成的，所以用户体验不大好
可以用Scroller来实现平滑的效果
Scroller本身是不能实现View的滑动的，它需要配合View的computeScroll()方法才能弹性滑动的效果

系统会在绘制View的时候在draw()方法中调用该方法，这个方法中我们调用父类的scrollTo()方法并通过Scroller来不断获取当前的滚动值，
每滑动一小段距离我们就调用invalidate()方法不断的进行重绘，重绘就会调用computeScroll()方法，这样我们就通过不断的移动一个小的距离并连贯起来就实现了平滑移动的效果

```
public CustomView(Context context, AttributeSet attrs) {
      super(context, attrs);
      mScroller = new Scroller(context);
  }
@Override
public void computeScroll() {
    super.computeScroll();
    if(mScroller.computeScrollOffset()){
        ((View) getParent()).scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
         //通过不断的重绘不断的调用computeScroll方法
         invalidate();
    }

    public void smoothScrollTo(int destX,int destY){
          int scrollX=getScrollX();
          int delta=destX-scrollX;
          //1000秒内滑向destX
          mScroller.startScroll(scrollX,0,delta,0,2000);
          invalidate();
      }
}
```

用Scroller的步骤如下：
1 、在自定义类new出一个：new Scroller(context);
2 、重写View的computeScroll方法，让它不断的间隔scrollTo一小段距离又invalidate
3、重写smoothScrollTo控制的速度