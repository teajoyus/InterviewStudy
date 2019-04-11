package com.example.recycleviewdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    RecyclerView rv;
    Handler handler = new Handler();
    List<News> newsList;
    void showToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        //瀑布流，需要定义每个item高度不同 不然就跟Grid布局一样的效果，
//        rv.setLayoutManager(new StaggeredGridLayoutManager(3,OrientationHelper.VERTICAL));

//        rv.setLayoutManager(new GridLayoutManager(this,2));

        //分割线的接口没有实现类 需要自己实现
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        if(!(rv.getLayoutManager() instanceof  StaggeredGridLayoutManager)){
            rv.addItemDecoration(dividerItemDecoration);
        }
        rv.setItemAnimator(new DefaultItemAnimator());
        //默认就是垂直
        ((LinearLayoutManager) layoutManager).setOrientation(OrientationHelper.VERTICAL);
        //当item改变不会重新计算item的宽高
        //调用adapter的增删改差方法的时候就不会重新计算，但是调用nofityDataSetChange的时候还是会
        //所以往往是直接先设置这个为true，当需要布局重新计算宽高的时候才调用nofityDataSetChange
        rv.setHasFixedSize(true);
        //3.准备数据
        newsList = new ArrayList<>();
        News news;
        for (int i = 1; i < 100; i++) {
            news = new News();
            news.title = "新闻标题内容新闻标题内容新闻标题内容新闻标题内容新闻标题内容" + i;
            news.source = "腾讯新闻" + i;
            news.time = "2019-01-17";
            newsList.add(news);
        }
        //3.设置适配器
        NewsAdapter newsAdapter = new NewsAdapter(newsList);
//        rv.setAdapter(newsAdapter);
        rv.setAdapter(new NormalAdapterWrapper(newsAdapter));
        //在 RecyclerView 中又找不到可以调用的 API 时，就可以跑到 LayoutManager 的文档去看看，基本都在那里
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //上面的只是说滑到那个位置在屏幕上可见的时候，然而比如说想把那个位置滑到最顶部，应该怎么实现

                //平滑滚动 中间的state好像没用到不知道怎么使用，另外可以研究下如何控制速度
                rv.getLayoutManager().smoothScrollToPosition(rv, null, 50);
                showToast("滚动到第50个item可见");
                //定位到某个位置 不平滑滚动
//                rv.getLayoutManager().scrollToPosition(50);
                //获取指定位置的view
                View view = rv.getLayoutManager().findViewByPosition(10);
                if (rv.getLayoutManager() instanceof LinearLayoutManager) {
                    //第一个可见的位置  注意由于上面正在平混滚动 这里就获取了，所以可能获取到的是滚动之前的
                    int firstVisiablePostion = ((LinearLayoutManager) rv.getLayoutManager()).findFirstVisibleItemPosition();
                    Log.i(TAG, "firstVisiablePostion:" + firstVisiablePostion);
                }
            }
        }, 2000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rv.getLayoutManager() instanceof LinearLayoutManager) {
                    //第一个可见的位置 只是可见，并不是完全可见，比如滑到后的最顶部只是显示了一截 那也是算第一个可见
                    int firstVisiablePostion = ((LinearLayoutManager) rv.getLayoutManager()).findFirstVisibleItemPosition();
                    Log.i(TAG, "firstVisiablePostion:" + firstVisiablePostion);
                    //第一个完全可见的位置
                    int firstCompletelyVisiablePostion = ((LinearLayoutManager) rv.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    Log.i(TAG, "firstCompletelyVisiablePostion:" + firstCompletelyVisiablePostion);
                    //最后一个可见的位置， 只是可见，并不是完全可见
                    int lastVisiablePostion = ((LinearLayoutManager) rv.getLayoutManager()).findLastVisibleItemPosition();
                    Log.i(TAG, "lastVisiablePostion:" + lastVisiablePostion);
                    //最后一个完全可见的位置
                    int lastCompletelyVisiablePostion = ((LinearLayoutManager) rv.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    Log.i(TAG, "lastCompletelyVisiablePostion:" + lastCompletelyVisiablePostion);


                    //自己做的将指定位置滑到最顶部
                    int pos = lastVisiablePostion;
                    //第一个参数是要在哪个位置上开始滑（固定位置），第二个参数是要把该位置的view滑到屏幕的哪个位置（相对位置）
                    ((LinearLayoutManager) rv.getLayoutManager()).scrollToPositionWithOffset(lastVisiablePostion, 0);
                    showToast("滚动到第"+lastVisiablePostion+"个item可见 把它置顶");
                }
            }
        }, 5000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rv.getLayoutManager().scrollToPosition(0);
                showToast("没有滚动效果 滑到最顶部");
            }
        },7000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showToast("添加item");
                addNewItem();
            }
        },10000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showToast("删除item");
                deleteItem();
            }
        },14000);
    }
    public void addNewItem() {
        News news = new News();
        news.title = "新插入以习近平同志为核心的党中央坚定不移推进全面深化改革";
        news.source = "新华网" ;
        news.time = "2018-8-6";
        newsList.add(3, news);
        ////更新数据集不是用adapter.notifyDataSetChanged()而是notifyItemInserted(position)与notifyItemRemoved(position) 否则没有动画效果。
        rv.getAdapter().notifyItemInserted(3);
    }

    public void deleteItem() {
        if(newsList == null || newsList.isEmpty()) {
            return;
        }
        newsList.remove(5);
        rv.getAdapter().notifyItemRemoved(5);
    }
    public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<News> list;

        public NewsAdapter(List<News> list) {
            this.list = list;
        }

        @Override
        public int getItemViewType(int position) {
//            return super.getItemViewType(position);
            return position % 3 + 1;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = null;
            if (viewType == 2) {
                v = getLayoutInflater().inflate(R.layout.item2, null, false);
            } else if (viewType == 3) {
                v = getLayoutInflater().inflate(R.layout.item3, null, false);
            } else {
                v = getLayoutInflater().inflate(R.layout.item, null, false);

            }
            RecyclerView.ViewHolder holder = null;
            holder = new MyViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((MyViewHolder) holder).title.setText(list.get(position).title);
            ((MyViewHolder) holder).time.setText(list.get(position).time);
            ((MyViewHolder) holder).source.setText(list.get(position).source);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, time, source;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            source = itemView.findViewById(R.id.source);
            time = itemView.findViewById(R.id.time);
        }
    }

    //    //初始化头view
