package com.yxytech.parkingcloud.baselibrary.http.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.load.engine.Resource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import com.yxytech.parkingcloud.baselibrary.http.interceptor.HttpCacheInterceptor;
import com.yxytech.parkingcloud.baselibrary.http.interceptor.HttpHeaderInterceptor;
import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit实例化类
 * Created by zhpan on 2018/3/21.
 */

public class RetrofitService {
    private static final String TAG = RetrofitService.class.getName();

    public static OkHttpClient.Builder getOkHttpClientBuilder() {

//        Interceptor interceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Response response = chain.proceed(chain.request());
//
//                Request request = chain.request();
//                String url = request.url().toString();
//                LogUtil.d("RetrofitService", "url: " + url);
//                //存入Session
//                if (response.header("Set-Cookie") != null) {
//                    // JSESSIONID=EEF29A1F6D276E60751850ACCCD25D88; Path=/police-check-service; HttpOnly
//                    String SetCookie = response.header("Set-Cookie");
//                    String Cookie = SetCookie.substring(0, SetCookie.indexOf(";"));
//                    PreferencesManager.getInstance(Utils.getContext()).put("cookie", Cookie);
//                }
//                return response;
//            }
//
//        };

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor((String message) -> {
            try {
                if (TextUtils.isEmpty(message)) return;
                String s = message.substring(0, 1);
                //如果收到响应是json才打印
                if ("{".equals(s) || "[".equals(s)) {
                    LogUtil.i("OKHttp--1---RetrofitService", URLDecoder.decode(message, "utf-8"));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                LogUtil.e("OKHttp-----RetrofitService", message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        File cacheFile = new File(BaseApplication.getAppContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
        return new OkHttpClient.Builder()
                .readTimeout(RxRetrofitApp.getDefaultTimeout(), TimeUnit.MILLISECONDS)
                .connectTimeout(RxRetrofitApp.getDefaultTimeout(), TimeUnit.MILLISECONDS)
//                .addInterceptor(interceptor)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new HttpHeaderInterceptor())
                .addNetworkInterceptor(new HttpCacheInterceptor())
                .sslSocketFactory(createSSLSocketFactory())  // https认证 如果要使用https且为自定义证书 可以去掉这两行注释，并自行配制证书。
                .hostnameVerifier((s, sslSession) -> true)
                .cache(cache);

    }

    public static Retrofit.Builder getRetrofitBuilder(String baseUrl) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
        OkHttpClient okHttpClient = RetrofitService.getOkHttpClientBuilder().build();
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))     //json 自动解析最外层
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl);
    }

    private static SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
            LogUtil.e(TAG, "createSSLSocketFactory: " + e.getMessage());
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates,
                                       String s) throws java.security.cert.CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }


    /**
     * 根据文件名获取证书路径
     **/
    public static SSLContext getSSLContextByName(Context context, String keyName) {
        // 设置证书路径
        String certPath = String.format("keystore/%s.jks", keyName);
        // 设置证书密码
        String certPass = "LE";
        // 获取证书
        return getSSLContext(context, certPath, certPass);
    }

    private static SSLContext getSSLContext(Context context, String certPath, String certPass) {
        try {
            KeyStore clientStore = KeyStore.getInstance("JKS");
            // 读取resource下的文件 支持jar方式启动
            InputStream inputStream = context.getAssets().open(certPath);
            char[] passArray = certPass.toCharArray();
            clientStore.load(inputStream, passArray);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, passArray);
            KeyManager[] kms = kmf.getKeyManagers();
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(kms, null, new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            LogUtil.i(TAG, "设置证书出错");
        }
        return null;
    }

}
