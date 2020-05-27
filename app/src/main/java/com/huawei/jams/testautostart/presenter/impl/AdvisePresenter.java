package com.huawei.jams.testautostart.presenter.impl;

import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.entity.Advise;
import com.huawei.jams.testautostart.model.impl.AdviseModel;
import com.huawei.jams.testautostart.model.inter.IAdviseModel;
import com.huawei.jams.testautostart.presenter.inter.IAdvisePresenter;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.view.inter.IAdviseView;

public class AdvisePresenter implements IAdvisePresenter {
    private static final String TAG = AppInfoPresenter.class.getName();
    private IAdviseModel mAdviseModel;//Model接口
    private IAdviseView adviseView;//View接口

    public AdvisePresenter(IAdviseView adviseView) {
        this.mAdviseModel = new AdviseModel();
        this.adviseView = adviseView;
    }

    @Override
    public void topicAdviseInfo() {
        mAdviseModel.subscribeVersion( new StompCallBack() {
            @Override
            public void onCallBack(int errorCode, String msg, Object data) {
                switch (errorCode) {
                    case ApiResponse.SUCCESS:
                        ((Advise) data).getFilePath();
                        adviseView.onTopicAdviseSuccess(((Advise) data).getFilePath());
                        break;
                    default:
                        adviseView.onTopicAdviseFail(msg);
                        break;
                }
            }
        });

    }
}
