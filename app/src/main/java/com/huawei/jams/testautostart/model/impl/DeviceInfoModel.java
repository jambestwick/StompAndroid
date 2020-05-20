package com.huawei.jams.testautostart.model.impl;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.entity.DeviceInfo;
import com.huawei.jams.testautostart.entity.vo.AlarmPropVO;
import com.huawei.jams.testautostart.model.inter.IDeviceInfoModel;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.utils.StompUtil;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.PreferencesManager;
import com.yxytech.parkingcloud.baselibrary.utils.TimeUtil;

import java.util.Date;

import io.reactivex.subscribers.DisposableSubscriber;
import ua.naiksoftware.stomp.dto.StompMessage;


public class DeviceInfoModel implements IDeviceInfoModel {
    private static final String TAG = DeviceInfoModel.class.getName();

    @Override
    public void bindDevice(String deviceUuid, String deviceType, StompCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceUuid", deviceUuid);
        jsonObject.addProperty("deviceType", deviceType);
        StompUtil.getInstance().sendStomp(IdeaApiService.DEVICE_BIND, jsonObject.toString());
        StompUtil.getInstance().receiveStomp(IdeaApiService.DEVICE_BIND, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",bindDevice onNext:" + stompMessage);
                ApiResponse<DeviceInfo> apiResponse = new GsonBuilder().create().fromJson(stompMessage.getPayload(), ApiResponse.class);
                if (apiResponse == null) {
                    callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                }
                switch (EnumResponseCode.getEnumByKey(apiResponse.getCode())) {
                    case SUCCESS://绑定成功
                        callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), apiResponse.getData());
                        break;
                    default:
                        callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                        break;
                }
            }

            @Override
            public void onError(Throwable t) {
                LogUtil.e(TAG, Thread.currentThread().getName() + ",bindDevice onError" + Log.getStackTraceString(t));
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",bindDevice onComplete");
            }
        });

    }

    /**
     * 上传设备状态
     */
    @Override
    public void uploadBoxState(String deviceUuid, String deviceType, String boxId, String boxState, StompCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceUuid", deviceUuid);
        jsonObject.addProperty("deviceType", deviceType);
        jsonObject.addProperty("boxId", boxId);
        jsonObject.addProperty("boxState", boxState);
        StompUtil.getInstance().sendStomp(IdeaApiService.DEVICE_UPDATE_BOX_STATE, jsonObject.toString());
        StompUtil.getInstance().receiveStomp(IdeaApiService.DEVICE_UPDATE_BOX_STATE, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",uploadBoxState onNext:" + stompMessage);
                ApiResponse<Boolean> apiResponse = new GsonBuilder().create().fromJson(stompMessage.getPayload(), ApiResponse.class);
                if (apiResponse == null) {
                    callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                }
                switch (EnumResponseCode.getEnumByKey(apiResponse.getCode())) {
                    case SUCCESS:
                        callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), apiResponse.getData());
                        break;
                    default:
                        callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                        break;
                }
            }

            @Override
            public void onError(Throwable t) {
                LogUtil.e(TAG, Thread.currentThread().getName() + ",uploadBoxState onError" + Log.getStackTraceString(t));
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",uploadBoxState onComplete");
            }
        });

    }

    @Override
    public void openBox(String deviceUuid, String sixCode, String token, StompCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceUuid", deviceUuid);
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("password", sixCode);
        StompUtil.getInstance().sendStomp(IdeaApiService.DEVICE_OPEN_BOX, jsonObject.toString());
        StompUtil.getInstance().receiveStomp(IdeaApiService.DEVICE_OPEN_BOX, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",openBox onNext:" + stompMessage);
                //返回开箱编号
                ApiResponse<String> apiResponse = new GsonBuilder().create().fromJson(stompMessage.getPayload(), ApiResponse.class);
                if (apiResponse == null) {
                    callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                }
                switch (EnumResponseCode.getEnumByKey(apiResponse.getCode())) {
                    case SUCCESS:
                        callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), apiResponse.getData());
                        break;
                    default:
                        callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                        break;
                }
            }

            @Override
            public void onError(Throwable t) {
                LogUtil.e(TAG, Thread.currentThread().getName() + ",openBox onError" + Log.getStackTraceString(t));
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",openBox onComplete");
            }
        });

    }

    @Override
    public void queryAlarmProperties(String deviceUuid, Date operatorTime, StompCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceUuid", deviceUuid);
        jsonObject.addProperty("operatorTime", TimeUtil.date2Str(operatorTime, TimeUtil.DEFAULT_MILL_TIME_FORMAT));
        StompUtil.getInstance().sendStomp(IdeaApiService.DEVICE_OPEN_BOX, jsonObject.toString());
        StompUtil.getInstance().receiveStomp(IdeaApiService.DEVICE_OPEN_BOX, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",queryAlarmProperties onNext:" + stompMessage);
                //返回开箱编号，根据编号开指定柜门
                ApiResponse<AlarmPropVO> apiResponse = new GsonBuilder().create().fromJson(stompMessage.getPayload(), ApiResponse.class);
                if (apiResponse == null) {
                    callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                }
                switch (EnumResponseCode.getEnumByKey(apiResponse.getCode())) {
                    case SUCCESS:
                        PreferencesManager.getInstance(BaseApp.getAppContext()).put(Constants.PATROL_TIME, apiResponse.getData().getIntervalTime());
                        PreferencesManager.getInstance(BaseApp.getAppContext()).put(Constants.PATROL_NUM, apiResponse.getData().getIntervalNum());
                        callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), apiResponse.getData());
                        break;
                    default:
                        callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                        break;
                }
            }

            @Override
            public void onError(Throwable t) {
                LogUtil.e(TAG, Thread.currentThread().getName() + ",queryAlarmProperties onError" + Log.getStackTraceString(t));
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",queryAlarmProperties onComplete");
            }
        });

    }

}
