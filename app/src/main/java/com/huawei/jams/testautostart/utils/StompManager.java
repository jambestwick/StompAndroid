package com.huawei.jams.testautostart.utils;

import android.app.Activity;
import android.content.Context;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yxytech.parkingcloud.baselibrary.http.common.DefaultObserver;
import com.yxytech.parkingcloud.baselibrary.http.common.ProgressUtils;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/5/14<p>
 * <p>更新时间：2020/5/14<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class StompManager {
    /*软引用對象*/
    private Activity context;
    private LifecycleProvider lifecycleProvider;

    public StompManager(Activity context, LifecycleProvider lifecycleProvider) {
        this.context = context;
        this.lifecycleProvider = lifecycleProvider;
    }

    public void doStompDeal(Observable observable, DefaultObserver defaultObserver) {
        defaultObserver.setContext(context);
        observable.compose(lifecycleProvider.bindToLifecycle())
                .compose(ProgressUtils.applyProgressBar(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(defaultObserver);
    }

    public void doStompDeal(String msg, Observable observable, DefaultObserver defaultObserver) {
        defaultObserver.setContext(context);
        observable.compose(lifecycleProvider.bindToLifecycle())
                .compose(ProgressUtils.applyProgressBar(context, msg))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(defaultObserver);
    }
}
