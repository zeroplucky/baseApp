package com.mindaxx.zhangp.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.WaitingView;
import com.contrarywind.SelectDialog;
import com.contrarywind.UniversalBottomLayout;
import com.contrarywind.timedailog.OnTimeSelectListener;
import com.contrarywind.timedailog.TimePickerBuilder;
import com.contrarywind.view.WheelView;
import com.mindaxx.zhangp.R;
import com.mindaxx.zhangp.base.BaseMvpFragment;
import com.mindaxx.zhangp.bean.event.KeyboardChangeEvent;
import com.mindaxx.zhangp.util.KeyboardUtils;
import com.mindaxx.zhangp.widget.KeyboardChangeListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AFragment extends BaseMvpFragment {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.title_name)
    TextView titleName;

    public static AFragment newInstance() {
        Bundle args = new Bundle();
        AFragment fragment = new AFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_a;
    }

    @Override
    protected void initView(View mView, Bundle savedInstanceState) {
        super.initView(mView, savedInstanceState);
        titleName.setText("居民信息核查");
        setKeybroadLinstenner();
    }

    /*
     * 设置键盘监听
     * */
    private void setKeybroadLinstenner() {
        new KeyboardChangeListener(getActivity()).setKeyBoardListener(new KeyboardChangeListener.KeyBoardListener() {
            @Override
            public void onKeyboardChange(boolean isShow, int keyboardHeight) {
                EventBus.getDefault().post(new KeyboardChangeEvent(isShow));
            }
        });
    }

    @OnClick({R.id.calarder, R.id.next,
            R.id.country, R.id.nation, R.id.credentials_type, R.id.has_date, R.id.sex,
            R.id.resident_type, R.id.tenant_period, R.id.authorization_scope})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:
                WaitingView.showSuccess(getContext(), "提交成功");
                break;
            case R.id.country:
                selectOne(new String[]{"中国", "外国"});
                break;
            case R.id.nation:
                selectOne(new String[]{"汉族", "少数名族"});
                break;
            case R.id.credentials_type:
                selectOne(new String[]{"身份证", "护照"});
                break;
            case R.id.has_date:
                if (KeyboardUtils.isSoftInputVisible(getActivity())) {
                    KeyboardUtils.hideSoftInput(getActivity());
                    return;
                }
                new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        Toast.makeText(mContext, "" + getTime(date), Toast.LENGTH_SHORT).show();
                    }
                }).setType(new boolean[]{true, true, true, false, false, false})
                        .setTitleText("出生日期设置")
                        .setSubmitColor(getResources().getColor(R.color.colorAccent))
                        .setCancelColor(getResources().getColor(R.color.colorAccent))
                        .build().show();
                break;
            case R.id.sex:
                selectOne(new String[]{"男", "女"});
                break;
            case R.id.resident_type:
                selectOne(new String[]{"常驻", "短租"});
                break;
            case R.id.tenant_period:
                if (KeyboardUtils.isSoftInputVisible(getActivity())) {
                    KeyboardUtils.hideSoftInput(getActivity());
                    return;
                }
                new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        Toast.makeText(mContext, "" + getTime(date), Toast.LENGTH_SHORT).show();
                    }
                }).setType(new boolean[]{true, true, true, false, false, false})
                        .setTitleText("租客有效期设置")
                        .setSubmitColor(getResources().getColor(R.color.colorAccent))
                        .setCancelColor(getResources().getColor(R.color.colorAccent))
                        .setRangDate(Calendar.getInstance(), null).build().show();
                break;
            case R.id.authorization_scope: // 授权范围
                break;
            case R.id.calarder:// 授权时间
                initTimePicker();
                break;
        }
    }


    private void selectOne(String[] content) {
        if (KeyboardUtils.isSoftInputVisible(getActivity())) {
            KeyboardUtils.hideSoftInput(getActivity());
            return;
        }
        new SelectDialog(getContext()).setAdapter(Arrays.asList(content)).setOnseletListener2(new SelectDialog.OnseletListener2() {
            @Override
            public void onItemSelect(int index) {
                Toast.makeText(mContext, "" + content, Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    /*
     * 时间选择
     * */
    private void initTimePicker() {
        if (KeyboardUtils.isSoftInputVisible(getActivity())) {
            KeyboardUtils.hideSoftInput(getActivity());
            return;
        }
        new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Toast.makeText(mContext, "" + getTime(date), Toast.LENGTH_SHORT).show();
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setTitleText("门禁授权时间设置")
                .setSubmitColor(getResources().getColor(R.color.colorAccent))
                .setCancelColor(getResources().getColor(R.color.colorAccent))
                .setRangDate(Calendar.getInstance(), null).build().show();
    }

    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        return format.format(date);
    }

    /*
     *
     * */
    private void MultiSelect() {
        new UniversalBottomLayout(getContext()) {
            @Override
            public int setLayout() {
                return R.layout.multi_select_dialog_;
            }

            @Override
            public void initData(View view) {
                Button btnSubmit = view.findViewById(R.id.btnSubmit);
                Button btnCancel = view.findViewById(R.id.btnCancel);
                WheelView op1 = view.findViewById(R.id.options1);
                WheelView op2 = view.findViewById(R.id.options2);
                WheelView op3 = view.findViewById(R.id.options3);


            }
        };
    }

}
