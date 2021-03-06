package com.mindaxx.zhangp.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.okgo.OkGo;
import com.mindaxx.zhangp.MainActivity;
import com.mindaxx.zhangp.MainFragment;
import com.mindaxx.zhangp.R;
import com.mindaxx.zhangp.base.BaseMvpFragment;
import com.mindaxx.zhangp.http.HttpManager;
import com.mindaxx.zhangp.util.NetworkStatsHelper;
import com.mindaxx.zhangp.util.SpUtil;
import com.mindaxx.zhangp.util.VibrateUtil;
import com.mindaxx.zhangp.widget.UniversalDialog;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class BFragment extends BaseMvpFragment {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.exite)
    TextView exite;
    @BindView(R.id.head_icon_rl)
    RelativeLayout headIconRl;
    @BindView(R.id.userImg)
    ImageView userImg;

    public static BFragment newInstance() {
        Bundle args = new Bundle();
        BFragment fragment = new BFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_b;
    }

    @Override
    protected void initView(View mView, Bundle savedInstanceState) {
        super.initView(mView, savedInstanceState);
        titleName.setText("我的");
    }

    @OnClick(R.id.exite)
    public void onExite() {
        UniversalDialog.Builder builder = new UniversalDialog.Builder();
        builder.content("是否登出？");
        UniversalDialog build = builder.build();
        build.show(getChildFragmentManager(), "");
        build.setOnDialogActionClickListener(action -> {
            switch (action) {
                case UniversalDialog.ACTION_POSITIVE:
                    build.dismiss();
                    SpUtil.saveIsLogin(false);
                    skipActivity(mContext, MainActivity.class);
                    break;
                case UniversalDialog.ACTION_NEGATIVE:
                    build.dismiss();
                    break;
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.reset_pass_word)
    public void onResetPassWord() {
        ((MainFragment) getParentFragment()).start(ResetPassWordFragment.newInstance());
    }

}
