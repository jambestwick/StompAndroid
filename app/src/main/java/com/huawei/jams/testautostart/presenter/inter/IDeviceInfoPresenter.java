package com.huawei.jams.testautostart.presenter.inter;

import com.huawei.jams.testautostart.databinding.ActivityMainBinding;
import com.huawei.jams.testautostart.databinding.ActivityWelcomeBinding;
import com.huawei.jams.testautostart.utils.KeyCabinetReceiver;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

public interface IDeviceInfoPresenter {

    void bindDevice(BaseActivity activity, LifecycleProvider lifecycleProvider, String sixCode);

    void uploadBoxState(String boxId, String boxState);

    void openBox(String password);


    void patrolBoxState(Timer timer, TimerTask timerTask);

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
     **/
    boolean boxListAllClose(boolean[] isOpens);
}
