package com.example.interviewstudy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.MyLogger;
import com.example.interviewstudy.R;

/**
 * Author:mihon
 * Time: 2019\1\25 0025.11:38
 * Description:This is SlideViewActivity
 */
public class SurfaceViewActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log_i("onCreate");
        setTitle(getClass().getSimpleName());
        RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(layout);
        SurfaceViewCanvas mySurfaceView = new SurfaceViewCanvas(this);
        mySurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(mySurfaceView);
//        setContentView(R.layout.activity_surface);
    }

    /**
     * Author:mihon
     * Time: 2019\2\15 0015.15:53
     * Description:This is SurfaceViewCanvas
     */
    public class SurfaceViewCanvas extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
        //创建画笔对象和路径
        private Paint p = new Paint();
        private Path path = new Path();
        private int index;
        private boolean drawing;
        public SurfaceViewCanvas(Context context) {
            super(context);
            initView();
        }

        public SurfaceViewCanvas(Context context, AttributeSet attrs) {
            super(context, attrs);
            initView();
        }

        private void initView() {
            //使用SurfaceView添加回调函数
            getHolder().addCallback(this);
            //画笔初始化，设置画笔的颜色
            p.setColor(Color.RED);
            //初始化画笔的大小
            p.setTextSize(10);
            p.setStrokeWidth(10);
            //给画笔清理锯齿
            p.setAntiAlias(true);
            p.setStyle(Paint.Style.STROKE);
            //添加监听
            setOnTouchListener(this);
        }

        //创建绘制方法
        public void draw() {
            //要绘制的话肯定要有一个画布,要通过getHolder()锁定画布,
            Canvas canvas = getHolder().lockCanvas();
            //初始化画布的颜色
            canvas.drawColor(Color.WHITE);
            //用drawPath进行绘制
            canvas.drawPath(path, p);
            //绘制结束后要解锁画布

            getHolder().unlockCanvasAndPost(canvas);
        }

        //这个方法用来清理画布
        public void clear() {
            //清除路径
            path.reset();
            //路径重置后调用draw方法
            draw();

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //在surface被改变的时候调用方法的执行
//            draw();
            drawing = true;
//            new Thread(){
//                @Override
//                public void run() {
//                    super.run();
//                    while (drawing){
//                        draw();
//                        try {
//                            Thread.sleep(16);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }.start();

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            drawing = false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                //处理按下事件
                case MotionEvent.ACTION_DOWN:
                    //按下的时候通过moveTo()绘制按下的这个点,获取按下点的X和Y坐标
                    path.moveTo(event.getX(), event.getY());
                    //获取之后调用draw()方法进行绘制
//                    draw();
                    break;

                //在移动的时候进行绘制
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(event.getX(), event.getY());
//                    draw();
                    break;
            }
            return true;
        }
    }
}
