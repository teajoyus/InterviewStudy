package com.example.base;

import android.os.Bundle;
 import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.github.mzule.activityrouter.annotation.Router;

/**
 * Author:mihon
 * Time: 2019\2\25 0025.9:38
 * Description:This is RouterDemo
 */
@Router("router_demo")
public class RouterDemo extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
