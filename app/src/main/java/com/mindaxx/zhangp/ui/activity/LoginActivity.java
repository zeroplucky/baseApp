package com.mindaxx.zhangp.ui.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.gyf.barlibrary.ImmersionBar;
import com.mindaxx.zhangp.MainActivity;
import com.mindaxx.zhangp.R;
import com.mindaxx.zhangp.base.BaseMvpActivity;
import com.mindaxx.zhangp.util.SpUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * 登录
 * */
public class LoginActivity extends BaseMvpActivity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .keyboardEnable(true)
                .statusBarColor("#012621")
                .statusBarAlpha(0.4f)
                .init();
    }

    @Override
    public boolean setStatusBar() {
        return false;
    }

    @OnClick(R.id.btn_login)
    public void onlogin() {
        SpUtil.saveIsLogin(true);
        skipActivity(mContext, MainActivity.class);
        finish();
    }
}
