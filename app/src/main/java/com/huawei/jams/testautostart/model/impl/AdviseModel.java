package com.huawei.jams.testautostart.model.impl;

import android.util.Log;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.entity.Advise;
import com.huawei.jams.testautostart.entity.Advise_Table;
import com.huawei.jams.testautostart.entity.vo.AdviseVO;
import com.huawei.jams.testautostart.model.inter.IAdviseModel;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.utils.StompUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import io.reactivex.subscribers.DisposableSubscriber;
import ua.naiksoftware.stomp.dto.StompMessage;


public class AdviseModel implements IAdviseModel {
    private static final String TAG = AdviseModel.class.getName();

    @Override
    public void subscribeVersion(StompCallBack callBack) {
        StompUtil.getInstance().receiveStomp(IdeaApiService.ADV_QUERY_VERSION, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeVersion onNext:" + stompMessage.toString());
                stompMessage.getPayload();
                AdviseVO adviseVO = new GsonBuilder().create().fromJson(stompMessage.getPayload(), AdviseVO.class);
                callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), adviseVO);
            }

            @Override
            public void onError(Throwable t) {
                LogUtil.e(TAG, Thread.currentThread().getName() + ",subscribeVersion onError:" + Log.getStackTraceString(t));
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeVersion onComplete");
            }
        });
    }
}
