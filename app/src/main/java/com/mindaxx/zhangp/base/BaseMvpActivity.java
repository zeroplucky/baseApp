package com.mindaxx.zhangp.base;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gyf.barlibrary.ImmersionBar;
import com.mindaxx.zhangp.MainActivity;
import com.mindaxx.zhangp.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public abstract class BaseMvpActivity<P extends MvpPresenter> extends SupportActivity implements MvpView {

    private P mPresenter;
    public BaseMvpActivity mContext;
    private Unbinder unbinder;
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager2.addActivity(this);
        mContext = BaseMvpActivity.this;
        getLifecycle().addObserver(new NetworkChangeObserver());
        if (setStatusBar()) {
            ImmersionBar.with(this)
                    .fitsSystemWindows(true)
                    .keyboardEnable(true)
                    .statusBarColor(R.color.colorPrimary)
                    .statusBarAlpha(0.1f)
                    .init();
        }
    }

    public boolean setStatusBar() {
        return true;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(mContext);
        if (setEvenBus()) {
            EventBus.getDefault().register(this);
        }

    }

    public boolean setEvenBus() {
        return false;
    }

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
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager2.removeActivity(this);
        if (setStatusBar()) {
            ImmersionBar.with(this).destroy();
        }
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (setEvenBus()) {
            EventBus.getDefault().unregister(this);
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

    // ====================================================================
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "onResume");
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }

    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(mContext,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(mContext,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    mContext, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限。");
        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
    // ====================================================================

    private class NetworkChangeObserver implements LifecycleObserver {

        private NetworkReceiver networkReceiver;

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        void start() {
            registerNetworkChangeListener();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        void stop() {
            unRegisterNetworkChangeListener();
        }

        private void registerNetworkChangeListener() {
            if (networkReceiver == null) {
                networkReceiver = new NetworkReceiver();
            }
            networkReceiver.setOnNetworkChangedListener(type -> {
                switch (type) {
                    case NetworkReceiver.NONE:
                        isShowNotNetworkView(true);
                        break;

                    case NetworkReceiver.WIFI:
                    case NetworkReceiver.MOBILE:
                        isShowNotNetworkView(false);
                        break;
                }
            });
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            registerReceiver(networkReceiver, intentFilter);
        }

        private void unRegisterNetworkChangeListener() {
            if (networkReceiver != null) {
                unregisterReceiver(networkReceiver);
            }
        }

        View notNetworkView;

        private void isShowNotNetworkView(boolean show) {
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            if (notNetworkView == null) {
                notNetworkView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.view_not_network_note, decorView, false);
                notNetworkView.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            if (show) {
                boolean isAddToLayout = notNetworkView.getParent() != null;
                notNetworkView.setVisibility(View.VISIBLE);
                if (!isAddToLayout) {
                    BaseUtil.addStatusBarHeightMarginTop(notNetworkView);
                    decorView.addView(notNetworkView);
                }
            } else {
                notNetworkView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (this instanceof MainActivity) {
            moveTaskToBack(false);
            return;
        }
        super.onBackPressedSupport();
    }
    //=======================================================================================================================

    /*
     * 退出
     * */
    public void onBack(View view) {
        finish();
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


}
