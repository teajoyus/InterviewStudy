package com.example.interviewstudy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;

import java.util.List;

public class ListActivity extends BaseActivity {
    ListView lv;
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_list);
        Log.i("ListActivity","contenxt:"+LayoutInflater.from(this).getContext().toString());
        lv = findViewById(R.id.lv);

        LayoutInflater.from(this).setFactory2(new LayoutInflater.Factory2() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                return null;
            }

            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                if(name.equals("TextView")){
                    Button button = new Button(context,attrs);
                    return button;
                }
                return null;
            }
        });
        lv.setStackFromBottom(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("lc_miao","setAdapter");
                lv.setAdapter(new MyAdapter());
            }
        },5000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("lc_miao","setAdapter");
                lv.setAdapter(new MyAdapter2());
            }
        },10000);
    }
    public class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 30;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return super.hasStableIds();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i("lc_miao","getView position:"+position);
            TextView tv =null;
            if(convertView==null){
                Log.i("lc_miao","convertView==null");
                 tv = new TextView(ListActivity.this);
                 convertView = tv;
                 tv.setGravity(Gravity.CENTER);
                 tv.setTextSize(40);
                tv.setTag(position);
            }else{
                tv = (TextView) convertView;
                Log.i("lc_miao","convertView pos:"+convertView.getTag());
            }
            tv.setText("我是 "+position);
            return tv;
        }
    }
    public class MyAdapter2 extends BaseAdapter{

        @Override
        public int getCount() {
            return 30;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i("lc_miao","MyAdapter2 getView position:"+position);
            Button tv =null;
            if(convertView==null){
                Log.i("lc_miao","convertView==null");
                 tv = new Button(ListActivity.this);
                 convertView = tv;
                 tv.setGravity(Gravity.CENTER);
                 tv.setTextSize(40);
                tv.setTag("Button"+position);
            }else{
                tv = (Button) convertView;
                Log.i("lc_miao","convertView pos:"+convertView.getTag());
            }
            tv.setText("我是 "+position);
            return tv;
        }
    }
    public static class MyListView extends ListView{

        public MyListView(Context context) {
            super(context);
        }

        public MyListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            Log.i("lc_miao","onMeasure");
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            Log.i("lc_miao","onLayout");
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
//            Log.i("lc_miao","onDraw");
        }

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
            Log.i("lc_miao","layoutChildren");
        }

//        @Override
//        protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
//            super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
//            Log.i("lc_miao","measureChild");
//        }
        private int startY;
        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            //注意并不一定获取到down事件，因为可能被button获取了
            if(ev.getAction()==MotionEvent.ACTION_DOWN){
                Log.i("lc_miao","22222222222222222222222222222222222222222222222222222222222222222222222222");
                startY = (int) ev.getY();
            }else if(ev.getAction()==MotionEvent.ACTION_MOVE){
                Log.i("lc_miao","距离："+((int)ev.getY() - startY)+", startY = "+startY+", curr:"+(int)ev.getY());
            }
            return super.onTouchEvent(ev);
        }
    }
}
