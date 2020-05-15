package com.huawei.jams.testautostart.model.impl;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.entity.AppInfo;
import com.huawei.jams.testautostart.model.inter.IAppInfoModel;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.service.StompService;
import com.yxytech.parkingcloud.baselibrary.http.common.ErrorCode;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.PackageUtils;

import io.reactivex.subscribers.DisposableSubscriber;
import ua.naiksoftware.stomp.client.StompMessage;

public class AppInfoModel implements IAppInfoModel {
    private static final String TAG = AppInfoModel.class.getName();

    @Override
    public void queryVersion(String token, String currentVer, StompCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("appVersion", currentVer);
        StompService.getInstance().sendStomp(IdeaApiService.APP_QUERY_VERSION, jsonObject.toString());
        StompService.getInstance().receiveStomp(IdeaApiService.APP_QUERY_VERSION, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                //返回数据
                stompMessage.getPayload();
                ApiResponse<AppInfo> apiResponse = new GsonBuilder().create().fromJson(stompMessage.getPayload(), ApiResponse.class);
                if (null != apiResponse.getData()) {
                    String versionName = PackageUtils.getVersionName(BaseApp.getAppContext());
                    if (!versionName.equals(apiResponse.getData().getAppName())) {
                        //下载

                    }

                }
                callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), apiResponse.getData());
            }

            @Override
            public void onError(Throwable t) {
                LogUtil.e(TAG, "onError" + Log.getStackTraceString(t));
                //错误异常
                callBack.onCallBack(ErrorCode.PARSE_JSON_ERROR, t.toString(), null);
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, "onComplete");

            }
        });
    }
}
