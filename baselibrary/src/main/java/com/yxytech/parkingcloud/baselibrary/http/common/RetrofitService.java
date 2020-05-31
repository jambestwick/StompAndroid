package com.yxytech.parkingcloud.baselibrary.http.common;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.yxytech.parkingcloud.baselibrary.http.interceptor.HttpCacheInterceptor;
import com.yxytech.parkingcloud.baselibrary.http.interceptor.HttpHeaderInterceptor;
import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.StrUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit实例化类
 * Created by zhpan on 2018/3/21.
 */

public class RetrofitService {
    private static final String TAG = RetrofitService.class.getName();

    public static OkHttpClient.Builder getOkHttpClientBuilder() {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtil.e(TAG, Thread.currentThread().getName() + ",accept:" + Log.getStackTraceString(throwable));
            }
        });
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor((String message) -> {
//            try {
//                if (TextUtils.isEmpty(message)) return;
//                String s = message.substring(0, 1);
//                //如果收到响应是json才打印
//                if ("{".equals(s) || "[".equals(s)) {
//                    LogUtil.i("OKHttp--1---RetrofitService", URLDecoder.decode(message, "utf-8"));
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                LogUtil.e("OKHttp-----RetrofitService", message);
//            }
//        });
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        File cacheFile = new File(BaseApplication.getAppContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
        return new OkHttpClient.Builder()
                .readTimeout(RxRetrofitApp.getDefaultTimeout(), TimeUnit.MILLISECONDS)
                .connectTimeout(RxRetrofitApp.getDefaultTimeout(), TimeUnit.MILLISECONDS)
                //.pingInterval(3, TimeUnit.SECONDS)
                //.addInterceptor(interceptor)
                //.addInterceptor(loggingInterceptor)
                // .addInterceptor(new HttpHeaderInterceptor())
                // .addNetworkInterceptor(new HttpCacheInterceptor())
                //.sslSocketFactory(createSSLSocketFactory())  // https认证 如果要使用https且为自定义证书 可以去掉这两行注释，并自行配制证书。
                .hostnameVerifier((hostname, session) -> true)
                .cache(cache);

    }

    public static SSLHelper.SSLParams setSSLParams(Context context) throws IOException {
        InputStream certIs = context.getAssets().open(SSLHelper.TRUSTSTORE_PUB_KEY);
        InputStream priKeyIs = context.getAssets().open(SSLHelper.CLIENT_PRI_KEY);
        return SSLHelper.getSslSocketFactory(null, priKeyIs, SSLHelper.CLIENT_BKS_PASSWORD);
    }

    public static Retrofit.Builder getRetrofitBuilder(String baseUrl) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
        OkHttpClient okHttpClient;
        SSLHelper.SSLParams sslParams = null;
        try {
            sslParams = setSSLParams(BaseApplication.getAppContext());
            okHttpClient = RetrofitService.getOkHttpClientBuilder().sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager).build();
        } catch (IOException e) {
            e.printStackTrace();
            okHttpClient = getOkHttpClientBuilder().build();
        }
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))     //json 自动解析最外层
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl);
    }
}
