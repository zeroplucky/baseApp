package com.mindaxx.zhangp.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.mindaxx.zhangp.R;
import com.mindaxx.zhangp.imageloader.view.GlideImageView;
import com.nineoldandroids.view.ViewHelper;


/**
 * github:https://github.com/MrBoudar/MyDragPhotoView
 */
public class FingerPanGroup extends FrameLayout {

    private float mDownX;
    private float mTranslationX;
    private float mLastTranslationX;
    private float mDownY;
    private float mTranslationY;
    private float mLastTranslationY;
    private static int MAX_TRANSLATE_Y = 300;
    private final static int MAX_EXIT_Y = 300;
    private final static long DURATION = 150;
    private boolean isAnimate = false;
    private int fadeIn = R.anim.fade_in;
    private int fadeOut = R.anim.fade_out;
    private onAlphaChangedListener mOnAlphaChangedListener;
    private GlideImageView photoView;

    public FingerPanGroup(Context context) {
        this(context, null);
    }

    public FingerPanGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FingerPanGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        photoView = (GlideImageView) ((RelativeLayout) getChildAt(0)).getChildAt(0);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = false;
        int action = ev.getAction() & ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getRawY();
                mDownX = ev.getRawX();
            case MotionEvent.ACTION_MOVE:
                if (null != photoView) {
                    float rawY = ev.getRawY();
                    if (rawY > mDownY + 20) {
                        isIntercept = true;
                    }
                }
                break;
        }
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getRawY();
                mDownX = event.getRawX();
            case MotionEvent.ACTION_MOVE:
                if (null != photoView) {
                    onOneFingerPanActionMove(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                onActionUp();
                break;
        }
        return true;
    }

    private void onOneFingerPanActionMove(MotionEvent event) {
        float moveY = event.getRawY();
        mTranslationY = moveY - mDownY + mLastTranslationY;
        mTranslationX = moveY - mDownX + mLastTranslationX;
        float percent = Math.abs(mTranslationY / (MAX_TRANSLATE_Y + photoView.getHeight()));
        Log.e("percent", "onOneFingerPanActionMove: " + percent);
        float mAlpha = (1 - percent * 3);
        if (mAlpha >= 1) {
            mAlpha = 0.9f;
        } else if (mAlpha < 0) {
            mAlpha = 0;
        }
        FrameLayout linearLayout = (FrameLayout) getParent();
        if (null != linearLayout) {
            linearLayout.getBackground().mutate().setAlpha((int) (mAlpha * 255));
        }
        //触发回调 根据距离处理其他控件的透明度 显示或者隐藏角标，文字信息等
        if (null != mOnAlphaChangedListener) {
            mOnAlphaChangedListener.onTranslationYChanged(mTranslationY);
            mOnAlphaChangedListener.onAlphaChanged(mAlpha);
        }
        ViewHelper.setScrollY(this, -(int) mTranslationY);
    }

    private void onActionUp() {
        if (Math.abs(mTranslationY) > MAX_EXIT_Y) {
            exitWithTranslation(mTranslationY);
        } else {
            resetCallBackAnimation();
        }
    }

    public void exitWithTranslation(float currentY) {
        if (currentY > 0) {
            ValueAnimator animDown = ValueAnimator.ofFloat(mTranslationY, getHeight());
            animDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fraction = (float) animation.getAnimatedValue();
                    ViewHelper.setScrollY(FingerPanGroup.this, -(int) fraction);
                }
            });
            animDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    reset();
                    try {

                        Activity activity = ((Activity) getContext());
                        activity.finish();
                        activity.overridePendingTransition(0, fadeOut);
                    } catch (Exception e) {
                        if (mOnAlphaChangedListener != null) {
                            mOnAlphaChangedListener.onfinish();
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animDown.setDuration(DURATION);
            animDown.setInterpolator(new LinearInterpolator());
            animDown.start();
        } else {
            ValueAnimator animUp = ValueAnimator.ofFloat(mTranslationY, -getHeight());
            animUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fraction = (float) animation.getAnimatedValue();
                    ViewHelper.setScrollY(FingerPanGroup.this, -(int) fraction);
                }
            });
            animUp.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    reset();
                    try {
                        Activity activity = ((Activity) getContext());
                        activity.finish();
                        activity.overridePendingTransition(0, fadeOut);
                    } catch (Exception e) {
                        if (mOnAlphaChangedListener != null) {
                            mOnAlphaChangedListener.onfinish();
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animUp.setDuration(DURATION);
            animUp.setInterpolator(new LinearInterpolator());
            animUp.start();
        }
    }

    private void resetCallBackAnimation() {
        ValueAnimator animatorY = ValueAnimator.ofFloat(mTranslationY, 0);
        animatorY.setDuration(DURATION);
        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (isAnimate) {
                    mTranslationY = (float) valueAnimator.getAnimatedValue();
                    mLastTranslationY = mTranslationY;
                    ViewHelper.setScrollY(FingerPanGroup.this, -(int) mTranslationY);
                }
            }
        });
        animatorY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAnimate) {
                    mTranslationY = 0;
                    FrameLayout linearLayout = (FrameLayout) getParent();
                    if (null != linearLayout) {
                        linearLayout.getBackground().mutate().setAlpha(255);
                    }
                    invalidate();
                    reset();
                }
                isAnimate = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorY.start();
    }


    public interface onAlphaChangedListener {
        void onAlphaChanged(float alpha);

        void onTranslationYChanged(float translationY);

        void onfinish();


    }


    public static class onAlphaChangedListenerImpl implements onAlphaChangedListener {

        @Override
        public void onAlphaChanged(float alpha) {
            Log.e("onAlphaChanged", "alpha: " + alpha);
        }

        @Override
        public void onTranslationYChanged(float translationY) {
            Log.e("onAlphaChanged", "translationY: " + translationY);
        }

        @Override
        public void onfinish() {
        }
    }

    //暴露的回调方法（可根据位移距离或者alpha来改变主UI控件的透明度等
    public void setOnAlphaChangeListener(onAlphaChangedListener alphaChangeListener) {
        mOnAlphaChangedListener = alphaChangeListener;
    }

    private void reset() {
        if (null != mOnAlphaChangedListener) {
            mOnAlphaChangedListener.onTranslationYChanged(mTranslationY);
            mOnAlphaChangedListener.onAlphaChanged(1);
        }
    }
}
