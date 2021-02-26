package com.example.interviewstudy.rxjava;

import android.os.Bundle;
import android.util.Log;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;
import com.example.interviewstudy.rxjava.retrofit.Api;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class RxJavaActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java);

        Retrofit retrofit = new Retrofit.Builder()
                //设置数据解析器
//                .addConverterFactory(GsonConverterFactory.create())
                //设置网络请求的Url地址
                .baseUrl("http://apis.baidu.com/txapi/")
                .build();
// 创建网络请求接口的实例
        final Api mApi = retrofit.create(Api.class);
        new Thread(){
            @Override
            public void run() {
                super.run();
                Call<String> call = mApi.login("","");
                try {
                    Response response = call.execute();
                    Log.i("222222222", String.valueOf(response.body()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