//    class HeaderViewHolder extends RecyclerView.ViewHolder{
//
//        ImageView iv_newsImage;
//        public HeaderViewHolder(View itemView) {
//            super(itemView);
//            iv_newsImage=(ImageView) itemView.findViewById(R.id.news_image);
//        }
//    }
//    //onDraw()和onDrawOver()这两个方法都是用于绘制间隔样式，我们只需要复写其中一个方法即可。
//    class DividerItemDecoration extends RecyclerView.ItemDecoration {
//        //在Item绘制之前被调用，该方法主要用于绘制间隔样式。
//        @Override
//        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//            super.onDraw(c, parent, state);
//        }
//
//        //在Item绘制之前被调用，该方法主要用于绘制间隔样式。
//        @Override
//        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
//            super.onDrawOver(c, parent, state);
//        }
//        //设置item的偏移量，偏移的部分用于填充间隔样式，即设置分割线的宽、高；在RecyclerView的onMesure()中会调用该方法。
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            super.getItemOffsets(outRect, view, parent, state);
//        }
//    }




    //RecyclerView默认没有提供类似addHeaderView()和addFooterView()的API，因此这里介绍如何优雅地实现这两个接口。
    //这里引入装饰器（Decorator）设计模式，该设计模式通过组合的方式，在不破话原有类代码的情况下，对原有类的功能进行扩展。
    class NormalAdapterWrapper extends RecyclerView.Adapter{
        //在不破坏原有类的情况下，对原有的适配器拓展heeder和foot
        private NewsAdapter adapter;
        private int type;//-1：header -2：foot  other:content
        private boolean hasHeader,hasFoot;
        public NormalAdapterWrapper(NewsAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getItemViewType(int position) {
            if(position==0){
                return -1;
            }else if(position==adapter.getItemCount()+2){
                return -2;
            }
            return super.getItemViewType(position);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType==-1){
                RelativeLayout layout = new RelativeLayout(MainActivity2.this);
                return new HeaderViewHolder(layout);
            }else if(viewType==-2){
                RelativeLayout layout = new RelativeLayout(MainActivity2.this);
                return new FootViewHolder(layout);
            }
            return adapter.onCreateViewHolder(parent,viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(position==0 && holder instanceof HeaderViewHolder){
                hasHeader = true;
            }else if(position==adapter.getItemCount()&& holder instanceof FootViewHolder){
                hasFoot = true;
            }else{
                int pos = hasHeader?position - 1:position;
                if(pos>=adapter.getItemCount()){
                    pos = adapter.getItemCount() - 1;
                }
                adapter.onBindViewHolder(holder,pos);
            }
        }

        @Override
        public long getItemId(int position) {
            return adapter.getItemId(position-1);
        }
        @Override
        public int getItemCount() {
            return adapter.getItemCount()+2;
        }
        class HeaderViewHolder extends RecyclerView.ViewHolder{
            TextView tv;
            public HeaderViewHolder(View itemView) {
                super(itemView);
                tv = new TextView(MainActivity2.this);
                tv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                tv.setText("我是头部");
                tv.setGravity(Gravity.CENTER);
                ((ViewGroup)itemView).addView(tv);
                itemView.setBackgroundColor(0xff00ffff);
            }
        }
        class FootViewHolder extends RecyclerView.ViewHolder{
            TextView tv;
            public FootViewHolder(View itemView) {
                super(itemView);
                tv = new TextView(MainActivity2.this);
                tv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                tv.setText("我是尾部");
                tv.setGravity(Gravity.CENTER);
                ((ViewGroup)itemView).addView(tv);
                itemView.setBackgroundColor(0xff00ff00);
            }
        }
    }
}
