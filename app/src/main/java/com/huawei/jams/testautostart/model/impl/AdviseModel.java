package com.huawei.jams.testautostart.model.impl;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.api.RetrofitHelper;
import com.huawei.jams.testautostart.entity.vo.AdviseVO;
import com.huawei.jams.testautostart.model.inter.IAdviseModel;
import com.huawei.jams.testautostart.presenter.inter.HttpDownloadCallBack;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.utils.StompUtil;
import com.yxytech.parkingcloud.baselibrary.http.HttpManager;
import com.yxytech.parkingcloud.baselibrary.http.common.FileDownLoadObserver;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

import java.io.File;

import io.reactivex.functions.Function;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.ResponseBody;
import ua.naiksoftware.stomp.dto.StompMessage;


public class AdviseModel implements IAdviseModel {
    private static final String TAG = AdviseModel.class.getName();
    private BaseActivity baseActivity;

    public AdviseModel(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Override
    public void subscribeVersion(StompCallBack callBack) {
        StompUtil.receiveStomp(IdeaApiService.ADV_QUERY_VERSION, new DisposableSubscriber<StompMessage>() {
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
                callBack.onCallBack(EnumResponseCode.EXCEPTION.getKey(), EnumResponseCode.EXCEPTION.getValue(), null);
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeVersion onComplete");
            }
        });
    }

    @Override
    public void downloadAdvise(String url, HttpDownloadCallBack callBack) {
        HttpManager httpManager = new HttpManager(baseActivity);
        FileDownLoadObserver<File> fileFileDownLoadObserver = new FileDownLoadObserver<File>() {
            @Override
            public void onDownLoadSuccess(File file) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",downloadAdvise onDownLoadSuccess:" + file);
                callBack.onDownLoadSuccess(file);

            }

            @Override
            public void onDownLoadFail(Throwable throwable) {
                callBack.onDownLoadFail(throwable);
            }

            @Override
            public void onProgress(int progress, long total) {
                callBack.onProgress(progress, total);
            }

            @Override
            public void onSuccess(File response) {

            }
        };

        httpManager.doHttpDeal(RetrofitHelper.getApiService().download(url).map(new Function<ResponseBody, Object>() {
                    @Override
                    public Object apply(ResponseBody responseBody) throws Exception {
                        return fileFileDownLoadObserver.saveFile(responseBody, Constants.ADVISE_DIR, url.substring(url.lastIndexOf("/") + 1));
                    }
                })
                , fileFileDownLoadObserver);
    }
}
