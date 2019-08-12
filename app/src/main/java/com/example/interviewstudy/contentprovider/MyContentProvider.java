package com.example.interviewstudy.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:mihon
 * Time: 2019\3\20 0020.17:55
 * Description:This is MyContentProvider
 */
public class MyContentProvider extends ContentProvider {
    public static List<String> data = new ArrayList<>();
    private static final UriMatcher URI_MATCHER = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(MyContentProvider.class.getName(), "study", 1);
        //使用通配符#，匹配任意数字
        URI_MATCHER.addURI(MyContentProvider.class.getName(), "study/#", 2);
    }

    //传进来Application的实例，自己会调用onCreate方法
    //所以在attachInfo方法之前去调用getConext()是null
    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
    }

    //比Application的onCreate先执行,但是能获取到getContext()，而这个context也就是application的实例，通过attachInfo传进来

    //主要进行一些ContentProvider初始化的工作
    @Override
    public boolean onCreate() {
        log("onCreate:" + getContext());
        log("onCreate current thread:" + Thread.currentThread().getName());
        //TODO 为什么返回true
        return false;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        log("call: method"+method+",arg:"+arg+",extras:"+extras);
        log("call current thread:" + Thread.currentThread().getName());
        if("callTest".equals(method)){
            Bundle bundle = callTest();
            return bundle;
        }
        return super.call(method, arg, extras);
    }
    public Bundle callTest(){
        log("callTest");
        Bundle bundle = new Bundle();
        bundle.putString("test","test");
        return bundle;
    }
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        log("query uri:" + uri);
        log("query current thread:" + Thread.currentThread().getName());
        Cursor cursor = new MyCursor();

        return cursor;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder, @Nullable CancellationSignal cancellationSignal) {
        log("query2 uri:" + uri);
        log("query2 this:" + this);
        log("query2 current thread:" + Thread.currentThread().getName());
        Cursor cursor = new MyCursor();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    //用来返回数据的MIME类型的方法。
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        log("getType");
        return null;
    }

    /**
     * 插入多条数据
     * @param uri
     * @param values
     * @return
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        log("insert uri:" + uri);
        log("insert current thread:" + Thread.currentThread().getName());
        String str = values.getAsString("str");
        data.add(str);
        getContext().getContentResolver().notifyChange(uri,null);
        //TODO 返回值的影响
        return null;
    }

    //uri:content://com.example.interviewstudy.contentprovider.MyContentProvider/study//1
    //然后getPathSegments打印的结果是：getPathSegments:[study, 1]
    //说明一个斜杆或者两个斜杠不受影响
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        log("delete uri:" + uri);
        //TODO getPathSegments是什么? 答：集合里是依次截取Uri内的字符串的，List集合的下标从0开始。第一个元素为第一个“/”右边的字符。即：第一个子部分（此处为notes.），不包含元字符串最左端的部分。（content://不算的，从后面的斜杠开始算）
        int index = Integer.parseInt(uri.getPathSegments().get(1));
        log("getPathSegments:" + uri.getPathSegments());
        if (index >= data.size()) {
            return 0;
        }
        data.remove(index);
        return 1;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        log("update uri:" + uri);
        int index = Integer.parseInt(uri.getPathSegments().get(1));
        if (index < data.size()) {
            data.remove(index);
            String str = values.getAsString("str");
            data.add(index, str);
            return 1;
        }
        return 0;
    }

    private void log(String str) {
        Log.i("lc_miao ContentProvider", str);
    }

    /**
     * 没去对接数据库 自己随便写个游标只重写getCount方法来测试用
     */
    class MyCursor implements Cursor {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public int getPosition() {
            return 0;
        }

        @Override
        public boolean move(int offset) {
            return false;
        }

        @Override
        public boolean moveToPosition(int position) {
            return false;
        }

        @Override
        public boolean moveToFirst() {
            return false;
        }

        @Override
        public boolean moveToLast() {
            return false;
        }

        @Override
        public boolean moveToNext() {
            return false;
        }

        @Override
        public boolean moveToPrevious() {
            return false;
        }

        @Override
        public boolean isFirst() {
            return false;
        }

        @Override
        public boolean isLast() {
            return false;
        }

        @Override
        public boolean isBeforeFirst() {
            return false;
        }

        @Override
        public boolean isAfterLast() {
            return false;
        }

        @Override
        public int getColumnIndex(String columnName) {
            return 0;
        }

        @Override
        public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
            return 0;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return null;
        }

        @Override
        public String[] getColumnNames() {
            return new String[0];
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public byte[] getBlob(int columnIndex) {
            return new byte[0];
        }

        @Override
        public String getString(int columnIndex) {
            return null;
        }

        @Override
        public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

        }

        @Override
        public short getShort(int columnIndex) {
            return 0;
        }

        @Override
        public int getInt(int columnIndex) {
            return 0;
        }

        @Override
        public long getLong(int columnIndex) {
            return 0;
        }

        @Override
        public float getFloat(int columnIndex) {
            return 0;
        }

        @Override
        public double getDouble(int columnIndex) {
            return 0;
        }

        @Override
        public int getType(int columnIndex) {
            return 0;
        }

        @Override
        public boolean isNull(int columnIndex) {
            return false;
        }

        @Override
        public void deactivate() {

        }

        @Override
        public boolean requery() {
            return false;
        }

        @Override
        public void close() {

        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void registerContentObserver(ContentObserver observer) {

        }

        @Override
        public void unregisterContentObserver(ContentObserver observer) {

        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void setNotificationUri(ContentResolver cr, Uri uri) {

        }

        @Override
        public Uri getNotificationUri() {
            return null;
        }

        @Override
        public boolean getWantsAllOnMoveCalls() {
            return false;
        }

        @Override
        public void setExtras(Bundle extras) {

        }

        @Override
        public Bundle getExtras() {
            return null;
        }

        @Override
        public Bundle respond(Bundle extras) {
            return null;
        }
    }
}
