package com.mindaxx.zhangp.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@TargetApi(Build.VERSION_CODES.M)
public class NetworkStatsHelper {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private NetworkStatsManager networkStatsManager;
    private int packageUid;

    public NetworkStatsHelper(Context context) {
        this.networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
        this.packageUid = uid(context);
    }

    /*
    * 获取所有程序一天内消耗的移动流量
    * */
    public long getAllDeviceTodayMobile(Context context) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context),
                    getTimesToday(),
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getTxBytes() + bucket.getRxBytes();
    }

    /*
    * 获取所有程序一个月内消耗的移动流量
    * */
    public long getAllDeviceMonthMobile(Context context) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context),
                    getTimesMonth(),
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getRxBytes() + bucket.getTxBytes();
    }

    /*
    * 获取所有程序一天内消耗的WIFI流量
    * */
    public long getAllDeviceTodayWifi() {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    "",
                    getTimesToday(),
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getRxBytes() + bucket.getTxBytes();
    }

    /*
    * 获取所有程序一月内消耗的WIFI流量
    * */
    public long getAllDeviceMonthWifi() {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    "",
                    getTimesMonth(),
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getRxBytes() + bucket.getTxBytes();
    }

    /*
    * 不准确，用第二种
    * */
    public long getTodayPackageBytesMobile(Context context) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context),
                    getTimesToday(),
                    System.currentTimeMillis(),
                    packageUid);
        } catch (Exception e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        long bite = 0;
        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket);
            bite += bucket.getRxBytes() + bucket.getTxBytes();
        }
        networkStats.close();
        return bite;
    }


    /*
    * 获取特定程序一天内消耗的移动流量
    * */
    public long getTodayPackageBytesMobile2(Context context) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.querySummary(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context),
                    getTimesToday(),
                    System.currentTimeMillis());
        } catch (Exception e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        long bite = 0;
        do {
            networkStats.getNextBucket(bucket);
            int uid = bucket.getUid();
            if (uid == packageUid) {
                bite += bucket.getRxBytes() + bucket.getTxBytes();
            }
        } while (networkStats.hasNextBucket());
        networkStats.close();
        return bite;
    }

    /*
    * 不准确，用第二种
    * */
    public long getMonthPackageBytesMobile(Context context) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context),
                    getTimesMonth(),
                    System.currentTimeMillis(),
                    packageUid);
        } catch (Exception e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        long bite = 0;
        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket);
            bite += bucket.getRxBytes() + bucket.getTxBytes();
        }
        networkStats.close();
        return bite;
    }

    /*
    * 获取特定程序一 月 内消耗的移动流量
    * */
    public long getMonthPackageBytesMobile2(Context context) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.querySummary(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context),
                    getTimesMonth(),
                    System.currentTimeMillis());
        } catch (Exception e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        long bite = 0;
        do {
            networkStats.getNextBucket(bucket);
            int uid = bucket.getUid();
            if (uid == packageUid) {
                bite += bucket.getRxBytes() + bucket.getTxBytes();
            }
        } while (networkStats.hasNextBucket());
        networkStats.close();
        return bite;
    }


    /*
    * 不准确，用第二种
    * */
    public long getTodayPackageBytesWifi(Context context) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    getTimesToday(),
                    System.currentTimeMillis(),
                    packageUid);
        } catch (Exception e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        long bite = 0;
        do {
            networkStats.getNextBucket(bucket);
            bite += bucket.getRxBytes() + bucket.getTxBytes();
        } while (networkStats.hasNextBucket());
        networkStats.close();
        return bite;
    }

    /*
    * 获取特定程序一天内消耗的WIFI流量
    * */
    public long getTodayPackageBytesWifi2(Context context) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.querySummary(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    getTimesToday(),
                    System.currentTimeMillis());
        } catch (Exception e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        long bite = 0;
        do {
            networkStats.getNextBucket(bucket);
            int uid = bucket.getUid();
            if (uid == packageUid) {
                bite += bucket.getRxBytes() + bucket.getTxBytes();
            }
        } while (networkStats.hasNextBucket());
        return bite;
    }

    /*
   * 不准确，用第二种
   * */
    public long getMonthPackageBytesWifi(Context context) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    getTimesMonth(),
                    System.currentTimeMillis(),
                    packageUid);
        } catch (Exception e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        long bite = 0;
        do {
            networkStats.getNextBucket(bucket);
            bite += bucket.getRxBytes() + bucket.getTxBytes();
        } while (networkStats.hasNextBucket());
        return bite;
    }

    /*
       * 获取特定程序一 月 内消耗的WIFI流量
       * */
    public long getMonthPackageBytesWifi2(Context context) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.querySummary(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    getTimesMonth(),
                    System.currentTimeMillis());
        } catch (Exception e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        long bite = 0;
        do {
            networkStats.getNextBucket(bucket);
            int uid = bucket.getUid();
            if (uid == packageUid) {
                bite += bucket.getRxBytes() + bucket.getTxBytes();
            }
        } while (networkStats.hasNextBucket());
        return bite;
    }

    private int uid(Context context) {
        PackageManager mPm = context.getPackageManager();
        String appName = getAppProcessName(context);
        int uid = -1;
        try {
            PackageInfo packageInfo = mPm.getPackageInfo(appName, PackageManager.GET_META_DATA);
            uid = packageInfo.applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
        }

        Log.e("networkStatsHelper", "uid: " + uid + "  ，appName = " + appName);
        return uid;
    }

    public static String getAppProcessName(Context context) {
        int pid = android.os.Process.myPid();//当前应用pid
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    public boolean hasPermissionToReadNetworkStats(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        requestReadNetworkStats(context);
        return false;
    }

    // 打开“有权查看使用情况的应用”页面
    private void requestReadNetworkStats(Context context) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 字节 转换为B MB GB
     *
     * @param size 字节大小
     * @return
     */
    public String bite(long size) {
        long rest = 0;
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size /= 1024;
        }

        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            rest = size % 1024;
            size /= 1024;
        }

        if (size < 1024) {
            size = size * 100;
            return String.valueOf((size / 100)) + "." + String.valueOf((rest * 100 / 1024 % 100)) + "MB";
        } else {
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
        }
    }


    @SuppressLint("MissingPermission")
    private String getSubscriberId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSubscriberId();
    }

    /**
     * 获取当天的零点时间
     */
    public static long getTimesToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long timeInMillis = cal.getTimeInMillis();
        String format = dateFormat.format(timeInMillis);
        return timeInMillis;
    }

    /*
    * 获得本月第一天0点时间
    * */
    public static long getTimesMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        long timeInMillis = cal.getTimeInMillis();
        String format = dateFormat.format(timeInMillis);
        return timeInMillis;
    }

    private void getNetStats(NetworkStatsHelper networkStatsHelper, Context context) {
        if (networkStatsHelper.hasPermissionToReadNetworkStats(context)) {

            long todayMobile = networkStatsHelper.getTodayPackageBytesMobile2(context);
            String todayMobile_ = networkStatsHelper.bite(todayMobile);

            long monthMobile2 = networkStatsHelper.getMonthPackageBytesMobile2(context);
            String monthMobile2_ = networkStatsHelper.bite(monthMobile2);

            long allTodayMobile = networkStatsHelper.getAllDeviceTodayMobile(context);
            String allTodayMobile1 = networkStatsHelper.bite(allTodayMobile);

            long allMonthMobile = networkStatsHelper.getAllDeviceMonthMobile(context);
            String allMonthMobile1 = networkStatsHelper.bite(allMonthMobile);
            Log.e("networkStatsHelper", "Mobile:----    , todayMobile_ =  " + todayMobile_ + "  , monthMobile2_ =  " + monthMobile2_ +
                    "  ,allTodayMobile =  " + allTodayMobile1 + "  , allMonthMobile = " + allMonthMobile1);

            long todayWifi2 = networkStatsHelper.getTodayPackageBytesWifi2(context);
            String todayWifi2_ = networkStatsHelper.bite(todayWifi2);

            long monthWifi2 = networkStatsHelper.getMonthPackageBytesWifi2(context);
            String monthWifi2_ = networkStatsHelper.bite(monthWifi2);

            long alltodayWifi = networkStatsHelper.getAllDeviceTodayWifi();
            String alltodayWifi_ = networkStatsHelper.bite(alltodayWifi);

            long allMonthWifi = networkStatsHelper.getAllDeviceMonthWifi();
            String allMonthWifi_ = networkStatsHelper.bite(allMonthWifi);
            Log.e("networkStatsHelper", "todayWifi2_: " + todayWifi2_ + "   , monthWifi2_ =  " + monthWifi2_ +
                    "   , alltodayWifi_ =  " + alltodayWifi_ + "  , allMonthWifi_ = " + allMonthWifi_);
        }
    }
}
