package com.huawei.jams.testautostart.model.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.entity.AppInfo;
import com.huawei.jams.testautostart.model.inter.IDeviceInfoModel;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.service.StompService;
import com.yxytech.parkingcloud.baselibrary.utils.PackageUtils;

import java.util.Date;

import io.reactivex.subscribers.DisposableSubscriber;
import ua.naiksoftware.stomp.client.StompMessage;

public class DeviceInfoModel implements IDeviceInfoModel {
    private static final String TAG = DeviceInfoModel.class.getName();

    @Override
    public void bindDevice(String deviceUuid, String deviceType, StompCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceUuid", deviceUuid);
        jsonObject.addProperty("deviceType", deviceType);
        StompService.getInstance().sendStomp(IdeaApiService.DEVICE_BIND,jsonObject.toString());
        StompService.getInstance().receiveStomp(IdeaApiService.DEVICE_BIND, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                stompMessage.getPayload();
                ApiResponse<AppInfo> apiResponse = new GsonBuilder().create().fromJson(stompMessage.getPayload(), ApiResponse.class);
                if (null != apiResponse.getData()) {
                    
                }
                callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), apiResponse);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });

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
