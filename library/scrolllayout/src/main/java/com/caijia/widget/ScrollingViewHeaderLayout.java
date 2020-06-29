/*
 * Copyright (C) 2017 juying, caijia.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.caijia.widget;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RequiresApi;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;

/**
 * Created by cai.jia on 2017/9/6 0006.
 */

public class ScrollingViewHeaderLayout extends FrameLayout implements NestedScrollingParent {

    private static final String TAG = "viewPager_header_layout";
    private static final int SCROLL_MODE_SNAP = 1;
    private static final int SCROLL_MODE_NORMAL = 2;
    private View headerView;
    private View scrollingViewParent;
    private NestedScrollingParentHelper nestedScrollingParentHelper;
    private int headVisibleMinHeight;
    private int scrollDistance;
    private int minFlingVelocity;
    private int maxFlingVelocity;
    private float initialMotionX;
    private float initialMotionY;
    private float lastTouchX;
    private float lastTouchY;
    private int activePointerId;
    private boolean isBeginDragged;
    private VelocityTracker velocityTracker;
    private int maxFlexibleHeight;
    private int touchSlop;

    /**
     * 这里如果用{@link android.widget.OverScroller 在有些手机上} {@link OverScroller#getCurrVelocity()} 为0
     */
    private Scroller mScroller;
    private FlingRunnable flingRunnable;
    private View currScrollingView;
    private OffsetChangeListener headerViewScrollListener;
    private int scrollMode;
    private boolean isDebug = true;
    private List<FlexibleViewWrapper> flexibleViews;
    private List<GradientViewWrapper> gradientViews;
    private ValueAnimator animator;
    private boolean isSeriesScroll;

    public ScrollingViewHeaderLayout(Context context) {
        this(context, null);
    }

