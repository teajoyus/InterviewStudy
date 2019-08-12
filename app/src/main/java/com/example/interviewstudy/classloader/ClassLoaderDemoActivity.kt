package com.example.interviewstudy.classloader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.interviewstudy.R
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader

class ClassLoaderDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_loader_demo)
    }
}
