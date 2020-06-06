package com.huawei.jams.testautostart.presenter.inter;

import com.huawei.jams.testautostart.databinding.ActivityMainBinding;
import com.huawei.jams.testautostart.databinding.ActivityWelcomeBinding;
import com.huawei.jams.testautostart.utils.KeyCabinetReceiver;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

public interface IDeviceInfoPresenter {


    void uploadBoxState(int boxState);

    void openBox(String sixCode);


    void patrolBoxState(Timer timer, TimerTask timerTask);

    /**
     * 刷新主页6位码
     **/
    void refreshMainCode2View(ActivityMainBinding binding, String inputCode);

    /**
     * 订阅打开柜门
     **/
    void topicOpenBox();

    /**
     * 订阅上传柜门状态
     **/
    void topicUploadBoxState();
}
