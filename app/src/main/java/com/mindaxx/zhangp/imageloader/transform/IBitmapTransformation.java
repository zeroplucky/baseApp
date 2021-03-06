package com.mindaxx.zhangp.imageloader.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;


public interface IBitmapTransformation {

    Bitmap transform(Bitmap source, ImageView imageView);

    Context getContext();
}
