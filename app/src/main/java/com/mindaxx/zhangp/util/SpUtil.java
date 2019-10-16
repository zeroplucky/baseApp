package com.mindaxx.zhangp.util;

import android.content.Context;

/**
 * Created by Administrator on 2019/10/15.
 */

public class SpUtil {

    private static Context mContext;

    private static String login = "login";


    public static void init(Context context) {
        if (context == null) {
            throw new NullPointerException("SpUtil must init...");
        }
        mContext = context;
    }


    public static void saveIsLogin(boolean isLogin) {
        SPUtils.init(mContext).putBoolean(login, isLogin);
    }

    public static boolean getIsLogin() {
        return SPUtils.init(mContext).getBoolean(login, false);
    }


}
