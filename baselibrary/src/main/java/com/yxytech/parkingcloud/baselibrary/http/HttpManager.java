package com.yxytech.parkingcloud.baselibrary.http;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yxytech.parkingcloud.baselibrary.http.common.DefaultObserver;
import com.yxytech.parkingcloud.baselibrary.http.common.ProgressUtils;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Water on 2018/3/30.
 */

public class HttpManager {

    /*软引用對象*/
    private BaseActivity baseActivity;
    private LifecycleProvider lifecycleProvider;

    public HttpManager(BaseActivity baseActivity, LifecycleProvider lifecycleProvider) {
        this.baseActivity = baseActivity;
        this.lifecycleProvider = lifecycleProvider;
    }

    /**
     * Http 请求
     *
     * @param observable      被观察者
     * @param defaultObserver 观察者
     **/
    public void doHttpDeal(Observable observable, DefaultObserver defaultObserver) {
        defaultObserver.setContext(baseActivity);
        observable.compose(lifecycleProvider.bindToLifecycle())
                .compose(ProgressUtils.applyProgressBar(baseActivity))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
        observable.compose(lifecycleProvider.bindToLifecycle())
                .compose(ProgressUtils.applyProgressBar(baseActivity, msg))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(defaultObserver);
    }


}
