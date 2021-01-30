package com.example.interviewstudy.jni

import android.os.Bundle
import com.example.interviewstudy.BaseActivity
import com.example.interviewstudy.R

class JniActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jni)
//        Toast.makeText(this, "${2 + 3}", Toast.LENGTH_LONG).show();
//        var call:MyJniCall =  MyJniCall()
//        Toast.makeText(this, "${MyJniCall.sayHello(2L)}", Toast.LENGTH_LONG).show()
    }
}
