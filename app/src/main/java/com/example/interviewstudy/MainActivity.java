package com.example.interviewstudy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interviewstudy.jetpack.LifeCycleTestActivity;
import com.example.interviewstudy.jni.JniActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class MainActivity extends BaseActivity {
    RecyclerView rv;
    List<String> title;
    Map<String, Class> activitys;

    public int getInt() {
        return 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLifecycle().addObserver(new LifecycleObserver() {
        });
        for (int i = 0; i < 2; i++) {
            int k = getInt();
            Context context = getBaseContext();
            for (int j = 0; j < 2; j++) {
                log_i(context.toString());
            }
        }
        Log.i("ListActivity", "context:" + LayoutInflater.from(this).getContext().toString());
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        initData();
        rv.setAdapter(new MyAdapter());


        try {
            throw new UnsatisfiedLinkError();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        testGson();
    }

    private void testGson() {
        Gson gson = new Gson();
        AABean aaBean = gson.fromJson("{\"userName\":\"名称\",\"userNum\":1}",AABean.class);
        System.out.println("222222222:"+gson.toJson(aaBean));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initData() {
        activitys = new HashMap<>();
        title = new ArrayList<>();
        put("slide view", com.example.interviewstudy.view.SlideViewActivity.class);
        put("linearLayout measure", com.example.interviewstudy.view.LinearMeasureActivity.class);
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
        put("lifeCycle", LifeCycleTestActivity.class);
//        put("Flutter", FlutterDemoActivity.class);
    }

    private void put(String key, Class clazz) {
        title.add(key);
        activitys.put(key, clazz);
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(MainActivity.this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
                    Intent intent = new Intent(MainActivity.this, c);
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
