package com.mindaxx.zhangp.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.lxj.xpopup2.CustomPopupView;
import com.lxj.xpopup.XPopup;
import com.mindaxx.zhangp.MainActivity;
import com.mindaxx.zhangp.R;
import com.mindaxx.zhangp.base.BaseMvpActivity;
import com.mindaxx.zhangp.http.URL2;
import com.mindaxx.zhangp.util.SizeUtils;
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

    @OnClick(R.id.set_up_ip)
    public void onSetupIp() {
        EditText editText = new EditText(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, 45);
        editText.setLayoutParams(params);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入IP").setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                URL2.BASE_URL = editText.getText().toString().trim();
                Toast.makeText(getApplicationContext(), URL2.BASE_URL, Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("取消", null).show();
    }

    // 类淘宝下来选择
    private void popupWindown(View op, String[] content) {
        new XPopup.Builder(this)
                .atView(op).offsetY(SizeUtils.px2dp(1))
                .asCustom(new CustomPopupView(mContext, op, content, new CustomPopupView.OnSelectListener2() {
                    @Override
                    public void onSelect(int id, String data) {
                        Toast.makeText(mContext, "  " + id + " . " + data, Toast.LENGTH_SHORT).show();
                    }
                })).show();
    }

}
