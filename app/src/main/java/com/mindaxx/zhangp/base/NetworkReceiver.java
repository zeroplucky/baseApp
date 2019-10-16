package com.mindaxx.zhangp.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 监听网络状态变化
 */
public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager == null || manager.getActiveNetworkInfo() == null
                    || !manager.getActiveNetworkInfo().isConnected()) {
                //当前没有网络连接
                setNetworkType(NONE);
                if (onNetworkChangedListener != null) {
                    onNetworkChangedListener.onNetworkChanged(NONE);
                }
                return;
            }

            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {
                int type = NONE;
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    //前WiFi连接可用
                    type = WIFI;

                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // 当前移动网络连接可用
                    type = MOBILE;
                }

                setNetworkType(type);
                if (onNetworkChangedListener != null) {
                    onNetworkChangedListener.onNetworkChanged(type);
                }
            }
        }
    }

    public static final int WIFI = 1;
    public static final int MOBILE = 2;
    public static final int NONE = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WIFI, MOBILE, NONE})
    public @interface NetworkType {

    }

    private int networkType = NONE;

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }

    public interface OnNetworkChangedListener {

        void onNetworkChanged(@NetworkType int type);
    }

    private OnNetworkChangedListener onNetworkChangedListener;

    public void setOnNetworkChangedListener(OnNetworkChangedListener onNetworkChangedListener) {
        this.onNetworkChangedListener = onNetworkChangedListener;
    }
}