package com.yxytech.parkingcloud.baselibrary.http.common;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.yxytech.parkingcloud.baselibrary.http.https.HttpsUtils;
import com.yxytech.parkingcloud.baselibrary.http.https.SSLHelper;
import com.yxytech.parkingcloud.baselibrary.http.https.SSLSocketFactoryCompat;
import com.yxytech.parkingcloud.baselibrary.http.https.Tls12SocketFactory;
import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Cache;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
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
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, null, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .build();

        List<ConnectionSpec> specs = new ArrayList<>();
        specs.add(cs);
        specs.add(ConnectionSpec.COMPATIBLE_TLS);
        specs.add(ConnectionSpec.CLEARTEXT);


//        OkHttpClient okHttpClient = RetrofitService
//                .getOkHttpClientBuilder()
//                .followRedirects(true)
//                .followSslRedirects(true)
//                .retryOnConnectionFailure(true)
//                .cache(null)
//                //.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
//                //.sslSocketFactory(new SSLSocketFactoryCompat(trustAllManager),trustAllManager)
//                .sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()))
//                .connectionSpecs(specs)
//                .build();


        return new Retrofit.Builder()
                .client(getNewHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))     //json 自动解析最外层
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl);
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, null, null);
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
            LogUtil.e(TAG, Thread.currentThread().getName() + "createSSLSocketFactory: " + Log.getStackTraceString(e));
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

    }

    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                LogUtil.e(TAG, "Error while setting TLS 1.2" + Log.getStackTraceString(exc));
            }
        }

        return client;
    }

    public static OkHttpClient getNewHttpClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS);

        return enableTls12OnPreLollipop(client).build();
    }

}
