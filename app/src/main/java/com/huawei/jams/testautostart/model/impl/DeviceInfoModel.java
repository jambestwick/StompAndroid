package com.huawei.jams.testautostart.model.impl;

import android.util.Log;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.api.RetrofitHelper;
import com.huawei.jams.testautostart.entity.DeviceInfo;
import com.huawei.jams.testautostart.entity.vo.AlarmPropVO;
import com.huawei.jams.testautostart.model.inter.IDeviceInfoModel;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.utils.StompUtil;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yxytech.parkingcloud.baselibrary.http.HttpManager;
import com.yxytech.parkingcloud.baselibrary.http.common.DefaultObserver;
import com.yxytech.parkingcloud.baselibrary.http.common.ErrorCode;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.PreferencesManager;
import com.yxytech.parkingcloud.baselibrary.utils.TimeUtil;
import io.reactivex.subscribers.DisposableSubscriber;
import ua.naiksoftware.stomp.dto.StompMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DeviceInfoModel implements IDeviceInfoModel {
    private static final String TAG = DeviceInfoModel.class.getName();

    /**
     * http请求方式
     **/
    @Override
    public void bindDevice(BaseActivity baseActivity, LifecycleProvider lifecycleProvider, String sixCode, StompCallBack callBack) {
        HttpManager httpManager = new HttpManager(baseActivity, lifecycleProvider);
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("sixCode", sixCode);
        httpManager.doHttpDeal(RetrofitHelper.getApiService().bindDevice(reqMap),
                new DefaultObserver<ApiResponse>() {
                    @Override
                    public void onSuccess(ApiResponse response) {
                        if (response == null) {
                            callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                        }
                        switch (EnumResponseCode.getEnumByKey(response.getCode())) {
                            case SUCCESS://绑定成功
                                callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), response.getData());
                                break;
                            default:
                                callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                                break;
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.e(TAG, e.toString());
                        callBack.onCallBack(ErrorCode.RESPONSE_FAILED, e.toString(), null);

                    }

                    @Override
                    public void onException(ExceptionReason reason) {
                        super.onException(reason);
                        LogUtil.e(TAG, reason.toString());
                        callBack.onCallBack(ErrorCode.RESPONSE_FAILED, reason.name(), null);
                    }

                    @Override
                    public void onFail(int errorCode, String cause) {
                        super.onFail(errorCode, cause);
                        callBack.onCallBack(errorCode, cause, null);
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
