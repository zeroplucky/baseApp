package com.mindaxx.zhangp.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.svprogresshud.WaitingView;
import com.mindaxx.zhangp.R;
import com.mindaxx.zhangp.base.BaseMvpFragment;
import com.mindaxx.zhangp.util.TimerUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 重置密码
 */
public class ResetPassWordFragment extends BaseMvpFragment implements TimerUtil.OnDownTimerListener {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.btn_get_code)
    TextView btn_get_code;

    public static ResetPassWordFragment newInstance() {
        Bundle args = new Bundle();
        ResetPassWordFragment fragment = new ResetPassWordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.reset_password_layout;
    }

    @Override
    protected void initView(View mView, Bundle savedInstanceState) {
        super.initView(mView, savedInstanceState);
        titleName.setText("重置密码");
        btnBack.setVisibility(View.VISIBLE);
        TimerUtil.getInstence().setOnDownTimerListener(this);
    }

    @OnClick(R.id.btn_back)
    public void onBack(View view) {
        onBackPressedSupport();
    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }

    @OnClick(R.id.bt_register)
    public void onregister() {
        WaitingView.showSuccess(getContext(), "提交成功");
    }

    @OnClick(R.id.btn_get_code)
    public void onGetCode() {
        if (TimerUtil.getTimerStatus()) {
            TimerUtil.getInstence().startTimer();
        }
    }

    @Override
    public void lintenerOnStart() {
        btn_get_code.setEnabled(false);
    }

    @Override
    public void lintenerOnTick(Long downTime) {

    }

    @Override
    public void lintenerOnFinish() {
        btn_get_code.setEnabled(true);
    }
}
