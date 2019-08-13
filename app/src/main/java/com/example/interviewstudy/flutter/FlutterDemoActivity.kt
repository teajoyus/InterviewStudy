package com.example.interviewstudy.flutter

import android.os.Bundle
import android.os.SystemClock
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Button
import com.example.interviewstudy.R
import io.flutter.app.FlutterActivity
import io.flutter.facade.Flutter
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.view.FlutterMain
import io.flutter.view.FlutterView

// class FlutterDemoActivity : AppCompatActivity() {
//    protected  var flutterView: FlutterView? = null
//    private val channelName = "samples.flutter.io/counter_native"
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_flutter)
//        // var则是变量  可变
//        var btn = findViewById<Button>(R.id.btn)
//        //val 只读，相当于 final
//        val context = this
//        btn.setOnClickListener {
//            flutterView = Flutter.createView(context, lifecycle, "main")
//            val layout = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            addContentView(flutterView, layout)
//        }
//    }
//}

class FlutterDemoActivity : FlutterActivity() {
    private val channelName = "samples.flutter.io/demo"
    //val 只读，相当于 final
    val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        FlutterMain.startInitialization(applicationContext)
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(context)
        MethodChannel(flutterView, channelName).setMethodCallHandler { methodCall, result ->
            when (methodCall.method) {
                "getDemoString" -> {
                    result.success(getDemoString())
                }
                else -> {
                    result.notImplemented()
                }
            }

        }
    }

    fun getDemoString(): String {
        return "now elapsed real time:${SystemClock.elapsedRealtime()}"
    }
}