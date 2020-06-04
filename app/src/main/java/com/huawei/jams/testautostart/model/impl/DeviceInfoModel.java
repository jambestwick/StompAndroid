package com.huawei.jams.testautostart.model.impl;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.api.RetrofitHelper;
import com.huawei.jams.testautostart.entity.DeviceInfo;
import com.huawei.jams.testautostart.entity.vo.BindDeviceVO;
import com.huawei.jams.testautostart.entity.vo.BoxStateVO;
import com.huawei.jams.testautostart.entity.vo.OpenBoxVO;
import com.huawei.jams.testautostart.model.inter.IDeviceInfoModel;
import com.huawei.jams.testautostart.presenter.inter.HttpCallBack;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.presenter.inter.StompSendBack;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.utils.StompUtil;
import com.yxytech.parkingcloud.baselibrary.http.HttpManager;
import com.yxytech.parkingcloud.baselibrary.http.common.DefaultObserver;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.PreferencesManager;
import com.yxytech.parkingcloud.baselibrary.utils.StrUtil;

import io.reactivex.subscribers.DisposableSubscriber;
import ua.naiksoftware.stomp.dto.StompMessage;


public class DeviceInfoModel implements IDeviceInfoModel {
    private static final String TAG = DeviceInfoModel.class.getName();
    private BaseActivity activity;

    public DeviceInfoModel(BaseActivity activity) {
        this.activity = activity;
    }

    /**
     * http请求方式
     **/
    @Override
    public void bindDevice(String sixCode, HttpCallBack callBack) {
        HttpManager httpManager = new HttpManager(activity);
        httpManager.doHttpDeal(RetrofitHelper.getApiService().bindDevice(sixCode),
                new DefaultObserver<BindDeviceVO>() {
                    @Override
                    public void onSuccess(BindDeviceVO response) {
                        LogUtil.d(TAG, Thread.currentThread().getName() + ",bindDevice onSuccess:" + response);
                        if (response == null) {
                            callBack.onCallBack(EnumResponseCode.COMMON_BIZ_ERROR.getKey(), EnumResponseCode.COMMON_BIZ_ERROR.getValue(), null);
                            return;
                        }
                        if (response.getErrcode() == 1) {
                            callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                            return;
                        }
                        PreferencesManager.getInstance(BaseApp.getAppContext()).put(Constants.ACCOUNT, response.getCabinetNumber());
                        PreferencesManager.getInstance(BaseApp.getAppContext()).put(Constants.PASSWORD, response.getCabinetPassword());
                        callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), response);

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.e(TAG, Thread.currentThread().getName() + ",bindDevice onError:" + Log.getStackTraceString(e));
                        callBack.onCallBack(EnumResponseCode.EXCEPTION.getKey(), EnumResponseCode.EXCEPTION.getValue(), e);

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
    public void uploadBoxState(int boxState, StompCallBack stompCallBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("boxState", boxState);
        StompUtil.getInstance().sendStomp(activity, IdeaApiService.DEVICE_UPDATE_BOX_STATE, jsonObject.toString(), new StompSendBack() {
            @Override
            public void onSendSuccess() {
                stompCallBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), boxState);
            }

            @Override
            public void onSendError(Throwable throwable) {
                stompCallBack.onCallBack(EnumResponseCode.EXCEPTION.getKey(), EnumResponseCode.EXCEPTION.getValue(), throwable);
            }
        });

    }

    @Override
    public void openBox(String sixCode, StompCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("openBoxCode", sixCode);
        StompUtil.getInstance().sendStomp(activity, IdeaApiService.DEVICE_OPEN_BOX, jsonObject.toString(), new StompSendBack() {
            @Override
            public void onSendSuccess() {
                callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), sixCode);
            }

            @Override
            public void onSendError(Throwable throwable) {
                callBack.onCallBack(EnumResponseCode.EXCEPTION.getKey(), EnumResponseCode.EXCEPTION.getValue(), throwable.getMessage());
            }
        });

    }

    @Override
    public void subscribeBoxState(StompCallBack callBack) {
        StompUtil.getInstance().receiveStomp(IdeaApiService.DEVICE_UPDATE_BOX_STATE_RECEIVE, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeBoxState onNext:" + stompMessage.toString());
                BoxStateVO boxStateVO = new GsonBuilder().create().fromJson(stompMessage.getPayload(), BoxStateVO.class);
                if (boxStateVO == null) {
                    callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                    return;
                }
                if (null != DeviceInfo.EnumBoxState.getEnumByKey(boxStateVO.getEventcode())) {
                    callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), boxStateVO.getEventcode());
                } else {
                    callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                }
            }

            @Override
            public void onError(Throwable t) {
                LogUtil.e(TAG, Thread.currentThread().getName() + ",subscribeBoxState onError:" + Log.getStackTraceString(t));
                callBack.onCallBack(EnumResponseCode.EXCEPTION.getKey(), EnumResponseCode.EXCEPTION.getValue(), null);
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeBoxState onComplete");
            }
        });
    }

    @Override
    public void subscribeOpenBox(StompCallBack callBack) {
        StompUtil.getInstance().receiveStomp(IdeaApiService.DEVICE_OPEN_BOX_RECEIVE, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeOpenBox onNext:" + stompMessage);
                //返回开箱编号
                OpenBoxVO openBoxVO = new GsonBuilder().create().fromJson(stompMessage.getPayload(), OpenBoxVO.class);
                if (openBoxVO == null) {
                    callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                    return;
                }
                if (StrUtil.isNotBlank(openBoxVO.getBoxNumber())) {
                    callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), openBoxVO.getBoxNumber());
                } else {
                    callBack.onCallBack(EnumResponseCode.FAILED.getKey(), EnumResponseCode.FAILED.getValue(), null);
                }
            }

            @Override
            public void onError(Throwable t) {
                LogUtil.e(TAG, Thread.currentThread().getName() + ",subscribeOpenBox onError:" + Log.getStackTraceString(t));
                callBack.onCallBack(EnumResponseCode.EXCEPTION.getKey(), EnumResponseCode.EXCEPTION.getValue(), t.getMessage());
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeOpenBox onComplete");
            }
        });
    }


}
