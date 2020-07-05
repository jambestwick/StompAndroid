package com.huawei.jams.testautostart.presenter.impl;

import android.util.Log;

import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.entity.AppInfo;
import com.huawei.jams.testautostart.entity.AppInfo_Table;
import com.huawei.jams.testautostart.entity.vo.AppVO;
import com.huawei.jams.testautostart.model.impl.AppInfoModel;
import com.huawei.jams.testautostart.model.inter.IAppInfoModel;
import com.huawei.jams.testautostart.presenter.inter.HttpDownloadCallBack;
import com.huawei.jams.testautostart.presenter.inter.IAppInfoPresenter;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.view.inter.IAppInfoView;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.PackageUtils;
import com.yxytech.parkingcloud.baselibrary.utils.StrUtil;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class AppInfoPresenter implements IAppInfoPresenter {
    private static final String TAG = AppInfoPresenter.class.getName();
    private IAppInfoModel mAppInfoModel;//Model接口
    private IAppInfoView appInfoView;//View接口

    public AppInfoPresenter(BaseActivity baseActivity, IAppInfoView appInfoView) {
        this.mAppInfoModel = new AppInfoModel(baseActivity);
        this.appInfoView = appInfoView;
    }

    @Override
    public void topicAppInfo() {
        mAppInfoModel.subscribeVersion((StompCallBack<AppVO>) (errorCode, msg, data) -> {
            if (errorCode == EnumResponseCode.SUCCESS.getKey()) {
                if (null != data) {
                    AppInfo lastAppInfo = SQLite.select().from(AppInfo.class).orderBy(AppInfo_Table.app_version, false).limit(1).querySingle();//倒数第一个广告
                    if (null == lastAppInfo) {
                        appInfoView.onTopicAppInfoSuccess(data.getDownloadUrl(), data.getVersion());
                    } else {
                        if (StrUtil.compareVerName(data.getVersion(), lastAppInfo.getAppVersion())) {//新版本比旧版本大则下载，否则不下载
                            appInfoView.onTopicAppInfoSuccess(data.getDownloadUrl(), data.getVersion());
                        }
                    }
                }
            } else {
                appInfoView.onTopicAppInfoFail(msg);
            }

        });

    }

    @Override
    public void downloadApp(String url, String version) {
        mAppInfoModel.downloadApp(url, new HttpDownloadCallBack<File>() {
            @Override
            public void onDownLoadSuccess(File o) {
                //更新下数据库
                AppInfo appInfo = new AppInfo(UUID.randomUUID(), o.getName(), version, new Date(), url, o.getAbsolutePath(), AppInfo.EnumForceUpdate.FORCE.value);
                appInfo.save();
                appInfoView.onDownloadAppSuccess(o.getAbsolutePath());
            }

            @Override
            public void onDownLoadFail(Throwable throwable) {
                Log.e(TAG, "downloadApp onDownLoadFail:" + Log.getStackTraceString(throwable));
                appInfoView.onDownloadAppFail(throwable.getMessage());

            }

            @Override
            public void onProgress(int progress, long total) {

            }
        });
    }


}
