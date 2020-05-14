package com.huawei.jams.testautostart.model.impl;

import com.huawei.jams.testautostart.model.inter.IDeviceInfoModel;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;

import java.util.Date;

public class DeviceInfoModel implements IDeviceInfoModel {
    private static final String TAG = DeviceInfoModel.class.getName();
    @Override
    public void bindDevice(String deviceUuid, String deviceType, StompCallBack callBack) {


    }

    @Override
    public void uploadBoxState(String deviceUuid, String deviceType, String boxId, String boxState, StompCallBack callBack) {

    }

    @Override
    public void openBox(String deviceUuid, String token, StompCallBack callBack) {

    }

    @Override
    public void queryAlarmProperties(String deviceUuid, Date operatorTime) {

    }
}
