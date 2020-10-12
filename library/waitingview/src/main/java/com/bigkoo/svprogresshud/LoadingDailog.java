/*
 * @时间 20-8-27 下午5:55
 * @作者 Administrator
 */

package com.bigkoo.svprogresshud;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class LoadingDailog extends Dialog {

    private static volatile LoadingDailog instance;

    private LoadingDailog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /* 1. -----------------------------------------  */
    public static void start(final Context context) {
        if (instance == null) {
            synchronized (LoadingDailog.class) {
                if (instance == null) {
                    instance = new Builder(context).create();
                    instance.show();
                }
            }
        } else {
            stopLoading();
            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    instance = new Builder(context).create();
                    instance.show();
                }
            }, 20);
        }
    }

    public static void stopLoading() {
        if (instance != null && instance.getWindow() != null) {
            try {
                instance.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }

    public static class Builder {

        private Context context;
        private boolean isCancelable = true;
        private boolean isCancelOutside = false;


        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置是否可以按返回键取消
         *
         * @param isCancelable
         * @return
         */

        public Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        /**
         * 设置是否可以取消
         *
         * @param isCancelOutside
         * @return
         */
        public Builder setCancelOutside(boolean isCancelOutside) {
            this.isCancelOutside = isCancelOutside;
            return this;
        }

        public LoadingDailog create() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_loading, null);
            final LoadingDailog loadingDailog = new LoadingDailog(context, R.style.DialogTheme);
            loadingDailog.setContentView(view);
            loadingDailog.setCancelable(isCancelable);
            loadingDailog.setCanceledOnTouchOutside(isCancelOutside);
            Window window = loadingDailog.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.dimAmount = 0f;
            window.setAttributes(attributes);
            loadingDailog.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        stopLoading();
                        return true;
                    }
                    return false;
                }
            });
            return loadingDailog;

        }


    }
}
