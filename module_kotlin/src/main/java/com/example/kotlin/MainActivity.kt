package com.example.kotlin

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var btn: Button? = null
    var tv: TextView? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kotlin_activity_main)
        btn = findViewById(R.id.btn1)
        tv = findViewById(R.id.tv1)
//        btn?.setOnClickListener({
//            tv?.setText("${1+5}")
//        })
        btn?.setOnClickListener {
            tv?.text = "${1 + 5}"
        }

    }
    }
