package com.bigkoo.svprogresshud;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.bigkoo.svprogresshud.view.ProgressDefaultView;


public class WaitingView extends Dialog {

    private static volatile WaitingView instance;
    private ProgressDefaultView mSharedView;

    private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
    );
    private static String TAG = "WaitingView";

    public WaitingView(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        this.setCanceledOnTouchOutside(false);
        mSharedView = new ProgressDefaultView(context);
        params.gravity = Gravity.CENTER;
        mSharedView.setLayoutParams(params);
        this.setContentView(mSharedView);
    }

    /* 1. -----------------------------------------  */
    public static void start(Context context) {
        if (instance == null) {
            synchronized (WaitingView.class) {
                if (instance == null) {
                    instance = new WaitingView(context);
                }
            }
        }
        instance.showWithStatus("加载中...");
    }

    /* 2. -----------------------------------------  */
    public static void startWithInfo(final Context context, final String info) {
        stopLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (instance == null) {
                    synchronized (WaitingView.class) {
                        if (instance == null) {
                            instance = new WaitingView(context);
                            Log.e(TAG, "INIT : " + context);
                        }
                    }
                }
                instance.showWithStatus(info);
            }
        }, 10);
    }


    /* 3. -----------------------------------------  */
    public static void showInfo(final Context context, final String info) {
        stopLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (instance == null) {
                    synchronized (WaitingView.class) {
                        if (instance == null) {
                            instance = new WaitingView(context);
                        }
                    }
                }
                instance.showInfoWithStatus(info);
            }
        }, 10);
    }

    /* 4. -----------------------------------------  */
    public static void showSuccess(final Context context, final String info) {
        stopLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (instance == null) {
                    synchronized (WaitingView.class) {
                        if (instance == null) {
                            instance = new WaitingView(context);
                        }
                    }
                }
                instance.showSuccessWithStatus(info);
            }
        }, 10);
    }


    /* 5. -----------------------------------------  */
    public static void showError(final Context context, final String info) {
        stopLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (instance == null) {
                    synchronized (WaitingView.class) {
                        if (instance == null) {
                            instance = new WaitingView(context);
                        }
                    }
                }
                instance.showErrorWithStatus(info);
            }
        }, 10);
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismiss();
        }
    };

    private void showWithStatus(String string) {
        Log.e(TAG, "showWithStatus: " + isShowing());
        if (isShowing()) {
            dismiss();
        }
        mSharedView.showWithStatus(string);
        try {
            show();
            Log.e(TAG, "showWithStatus: " + isShowing());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "showWithStatus: " + Log.getStackTraceString(e));
        }
    }

    private void showInfoWithStatus(String string) {
        if (isShowing()) {
            dismiss();
        }
        mSharedView.showInfoWithStatus(string);
        try {
            show();
            scheduleDismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "showInfoWithStatus: " + Log.getStackTraceString(e));
        }
    }

    private void showSuccessWithStatus(String string) {
        if (isShowing()) {
            dismiss();
        }
        mSharedView.showSuccessWithStatus(string);
        try {
            show();
            scheduleDismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "showSuccessWithStatus: " + Log.getStackTraceString(e));
        }
    }

    private void showErrorWithStatus(String string) {
        if (isShowing()) {
            dismiss();
        }
        mSharedView.showErrorWithStatus(string);
        try {
            show();
            scheduleDismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "showErrorWithStatus: " + Log.getStackTraceString(e));
        }
    }

    private void scheduleDismiss() {
        try {
            if (mHandler == null) return;
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessageDelayed(0, 500);
        } catch (Exception e) {
            Log.e(TAG, "scheduleDismiss: " + Log.getStackTraceString(e));
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mSharedView.dismiss();
    }
}
