package com.mindaxx.clgk.base;

import android.support.annotation.UiThread;


public interface MvpPresenter<V extends MvpView> {


    @UiThread
    void attachView(V view);

    @UiThread
    void detachView();

    @UiThread
    void destroy();
}
