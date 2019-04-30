package com.example.interviewstudy;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;


/**
 * Author:mihon
 * Time: 2019\3\11 0011.15:38
 * Description:This is BaseFragment
 */
public class BaseFragment extends Fragment {
    protected  MyLogger lifeCycleLogger= new MyLogger(getClass().getSimpleName(),"Life_Cycle@" + Integer.toHexString(hashCode()));
    //    protected String TAG = getClass().getSimpleName();
    private static final boolean printLifeCycle = true;
    private static final boolean printBeforeLifeCycle = true;

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        log_life("onInflate");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        log_life("onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log_life("onCreate");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        log_life("onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log_life("onActivityCreated");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        log_life("onViewStateRestored");
    }

    @Override
    public void onStart() {
        super.onStart();
        log_life("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        log_life("onResume");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        log_life("onCreateOptionsMenu");
    }

    @Override
    public void onPause() {
        super.onPause();
        log_life("onPause");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        log_life("onSaveInstanceState");
    }

    @Override
    public void onStop() {
        super.onStop();
        log_life("onStop");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log_life("onActivityResult");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        log_life("onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log_life("onDestroy");
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        log_life("onDestroyOptionsMenu");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        log_life("onDestroy");
    }
    protected  void log_life(String str){
        lifeCycleLogger.i(str);
    }
}
