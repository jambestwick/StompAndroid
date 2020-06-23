package com.huawei.jams.testautostart.model.impl;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.api.IdeaApiService;
import com.huawei.jams.testautostart.api.RetrofitHelper;
import com.huawei.jams.testautostart.entity.vo.AppVO;
import com.huawei.jams.testautostart.model.inter.IAppInfoModel;
import com.huawei.jams.testautostart.presenter.inter.HttpDownloadCallBack;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.utils.StompUtil;
import com.yxytech.parkingcloud.baselibrary.http.HttpManager;
import com.yxytech.parkingcloud.baselibrary.http.common.ErrorCode;
import com.yxytech.parkingcloud.baselibrary.http.common.FileDownLoadObserver;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

import io.reactivex.functions.Function;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.ResponseBody;
import ua.naiksoftware.stomp.dto.StompMessage;

import java.io.File;


public class AppInfoModel implements IAppInfoModel {
    private static final String TAG = AppInfoModel.class.getName();
    private BaseActivity baseActivity;

    public AppInfoModel(BaseActivity activity) {
        this.baseActivity = activity;
    }

    @Override
    public void subscribeVersion(StompCallBack callBack) {
        StompUtil.receiveStomp(IdeaApiService.APP_QUERY_VERSION, new DisposableSubscriber<StompMessage>() {
            @Override
            public void onNext(StompMessage stompMessage) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeVersion onNext:" + stompMessage.toString());
                //返回数据
                stompMessage.getPayload();
                AppVO appVO = new GsonBuilder().create().fromJson(stompMessage.getPayload(), AppVO.class);
                callBack.onCallBack(EnumResponseCode.SUCCESS.getKey(), EnumResponseCode.SUCCESS.getValue(), appVO);
            }

            @Override
            public void onError(Throwable t) {
                LogUtil.e(TAG, Thread.currentThread().getName() + ",subscribeVersion onError:" + Log.getStackTraceString(t));
                //错误异常
                callBack.onCallBack(EnumResponseCode.EXCEPTION.getKey(), EnumResponseCode.EXCEPTION.getValue(), null);
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",subscribeVersion onComplete");

            }

        });
    }

    @Override
    public void downloadApp(String url, HttpDownloadCallBack callBack) {
        HttpManager httpManager = new HttpManager(baseActivity);
        FileDownLoadObserver<File> fileFileDownLoadObserver = new FileDownLoadObserver<File>() {
            @Override
            public void onDownLoadSuccess(File file) {
                LogUtil.d(TAG, Thread.currentThread().getName() + ",downloadApp onDownLoadSuccess:" + file);
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
                        return fileFileDownLoadObserver.saveFile(responseBody, Constants.APP_DIR, url.substring(url.lastIndexOf("/") + 1));
                    }
                })
                , fileFileDownLoadObserver);
    }

}
