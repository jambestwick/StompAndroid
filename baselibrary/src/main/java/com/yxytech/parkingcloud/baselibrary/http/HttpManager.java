package com.yxytech.parkingcloud.baselibrary.http;

import android.util.Log;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yxytech.parkingcloud.baselibrary.http.common.DefaultObserver;
import com.yxytech.parkingcloud.baselibrary.http.common.ProgressUtils;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Water on 2018/3/30.
 */

public class HttpManager {

    /*软引用對象*/
    private BaseActivity baseActivity;

    public HttpManager(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    /**
     * Http 请求
     *
     * @param observable      被观察者
     * @param defaultObserver 观察者
     **/
    public void doHttpDeal(Observable observable, DefaultObserver defaultObserver) {
        defaultObserver.setContext(baseActivity);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(baseActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(ProgressUtils.applyProgressBar(baseActivity))
                .subscribe(defaultObserver);//订阅到观察者
    }

    /**
     * Http 请求
     *
     * @param observable      被观察者
     * @param defaultObserver 观察者
     * @param msg             Loading时的文字
     **/
    public void doHttpDeal(String msg, Observable observable, DefaultObserver defaultObserver) {
        defaultObserver.setContext(baseActivity);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(baseActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(ProgressUtils.applyProgressBar(baseActivity, msg))
                .subscribe(defaultObserver);
    }

    /**
     * Http 下载
     **/
    public void doHttpDownload(Observable observable, DefaultObserver defaultObserver) {
        defaultObserver.setContext(baseActivity);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(baseActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(ProgressUtils.applyProgressBar(baseActivity))
                .subscribe(defaultObserver);
    }



}
