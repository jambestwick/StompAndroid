package com.huawei.jams.testautostart.presenter.impl;

import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.entity.AppInfo;
import com.huawei.jams.testautostart.model.impl.AppInfoModel;
import com.huawei.jams.testautostart.model.inter.IAppInfoModel;
import com.huawei.jams.testautostart.presenter.inter.IAppInfoPresenter;
import com.huawei.jams.testautostart.view.inter.IMainView;

public class AppInfoPresenter implements IAppInfoPresenter {
    private static final String TAG = AppInfoPresenter.class.getName();
    private IAppInfoModel mAppInfoModel;//Model接口
    private IMainView mainView;//View接口

    public AppInfoPresenter(IMainView mainView) {
        this.mAppInfoModel = new AppInfoModel();
        this.mainView = mainView;
    }

    @Override
    public void topicAppInfo() {
        mAppInfoModel.subscribeVersion((errorCode, msg, data) -> {
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
