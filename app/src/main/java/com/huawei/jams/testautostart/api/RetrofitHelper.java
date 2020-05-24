package com.huawei.jams.testautostart.api;


import com.yxytech.parkingcloud.baselibrary.http.common.IdeaApi;
import com.yxytech.parkingcloud.baselibrary.http.common.RxRetrofitApp;

public class RetrofitHelper {
    private static IdeaApiService mIdeaApiService;

    public static IdeaApiService getApiService() {
        return mIdeaApiService;
    }

    static {
        mIdeaApiService = IdeaApi.getApiService(IdeaApiService.class, RxRetrofitApp.getApiServerUrl());
    }
}
