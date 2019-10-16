package com.mindaxx.zhangp.base;


public interface MvpView {


    void onHttpEmpty();

    void onHttpLoadNoMore();

    void onHttpFailed(String failed);

    void onHttpFinished();

}
