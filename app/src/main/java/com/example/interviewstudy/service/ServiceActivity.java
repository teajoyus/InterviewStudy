package com.example.interviewstudy.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;

public class ServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_i("onServiceConnected before");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                bindService(new Intent(getBaseContext(), AService.class), new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        log_i("onServiceConnected："+service);
                        //main Thread
                        log_life("onServiceConnected current thread："+Thread.currentThread());
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        log_life("onServiceConnected");
                    }
                }, Service.BIND_AUTO_CREATE);
                log_i("onServiceConnected code ");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log_i("onServiceConnected code2 ");
//                startService(new Intent(ServiceActivity.this,AService.class));
//                startService(new Intent(ServiceActivity.this,CService.class));
            }
        });

    }

}
