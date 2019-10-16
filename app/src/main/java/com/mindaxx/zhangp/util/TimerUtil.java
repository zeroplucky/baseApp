package com.mindaxx.zhangp.util;

import android.os.CountDownTimer;

/**
 * Created by "liulihu" on 2017/10/31 0031.
 * email:511421121@qq.com
 */
public class TimerUtil extends CountDownTimer {

    private static boolean timeOut = true;// 时间到
    private long downTime = 0;// 剩余时间

    public static void setTimeOut(boolean timeOut) {
        TimerUtil.timeOut = timeOut;
    }

    /**
     * 计时器的构造方法
     *
     * @param millisInFuture    总共的时间
     * @param countDownInterval 没次计时的间隔
     */
    private TimerUtil(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    private static TimerUtil mInsetnce;

    /**
     * 这里弄成单例模式是以为多个界面公用一个计时器 不使用同步是应为不是高并发
     *
     * @return
     */
    public static TimerUtil getInstence() {
        if (mInsetnce == null) {
            mInsetnce = new TimerUtil(60000, 1000);
        }
        return mInsetnce;
    }

    /**
     * 获取计时器的状态 真 怎可以计数 假 则 正在计数
     *
     * @return
     */
    public static boolean getTimerStatus() {
        return timeOut;
    }

    /**
     * 开始计时
     */
    public void startTimer() {
        if (timeOut) {
            mInsetnce.start();
            timeOut = false;
            if (onDownTimerListener != null) {
                onDownTimerListener.lintenerOnStart();
            }
        }
    }

    @Override
    public void onTick(long millisUntilFinished) {
        downTime = millisUntilFinished / 1000;
        if (onDownTimerListener != null) {
            onDownTimerListener.lintenerOnTick(downTime);
        }
    }

    @Override
    public void onFinish() {
        timeOut = true;
        if (onDownTimerListener != null) {
            onDownTimerListener.lintenerOnFinish();
        }
    }

    private OnDownTimerListener onDownTimerListener;

    public void setOnDownTimerListener(OnDownTimerListener listener) {
        this.onDownTimerListener = listener;
    }

    public interface OnDownTimerListener {
        /**
         * 计时开始
         */
        public void lintenerOnStart();

        /**
         * 实时显示变化的数字
         *
         * @param downTime
         */
        public void lintenerOnTick(Long downTime);

        /**
         * 计时完成
         */
        public void lintenerOnFinish();
    }

}
