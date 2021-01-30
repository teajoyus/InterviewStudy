package com.example.recycleflutter;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @Author linmh
 * @email 625321521@qq.com
 * @date 2020-08-20 18:09
 * @desc
 */
class FlutterMethodPlugin implements MethodChannel.MethodCallHandler {
    public static String PLUGIN_NAME = "flutter_plugin";
    private MethodCall curCall;
    private MethodChannel.Result curResult;

    @Override
    public void onMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        this.curCall = methodCall;
        this.curResult = result;
        Log.i("FlutterMethodPlugin", "onMethodCall: method=" + methodCall.method);
        switch (methodCall.method) {
            case FlutterPluginMethodNames.TOAST:
                showToast();
                break;
            case FlutterPluginMethodNames.LOG:
                printLog();
                break;
            default:
                result.notImplemented();
        }
    }

    private void printLog() {
        String tag = curCall.argument("tag");
        String msg = curCall.argument("msg");
        Log.i(tag == null ? "" : tag, msg == null ? "" : msg);
    }

    private void showToast() {
        String msg = curCall.argument("msg");
        Toast.makeText(MyApplication.myApp, msg, Toast.LENGTH_SHORT).show();
        //4.返回值
        curResult.success("成功啦");
    }

    interface FlutterPluginMethodNames {
        String TOAST = "toast";
        String LOG = "log";
    }

}

