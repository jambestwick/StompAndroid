package com.huawei.jams.testautostart.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.presenter.inter.StompSendBack;
import com.huawei.jams.testautostart.view.activity.MainActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yxytech.parkingcloud.baselibrary.dialog.DialogUtils;
import com.yxytech.parkingcloud.baselibrary.http.common.ProgressUtils;
import com.yxytech.parkingcloud.baselibrary.http.common.RetrofitService;
import com.yxytech.parkingcloud.baselibrary.http.https.SSLHelper;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.AppManager;
import com.yxytech.parkingcloud.baselibrary.utils.Base64Util;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.CompletableObserver;
import io.reactivex.CompletableTransformer;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

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
    private boolean isConnecting = false;
    public static final long RECONNECT_TIME_INTERVAL = 30 * Constants.ONE_MILL_SECOND;
    public static final long RECONNECT_TIME_DELY = 5 * Constants.ONE_MILL_SECOND;
    private static final long HEART_BEAT = 10 * Constants.ONE_MILL_SECOND;
    private static StompUtil instance;

    private List<StompConnectListener> connectListeners = new ArrayList<>();

    public static StompUtil getInstance() {
        if (instance == null) {
            synchronized (StompUtil.class) {
                if (instance == null) {
                    instance = new StompUtil();
                }
            }
        }
        return instance;
    }


    public void setConnectListener(StompConnectListener stompConnectListener) {
        connectListeners.add(stompConnectListener);
    }

    public boolean removeConnectListener(StompConnectListener stompConnectListener) {
        return connectListeners.remove(stompConnectListener);
    }

    public void clearConnectListener() {
        connectListeners.clear();
    }

    public boolean isNeedConnect() {
        return mNeedConnect;
    }

    public void setmNeedConnect(boolean mNeedConnect) {
        this.mNeedConnect = mNeedConnect;
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    //创建长连接，服务器端没有心跳机制的情况下，启动timer来检查长连接是否断开，如果断开就执行重连
    //长生命周期的连接贯穿APP整个周期
    public void createStompClient(String userName, String password) {
        try {
            connect(userName, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("CheckResult")
    private void connect(String userName, String password) throws IOException {
        SSLHelper.SSLParams sslParams = RetrofitService.setSSLParams(BaseApp.getAppContext());
        OkHttpClient okHttpClient = RetrofitService.getOkHttpClientBuilder().sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager).build();
        mStompClient = Stomp.over(OKHTTP, IdeaApiService.WS_URI, null, okHttpClient);
        List<StompHeader> _headers = new ArrayList<>();
        _headers.add(new StompHeader("Authorization", Base64Util.encodeBasicAuth(userName, password)));
        //_headers.add(new StompHeader("Authorization", Base64Util.encodeBasicAuth("00002", "AAAAAAAAAAAAAAAAAAAA_2")));
        LogUtil.d(TAG, Thread.currentThread().getName() + ",Stomp connection start to connect...");
        isConnecting = true;
        mStompClient.connect(_headers);
        mStompClient.lifecycle()
                //.onBackpressureBuffer()
                //.compose(ProgressUtils.applyProgressBarStomp(activity))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                            //关注lifecycleEvent的回调来决定是否重连
                            switch (lifecycleEvent.getType()) {
                                case OPENED:
                                    mNeedConnect = false;
                                    isConnecting = false;
                                    LogUtil.d(TAG, Thread.currentThread().getName() + ",Stomp connection opened");
                                    for (StompConnectListener connectListener : connectListeners) {
                                        connectListener.onConnectState(EnumConnectState.CONNECT);
                                    }
                                    //topicMessage();
                                    break;
                                case ERROR:
                                    mNeedConnect = true;
                                    isConnecting = false;
                                    LogUtil.e(TAG, Thread.currentThread().getName() + ",Stomp connection error :" + Log.getStackTraceString(lifecycleEvent.getException()));
//                                    for (StompConnectListener connectListener : connectListeners) {
//                                        connectListener.onConnectState(EnumConnectState.ERROR);
//                                    }
                                    break;
                                case CLOSED:
                                    mNeedConnect = true;
                                    isConnecting = false;
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
        mStompClient = null;
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
                        LogUtil.d(TAG, Thread.currentThread().getName() + ",sendStomp:doOnSubscribe");
                    })
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            sendBack.onSendSuccess();
                            LogUtil.d(TAG, Thread.currentThread().getName() + ",sendStomp:" + destPath + ",data:" + jsonMsg + ",successfully");
                            if (null != dialogUtils) {
                                dialogUtils.dismissProgress();
                            }
                        }

                        @Override
                        public void onComplete() {
                            LogUtil.d(TAG, Thread.currentThread().getName() + ",sendStomp:onComplete");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            sendBack.onSendError(throwable);
                            LogUtil.e(TAG, Thread.currentThread().getName() + ",sendStomp:onError:" + destPath + ",data:" + jsonMsg + "," + throwable);
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
                    .doOnError(throwable -> {
                        LogUtil.e(TAG, Thread.currentThread().getName() + ",receiveStomp:" + destPath + ",doOnError:" + Log.getStackTraceString(throwable));
                        // log the error and tell the service to resubscribe
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(flowableSubscriber);
        }
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
