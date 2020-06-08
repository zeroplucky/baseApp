package com.mindaxx.zhangp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.ForegroundNotification;
import com.fanjun.keeplive.config.ForegroundNotificationClickListener;
import com.fanjun.keeplive.config.KeepLiveService;
import com.minda.logger.AndroidLogAdapter;
import com.minda.logger.DiskLogAdapter;
import com.minda.logger.Logger;
import com.mindaxx.zhangp.http.HttpManager;
import com.mindaxx.zhangp.imageloader.ProgressManager;
import com.mindaxx.zhangp.util.SpUtil;
import com.mindaxx.zhangp.util.UncaughtExceptionHandlerImpl;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import java.io.InputStream;


/**
 * Created by Administrator on 2019/5/21.
 */

public class App extends Application {

    public static Context mContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = base;
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SpUtil.init(getApplicationContext());
        HttpManager.init(this);
        keepLive();
        initGlide();
        initBugly();
        initLogger();
        initCrashThrowable();
    }

    private void initGlide() {
        Glide.get(this).register(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(ProgressManager.getOkHttpClient()));
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
        // Logger.addLogAdapter(new DiskLogAdapter(getApplicationContext()));
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

    //启动保活服务
    private void keepLive() {
        KeepLive.startWork(this, KeepLive.RunMode.ENERGY, new ForegroundNotification("测试", "描述", R.mipmap.ic_launcher,

                        new ForegroundNotificationClickListener() {
                            @Override
                            public void foregroundNotificationClick(Context context, Intent intent) {
                                //定义前台服务的通知点击事件
                            }
                        }),
                //你需要保活的服务，如socket连接、定时任务等，建议不用匿名内部类的方式在这里写
                new KeepLiveService() {
                    /**
                     * 运行中
                     * 由于服务可能会多次自动启动，该方法可能重复调用
                     */
                    @Override
                    public void onWorking() {
                        Log.e("KeepLive", "onWorking: ------------------------");
                    }

                    /**
                     * 服务终止
                     * 由于服务可能会被多次终止，该方法可能重复调用，需同onWorking配套使用，如注册和注销broadcast
                     */
                    @Override
                    public void onStop() {

                    }
                }
        );
    }

}
