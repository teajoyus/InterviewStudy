package com.example.interviewstudy.contentprovider;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;

public class ContentResolverActivity extends BaseActivity {
    ListView lv;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    ContentObserver contentObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_resolver);
        lv = findViewById(R.id.lv);
        Uri uri = Uri.parse("content://" + MyContentProvider.class.getName() + "/study");
        getContentResolver().registerContentObserver(uri, true,contentObserver =  new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                log_i("onChange selfChange:"+selfChange+",uri:"+uri);
                super.onChange(selfChange, uri);
            }
        });
    }

    @Override
    protected void onDestroy() {
        getContentResolver().unregisterContentObserver(contentObserver);
        super.onDestroy();
    }

    public void update(View view) {
        String uriString;
        Uri uri = Uri.parse("content://" + MyContentProvider.class.getName());
        uri = Uri.withAppendedPath(uri, "study");
        uri = Uri.withAppendedPath(uri, "2");
        ContentValues values = new ContentValues();
        values.put("str", "我是update");
        getContentResolver().update(uri, values, null, null);

    }

    public void add(View view) {
        Uri uri = Uri.parse("content://" + MyContentProvider.class.getName() + "/study");
        ContentValues values = new ContentValues();
        values.put("str", "1123");
        getContentResolver().insert(uri, values);
    }

    public void update_lv(View view) {
        lv.setAdapter(new MyBaseAdapter());
    }

    public void del(View view) {
        Uri uri = Uri.parse("content://" + MyContentProvider.class.getName() + "/study");
        uri = Uri.withAppendedPath(uri, "/1");
        getContentResolver().delete(uri, null, null);
    }

    public void query(View view) {
        final Uri uri = Uri.parse("content://" + MyContentProvider.class.getName());
        new Thread("Thread-1") {
            @Override
            public void run() {
                super.run();
                final Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "查询到" + cursor.getCount() + "条记录", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();


    }

    class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return MyContentProvider.data.size();
        }

        @Override
        public Object getItem(int position) {
            return MyContentProvider.data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getActivity());
            tv.setText(MyContentProvider.data.get(position));
            tv.setTextSize(26);
            tv.setGravity(Gravity.CENTER);
            return tv;
        }
    }
}
