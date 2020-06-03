package com.huawei.jams.testautostart.presenter.impl;

import android.text.TextUtils;
import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.entity.AppInfo;
import com.huawei.jams.testautostart.entity.vo.AppVO;
import com.huawei.jams.testautostart.model.impl.AppInfoModel;
import com.huawei.jams.testautostart.model.inter.IAppInfoModel;
import com.huawei.jams.testautostart.presenter.inter.HttpDownloadCallBack;
import com.huawei.jams.testautostart.presenter.inter.IAppInfoPresenter;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.view.inter.IAdviseView;
import com.huawei.jams.testautostart.view.inter.IAppInfoView;
import com.yxytech.parkingcloud.baselibrary.http.common.FileDownLoadObserver;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.PackageUtils;

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
            if (errorCode == ApiResponse.SUCCESS) {
                if (null != data) {
                    String versionName = PackageUtils.getVersionName(BaseApp.getAppContext());
                    if (compareVerName(data.getVersion(), versionName)) {
                        appInfoView.onTopicAppInfoSuccess(data.getDownloadUrl(), data.getVersion());
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
                AppInfo appInfo = new AppInfo(UUID.randomUUID(), version, new Date(), url, o.getAbsolutePath(), AppInfo.EnumForceUpdate.FORCE.value);
                appInfo.save();
            }

            @Override
            public void onDownLoadFail(Throwable throwable) {

            }

            @Override
            public void onProgress(int progress, long total) {

            }
        });
    }

    /**
     * @param ver1 大于ver2 @return true
     **/
    private boolean compareVerName(String ver1, String ver2) {
        if (!TextUtils.isEmpty(ver1) && !TextUtils.isEmpty(ver2)) {
            String verStr1 = ver1.replaceAll(".", "");
            String verStr2 = ver2.replaceAll(".", "");
            if (TextUtils.isDigitsOnly(verStr1) && TextUtils.isDigitsOnly(verStr2)) {
                int verInt1 = Integer.parseInt(ver1);
                int verInt2 = Integer.parseInt(ver2);
                return verInt1 > verInt2;
            }
        }
        return false;
    }


}
