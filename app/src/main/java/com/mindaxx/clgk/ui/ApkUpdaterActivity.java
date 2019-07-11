package com.mindaxx.clgk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mindaxx.clgk.R;
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
        FirUpdater firUpdater = new FirUpdater(this);
        firUpdater.apiToken(API_TOKEN)
                .appId(APP_ID)
//                .setForceUpDater(true)
                .checkVersion();
    }

    private void initView() {
        mAppVersion = (TextView) findViewById(R.id.app_version);
    }
}
