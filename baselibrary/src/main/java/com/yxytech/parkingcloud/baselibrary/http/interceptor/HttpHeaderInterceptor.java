package com.yxytech.parkingcloud.baselibrary.http.interceptor;

import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.PreferencesManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhpan on 2018/3/21.
 */

public class HttpHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //  配置请求头
        String accessToken = "token";
        String tokenType = "tokenType";
//        String cookie = PreferencesManager.getInstance(Utils.getContext()).get("cookie");
        String token = PreferencesManager.getInstance(BaseApplication.getAppContext()).get("token");
        LogUtil.d("HttpHeaderInterceptor", "Cookie: " + token);
        Request request = chain.request().newBuilder()
                .header("app_key", "appId")
                .header("Authorization", tokenType + " " + accessToken)
                .header("Content-Type", "application/json")
                .addHeader("Connection", "close")
                .addHeader("Accept-Encoding", "identity")
                .addHeader("token", token)
                .build();
        return chain.proceed(request);
    }
}
