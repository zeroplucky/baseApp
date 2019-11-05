package com.mindaxx.zhangp.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mindaxx.zhangp.MainActivity;
import com.mindaxx.zhangp.MainFragment;
import com.mindaxx.zhangp.R;
import com.mindaxx.zhangp.base.BaseMvpFragment;
import com.mindaxx.zhangp.util.SpUtil;
import com.mindaxx.zhangp.widget.PictureDialog;
import com.mindaxx.zhangp.widget.PictureDialog2;
import com.mindaxx.zhangp.widget.UniversalDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    @OnClick(R.id.reset_pass_word)
    public void onResetPassWord() {
        ((MainFragment) getParentFragment()).start(ResetPassWordFragment.newInstance());
    }

    @OnClick(R.id.head_icon_rl)
    public void onClickHeadIcon() {
        List<String> list = new ArrayList<>();
        list.add("https://www.baidu.com/img/bd_logo1.png");
        list.add("https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png");
        new PictureDialog2(getContext(), list).show();
    }
}
