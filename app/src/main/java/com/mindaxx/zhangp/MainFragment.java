package com.mindaxx.zhangp;


import android.os.Bundle;
import android.view.View;

import com.chaychan.library.BottomBarItem;
import com.chaychan.library.BottomBarLayout;
import com.mindaxx.zhangp.base.BaseMvpFragment;
import com.mindaxx.zhangp.bean.event.KeyboardChangeEvent;
import com.mindaxx.zhangp.ui.fragment.AFragment;
import com.mindaxx.zhangp.ui.fragment.BFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import me.yokeyword.fragmentation.ISupportFragment;


/**
 * 主fragment
 */
public class MainFragment extends BaseMvpFragment implements BottomBarLayout.OnItemSelectedListener {

    @BindView(R.id.barLayout)
    BottomBarLayout barLayout;
    public static final int first = 0;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public final ISupportFragment fragments[] = {
            AFragment.newInstance(),
            BFragment.newInstance()
    };

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(View mView, Bundle savedInstanceState) {
        super.initView(mView, savedInstanceState);
        EventBus.getDefault().register(this);
        barLayout.setOnItemSelectedListener(this);
        loadMultipleRootFragment(R.id.contentLayout, first,
                fragments[0],
                fragments[1]);
    }

    @Override
    public void onItemSelected(BottomBarItem bottomBarItem, int previousPosition, int currentPosition) {
        showHideFragment(fragments[currentPosition]);
    }


    @Subscribe
    public void KeyboardChangelistener(KeyboardChangeEvent chang) {
        if (chang.isShow) {
            barLayout.setVisibility(View.GONE);
        } else {
            barLayout.postDelayed(() -> {
                barLayout.setVisibility(View.VISIBLE);
            }, 50);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
