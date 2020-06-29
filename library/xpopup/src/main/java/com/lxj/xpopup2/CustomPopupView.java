package com.lxj.xpopup2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;

import com.lxj.easyadapter.EasyAdapter;
import com.lxj.easyadapter.ViewHolder;
import com.lxj.xpopup2.R;
import com.lxj.xpopup.impl.PartShadowPopupView;
import com.lxj.xpopup.widget.VerticalRecyclerView;

import java.util.Arrays;
import java.util.HashMap;

public class CustomPopupView extends PartShadowPopupView {

    private int width;
    private int height;
    VerticalRecyclerView recyclerView;
    private HashMap<Integer, String> map = new HashMap<>();

    public CustomPopupView(@NonNull Context context, View view, String[] data, OnSelectListener2 selectListener) {
        super(context);
        this.selectListener = selectListener;
        this.data = data;
        width = view.getWidth();
        map.clear();
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.xpopup_attach_impl_list;
    }


    @Override
    protected int getMaxWidth() {
        return width;
    }

    @Override
    protected int getMaxHeight() {
        return 600;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        recyclerView = (VerticalRecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(width, -2));
        recyclerView.setupDivider();
        final EasyAdapter<String> adapter = new EasyAdapter<String>(Arrays.asList(data), R.layout.xpopup_adapter_text2) {
            @Override
            protected void bind(@NonNull ViewHolder holder, @NonNull final String s, final int position) {
                holder.setText(R.id.tv_text, s);

                holder.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (selectListener != null) {
                            selectListener.onSelect(position, data[position]);
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    String[] data;

    private OnSelectListener2 selectListener;

    public CustomPopupView setOnSelectListener(OnSelectListener2 selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    public interface OnSelectListener2 {
        void onSelect(int id, String data);
    }

    @Override
    protected Drawable getPopupBackground() {
        return super.getPopupBackground();
    }
}
