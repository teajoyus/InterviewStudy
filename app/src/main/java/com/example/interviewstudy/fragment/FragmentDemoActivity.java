package com.example.interviewstudy.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.ViewGroup;

import com.example.interviewstudy.BaseActivity;
import com.example.interviewstudy.R;

public class FragmentDemoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_demo);
//        getFragmentManager().beginTransaction()
//                .addToBackStack()
    }
}
