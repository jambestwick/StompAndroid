package com.huawei.jams.testautostart;

import android.content.Intent;

import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.service.StompService;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.yxytech.parkingcloud.baselibrary.http.common.RxRetrofitApp;
import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/4/23<p>
 * <p>更新时间：2020/4/23<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class BaseApp extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);

    }

    @Override
    protected void initRxRetrofitApp() {
        RxRetrofitApp.init(IdeaApiService.SERVER_HOST);
    }

}
