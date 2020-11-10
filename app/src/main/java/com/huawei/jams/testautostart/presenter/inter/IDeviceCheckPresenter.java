package com.huawei.jams.testautostart.presenter.inter;

import com.huawei.jams.testautostart.databinding.ActivityWelcomeBinding;

public interface IDeviceCheckPresenter {
    void bindDevice(String sixCode);

    /**
     * 刷新欢迎页6位码
     **/
    void refreshWelcomeCode2View(ActivityWelcomeBinding binding, String inputCode);

    /**
     * 判断是否存在账号密码信息
     **/
    boolean hasAccountPassword(String account, String password);
}
