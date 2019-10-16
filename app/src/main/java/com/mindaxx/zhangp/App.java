package com.mindaxx.zhangp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.minda.logger.AndroidLogAdapter;
import com.minda.logger.DiskLogAdapter;
import com.minda.logger.Logger;
import com.mindaxx.zhangp.util.SpUtil;
import com.mindaxx.zhangp.util.UncaughtExceptionHandlerImpl;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;


/**
 * Created by Administrator on 2019/5/21.
 */

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SpUtil.init(getApplicationContext());
        initBugly();
        initLogger();
        initCrashThrowable();
    }

    private void initBugly() {
        Beta.autoInit = true;
        Beta.initDelay = 1 * 1000;
        Beta.autoCheckUpgrade = true;
        Bugly.init(getApplicationContext(), "3699d7411b", false);
    }

    /*
     * 日志
     * */
    private void initLogger() {
        Logger.addLogAdapter(new AndroidLogAdapter());
        //Logger.addLogAdapter(new DiskLogAdapter(getApplicationContext()));
    }

    /*
     * 捕获异常
     * */
    private void initCrashThrowable() {
        final Context applicationContext = getApplicationContext();
        UncaughtExceptionHandlerImpl.getInstance().init(applicationContext, MainActivity.class,
                new UncaughtExceptionHandlerImpl.OnCrashBack() {
                    @Override
                    public void onBack(Throwable ex, String result) {
                        Logger.logWithTag("throwable-123", result, ex);
                    }
                });
    }

}
