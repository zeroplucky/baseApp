package com.mindaxx.zhangp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.utils.HttpUtils;
import com.lzy.okgo.utils.IOUtils;
import com.mindaxx.zhangp.base.BaseMvpActivity;
import com.mindaxx.zhangp.imageloader.OnProgressListener;
import com.mindaxx.zhangp.ui.activity.LoginActivity;
import com.mindaxx.zhangp.util.SpUtil;
import com.sunfusheng.ApkUpdater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends BaseMvpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        skip2next();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        skip2next();
    }

    private void skip2next() {
        boolean isLogin = SpUtil.getIsLogin();
        if (!isLogin) {
            skipActivity(mContext, LoginActivity.class);
            finish();
            return;
        }
        MainFragment fragment = findFragment(MainFragment.class);
        if (fragment == null) {
            loadRootFragment(R.id.contentLayout, MainFragment.newInstance()); // 审批角色
        }
    }

}
