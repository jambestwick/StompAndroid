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
import com.huawei.jams.testautostart.presenter.inter.HttpCallBack;
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
    private BaseActivity activity;
    private LifecycleProvider lifecycleProvider;

    public DeviceInfoModel(BaseActivity activity, LifecycleProvider lifecycleProvider) {
        this.activity = activity;
        this.lifecycleProvider = lifecycleProvider;
    }

    /**
     * http请求方式
     **/
    @Override
    public void bindDevice(BaseActivity baseActivity, LifecycleProvider lifecycleProvider, String sixCode, HttpCallBack callBack) {
        HttpManager httpManager = new HttpManager(baseActivity);
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("sixCode", sixCode);
        httpManager.doHttpDeal(RetrofitHelper.getApiService().bindDevice(reqMap),
                new DefaultObserver<ApiResponse>() {
                    @Override
                    public void onSuccess(ApiResponse response) {
                        LogUtil.d(TAG, Thread.currentThread().getName() + ",bindDevice onSuccess:" + response);
                        if (response == null) {
                            callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                        }
                        switch (EnumResponseCode.getEnumByKey(response.getCode())) {
                            case SUCCESS://绑定成功

                                PreferencesManager.getInstance(BaseApp.getAppContext()).put(Constants.ACCOUNT, response.getData());
                                PreferencesManager.getInstance(BaseApp.getAppContext()).put(Constants.PASSWORD, response.getData());
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
                        LogUtil.e(TAG, Thread.currentThread().getName() + ",bindDevice onError:" + Log.getStackTraceString(e));
                        callBack.onCallBack(ErrorCode.RESPONSE_FAILED, e.toString(), null);

                    }

                    @Override
                    public void onException(ExceptionReason reason) {
                        super.onException(reason);
                        LogUtil.e(TAG, Thread.currentThread().getName() + ",bindDevice onException:" + reason);
                        //callBack.onCallBack(ErrorCode.RESPONSE_FAILED, reason.name(), null);
                    }

                    @Override
                    public void onFail(int errorCode, String cause) {
                        super.onFail(errorCode, cause);
                        LogUtil.e(TAG, Thread.currentThread().getName() + ",bindDevice onFail,errorCode" + errorCode + ",cause" + cause);
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
        StompUtil.getInstance().sendStomp(activity, lifecycleProvider, IdeaApiService.DEVICE_UPDATE_BOX_STATE, jsonObject.toString());

    }

    @Override
    public void openBox(String deviceUuid, String sixCode, String token, StompCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceUuid", deviceUuid);
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("password", sixCode);
        StompUtil.getInstance().sendStomp(activity, lifecycleProvider, IdeaApiService.DEVICE_OPEN_BOX, jsonObject.toString());

    }

    @Override
    public void subscribeBoxState(StompCallBack callBack) {
        StompUtil.getInstance().receiveStomp(IdeaApiService.DEVICE_UPDATE_BOX_STATE, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeBoxState onNext:" + stompMessage.toString());
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
                LogUtil.e(TAG, Thread.currentThread().getName() + ",subscribeBoxState onError:" + Log.getStackTraceString(t));
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeBoxState onComplete");
            }
        });
    }

    @Override
    public void subscribeOpenBox(StompCallBack callBack) {
        StompUtil.getInstance().receiveStomp(IdeaApiService.DEVICE_OPEN_BOX, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeOpenBox onNext:" + stompMessage);
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
                LogUtil.e(TAG, Thread.currentThread().getName() + ",subscribeOpenBox onError:" + Log.getStackTraceString(t));
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeOpenBox onComplete");
            }
        });
    }


}
