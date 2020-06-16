package com.huawei.jams.testautostart.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.presenter.inter.StompSendBack;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yxytech.parkingcloud.baselibrary.dialog.DialogUtils;
import com.yxytech.parkingcloud.baselibrary.http.common.ProgressUtils;
import com.yxytech.parkingcloud.baselibrary.http.common.RetrofitService;
import com.yxytech.parkingcloud.baselibrary.http.https.SSLHelper;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.Base64Util;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.NetworkUtils;

import io.reactivex.CompletableObserver;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static ua.naiksoftware.stomp.Stomp.ConnectionProvider.OKHTTP;

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
    private static final long RECONNECT_TIME_INTERVAL = 30 * 1000;
    private static final long RECONNECT_TIME_DELY = 5 * 1000;
    private static final int HEART_BEAT = 1000;

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

    private List<StompConnectListener> connectListeners = new ArrayList<>();

    public void setConnectListener(StompConnectListener stompConnectListener) {
        connectListeners.add(stompConnectListener);
    }

    public boolean removeConnectListener(StompConnectListener stompConnectListener) {
        return connectListeners.remove(stompConnectListener);
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
                Log.d(TAG, Thread.currentThread().getName() + ", debug in timer to connect stomp======================");
                if (mNeedConnect && NetworkUtils.isConnected()) {//如果需要重连（连接ERROR或者CLOSED）并且网络状态连接正常
                    mStompClient = null;
                    new Handler(Looper.getMainLooper()).post(() -> {
                        try {
                            connect(userName, password);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    Log.d(TAG, Thread.currentThread().getName() + ",forlan debug start connect WS_URI:" + IdeaApiService.WS_URI);
                }
            }
        }, RECONNECT_TIME_DELY, RECONNECT_TIME_INTERVAL);
    }


    @SuppressLint("CheckResult")
    private void connect(String userName, String password) throws IOException {
        SSLHelper.SSLParams sslParams = RetrofitService.setSSLParams(BaseApp.getAppContext());
        OkHttpClient okHttpClient = RetrofitService.getOkHttpClientBuilder().sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager).build();
        mStompClient = Stomp.over(OKHTTP, IdeaApiService.WS_URI, null, okHttpClient);
        mStompClient.withClientHeartbeat(HEART_BEAT).withServerHeartbeat(HEART_BEAT);
        List<StompHeader> _headers = new ArrayList<>();
        _headers.add(new StompHeader("Authorization", Base64Util.encodeBasicAuth(userName, password)));
        mStompClient.connect(_headers);
        mStompClient.lifecycle()
                //.compose(ProgressUtils.applyProgressBarStomp(activity))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                            //关注lifecycleEvent的回调来决定是否重连
                            switch (lifecycleEvent.getType()) {
                                case OPENED:
                                    mNeedConnect = false;
                                    LogUtil.d(TAG, Thread.currentThread().getName() + ",Stomp connection opened");
                                    for (StompConnectListener connectListener : connectListeners) {

                                        connectListener.onConnectState(EnumConnectState.CONNECT);
                                    }
                                    //topicMessage();
                                    break;
                                case ERROR:
                                    mNeedConnect = true;
                                    LogUtil.e(TAG, Thread.currentThread().getName() + ",Stomp connection error :" + lifecycleEvent.getException());
//                                    for (StompConnectListener connectListener : connectListeners) {
//                                        connectListener.onConnectState(EnumConnectState.ERROR);
//                                    }
                                    break;
                                case CLOSED:
                                    mNeedConnect = true;
                                    LogUtil.d(TAG, Thread.currentThread().getName() + ",Stomp connection closed");
                                    for (StompConnectListener connectListener : connectListeners) {
                                        connectListener.onConnectState(EnumConnectState.CLOSE);
                                    }
                                    break;
                                case FAILED_SERVER_HEARTBEAT:
                                    LogUtil.d(TAG, Thread.currentThread().getName() + ",Stomp fail server heartbeat");
                                    break;

                            }
                        }, throwable -> LogUtil.e(TAG, Thread.currentThread().getName() + ",Stomp connect Throwable:" + Log.getStackTraceString(throwable))
                );


    }


    public void disconnect() {
        if (mStompClient != null) mStompClient.disconnect();
    }

    /**
     * 发送信息
     ***/
    @SuppressLint("CheckResult")
    public void sendStomp(BaseActivity activity, String destPath, String jsonMsg, StompSendBack sendBack) {
        if (mStompClient != null) {
            DialogUtils dialogUtils = new DialogUtils();
            dialogUtils.showProgress(activity);
            mStompClient.send(destPath, jsonMsg)
                    .retry(2)
                    .unsubscribeOn(Schedulers.newThread())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> {
                        LogUtil.d(TAG, "doOnSubscribe");
                    })
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            sendBack.onSendSuccess();
                            LogUtil.d(TAG, Thread.currentThread() + "STOMP send" + destPath + ",data:" + jsonMsg + ",successfully");
                            if (null != dialogUtils) {
                                dialogUtils.dismissProgress();
                            }
                        }

                        @Override
                        public void onComplete() {
                            LogUtil.d(TAG, "onComplete");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            sendBack.onSendError(throwable);
                            LogUtil.e(TAG, "Error send STOMP " + destPath + ",data:" + jsonMsg + throwable);
                            if (null != dialogUtils) {
                                dialogUtils.dismissProgress();
                            }
                        }

                    });
        }

    }

    /**
     * 订阅信息
     */
    public void receiveStomp(String destPath, FlowableSubscriber<StompMessage> flowableSubscriber) {
        if (mStompClient != null) {
            mStompClient.topic(destPath)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(flowableSubscriber);
        }
    }

    private void topicMessage() {
        Disposable dispTopic1 = mStompClient.topic("/user/queue/receive-settings")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
        Disposable dispTopic2 = mStompClient.topic("/user/queue/receive-transaction")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
        Disposable dispTopic3 = mStompClient.topic("/user/queue/receive-transaction-completion-confirmation")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
    }

    private CompletableTransformer applySchedulers(BaseActivity activity) {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(ProgressUtils.applyProgressBarStomp1(activity));
    }

    public interface StompConnectListener {
        void onConnectState(EnumConnectState enumConnectState);
    }

    public enum EnumConnectState {
        CONNECT, CLOSE, ERROR
    }


}
