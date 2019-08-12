package com.example.interviewstudy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.interviewstudy.flutter.FlutterActivity;
import com.example.interviewstudy.jni.JniActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {
    RecyclerView rv;
    List<String> title;
    Map<String,Class> activitys;
    public int getInt(){
        return 2;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 2; i++) {
            int k = getInt();
            Context context = getBaseContext();
            for (int j = 0; j < 2; j++) {
                log_i(context.toString());
            }
        }
        Log.i("ListActivity","context:"+LayoutInflater.from(this).getContext().toString());
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        initData();
        rv.setAdapter(new MyAdapter());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initData(){
        activitys = new HashMap<>();
        title = new ArrayList<>();
        put("slide view", com.example.interviewstudy.view.SlideViewActivity.class);
        put("linearLayout measure", com.example.interviewstudy.view.LinearMeasureActivity .class);
        put("List View", com.example.interviewstudy.view.ListActivity.class);
        put("Rxjava study", com.example.interviewstudy.view.RxJavaActivity.class);
        put("SurfaceView study", com.example.interviewstudy.view.SurfaceViewActivity.class);
        put("launcher mode", com.example.interviewstudy.launchermode.LauncherModeActivity.class);
        put("Activity Router", com.example.interviewstudy.router.RouterDemoActivity.class);
        put("Activity Hook", com.example.interviewstudy.hook.HookActivity.class);
        put("Activity Fragment", com.example.interviewstudy.fragment.FragmentDemoActivity.class);
        put("Service", com.example.interviewstudy.service.ServiceActivity.class);
        put("自定义流式布局", com.example.interviewstudy.view.FlowActivity.class);
        put("ContentResolverActivity", com.example.interviewstudy.contentprovider.ContentResolverActivity.class);
        put("trace methed", com.example.interviewstudy.trace.TraceMethedActivity.class);
        put("ConstraintLayout用法", com.example.interviewstudy.view.ConstraintLayoutActivity.class);
        put("jni 调用", JniActivity.class);
        put("Flutter", FlutterActivity.class);
    }
    private void put(String key,Class clazz){
        title.add(key);
        activitys.put(key,clazz);
    }
    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(MainActivity.this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(28);
            textView.setTextColor(Color.BLACK);
            return new MyViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView tv = (TextView) holder.itemView;
            tv.setText(title.get(position));
            tv.setTag(position);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = (Integer) v.getTag();
                    Class c = activitys.get(title.get(p));
                    Intent intent = new Intent(MainActivity.this,c);
                    startHookActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return title.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder {

            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
