package com.huodao.module_recycle.view.home;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.huodao.platformsdk.logic.core.statusbar.StatusBarUtils;

/**
 * author : linmh
 * date : 2021/3/3 14:20
 * description :通过动态修改容器高度来避免键盘遮挡问题
 */
public class KeyboardHeightAdjustment {
    public static void assistActivity(Activity activity) {
        new KeyboardHeightAdjustment(activity);
    }

    public static void assistFragment(Fragment fragment) {
        new KeyboardHeightAdjustment(fragment);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private int initHeight;

    private KeyboardHeightAdjustment(Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        if (content != null) {
            initHook(content.getChildAt(0));
        }
    }

    private KeyboardHeightAdjustment(Fragment fragment) {
        if (fragment != null) {
            initHook(fragment.getView());
        }
    }

    private void initHook(View view) {
        if (view == null) return;
        mChildOfContent = view;
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int tempHeight = mChildOfContent.getHeight();
        if (initHeight < tempHeight) {
            initHeight = tempHeight;
        }

        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {

            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {

                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                frameLayoutParams.height = initHeight;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top + StatusBarUtils.getStatusBarHeight(mChildOfContent.getContext()));
    }

}
