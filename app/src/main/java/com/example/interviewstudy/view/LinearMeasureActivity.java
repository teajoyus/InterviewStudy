package com.example.interviewstudy.view;

import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.interviewstudy.R;

/**
 * Author:mihon
 * Time: 2019\1\25 0025.11:38
 * Description:This is SlideViewActivity
 */
public class LinearMeasureActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("2222222222222","onCreate "+this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linear_measure);
    }

    @Override
    protected void onResume() {
        Log.i("2222222222222","onResume "+this);
        super.onResume();
    }

    public static class MyView extends View{

        public MyView(Context context) {
            super(context);
        }

        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            printCallStatck();
            Log.i("2222222222222","onMeasure "+this);
        }
    }
    public static void printCallStatck() {
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {
//               Log.i("2222222222222",stackElements[i].getClassName()+"/t");
//               Log.i("2222222222222",stackElements[i].getFileName()+"/t");
//               Log.i("2222222222222",stackElements[i].getLineNumber()+"/t");
                Log.i("2222222222222",stackElements[i].getClassName()+"/t"+stackElements[i].getMethodName());
                Log.i("2222222222222","-----------------------------------");
            }
        }
    }
}
