package com.mindaxx.clgk.base;


public interface MvpView {


    void onHttpEmpty();

    void onHttpLoadNoMore();

    void onHttpFailed(String failed);

    void onHttpFinished();

}
