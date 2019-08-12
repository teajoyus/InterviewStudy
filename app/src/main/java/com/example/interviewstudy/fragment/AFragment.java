package com.example.interviewstudy.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.interviewstudy.BaseFragment;
import com.example.interviewstudy.R;

/**
 * Author:mihon
 * Time: 2019\3\12 0012.9:58
 * Description:This is AFragment
 */
public class AFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, container, false);
        SparseArray<String> sparseArray = new SparseArray<>();
        sparseArray.put(1,"324");
        return view;
    }
}

