package com.mindaxx.zhangp.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mindaxx.zhangp.R;
import com.mindaxx.zhangp.util.NotificationUtils;
import com.sunfusheng.FirUpdater;
import com.sunfusheng.FirUpdaterUtils;

public class ApkUpdaterActivity extends AppCompatActivity {

    private TextView mAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_updater);
        initView();
        mAppVersion.setText(" V" + FirUpdaterUtils.getVersionName(this));
    }

    public void checkApkDown(View view) {
        String appName = getResources().getString(R.string.app_name);
        String apk_url = "";
        boolean forceup = false;
        String versionId = "";
        String updateDesc = "";
        new FirUpdater(this)
                .checkVersion(apk_url, forceup, appName, versionId + "", updateDesc);
    }

    private void initView() {
        mAppVersion = (TextView) findViewById(R.id.app_version);
    }

    /*
     * 通知
     * */
    public void Notify(View view) {
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.sendNotification(1, "xxxxx", "xxxxx", R.mipmap.ic_launcher);
    }


    /*
     * 跳转到设置
     * */
    public void onViewClicked(View view) {
        try {
            Intent intent = new Intent();
            //下面这种方案是直接跳转到当前应用的设置界面。
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getApplication().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } catch (Exception e) {
        }
    }
}
