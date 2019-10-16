package com.mindaxx.zhangp.base;


public interface MainContract {

    interface MainView extends MvpView {

        void showData(String data);
    }

    interface MainPresenter extends MvpPresenter<MainView> {

        void getData();
    }
}
