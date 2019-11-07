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

    private NetworkStatsHelper networkStatsHelper;

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
        networkStatsHelper = new NetworkStatsHelper(getContext());
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

    @OnClick(R.id.head_icon_rl)
    public void onClickHeadIcon() {
        HttpManager.get("http://news.baidu.com/", null);
        getNetStats(networkStatsHelper, getContext());
        VibrateUtil.init(getContext()).makePattern().beat(1000).rest(5).playPattern();
    }

    private void getNetStats(NetworkStatsHelper networkStatsHelper, Context context) {
        if (networkStatsHelper.hasPermissionToReadNetworkStats(context)) {

            long todayMobile = networkStatsHelper.getTodayPackageBytesMobile2(context);
            String todayMobile_ = networkStatsHelper.bite(todayMobile);

            long monthMobile2 = networkStatsHelper.getMonthPackageBytesMobile2(context);
            String monthMobile2_ = networkStatsHelper.bite(monthMobile2);

            long allTodayMobile = networkStatsHelper.getAllDeviceTodayMobile(context);
            String allTodayMobile1 = networkStatsHelper.bite(allTodayMobile);

            long allMonthMobile = networkStatsHelper.getAllDeviceMonthMobile(context);
            String allMonthMobile1 = networkStatsHelper.bite(allMonthMobile);
            Log.e("networkStatsHelper", "Mobile:----    , todayMobile_ =  " + todayMobile_ + "  , monthMobile2_ =  " + monthMobile2_ +
                    "  ,allTodayMobile =  " + allTodayMobile1 + "  , allMonthMobile = " + allMonthMobile1);

            long todayWifi2 = networkStatsHelper.getTodayPackageBytesWifi2(context);
            String todayWifi2_ = networkStatsHelper.bite(todayWifi2);

            long monthWifi2 = networkStatsHelper.getMonthPackageBytesWifi2(context);
            String monthWifi2_ = networkStatsHelper.bite(monthWifi2);

            long alltodayWifi = networkStatsHelper.getAllDeviceTodayWifi();
            String alltodayWifi_ = networkStatsHelper.bite(alltodayWifi);

            long allMonthWifi = networkStatsHelper.getAllDeviceMonthWifi();
            String allMonthWifi_ = networkStatsHelper.bite(allMonthWifi);
            Log.e("networkStatsHelper", "todayWifi2_: " + todayWifi2_ + "   , monthWifi2_ =  " + monthWifi2_ +
                    "   , alltodayWifi_ =  " + alltodayWifi_ + "  , allMonthWifi_ = " + allMonthWifi_);
        }
    }


}
