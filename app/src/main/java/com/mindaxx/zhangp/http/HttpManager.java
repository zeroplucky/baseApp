package com.mindaxx.zhangp.http;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/4/26.
 */

public class HttpManager {

    private static volatile HttpManager instance;
    private static OkHttpClient.Builder builder;
    public static Context mContext;

    private HttpManager(Application context) {
        mContext = context;
        builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.e("zpxx:", message));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);
        //信任所有证书
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        builder.hostnameVerifier(new SafeHostnameVerifier());
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.connectTimeout(30, TimeUnit.SECONDS);
        OkGo.getInstance().init(context).setRetryCount(0).setOkHttpClient(builder.build());
    }


    public static HttpManager init(Application context) {
        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null) {
                    instance = new HttpManager(context);
                }
            }
        }
        return instance;
    }

    public static OkHttpClient.Builder getBuilder() {
        return builder;
    }

    /*
   *
   * */
    public static void cancel(String url) {
        OkGo.getInstance().cancelTag(url);
    }

    public static void get(String url, OnHttpCall httpBack) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        GetRequest<String> request = OkGo.<String>get(url)
                .tag(url);
        request.execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                httpBack.onSuccess(body);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                httpBack.onFailed(response.message());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                httpBack.onFinish();
            }

        });
    }

    public static void get(String url, Map<String, String> params, OnHttpCall httpBack) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        GetRequest<String> request = OkGo.<String>get(url)
                .tag(url);
        if (params != null) {
            request.params(params);
        }
        request.execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                httpBack.onSuccess(body);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                httpBack.onFailed(response.message());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                httpBack.onFinish();
            }

        });
    }


    public static void post(String url, Map<String, String> params, OnHttpCall httpBack) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        PostRequest<String> request = OkGo.<String>post(url)
                .tag(url);
        if (params != null) {
            request.params(params);
        }
        request.execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                httpBack.onSuccess(body);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                httpBack.onFailed(response.message());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                httpBack.onFinish();
            }
        });
    }

    public static void up(String url, Map<String, String> params, Map<String, File> files, OnHttpCall httpBack) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        PostRequest<String> request = OkGo.<String>post(url)
                .tag(url);
        request.isMultipart(true);
        if (params != null) {
            request.params(params);
        }
        if (files != null) {
            for (String key : files.keySet()) {
                request.params(key, files.get(key));
            }
        }
        request.execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                httpBack.onSuccess(body);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                httpBack.onFailed(response.message());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                httpBack.onFinish();
            }
        });
    }


    public static void upFiles(String url, Map<String, String> params, Map<String, List<File>> files, OnHttpCall httpBack) {
        PostRequest<String> request = OkGo.<String>post(url)
                .tag(url);
        request.isMultipart(true);
        if (params != null) {
            request.params(params);
        }
        if (files != null) {
            for (String key : files.keySet()) {
                request.addFileParams(key, files.get(key));
            }
            request.execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    String body = response.body();
                    httpBack.onSuccess(body);
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    httpBack.onFailed(response.message());
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    httpBack.onFinish();
                }
            });
        }
    }

    private class SafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            //验证主机名是否匹配
            return true;
        }
    }


}