    public ScrollingViewHeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollingViewHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollingViewHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.ScrollingViewHeaderLayout);
            headVisibleMinHeight = a.getDimensionPixelOffset(R.styleable.ScrollingViewHeaderLayout_headVisibleMinHeight, 0);
            scrollMode = a.getInt(R.styleable.ScrollingViewHeaderLayout_headScrollMode, SCROLL_MODE_NORMAL);
            isSeriesScroll = a.getBoolean(R.styleable.ScrollingViewHeaderLayout_isSeriesScroll, false);

        } finally {
            if (a != null) {
                a.recycle();
            }
        }

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
        minFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        maxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();

        nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mScroller = new Scroller(context);
        flingRunnable = new FlingRunnable();

        flexibleViews = new ArrayList<>();
        gradientViews = new ArrayList<>();
    }

    public void setHeadVisibleMinHeight(int minHeight) {
        this.headVisibleMinHeight = minHeight;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            boolean isHeaderLayout = lp.isHeaderLayout;
            boolean isScrollingLayout = lp.isScrollingLayout;
            boolean isFlexibleLayout = lp.isFlexibleLayout;
            float flexibleRatio = lp.flexibleRatio;
            boolean isGradientLayout = lp.isGradientLayout;
            int gradientColor = lp.gradientColor;

            if (isFlexibleLayout) {
                FlexibleViewWrapper wrapper = new FlexibleViewWrapper(child, flexibleRatio);
                flexibleViews.add(wrapper);
            }

            if (isGradientLayout) {
                GradientViewWrapper wrapper = new GradientViewWrapper(child, gradientColor);
                gradientViews.add(wrapper);
            }

            if (isHeaderLayout) {
                headerView = child;
            }

            if (isScrollingLayout) {
                scrollingViewParent = child;
            }
        }

        if (childCount == 2 && headerView == null) {
            headerView = getChildAt(0);
        }

        if (childCount == 2 && scrollingViewParent == null) {
            scrollingViewParent = getChildAt(1);
        }

        if (headerView == null || scrollingViewParent == null) {
            throw new RuntimeException("please set header layout and scrolling layout");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (scrollingViewParent != null && headVisibleMinHeight > 0) {
            scrollingViewParent.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(
                            scrollingViewParent.getMeasuredHeight() - headVisibleMinHeight,
                            MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int left = getPaddingLeft();
        int right = getMeasuredWidth() - getPaddingRight();
        int headerViewHeight = headerView.getMeasuredHeight();
        headerView.layout(left, getPaddingTop(), right, getPaddingTop() + headerViewHeight);
        scrollingViewParent.layout(left, getPaddingTop() + headerViewHeight, right,
                getPaddingTop() + headerViewHeight + scrollingViewParent.getMeasuredHeight());
    }

    private void onMoveDown(MotionEvent ev) {
        initialMotionY = lastTouchY = ev.getY(0);
        initialMotionX = lastTouchX = ev.getX(0);
        activePointerId = ev.getPointerId(0);
        isBeginDragged = false;
    }

    private void onSecondaryPointerDown(MotionEvent ev) {
        int index = ev.getActionIndex();
        activePointerId = ev.getPointerId(index);
        lastTouchX = ev.getX(index);
        lastTouchY = ev.getY(index);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int index = ev.getActionIndex();
        if (ev.getPointerId(index) == activePointerId) {
            final int newIndex = index == 0 ? 1 : 0;
            activePointerId = ev.getPointerId(newIndex);
            lastTouchX = ev.getX(newIndex);
            lastTouchY = ev.getY(newIndex);
        }
    }

    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    private void recyclerVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private void computeVelocity() {
        if (velocityTracker != null) {
            velocityTracker.computeCurrentVelocity(1000);
        }
    }

    /**
     * 获取当前可伸缩的高度
     *
     * @return
     */
    private int getRemainingFlexibleHeight() {
        return headerView.getHeight() - headVisibleMinHeight + scrollDistance;
    }

    private
    @Nullable
    View getCurrentScrollView() {
        if (scrollingViewParent == null) {
            return null;
        }

        return findScrollingView(scrollingViewParent);
    }

    private View findScrollingView(View view) {
        if (isScrollingView(view)) {
            return view;
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            if (group instanceof ViewPager) {
                ViewPager pager = (ViewPager) group;
                PagerAdapter adapter = pager.getAdapter();
                if (!(adapter instanceof CurrentViewProvider)) {
                    throw new RuntimeException("PagerAdapter must be implements"
                            + CurrentViewProvider.class.getCanonicalName());
                }

                CurrentViewProvider provider = (CurrentViewProvider) adapter;
                View currentView = provider.provideCurrentView(pager.getCurrentItem());
                View scrollingView = findScrollingView(currentView);
                if (scrollingView != null)
                    log("scrollingView=" + scrollingView.toString());
                return scrollingView;

            } else {
                int childCount = group.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = group.getChildAt(i);
                    View scrollingView = findScrollingView(child);
                    if (scrollingView != null)
                        return scrollingView;
                }
            }
        }
        return null;
    }

    private boolean isScrollingView(View view) {
        return view != null && (view instanceof ScrollingView || view instanceof AbsListView
                || view instanceof ScrollView || view instanceof WebView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        addVelocityTracker(ev);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                onMoveDown(ev);
                currScrollingView = getCurrentScrollView();
                maxFlexibleHeight = headerView.getMeasuredHeight() - headVisibleMinHeight;
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                onSecondaryPointerDown(ev);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int index = ev.findPointerIndex(activePointerId);
                if (index < 0) {
                    return false;
                }

                float x = ev.getX(index);
                float y = ev.getY(index);

                startDragging(x, y);

                lastTouchX = x;
                lastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                recyclerVelocityTracker();
                isBeginDragged = false;
                activePointerId = INVALID_POINTER;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
                break;
            }
        }
        return isBeginDragged;
    }

    private void startDragging(float x, float y) {
        if (currScrollingView == null) {
            log("currentScrollView is null");
            return;
        }
        float dx = x - lastTouchX;
        float dy = y - lastTouchY;
        float xDis = Math.abs(x - initialMotionX);
        float yDis = Math.abs(y - initialMotionY);
        if (!isBeginDragged && yDis > touchSlop) {
            boolean isTop = scrollingViewIsTop();
            int remainingFlexibleHeight = getRemainingFlexibleHeight();
            if ((remainingFlexibleHeight != 0 && dy < 0) || (isTop && dy > 0)) {
                isBeginDragged = true;
            }
            log("isBeginDragged = " + isBeginDragged);
        }
    }

    public boolean scrollingViewIsTop() {
        if (currScrollingView == null) {
            return false;
        }
        return !ViewCompat.canScrollVertically(currScrollingView, -1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        addVelocityTracker(ev);

        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                onMoveDown(ev);
                currScrollingView = getCurrentScrollView();
                maxFlexibleHeight = headerView.getMeasuredHeight() - headVisibleMinHeight;
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                onSecondaryPointerDown(ev);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int index = ev.findPointerIndex(activePointerId);
                if (index < 0) {
                    return false;
                }

                float x = ev.getX(index);
                float y = ev.getY(index);

                startDragging(x, y);
                //move
                int dy = (int) (y - lastTouchY);
                log("move dy = " + dy);

                //上滑
                if (dy < 0) {
                    //上滑动,可伸缩的距离已经滑完,这时滑动dy不进行平移,而是滑动currScrollingView
                    int overflowDis = translateChild(dy);
                    if (overflowDis != 0) {
                        handleCurrentScrollingView(-overflowDis);
                    }

                } else {
                    //将currScrollingView滑动到顶部,然后再平移头部
                    boolean isTop = scrollingViewIsTop();
                    if (isTop) {
                        translateChild(dy);

                    } else {
                        handleCurrentScrollingView(-dy);
                    }

                    log("move isTop = " + isTop);
                }

                lastTouchX = x;
                lastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                int index = ev.findPointerIndex(activePointerId);
                if (index < 0) {
                    return false;
                }
                computeVelocity();
                int velocityY = (int) velocityTracker.getYVelocity(activePointerId);
                //fling
                log("touch fling velocityY = " + -velocityY);

                if (velocityIsValid(velocityY)) {
                    fling(-velocityY);

                } else {
                    flingOrTouchStop();
                }
                activePointerId = INVALID_POINTER;
                isBeginDragged = false;
                break;
            }
        }
        return true;
    }

    private void handleCurrentScrollingView(int dy) {
        currScrollingView.scrollBy(0, dy);
    }

    private boolean velocityIsValid(int velocity) {
        int absVelocity = Math.abs(velocity);
        return absVelocity > minFlingVelocity && absVelocity < maxFlingVelocity;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {

    }

    @Override
    public void onStopNestedScroll(View target) {

    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        boolean isTop = scrollingViewIsTop();
        log("onNestedPreScroll  =" + isTop);
        if (isTop) {
            //可以向上滚动的距离 dy>0表示向上滚动
            int remainingFlexibleHeight = getRemainingFlexibleHeight();
            if (dy > 0 && remainingFlexibleHeight > 0) {
                if (dy > remainingFlexibleHeight) {
                    consumed[1] = remainingFlexibleHeight;
                } else {
                    consumed[1] = dy;
                }
                translateChild(-consumed[1]);

            } else if (dy < 0 && remainingFlexibleHeight < maxFlexibleHeight) {
                log("pre scroll down");
                if (dy > maxFlexibleHeight - remainingFlexibleHeight) {
                    consumed[1] = maxFlexibleHeight - remainingFlexibleHeight;

                } else {
                    consumed[1] = dy;
                }
                translateChild(-consumed[1]);
            }
        }
    }

    /**
     * 滚动HeaderView 和 TargetView
     *
     * @param dy y轴的距离差
     * @return 返回溢出距离(假如 距离顶部的距离3, dy = 5, 这时溢出为2)
     */
    private int translateChild(int dy) {
        scrollDistance += dy;
        int overflowDis = 0;
        if (scrollDistance > 0) {
            overflowDis = scrollDistance;
            scrollDistance = 0;
        }

        if (scrollDistance < -maxFlexibleHeight) {
            overflowDis = scrollDistance + maxFlexibleHeight;
            scrollDistance = -maxFlexibleHeight;
        }

        headerView.setTranslationY(scrollDistance);
        scrollingViewParent.setTranslationY(scrollDistance);
        if (headerViewScrollListener != null) {
            headerViewScrollListener.onOffsetChanged(scrollDistance, maxFlexibleHeight);
        }
        handleFlexibleView(scrollDistance);
        handleGradientView(scrollDistance, maxFlexibleHeight);
        return overflowDis;
    }

    private void handleGradientView(int scrollDistance, int maxFlexibleHeight) {
        if (gradientViews.isEmpty()) {
            return;
        }

        for (GradientViewWrapper wrapper : gradientViews) {
            View gradientView = wrapper.gradientView;
            int color = wrapper.gradientColor;
            gradientView.setBackgroundColor(color);
            float alpha = (float) Math.abs(scrollDistance) / maxFlexibleHeight;
            gradientView.setTranslationY(scrollDistance);
            gradientView.setAlpha(alpha);
        }
    }

    private void handleFlexibleView(int scrollDistance) {
        if (flexibleViews.isEmpty()) {
            return;
        }

        for (FlexibleViewWrapper wrapper : flexibleViews) {
            View flexibleView = wrapper.flexibleView;
            float ratio = wrapper.ratio;
            flexibleView.setTranslationY(scrollDistance * ratio);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        //向下滑时，currentScrollView 到top ,然后手动滑动头部。
        if (isSeriesScroll && velocityY < 0) {
            log("onNestedPreFling velocityY = " + velocityY);
            flingScrollingViewToTop((int) velocityY);
        }
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    private int previousY;

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller != null && mScroller.computeScrollOffset()) {
            int currY = mScroller.getCurrY();
            int dy = currY - previousY;
            if (scrollingViewIsTop()) {
                log("computeScroll dy = " + dy);
                translateChild(-dy);
            }
            previousY = currY;
            if (scrollDistance < 0) {
                ViewCompat.postInvalidateOnAnimation(this);

            } else {
                mScroller.abortAnimation();
            }

        } else {
            previousY = 0;
        }
    }

    private void fling(int velocityY) {
        if (!mScroller.isFinished()) {
            log("fling is not finish");
            return;
        }

        mScroller.fling(
                0, 0, //init value
                0, velocityY, //velocity
                0, 0, // x
                Integer.MIN_VALUE, Integer.MAX_VALUE); //y
        if (mScroller.computeScrollOffset()) {
            flingRunnable.setDirection(velocityY > 0 ? FlingRunnable.UP : FlingRunnable.DOWN);
            ViewCompat.postOnAnimation(this, flingRunnable);
        }
    }

    /**
     * ScrollingView滑动到顶部,然后在惯性滑动头部
     *
     * @param velocityY
     */
    private void flingScrollingViewToTop(int velocityY) {
        if (!mScroller.isFinished()) {
            return;
        }

        mScroller.fling(
                0, 0, //init value
                0, velocityY, //velocity
                0, 0, // x
                Integer.MIN_VALUE, Integer.MAX_VALUE); //y
        if (mScroller.computeScrollOffset()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void flingTarget(int currVelocity) {
        final View targetView = currScrollingView;
        if (targetView == null) {
            log("fling target is null");
            return;
        }

        if (targetView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) targetView;
            recyclerView.fling(0, currVelocity);

        } else if (targetView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) targetView;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                absListView.fling(currVelocity);
            }

        } else if (targetView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) targetView;
            scrollView.fling((int) mScroller.getCurrVelocity());

        } else if (targetView instanceof NestedScrollView) {
            NestedScrollView scrollView = (NestedScrollView) targetView;
            scrollView.fling(currVelocity);

        } else if (targetView instanceof WebView) {
            WebView webView = (WebView) targetView;
            webView.flingScroll(0, currVelocity);
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return nestedScrollingParentHelper.getNestedScrollAxes();
    }

    public void setOnHeaderViewScrollListener(OffsetChangeListener listener) {
        this.headerViewScrollListener = listener;
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    private void log(String s) {
        if (isDebug) {
            Log.d(TAG, s);
        }
    }

    private void flingOrTouchStop() {
        if (scrollMode == SCROLL_MODE_SNAP) {
            boolean up = Math.abs(scrollDistance) > maxFlexibleHeight * 0.5f;
            animTranslate(0, (up ? maxFlexibleHeight : 0) - Math.abs(scrollDistance));
        }
    }

    private boolean isAnimRunning() {
        return animator != null && animator.isStarted();
    }

    private void animTranslate(float start, float end) {
        if (isAnimRunning()) {
            return;
        }
        animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(computeDuration(end - start));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                translateChild((int) -value);
            }
        });
        animator.start();
    }

    private long computeDuration(float distance) {
        return pxToDp(Math.round(Math.abs(distance))) * 4;
    }

    private int pxToDp(@Px int value) {
        return (int) Math.ceil(value / getResources().getDisplayMetrics().density);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (Build.VERSION.SDK_INT >= 19 && p instanceof LinearLayout.LayoutParams) {
            return new LayoutParams((LinearLayout.LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    public interface CurrentViewProvider {

        View provideCurrentView(int position);
    }

    public interface OffsetChangeListener {

        /**
         * @param scrollDis    头部View滚动的距离
         * @param maxScrollDis 头部View最大滚动的距离
         */
        void onOffsetChanged(int scrollDis, int maxScrollDis);
    }

    private static class FlexibleViewWrapper {

        View flexibleView;
        float ratio;

        public FlexibleViewWrapper(View flexibleView, float ratio) {
            this.flexibleView = flexibleView;
            this.ratio = ratio;
        }
    }

    private static class GradientViewWrapper {

        View gradientView;
        int gradientColor;

        public GradientViewWrapper(View gradientView, int gradientColor) {
            this.gradientView = gradientView;
            this.gradientColor = gradientColor;
        }
    }

    private static class LayoutParams extends FrameLayout.LayoutParams {

        private boolean isHeaderLayout;
        private boolean isScrollingLayout;
        private boolean isFlexibleLayout;
        private float flexibleRatio;
        private boolean isGradientLayout;
        private int gradientColor;

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = null;
            try {
                a = c.obtainStyledAttributes(attrs, R.styleable.ScrollingViewHeaderLayout_Layout);
                isHeaderLayout = a.getBoolean(R.styleable.ScrollingViewHeaderLayout_Layout_isHeaderLayout, false);
                isScrollingLayout = a.getBoolean(R.styleable.ScrollingViewHeaderLayout_Layout_isScrollingLayout, false);
                isFlexibleLayout = a.getBoolean(R.styleable.ScrollingViewHeaderLayout_Layout_isFlexibleLayout, false);
                flexibleRatio = a.getFloat(R.styleable.ScrollingViewHeaderLayout_Layout_flexibleRatio, 0.5f);
                isGradientLayout = a.getBoolean(R.styleable.ScrollingViewHeaderLayout_Layout_isGradientLayout, false);
                gradientColor = a.getColor(R.styleable.ScrollingViewHeaderLayout_Layout_gradientColor, Color.BLUE);

            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull MarginLayoutParams source) {
            super(source);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public LayoutParams(@NonNull FrameLayout.LayoutParams source) {
            super(source);
        }
    }

    private class FlingRunnable implements Runnable {

        static final int DOWN = 1;
        static final int UP = -1;
        private int direction;

        private int oldCurrY;

        void setDirection(int direction) {
            this.direction = direction;
        }

        private void reset() {
            oldCurrY = 0;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                int currY = mScroller.getCurrY();
                log("currY = " + currY);
                int dy = oldCurrY - currY;
                translateChild(dy);
                oldCurrY = currY;
                switch (direction) {
                    case UP: {
                        boolean toTop = scrollDistance <= -maxFlexibleHeight;
                        if (toTop) {
                            reset();
                            int currVelocity = (int) mScroller.getCurrVelocity();
                            log("fling up currVelocity " + currVelocity);
                            mScroller.abortAnimation();
                            flingTarget(currVelocity);

                        } else {
                            ViewCompat.postOnAnimation(ScrollingViewHeaderLayout.this, this);
                        }
                        break;
                    }

                    case DOWN: {
                        boolean toBottom = scrollDistance >= 0;
                        if (toBottom) {
                            reset();
                            mScroller.abortAnimation();

                        } else {
                            ViewCompat.postOnAnimation(ScrollingViewHeaderLayout.this, this);
                        }
                        break;
                    }
                }

            } else {
                reset();
                flingOrTouchStop();
                log("fling stop");
            }
        }
    }
}
