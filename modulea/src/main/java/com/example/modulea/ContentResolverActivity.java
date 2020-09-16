package com.example.modulea;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContentResolverActivity extends AppCompatActivity {
    public static final String MY_CONTENT_PROVIDER_URI = "content://com.example.interviewstudy.contentprovider.MyContentProvider";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_resolver);
}

    public void update(View view) {
        String uriString;
        Uri uri = Uri.parse(MY_CONTENT_PROVIDER_URI);
        uri = Uri.withAppendedPath(uri,"study");
        uri = Uri.withAppendedPath(uri,"2");
        ContentValues values = new ContentValues();
        values.put("str","我是update");
        getContentResolver().update(uri,values,null,null);

    }

    public void add(View view) {
        Uri uri = Uri.parse(MY_CONTENT_PROVIDER_URI+"/study");
        ContentValues values = new ContentValues();
        values.put("str","1123");
        getContentResolver().insert(uri,values);
    }

    public void del(View view) {
        Uri uri = Uri.parse(MY_CONTENT_PROVIDER_URI+"/study");
        uri = Uri.withAppendedPath(uri,"/1");
        getContentResolver().delete(uri,null,null);
    }

    public void query(View view) {
        Uri uri = Uri.parse(MY_CONTENT_PROVIDER_URI);
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor==null){
            Toast.makeText(this,"cursor==null",Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this,"查询到"+cursor.getCount()+"条记录",Toast.LENGTH_SHORT).show();
    }
}
