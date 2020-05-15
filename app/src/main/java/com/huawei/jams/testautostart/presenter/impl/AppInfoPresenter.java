package com.huawei.jams.testautostart.presenter.impl;

import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.entity.AppInfo;
import com.huawei.jams.testautostart.model.impl.AppInfoModel;
import com.huawei.jams.testautostart.model.inter.IAppInfoModel;
import com.huawei.jams.testautostart.presenter.inter.IAppInfoPresenter;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.view.inter.IMainView;
import com.yxytech.parkingcloud.baselibrary.utils.PackageUtils;
import com.yxytech.parkingcloud.baselibrary.utils.PreferencesManager;

public class AppInfoPresenter implements IAppInfoPresenter {
    private static final String TAG = AppInfoPresenter.class.getName();
    private IAppInfoModel mAppInfoModel;//Model接口
    private IMainView mainView;//View接口

    public AppInfoPresenter(IMainView mainView) {
        this.mAppInfoModel = new AppInfoModel();
        this.mainView = mainView;
    }

    @Override
    public void queryAppInfo() {
        String token = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.TOKEN);
        String currentAppVer = PackageUtils.getVersionName(BaseApp.getAppContext());
        mAppInfoModel.queryVersion(token, currentAppVer, (errorCode, msg, data) -> {
            switch (errorCode) {
                case ApiResponse.SUCCESS:
                    mainView.onQueryAppInfoSuccess(((AppInfo) data).getUrl());
                    break;
                default:
                    mainView.onQueryAppInfoFail(msg);
                    break;
            }
        });

    }
}
