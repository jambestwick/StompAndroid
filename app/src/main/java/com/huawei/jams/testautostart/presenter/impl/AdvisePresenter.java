package com.huawei.jams.testautostart.presenter.impl;

import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.entity.Advise;
import com.huawei.jams.testautostart.entity.Advise_Table;
import com.huawei.jams.testautostart.model.impl.AdviseModel;
import com.huawei.jams.testautostart.model.inter.IAdviseModel;
import com.huawei.jams.testautostart.presenter.inter.IAdvisePresenter;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.view.inter.IMainView;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yxytech.parkingcloud.baselibrary.utils.PreferencesManager;

public class AdvisePresenter implements IAdvisePresenter {
    private static final String TAG = AppInfoPresenter.class.getName();
    private IAdviseModel mAdviseModel;//Model接口
    private IMainView mainView;//View接口

    public AdvisePresenter(IMainView mainView) {
        this.mAdviseModel = new AdviseModel();
        this.mainView = mainView;
    }

    @Override
    public void queryAdviseInfo() {
        String token = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.TOKEN);
        Advise lastAdvise = SQLite.select().from(Advise.class).orderBy(Advise_Table.adv_version, false).limit(1).querySingle();

        mAdviseModel.queryVersion(token, lastAdvise.getAdvVersion(), new StompCallBack() {
            @Override
            public void onCallBack(int errorCode, String msg, Object data) {
                switch (errorCode) {
                    case ApiResponse.SUCCESS:
                        ((Advise) data).getFilePath();
                        mainView.onQueryAdviseSuccess(((Advise) data).getFilePath());
                        break;
                    default:
                        mainView.onQueryAdviseFail(msg);
                        break;
                }
            }
        });

    }
}
