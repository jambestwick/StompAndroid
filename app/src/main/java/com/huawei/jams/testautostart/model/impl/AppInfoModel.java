package com.huawei.jams.testautostart.model.impl;

import android.util.Log;
import com.google.gson.GsonBuilder;
import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.entity.AppInfo;
import com.huawei.jams.testautostart.model.inter.IAppInfoModel;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.utils.StompUtil;
import com.yxytech.parkingcloud.baselibrary.http.common.ErrorCode;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.PackageUtils;
import io.reactivex.subscribers.DisposableSubscriber;
import ua.naiksoftware.stomp.dto.StompMessage;


public class AppInfoModel implements IAppInfoModel {
    private static final String TAG = AppInfoModel.class.getName();

    @Override
    public void subscribeVersion(StompCallBack callBack) {
        StompUtil.getInstance().receiveStomp(IdeaApiService.APP_QUERY_VERSION, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeVersion onNext:" + stompMessage.toString());
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
                LogUtil.e(TAG, Thread.currentThread().getName() + ",subscribeVersion onError:" + Log.getStackTraceString(t));
                //错误异常
                callBack.onCallBack(ErrorCode.PARSE_JSON_ERROR, t.toString(), null);
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeVersion onComplete");

            }

        });
    }
}
