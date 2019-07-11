package com.mindaxx.clgk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mindaxx.clgk.R;
import com.mindaxx.clgk.util.NotificationUtils;
import com.sunfusheng.FirUpdater;
import com.sunfusheng.FirUpdaterUtils;

import javax.sql.DataSource;

public class ApkUpdaterActivity extends AppCompatActivity {

    private TextView mAppVersion;
    public static final String API_TOKEN = "012bf9b3344f209e61f4ba8fa872855a";
    public static final String APP_ID = "5d26e1b1f9454854ff3452ac";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_updater);
        initView();
        mAppVersion.setText(" V" + FirUpdaterUtils.getVersionName(this));
    }

    public void checkApkDown(View view) {
//        FirUpdater firUpdater = new FirUpdater(this);
//        firUpdater.apiToken(API_TOKEN)
//                .appId(APP_ID)
////                .setForceUpDater(true)
//                .checkVersion();
        onViewClicked();
    }

    private void initView() {
        mAppVersion = (TextView) findViewById(R.id.app_version);
    }

    public void Notify(View view) {
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.sendNotification(1, "xxxxx", "xxxxx", R.mipmap.ic_launcher);
    }


    public void onViewClicked() {
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
