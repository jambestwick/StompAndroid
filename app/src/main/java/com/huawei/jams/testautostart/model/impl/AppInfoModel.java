package com.huawei.jams.testautostart.model.impl;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.entity.AppInfo;
import com.huawei.jams.testautostart.model.inter.IAppInfoModel;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.service.StompService;
import com.yxytech.parkingcloud.baselibrary.http.common.ErrorCode;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import io.reactivex.subscribers.DisposableSubscriber;
import ua.naiksoftware.stomp.client.StompMessage;

public class AppInfoModel implements IAppInfoModel {
    private static final String TAG = AppInfoModel.class.getName();

    @Override
    public void queryVersion(String token, String currentVer, StompCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("appVersion", currentVer);
        StompService.getInstance().sendStomp(IdeaApiService.QUERY_APP_VERSION, jsonObject.toString());
        StompService.getInstance().receiveStomp(IdeaApiService.QUERY_APP_VERSION, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                //返回数据
                stompMessage.getPayload();
                AppInfo appInfo = new GsonBuilder().create().fromJson(stompMessage.getPayload(), AppInfo.class);
                callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), appInfo);
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
