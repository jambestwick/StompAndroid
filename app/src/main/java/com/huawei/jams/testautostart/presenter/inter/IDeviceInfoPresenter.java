package com.huawei.jams.testautostart.presenter.inter;

public interface IDeviceInfoPresenter {

    void bindDevice();

    void uploadBoxState(String boxId, String boxState);

    void queryAlarmProp();

    void openBox(String password);


    void patrolBoxState(String boxId, String boxState);
}
