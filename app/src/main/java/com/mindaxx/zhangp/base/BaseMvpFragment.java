package com.mindaxx.zhangp.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;


public abstract class BaseMvpFragment<P extends MvpPresenter> extends SupportFragment implements MvpView {

    private P mPresenter;
    public FragmentActivity mContext;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = null;
        mContext = getActivity();
        if (mView == null && setContentViewId() > 0) {
            mView = mInflater.inflate(setContentViewId(), container, false);
            unbinder = ButterKnife.bind(this, mView);
            initView(mView, savedInstanceState);
            mView.postDelayed(() -> {
                initDataDelayed();
            }, 50);

        }
        return mView;
    }


    @Override
    public void onSupportVisible() {
        super.onSupportVisible();

    }

    protected void initDataDelayed() {

    }

    protected void initView(View mView, Bundle savedInstanceState) {

    }

    protected abstract int setContentViewId();


    @Override
    public void onHttpFailed(String error) {

    }

    @Override
    public void onHttpEmpty() {

    }

    @Override
    public void onHttpLoadNoMore() {

    }

    @Override
    public void onHttpFinished() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }
    }

    @NonNull
    public P createPresenter() {
        return null;
    }

    protected P getPresenter() {
        if (mPresenter == null) {
            synchronized (BaseMvpActivity.class) {
                if (mPresenter == null) {
                    mPresenter = createPresenter();
                    mPresenter.attachView(this);
                }
            }
        }
        return mPresenter;
    }

    /*  Activity 跳转 */
    public void skipActivity(Context context, Class<?> goal) {
        Intent intent = new Intent(context, goal);
        context.startActivity(intent);
    }

    /* Activity 跳转*/
    public void skipActivity(Context context, Class<?> goal, Bundle bundle) {
        Intent intent = new Intent(context, goal);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /* Activity 跳转*/
    public void skipActivityForResult(BaseMvpFragment context, Class<?> goal, int resultCode) {
        Intent intent = new Intent(context.getContext(), goal);
        context.startActivityForResult(intent, resultCode);
    }

    /* Activity 跳转*/
    public void skipActivityForResult(BaseMvpFragment context, Class<?> goal, Bundle bundle, int resultCode) {
        Intent intent = new Intent(context.getContext(), goal);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, resultCode);
    }


}
