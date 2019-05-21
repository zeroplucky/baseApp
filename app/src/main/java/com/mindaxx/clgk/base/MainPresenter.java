package com.mindaxx.clgk.base;

import android.os.Handler;


public class MainPresenter extends PresenterDelegate<MainContract.MainView> implements MainContract.MainPresenter {

    @Override
    public void getData() {
        /**
         * 模拟网络请求数据
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isViewAttached()) return;
                getView().showData("Hello MVPFrame");
            }
        }, 2000);
    }
}
