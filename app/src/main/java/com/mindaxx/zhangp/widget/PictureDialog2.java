package com.mindaxx.zhangp.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mindaxx.zhangp.App;
import com.mindaxx.zhangp.R;
import com.mindaxx.zhangp.imageloader.OnProgressListener;
import com.mindaxx.zhangp.imageloader.view.CircleProgressView;
import com.mindaxx.zhangp.imageloader.view.GlideImageView;

import java.util.List;

/**
 * 显示图片
 */
public class PictureDialog2 extends Dialog {

    private Window window;
    private List<String> imageUrl;
    private ViewPager mViewPager;
    private LinearLayout bottomll;
    private Drawable drawable1, drawable2;

    public PictureDialog2(@NonNull Context context, List<String> imageUrl) {
        super(context, R.style.photoWindowStyle);
        this.imageUrl = imageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View parent = View.inflate(getContext(), R.layout.picture_view_layout2, null);
        setContentView(parent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.dimAmount = 0f;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        //
        mViewPager = parent.findViewById(R.id.viewpager);
        bottomll = parent.findViewById(R.id.bottom_ll);
        IndexPagerAdapter adapter = new IndexPagerAdapter(getContext(), imageUrl);
        mViewPager.setAdapter(adapter);
        addBottomLayout();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                enable(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void addBottomLayout() {
        drawable1 = getContext().getResources().getDrawable(R.drawable._shape_before);
        drawable2 = getContext().getResources().getDrawable(R.drawable._shape_after);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(8), dp2px(8));
        params.gravity = Gravity.CENTER;
        params.leftMargin = params.rightMargin = dp2px(4);
        for (int i = 0; i < imageUrl.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackground(drawable1);
            imageView.setLayoutParams(params);
            bottomll.addView(imageView);
        }
        bottomll.getChildAt(0).setBackground(drawable2);
    }

    private void enable(int i) {
        int childCount = bottomll.getChildCount();
        for (int j = 0; j < childCount; j++) {
            bottomll.getChildAt(j).setBackground(drawable1);
        }
        bottomll.getChildAt(i).setBackground(drawable2);
    }

    class IndexPagerAdapter extends PagerAdapter {

        private List<String> data;
        private Context context;

        public IndexPagerAdapter(Context context, List<String> data) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View parent = View.inflate(context, R.layout.picture_view_layout, null);
            GlideImageView imageView = parent.findViewById(R.id.preview_image);
            CircleProgressView progressView = parent.findViewById(R.id.progressView);
            FingerPanGroup group = parent.findViewById(R.id.group);
            group.setOnAlphaChangeListener(new FingerPanGroup.onAlphaChangedListenerImpl() {
                @Override
                public void onfinish() {
                    PictureDialog2.this.dismiss();
                }
            });
            imageView.load(data.get(position), R.drawable.no_img, new OnProgressListener() {
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
            container.addView(parent);
            return parent;
        }
    }

    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
