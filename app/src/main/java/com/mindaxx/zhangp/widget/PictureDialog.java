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

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.mindaxx.zhangp.R;
import com.mindaxx.zhangp.imageloader.GlideImageLoader;
import com.mindaxx.zhangp.imageloader.GlideImageLoader2;
import com.mindaxx.zhangp.imageloader.OnProgressListener;
import com.mindaxx.zhangp.imageloader.view.CircleProgressView;
import com.mindaxx.zhangp.imageloader.view.GlideImageView;

/**
 * 显示图片
 */
public class PictureDialog extends Dialog implements FingerPanGroup.onAlphaChangedListener {

    private Window window;
    private GlideImageView imageView;
    private FingerPanGroup group;
    private String imageUrl;
    CircleProgressView progressView;

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
        progressView = findViewById(R.id.progressView);
        group = parent.findViewById(R.id.group);
        imageView.load(imageUrl, 0, new OnProgressListener() {
            @Override
            public void onProgress(boolean isComplete, int percentage, long bytesRead, long totalBytes) {
                if (isComplete) {
                    progressView.setVisibility(View.GONE);
                } else {
                    progressView.setVisibility(View.VISIBLE);
                    progressView.setProgress(percentage);
                }
            }
        });
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
