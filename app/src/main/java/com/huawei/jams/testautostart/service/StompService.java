package com.huawei.jams.testautostart.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.utils.StompManager;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yxytech.parkingcloud.baselibrary.http.common.DefaultObserver;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.NetworkUtils;

import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.functions.Action1;
import ua.naiksoftware.stomp.ConnectionProvider;
import ua.naiksoftware.stomp.LifecycleEvent;
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

    public static StompService instance;
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

    private void connect() {
        mStompClient = Stomp.over(ConnectionProvider.class, IdeaApiService.WS_URI);
        mStompClient.connect();
        mStompClient.lifecycle().subscribe(new Action1<LifecycleEvent>() {
            @Override
            public void call(LifecycleEvent lifecycleEvent) {
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
            }
        });
        registerStompTopic();
    }

    private void disconnect() {
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
    }

    //创建长连接，服务器端没有心跳机制的情况下，启动timer来检查长连接是否断开，如果断开就执行重连
    private void createStompClient() {
        connect();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "forlan debug in timer ======================");
                if (mNeedConnect && NetworkUtils.isConnected()) {
                    mStompClient = null;
                    connect();
                    Log.d(TAG, "forlan debug start connect WS_URI");
                }
            }
        }, RECONNECT_TIME_INTERVAL, RECONNECT_TIME_INTERVAL);
    }

    //点对点订阅，根据用户名来推送消息
    private void registerStompTopic() {
        mStompClient.topic("/user/" + "xxx" + "/msg").subscribe(new Action1<StompMessage>() {
            @Override
            public void call(StompMessage stompMessage) {
                Log.d(TAG, "debug msg is " + stompMessage.getPayload());
            }
        });
    }

    public interface Callback<T> {
        void onDataReceive(T t);
    }

//    public void sendData(String data, Activity activity,LifecycleProvider lifecycleProvider,DefaultObserver defaultObserver) {
//        if (mStompClient != null) {
//            Observable observable = mStompClient.send(data);
//            StompManager stompManager =new StompManager(activity,lifecycleProvider);
//            stompManager.doStompDeal(observable,defaultObserver);
//
//        }
//        //callback.onDataReceive(observable.observeOn());
//    }

}
