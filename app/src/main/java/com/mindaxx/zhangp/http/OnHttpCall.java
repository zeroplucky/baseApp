package com.mindaxx.zhangp.http;

/**
 * Created by Administrator on 2018/4/26.
 */

public abstract class OnHttpCall {

    public abstract void onSuccess(String response);

    public void onFailed(String info) {
    }

    public void onFinish() {
    }
}
