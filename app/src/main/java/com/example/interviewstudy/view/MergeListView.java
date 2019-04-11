package com.example.interviewstudy.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.interviewstudy.R;

/**
 * Author:mihon
 * Time: 2019\3\6 0006.17:23
 * Description:This is MergeListView
 */
public class MergeListView extends LinearLayout {
    private static final String TAG ="MergeListView";

    public MergeListView(Context context) {
        super(context);
        init(context);
    }

    public MergeListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MergeListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){

        Log.i(TAG,"init");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                LayoutInflater.from(getContext()).inflate(R.layout.merge_test,MergeListView.this);
                Log.i(TAG,"init getChildCount():"+getChildCount());
            }
        },2000);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i(TAG,"onFinishInflate getChildCount():"+getChildCount());
    }
    public static class MergeButton extends AppCompatButton {

        public MergeButton(Context context) {
            super(context);
        }

        public MergeButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MergeButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
            Log.i(TAG,"MergeButton onFinishInflate:");
        }
    }
}
