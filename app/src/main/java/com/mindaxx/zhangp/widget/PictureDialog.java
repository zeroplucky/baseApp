package com.mindaxx.zhangp.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.mindaxx.zhangp.R;
import com.mindaxx.zhangp.imageloader.ImageLoader2;

/**
 * 显示图片
 */
public class PictureDialog extends Dialog implements FingerPanGroup.onAlphaChangedListener {

    private Window window;
    private PhotoView imageView;
    private FingerPanGroup group;
    private String imageUrl;


    public PictureDialog(@NonNull Context context, String imageUrl) {
        super(context, R.style.photoWindowStyle);
        this.imageUrl = imageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View parent = View.inflate(getContext(), R.layout.picture_view_layout, null);
        setContentView(parent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.dimAmount = 0f;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);

        imageView = parent.findViewById(R.id.preview_image);
        group = parent.findViewById(R.id.group);

        ImageLoader2.loadImg(imageUrl, imageView);

        group.setOnAlphaChangeListener(this);
        imageView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                dismiss();
            }
        });
    }


    @Override
    public void onAlphaChanged(float alpha) {

    }

    @Override
    public void onTranslationYChanged(float translationY) {

    }

    @Override
    public void onfinish() {
        dismiss();
    }

}
