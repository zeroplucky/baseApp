package com.contrarywind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.timedailog.ArrayWheelAdapter;
import com.contrarywind.timedailog.BasePickerView;
import com.contrarywind.timedailog.PickerOptions;
import com.contrarywind.view.R;
import com.contrarywind.view.WheelView;

import java.util.List;

/**
 * Created by Administrator on 2019/10/15.
 */

public class SelectDialog<T> extends BasePickerView implements View.OnClickListener {

    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";
    private List<T> items;
    private WheelView wv_option1;
    private int index;

    public SelectDialog(Context context) {
        super(context);
        mPickerOptions = new PickerOptions();
        initView(context);
    }


    private void initView(Context context) {

        initViews();
        initAnim();

        LayoutInflater.from(context).inflate(R.layout.select_dialog_, contentContainer);
        setOutSideCancelable(true);
        //确定和取消按钮
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        btnSubmit.setTag(TAG_SUBMIT);
        btnCancel.setTag(TAG_CANCEL);

        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        wv_option1 = (WheelView) findViewById(R.id.options1);// 初始化时显示的数据

        // 添加监听
        wv_option1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                SelectDialog.this.index = index;
            }
        });
    }

    public SelectDialog setAdapter(List<T> list) {
        this.items = list;
        if (items == null || items.size() == 0) return this;
        wv_option1.setAdapter(new ArrayWheelAdapter(items));
        wv_option1.setCyclic(false);
        wv_option1.setCurrentItem(0);

        return this;
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_SUBMIT)) {
            if (onseletListener2 != null && items != null && items.size() != 0) {
                onseletListener2.onItemSelect(index);
            }
        }
        dismiss();
    }


    public static interface OnseletListener2 {
        void onItemSelect(int index);
    }

    private OnseletListener2 onseletListener2;

    public SelectDialog setOnseletListener2(OnseletListener2 onseletListener2) {
        this.onseletListener2 = onseletListener2;
        return this;
    }
}
