package com.huawei.jams.testautostart.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.huawei.jams.testautostart.api.IdeaApiService;
import com.yxytech.parkingcloud.baselibrary.http.common.RetrofitService;
import com.yxytech.parkingcloud.baselibrary.utils.Base64Util;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.OkHttpClient;
import okhttp3.internal.tls.OkHostnameVerifier;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/5/7<p>
 * <p>更新时间：2020/5/7<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class StompService extends Service {
    private static final String TAG = StompService.class.getName();
    private StompClient mStompClient;
    private boolean mNeedConnect;
    private Timer mTimer = new Timer();
    private static final long RECONNECT_TIME_INTERVAL = 1000;

    private static StompService instance;
    private static final Object lock = new Object();

    public static StompService getInstance() {
        StompService instance = StompService.instance;
        if (instance == null) {
            synchronized (lock) {
                instance = StompService.instance;
                if (instance == null) {
                    StompService.instance = instance = new StompService();
                }
            }
        }
        return instance;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "onBind(),Intent:" + intent);
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "onUnbind(),Intent:" + intent);
        return super.onUnbind(intent);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate()");
        createStompClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand(),Intent:" + intent + ",flags:" + flags + ",startId:" + startId);
        LogUtil.d(TAG, "Stomp 线程名称：" + Thread.currentThread().getName());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy()");
        disconnect();
    }

    @SuppressLint("CheckResult")
    private void connect() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", Base64Util.encodeBasicAuth("100000000000001", "AAAAAAAAAAAAAAAAAAAA_1"));
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, IdeaApiService.WS_URI, headers, RetrofitService.getOkHttpClientBuilder().sslSocketFactory(RetrofitService.getSSLContextByName(this, "le").getSocketFactory()).build());
        mStompClient.connect();
        mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    //关注lifecycleEvent的回调来决定是否重连
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            mNeedConnect = false;
                            LogUtil.d(TAG, "Stomp connection opened");
                            break;
                        case ERROR:
                            mNeedConnect = true;
                            LogUtil.d(TAG, "Stomp connection error :" + lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            mNeedConnect = true;
                            LogUtil.d(TAG, "Stomp connection closed");
                            break;
                    }
                });
    }

    private void disconnect() {
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
    }

    //创建长连接，服务器端没有心跳机制的情况下，启动timer来检查长连接是否断开，如果断开就执行重连
    private void createStompClient() {
        connect();
//        mTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Log.d(TAG, "forlan debug in timer ======================");
//                if (mNeedConnect && NetworkUtils.isConnected()) {
//                    mStompClient = null;
//                    connect();
//                    Log.d(TAG, "forlan debug start connect WS_URI");
//                }
//            }
//        }, RECONNECT_TIME_INTERVAL, RECONNECT_TIME_INTERVAL);
    }

    //点对点订阅，根据用户名来推送消息
//    private void registerStompTopic() {
//        mStompClient.topic("/user/" + "xxx" + "/msg").subscribe((Action1<StompMessage>) stompMessage -> Log.d(TAG, "debug msg is " + stompMessage.getPayload()));
//    }

    /**
     * 发送信息
     ***/
    @SuppressLint("CheckResult")
    public void sendStomp(String destPath, String jsonMsg) {
        mStompClient.send("", jsonMsg)
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP send successfully");
                }, throwable -> {
                    Log.e(TAG, "Error send STOMP ", throwable);
                });

    }

    public void receiveStomp(String destPath, DisposableSubscriber<StompMessage> disposableSubscriber) {
        mStompClient.topic(destPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableSubscriber);

//                .subscribe(topicMessage -> {
//                    Log.d(TAG, "Received " + topicMessage.getPayload());
//                }, throwable -> {
//                    Log.e(TAG, "Error on subscribe topic", throwable);
//                });
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

//    private OkHttpClient buildOKHttpClient() {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        StandardWebSocketClient wsClient = new StandardWebSocketClient();
//        File ksFile = new File(System.getProperty("user.dir"), "keystore/le.jks");
//        Properties systemProperties = System.getProperties();
////		systemProperties.put("javax.net.debug", "all");
//        InputStream inputStream = context.getAssets().open("cacert.pem");
//        systemProperties.put(SSLContextConfigurator.TRUST_STORE_FILE, ksFile.getAbsolutePath());
//        systemProperties.put(SSLContextConfigurator.TRUST_STORE_PASSWORD, "LE");
//        systemProperties.put(SSLContextConfigurator.TRUST_STORE_TYPE, "jks");
//        SSLContextConfigurator sslContextConfigurator = new SSLContextConfigurator();
//        sslContextConfigurator.retrieve(systemProperties);
//        SSLEngineFactory sslEngineConfigurator = new SSLEngineConfigurator(sslContextConfigurator, true, false, false);
//        wsClient.getUserProperties().put(ClientProperties.SSL_ENGINE_CONFIGURATOR, sslEngineConfigurator);
//
//        stompClient = new WebSocketStompClient(wsClient);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        taskScheduler = new ThreadPoolTaskScheduler();
//        taskScheduler.setPoolSize(1);
//        taskScheduler.initialize();
//        stompClient.setTaskScheduler(taskScheduler);
//        return okHttpClient;
//    }


}
