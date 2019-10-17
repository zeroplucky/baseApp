package com.contrarywind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.contrarywind.timedailog.BasePickerView;
import com.contrarywind.timedailog.PickerOptions;
import com.contrarywind.view.R;

public abstract class UniversalBottomLayout extends BasePickerView {

    public UniversalBottomLayout(Context context) {
        super(context);
        mPickerOptions = new PickerOptions();
        initView(context);
    }

    private void initView(Context context) {
        initViews();
        initAnim();
        setOutSideCancelable(true);
        LayoutInflater.from(context).inflate(setLayout(), contentContainer);
        initData(contentContainer);
    }

    public abstract int setLayout();

    public abstract void initData(View view);

}
