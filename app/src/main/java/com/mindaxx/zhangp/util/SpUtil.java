package com.mindaxx.zhangp.util;

import android.content.Context;
import android.os.Parcelable;

import com.tencent.mmkv.MMKV;

import java.util.Set;

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

    public static String getString(String key) {
        return MMKVUtil.init(mContext).getString(key);
    }

    public boolean getBool(String key) {
        return MMKVUtil.init(mContext).getBool(key);
    }

    public int getInt(String key) {
        return MMKVUtil.init(mContext).getInt(key);
    }

    public long getLong(String key) {
        return MMKVUtil.init(mContext).getLong(key);
    }

    public float getFloat(String key) {
        return MMKVUtil.init(mContext).getFloat(key);
    }

    public double getDouble(String key) {
        return MMKVUtil.init(mContext).getDouble(key);
    }

    public byte[] getBytes(String key) {
        return MMKVUtil.init(mContext).getBytes(key);
    }

    public Set<String> getStringSet(String key) {
        return MMKVUtil.init(mContext).getStringSet(key);
    }

    public <T extends Parcelable> T getParcelable(String key, Class<T> clazz) {
        return MMKVUtil.init(mContext).getParcelable(key, clazz);
    }

    /**/
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
