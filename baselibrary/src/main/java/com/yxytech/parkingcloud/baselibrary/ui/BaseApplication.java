package com.yxytech.parkingcloud.baselibrary.ui;

import android.app.Application;
import android.content.Context;

/**
 * <p>文件描述：<p>
 * <p>作者：think<p>
 * <p>创建时间：2019/5/9<p>
 * <p>更新时间：2019/5/9<p>
 * <p>版本号：${VERSION}<p>
 */
public abstract class BaseApplication extends Application {
    private static BaseApplication application;

    public static Context getAppContext() {
        if (application != null) return application;
        throw new NullPointerException("u should init first");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initRxRetrofitApp();
        application = this;
    }

    /**
     * 重写此方法以完成http请求的初始化
     */
    protected abstract void initRxRetrofitApp();
}
