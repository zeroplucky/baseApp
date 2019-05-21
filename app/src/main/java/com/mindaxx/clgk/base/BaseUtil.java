package com.mindaxx.clgk.base;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

public class BaseUtil {

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 为View增加一个状态栏高度的MarginTop
     *
     * @param view
     */
    public static void addStatusBarHeightMarginTop(View view) {
        if (view == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = getStatusBarHeight(view.getContext());
            ViewGroup.LayoutParams p = view.getLayoutParams();
            if (p != null && p instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) p).topMargin += statusBarHeight;
            }
        }
    }
}
