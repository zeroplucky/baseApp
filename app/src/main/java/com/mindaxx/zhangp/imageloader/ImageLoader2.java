package com.mindaxx.zhangp.imageloader;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mindaxx.zhangp.R;


public class ImageLoader2 {

    public static void loadImg(String url, ImageView view) {
        Glide.with(view.getContext())
                .load(url)
                .into(view);
    }

    public static void loadImgWhit(String url, @DrawableRes int draw, ImageView view) {
        Glide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.no_img)
                .error(draw).into(view);
    }

    public static void displayCircleImage(Context context, String url, ImageView imageView, int defRes) {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(defRes).error(defRes)
                .bitmapTransform(new GlideTransform(context, new CircleBitmapTransformation(context)))
                .into(imageView);
    }

    public static void displayRoundImage(Context context, String url, ImageView imageView, int defRes, int radius) {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(defRes).error(defRes)
                .bitmapTransform(new GlideTransform(context, new RoundBitmapTransformation(imageView.getContext(), radius)))
                .into(imageView);
    }

    public static void displayBlurImage(Context context, String url, ImageView imageView, int defRes, int blurRadius) {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(defRes).error(defRes)
                .bitmapTransform(new GlideTransform(context, new BlurBitmapTransformation(context, blurRadius)))
                .into(imageView);
    }
}
