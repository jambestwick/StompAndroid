package com.huawei.jams.testautostart.view.inter;

public interface IMainView {

    void onQueryAppInfoSuccess(String url);

    void onQueryAppInfoFail(String reason);

    void onQueryAdviseSuccess(String url);

    void onQueryAdviseFail(String reason);

    void onOpenBoxSuccess(String boxId);

    void onOpenBoxFail(String reason);

    void onQueryAlarmPropSuccess();

    void onQueryAlarmPropFail(String reason);

    void onUploadBoxStateSuccess();

    void onUploadBoxStateFail(String reason);

    void onBindDeviceSuccess();

    void onBindDeviceFail(String reason);


}
