package com.mindaxx.zhangp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mindaxx.zhangp.base.BaseMvpActivity;
import com.mindaxx.zhangp.ui.activity.LoginActivity;
import com.mindaxx.zhangp.util.SpUtil;
import com.sunfusheng.ApkUpdater;

public class MainActivity extends BaseMvpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //skip2next();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //skip2next();
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

    public void luzhi(View view) {
//        Intent intent = new Intent(this, RecordActivity.class);
//        startActivityForResult(intent, 111);
    }
}
