package com.huawei.jams.testautostart.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.yxytech.parkingcloud.baselibrary.http.common.RetrofitService;
import com.yxytech.parkingcloud.baselibrary.http.common.SSLHelper;
import com.yxytech.parkingcloud.baselibrary.utils.Base64Util;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.NetworkUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/5/20<p>
 * <p>更新时间：2020/5/20<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class StompUtil {

    private static final String TAG = StompUtil.class.getName();
    private StompClient mStompClient;
    private boolean mNeedConnect;
    private static final long RECONNECT_TIME_INTERVAL = 5 * 1000;

    private static StompUtil instance;
    private static final Object lock = new Object();

    public static StompUtil getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new StompUtil();
                }
            }
        }
        return instance;
    }

    //创建长连接，服务器端没有心跳机制的情况下，启动timer来检查长连接是否断开，如果断开就执行重连
    public void createStompClient(String userName, String password) {
        try {
            connect(userName, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "forlan debug in timer ======================");
                if (mNeedConnect && NetworkUtils.isConnected()) {
                    mStompClient = null;
                    try {
                        connect(userName, password);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "forlan debug start connect WS_URI");
                }
            }
        }, RECONNECT_TIME_INTERVAL, RECONNECT_TIME_INTERVAL);
    }

    //点对点订阅，根据用户名来推送消息
//    private void registerStompTopic() {
//        mStompClient.topic("/user/" + "xxx" + "/msg").subscribe((Action1<StompMessage>) stompMessage -> Log.d(TAG, "debug msg is " + stompMessage.getPayload()));
//    }

    @SuppressLint("CheckResult")
    private void connect(String userName, String password) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", Base64Util.encodeBasicAuth(userName, password));
        SSLHelper.SSLParams sslParams = RetrofitService.setSSLParams(BaseApp.getAppContext());
        OkHttpClient okHttpClient = RetrofitService.getOkHttpClientBuilder().sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager).build();
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, IdeaApiService.WS_URI, headers, okHttpClient);
        mStompClient.connect();
        mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    //关注lifecycleEvent的回调来决定是否重连
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            mNeedConnect = false;
                            LogUtil.d(TAG, Thread.currentThread().getName() + ",Stomp connection opened");
                            mStompClient.topic("/user/queue/receive-settings", null);
                            mStompClient.topic("/user/queue/receive-transaction", null);
                            mStompClient.topic("/user/queue/receive-transaction-completion-confirmation", null);
                            break;
                        case ERROR:
                            mNeedConnect = true;
                            LogUtil.e(TAG, Thread.currentThread().getName() + ",Stomp connection error :" + lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            mNeedConnect = true;
                            LogUtil.d(TAG, Thread.currentThread().getName() + ",Stomp connection closed");
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            LogUtil.d(TAG, Thread.currentThread().getName() + ",Stomp connection fail server heartBeat");
                            break;

                    }
                });
    }

    public void disconnect() {
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
    }

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
}
