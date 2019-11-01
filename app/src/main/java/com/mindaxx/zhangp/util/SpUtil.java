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

    public static void putObj(String key, Object value) {
        MMKVUtil.init(mContext).putObj(key, value);
    }

    public static String getString(String key, String value) {
        return MMKVUtil.init(mContext).getString(key, value);
    }

    public static void saveIsLogin(boolean isLogin) {
        MMKVUtil.init(mContext).putObj(login, isLogin);
    }

    public static boolean getIsLogin() {
        return MMKVUtil.init(mContext).getBool(login);
    }

    public static void saveUserId(String infoId) {
        MMKVUtil.init(mContext).putObj(user_id, infoId);
    }

    public static String getUserId() {
        return MMKVUtil.init(mContext).getString(user_id);
    }

    public static void savePassWord(String passWord) {
        MMKVUtil.init(mContext).putObj(pass_word, passWord);
    }

    public static String getPassWord() {
        return MMKVUtil.init(mContext).getString(pass_word);
    }

}
