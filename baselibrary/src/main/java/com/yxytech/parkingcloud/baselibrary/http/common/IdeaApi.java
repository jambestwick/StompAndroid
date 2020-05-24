package com.yxytech.parkingcloud.baselibrary.http.common;

import retrofit2.Retrofit;

import java.io.IOException;

/**
 * Created by zhpan on 2017/4/1.
 */

public class IdeaApi {
    public static <T> T getApiService(Class<T> cls, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder().build();
        try {
            retrofit = RetrofitService.getRetrofitBuilder(baseUrl).build();
            return retrofit.create(cls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retrofit.create(cls);
    }
}
