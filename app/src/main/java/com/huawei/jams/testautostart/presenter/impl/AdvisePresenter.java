package com.huawei.jams.testautostart.presenter.impl;

import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.entity.Advise;
import com.huawei.jams.testautostart.entity.Advise_Table;
import com.huawei.jams.testautostart.entity.vo.AdviseVO;
import com.huawei.jams.testautostart.model.impl.AdviseModel;
import com.huawei.jams.testautostart.model.inter.IAdviseModel;
import com.huawei.jams.testautostart.presenter.inter.IAdvisePresenter;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.view.inter.IAdviseView;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yxytech.parkingcloud.baselibrary.utils.StrUtil;

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
        mAdviseModel.subscribeVersion(new StompCallBack<AdviseVO>() {
            @Override
            public void onCallBack(int errorCode, String msg, AdviseVO data) {
                switch (errorCode) {
                    case ApiResponse.SUCCESS:
                        Advise currentAdv = SQLite.select().from(Advise.class).orderBy(Advise_Table.adv_version, false).limit(1).querySingle();
                        if (null == currentAdv) {
                            adviseView.onTopicAdviseSuccess(data.getDownloadUrl(), data.getVersion());
                        } else {
                            if (StrUtil.compareVerName(data.getVersion(), currentAdv.getAdvVersion())) {
                                adviseView.onTopicAdviseSuccess(data.getDownloadUrl(), data.getVersion());
                            }
                        }
                        break;
                    default:
                        adviseView.onTopicAdviseFail(msg);
                        break;
                }
            }
        });

    }

    @Override
    public void downloadAdvise(String url, String newVer) {

    }
}
