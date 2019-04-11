刘望舒：http://liuwangshu.cn/application/view/1-coordinate-system.html

Android的坐标系是以左上角为原点的，从原点以右为x轴正方向，从原点向下为y轴的正方向（有区别与数学上的坐标系的第四象限）

![](https://upload-images.jianshu.io/upload_images/1417629-0898e744ad00732d.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
安卓的坐标原点是在左上角

X轴从左往右为正方向 递增
Y轴从上往下为正方向 递增

所以比如下滑操作，那么一开始的y轴坐标是小于结束后的Y轴坐标的

如果前后进行相减（Y后-Y前 - Y差），那么如果得到的差值是正数，则代表下滑

在MotionEvent中获取的getRawX()和getRawY()获取的坐标都是Android坐标系的坐标



![](https://upload-images.jianshu.io/upload_images/1417629-26a97758783d014c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

正对于距离父布局的距离：
getTop()：获取View自身顶边到其父布局顶边的距离
getLeft()：获取View自身左边到其父布局左边的距离
getRight()：获取View自身右边到其父布局左边的距离
getBottom()：获取View自身底边到其父布局顶边的距离


针对于整个屏幕的坐标：
getRawX()和getRawY()

针对于View内的坐标：
getX()：获取点击事件距离控件左边的距离，即视图坐标
getY()：获取点击事件距离控件顶边的距离，即视图坐标




