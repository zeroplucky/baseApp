package com.sunfusheng;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author sunfusheng on 2018/2/17.
 */
public class FirDownloader {

    private static final int STATE_SUCCESS = -1;
    private static final int STATE_ERROR = -2;

    private Context context;
    private FirAppInfo.AppInfo appInfo;
    private int fileLength;
    private int currLength;

    public static boolean isGoOn;
    private int lastProgress = 0;
    private OnDownLoadListener onDownLoadListener;

    private String url;
    private String apkName;
    private String path;

    public FirDownloader(Context context, FirAppInfo.AppInfo appInfo) {
        this.context = context;
        this.appInfo = appInfo;
        this.fileLength = appInfo.appSize;
        this.url = appInfo.appInstallUrl;
        this.path = appInfo.apkLocalUrl;
    }

    public FirDownloader(Context context, String url, String apkName, String path) {
        this.context = context;
        this.url = url;
        this.apkName = apkName;
        this.path = path;
    }

    public void downloadApk() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                isGoOn = true;
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                currLength = FirUpdaterUtils.getCurrLengthValue(context, apkName);
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.setRequestProperty("Range", "bytes=" + currLength + "-");
                FirUpdaterUtils.logger("currLength: " + currLength + " fileLength: " + fileLength);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    if (fileLength == 0) {
                        fileLength = conn.getContentLength();
                    }
                    InputStream is = conn.getInputStream();
                    RandomAccessFile raf = new RandomAccessFile(path, "rwd");
                    raf.setLength(fileLength);
                    raf.seek(currLength);

                    int len;
                    int lastProgress = 0;
                    byte[] buffer = new byte[1024];

                    while ((len = is.read(buffer)) != -1) {
                        if (!isGoOn) {
                            break;
                        }

                        if (onDownLoadListener != null) {
                            currLength += len;
                            int progress = (int) ((float) currLength / (float) fileLength * 100);
                            if (lastProgress != progress) {
                                lastProgress = progress;
                                handler.sendEmptyMessage(progress);
                            }
                        }

                        raf.write(buffer, 0, len);
                    }

                    FirUpdaterUtils.closeQuietly(is, raf);

                    if (!isGoOn && currLength < fileLength) {
                        FirUpdaterUtils.putCurrLengthValue(context, apkName, currLength);
                    } else {
                        FirUpdaterUtils.putCurrLengthValue(context, apkName, 0);
                        handler.sendEmptyMessage(100);
                        handler.sendEmptyMessage(STATE_SUCCESS);
                    }
                } else {
                    handler.sendMessage(handler.obtainMessage(STATE_ERROR, "下载受限啦，明日早来哦^_^"));
                }
                conn.disconnect();
            } catch (Exception e) {
                FirUpdaterUtils.loggerError(e);
                FirUpdaterUtils.putCurrLengthValue(context, apkName, 0);
                handler.sendEmptyMessage(STATE_ERROR);
            } finally {
                isGoOn = false;
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    public boolean isGoOn() {
        return isGoOn;
    }

    public boolean isGoOnWithAppId(String appId) {
        return isGoOn && appInfo.appId.equalsIgnoreCase(appId);
    }

    public void cancel() {
        isGoOn = false;
    }

    public void setOnDownLoadListener(OnDownLoadListener onDownLoadListener) {
        this.onDownLoadListener = onDownLoadListener;
    }

    public interface OnDownLoadListener {
        void onProgress(int progress);

        void onSuccess();

        void onError();
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (onDownLoadListener != null) {
                switch (msg.what) {
                    case STATE_SUCCESS:
                        onDownLoadListener.onSuccess();
                        break;
                    case STATE_ERROR:
                        onDownLoadListener.onError();
                        if (msg.obj != null && msg.obj instanceof String) {
                            Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        if (lastProgress != msg.what) {
                            lastProgress = msg.what;
                            onDownLoadListener.onProgress(msg.what);
                        }
                        break;
                }
            }
        }
    };
}
