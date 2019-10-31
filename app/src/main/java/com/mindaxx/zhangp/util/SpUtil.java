package com.mindaxx.zhangp.util;

import android.content.Context;

/**
 * Created by Administrator on 2019/10/15.
 */

public class SpUtil {

    private static Context mContext;

    private static final String login = "login";
    public static final String user_id = "user_id";
    public static final String pass_word = "pass_word";

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

    public static void saveUserId(String infoId) {
        SPUtils.init(mContext).putString(user_id, infoId);
    }

    public static String getUserId() {
        return SPUtils.init(mContext).getString(user_id);
    }

    public static void savePassWord(String passWord) {
        SPUtils.init(mContext).putString(pass_word, passWord);
    }

    public static String getPassWord() {
        return SPUtils.init(mContext).getString(pass_word);
    }

}
