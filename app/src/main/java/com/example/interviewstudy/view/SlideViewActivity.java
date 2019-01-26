package com.example.interviewstudy.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.interviewstudy.R;

/**
 * Author:mihon
 * Time: 2019\1\25 0025.11:38
 * Description:This is SlideViewActivity
 */
public class SlideViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        Button view = new MyView(this);
//        view.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
//        layout.addView(view);
//        view.setText("Slide");
//        setContentView(layout);
        final View view = new View(this);
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(view);
        setContentView(layout);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.i("22222222","layout height:"+layout.getMeasuredHeight());
                Log.i("22222222","view height:"+view.getMeasuredHeight());
            }
        });
    }

    class MyView extends android.support.v7.widget.AppCompatButton {

        public MyView(Context context) {
            super(context);
        }

        public MyView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        float lastY, lastX;

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
}
