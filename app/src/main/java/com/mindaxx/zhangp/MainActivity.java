package com.mindaxx.zhangp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.utils.HttpUtils;
import com.lzy.okgo.utils.IOUtils;
import com.mindaxx.zhangp.base.BaseMvpActivity;
import com.mindaxx.zhangp.imageloader.OnProgressListener;
import com.mindaxx.zhangp.ui.activity.LoginActivity;
import com.mindaxx.zhangp.util.SpUtil;
import com.sunfusheng.ApkUpdater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends BaseMvpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //skip2next();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //skip2next();
    }

    private void skip2next() {
        boolean isLogin = SpUtil.getIsLogin();
        if (!isLogin) {
            skipActivity(mContext, LoginActivity.class);
            finish();
            return;
        }
        MainFragment fragment = findFragment(MainFragment.class);
        if (fragment == null) {
            loadRootFragment(R.id.contentLayout, MainFragment.newInstance()); // 审批角色
        }
    }

    public void down(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://121.29.10.1/f5.market.mi-img.com/download/AppStore/0b8b552a1df0a8bc417a5afae3a26b2fb1342a909/com.qiyi.video.apk";
//                OkGo.<File>get(url).execute(new FileCallback() {
//                    @Override
//                    public void onSuccess(Response<File> response) {
//
//                    }
//
//                    @Override
//                    public void downloadProgress(Progress progress) {
//                        super.downloadProgress(progress);
//                        Log.e("xxx", "downloadProgress: " + progress.currentSize + "   " + progress.totalSize);
//                    }
//                });

                Request.Builder builder = new Request.Builder();
                Request build = builder.url(url).build();
                OkHttpClient client = new OkHttpClient.Builder().build();
                Call call = client.newCall(build);
                try {
                    Response response = call.execute();
                    convertResponse(response);
                } catch (Throwable e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    public static final String DM_TARGET_FOLDER = File.separator + "download" + File.separator; //下载目标文件夹
    private String folder;                  //目标文件存储的文件夹路径
    private String fileName;                //目标文件存储的文件名

    public File convertResponse(Response response) throws Throwable {
        String url = response.request().url().toString();
        if (TextUtils.isEmpty(folder))
            folder = Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER;
        if (TextUtils.isEmpty(fileName)) fileName = HttpUtils.getNetFileName(response, url);

        File dir = new File(folder);
        IOUtils.createFolder(dir);
        File file = new File(dir, fileName);
        IOUtils.delFileOrFolder(file);

        InputStream bodyStream = null;
        byte[] buffer = new byte[8192];
        FileOutputStream fileOutputStream = null;
        try {
            ResponseBody body = response.body();
            if (body == null) return null;

            bodyStream = body.byteStream();
            Progress progress = new Progress();
            progress.totalSize = body.contentLength();
            progress.fileName = fileName;
            progress.filePath = file.getAbsolutePath();
            progress.status = Progress.LOADING;
            progress.url = url;
            progress.tag = url;

            int len;
            fileOutputStream = new FileOutputStream(file);
            while ((len = bodyStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
                Progress.changeProgress(progress, len, new Progress.Action() {
                    @Override
                    public void call(Progress progress) {
                        Log.e("xxx", "downloadProgress: " + progress.currentSize + "   " + progress.totalSize);
                    }
                });
            }
            fileOutputStream.flush();
            return file;
        } finally {
            IOUtils.closeQuietly(bodyStream);
            IOUtils.closeQuietly(fileOutputStream);
        }
    }
}
