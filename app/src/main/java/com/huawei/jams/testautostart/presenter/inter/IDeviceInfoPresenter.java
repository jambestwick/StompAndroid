package com.huawei.jams.testautostart.presenter.inter;

import com.huawei.jams.testautostart.databinding.ActivityMainBinding;
import com.huawei.jams.testautostart.databinding.ActivityWelcomeBinding;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;

public interface IDeviceInfoPresenter {

    void bindDevice(BaseActivity activity, LifecycleProvider lifecycleProvider, String sixCode);

    void uploadBoxState(String boxId, String boxState);

    void queryAlarmProp();

    void openBox(String password, int times);


    void patrolBoxState(String boxId, String boxState);

    /**
     * 刷新主页6位码
     **/
    void refreshMainCode2View(ActivityMainBinding binding, String inputCode);

    /**
     * 刷新欢迎页6位码
     **/
    void refreshWelcomeCode2View(ActivityWelcomeBinding binding, String inputCode);

    /**
     * 判断是否全关
     *
     * **/
    boolean boxListAllClose(boolean[] isOpens);
}
