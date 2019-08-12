package com.example.interviewstudy.flutter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.interviewstudy.R
import io.flutter.facade.Flutter
import io.flutter.view.FlutterView

class FlutterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flutter)
        // var则是变量  可变
        var btn = findViewById<Button>(R.id.btn)
        //val 只读，相当于 final
        val context = this
//        btn.setOnClickListener {
//            Toast.makeText(context, "${it.id}", Toast.LENGTH_LONG).show()
//
//        }


        btn.setOnClickListener {
            val flutterView = Flutter.createView(context, lifecycle, "main")
            val layout = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            addContentView(flutterView, layout)
        }
    }
}
