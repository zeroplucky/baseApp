package com.sunfusheng;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

/**
 * @author sunfusheng on 2018/2/17.
 */
public class ApkUpdater {
    private Context context;
    private String apiToken;
    private String appId;
    private String appVersionUrl;
    private String apkPath;
    private FirAppInfo.AppInfo appInfo;
    private boolean forceUpDater = false;

    private FirDialog firDialog;
    private FirDownloader firDownloader;
    private FirNotification firNotification;

    public ApkUpdater(Context context) {
        this(context, null, null);
    }

    public ApkUpdater(Context context, String apiToken, String appId) {
        this.context = context;
        this.apiToken = apiToken;
        this.appId = appId;
    }

    public ApkUpdater apiToken(String apiToken) {
        this.apiToken = apiToken;
        return this;
    }

    public ApkUpdater appId(String appId) {
        this.appId = appId;
        return this;
    }

    public ApkUpdater apkPath(String apkPath) {
        this.apkPath = apkPath;
        return this;
    }

    public ApkUpdater setForceUpDater(boolean forceUpDater) {
        this.forceUpDater = forceUpDater;
        return this;
    }

    public void checkVersion() {
        if (TextUtils.isEmpty(apiToken) || TextUtils.isEmpty(appId)) {
            Toast.makeText(context, "请设置 API TOKEN && APP ID", Toast.LENGTH_LONG).show();
            return;
        }

        this.appVersionUrl = "http://api.fir.im/apps/latest/" + appId + "?api_token=" + apiToken;

        if (firDownloader != null && firDownloader.isGoOn()) {
            Toast.makeText(context, "正在下载【" + appInfo.apkName + "】，请稍后", Toast.LENGTH_LONG).show();
            return;
        }

        FirPermissionHelper.getInstant().requestPermission(context, new FirPermissionHelper.OnPermissionCallback() {
            @Override
            public void onGranted() {
                requestAppInfo();
            }

            @Override
            public void onDenied() {
                Toast.makeText(context, "申请权限未通过", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestAppInfo() {
        new Thread(() -> {
            appInfo = new FirAppInfo().requestAppInfo(appVersionUrl);
            if (appInfo == null) {
                return;
            }

            String apkName = appInfo.appName + "-" + appInfo.appVersionName + ".apk";
            if (TextUtils.isEmpty(apkPath)) {
                apkPath = Environment.getExternalStorageDirectory() + File.separator;
            }

            appInfo.appId = appId;
            appInfo.apkName = apkName;
            appInfo.apkPath = apkPath;
            appInfo.apkLocalUrl = apkPath + apkName;
            FirUpdaterUtils.logger(appInfo.toString());

            boolean needUpdate = appInfo.appVersionCode > FirUpdaterUtils.getVersionCode(context);
            if (needUpdate) {
                FirUpdaterUtils.runOnMainThread(this::initFirDialog);
            }
        }).start();
    }


    private void initFirDialog() {
        firDialog = new FirDialog(forceUpDater);
        firDialog.showAppInfoDialog(context, appInfo);
        firDialog.setOnClickDownloadDialogListener(new FirDialog.OnClickDownloadDialogListener() {
            @Override
            public void onClickDownload(DialogInterface dialog) {
                downloadApk();
            }

            @Override
            public void onClickBackgroundDownload(DialogInterface dialog) {
                firNotification = new FirNotification().createBuilder(context, true);
                firNotification.setContentTitle(appInfo.appName);
            }

            @Override
            public void onClickCancelDownload(DialogInterface dialog) {
                firDownloader.cancel();
            }
        });
    }

    private void downloadApk() {
        firDialog.showDownloadDialog(context, 0);
        firDownloader = new FirDownloader(context.getApplicationContext(), appInfo);
        firDownloader.setOnDownLoadListener(new FirDownloader.OnDownLoadListener() {
            @Override
            public void onProgress(int progress) {
                firDialog.showDownloadDialog(context, progress);
                if (firNotification != null) {
                    firNotification.setContentText("下载更新中..." + progress + "%");
                    firNotification.notifyNotification(progress);
                }
            }

            @Override
            public void onSuccess() {
                firDialog.dismissDownloadDialog();
                if (firNotification != null) {
                    firNotification.cancel();
                }
                firNotification = new FirNotification().createBuilder(context, false);
                firNotification.setContentIntent(FirUpdaterUtils.getInstallApkIntent(context, appInfo.apkLocalUrl));
                firNotification.setContentTitle(appInfo.appName);
                firNotification.setContentText("下载完成，点击安装");
                firNotification.notifyNotification();
                FirUpdaterUtils.installApk(context, appInfo.apkLocalUrl);
            }

            @Override
            public void onError() {
                firDialog.dismissDownloadDialog();
                if (firNotification != null) {
                    firNotification.cancel();
                }
            }
        });
        firDownloader.downloadApk();
    }

    public FirDialog getFirDialog() {
        return firDialog;
    }

    public FirNotification getFirNotification() {
        return firNotification;
    }

    /*
     *
     *
     * */
    public void checkVersion(String url, boolean forceUpDater, String appName, String appVersionName, String updateDesc) {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(context, "请设置 url", Toast.LENGTH_LONG).show();
            return;
        }

        this.appVersionUrl = url;

        if (firDownloader != null && firDownloader.isGoOn()) {
            Toast.makeText(context, "正在下载【" + appName + "】，请稍后", Toast.LENGTH_LONG).show();
            return;
        }

        if (firDownloader == null && FirDownloader.isGoOn) {
            Toast.makeText(context, "正在下载【" + appName + "】，请稍后", Toast.LENGTH_LONG).show();
            return;
        }

        FirPermissionHelper.getInstant().requestPermission(context, new FirPermissionHelper.OnPermissionCallback() {
            @Override
            public void onGranted() {
                String apkName = appName + "-" + appVersionName + ".apk";
                if (TextUtils.isEmpty(apkPath)) {
                    apkPath = Environment.getExternalStorageDirectory() + File.separator;
                }
                FirUpdaterUtils.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        initFirDialog(forceUpDater, appName, appVersionName, updateDesc, apkName);
                    }
                });
            }

            @Override
            public void onDenied() {
                Toast.makeText(context, "申请权限未通过", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initFirDialog(boolean forceUpDater, String appName, String appVersionName, String updateDesc, String apkName) {
        firDialog = new FirDialog(forceUpDater);
        firDialog.showAppInfoDialog(context, appName, appVersionName, updateDesc);
        firDialog.setOnClickDownloadDialogListener(new FirDialog.OnClickDownloadDialogListener() {
            @Override
            public void onClickDownload(DialogInterface dialog) {
                downloadApk(appVersionUrl, appName, apkName, apkPath + apkName);
            }

            @Override
            public void onClickBackgroundDownload(DialogInterface dialog) {
                firNotification = new FirNotification().createBuilder(context, true);
                firNotification.setContentTitle(appName);
            }

            @Override
            public void onClickCancelDownload(DialogInterface dialog) {
                firDownloader.cancel();
            }
        });
    }

    private void downloadApk(String url, String appName, String apkName, String path) {
        firDialog.showDownloadDialog(context, 0);
        firDownloader = new FirDownloader(context.getApplicationContext(), url, apkName, path);
        firDownloader.setOnDownLoadListener(new FirDownloader.OnDownLoadListener() {
            @Override
            public void onProgress(int progress) {
                firDialog.showDownloadDialog(context, progress);
                if (firNotification != null) {
                    firNotification.setContentText("下载更新中..." + progress + "%");
                    firNotification.notifyNotification(progress);
                }
            }

            @Override
            public void onSuccess() {
                firDialog.dismissDownloadDialog();
                if (firNotification != null) {
                    firNotification.cancel();
                }
                firNotification = new FirNotification().createBuilder(context, false);
                firNotification.setContentIntent(FirUpdaterUtils.getInstallApkIntent(context, path));
                firNotification.setContentTitle(appName);
                firNotification.setContentText("下载完成，点击安装");
                firNotification.notifyNotification();
                FirUpdaterUtils.installApk(context, path);
            }

            @Override
            public void onError() {
                firDialog.dismissDownloadDialog();
                if (firNotification != null) {
                    firNotification.cancel();
                }
            }
        });
        firDownloader.downloadApk();
    }
}
