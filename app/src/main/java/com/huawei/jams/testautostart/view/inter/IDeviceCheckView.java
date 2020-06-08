package com.huawei.jams.testautostart.view.inter;

public interface IDeviceCheckView {
    void onBindDeviceSuccess(String account, String password);

    void onBindDeviceFail(String reason);
}
