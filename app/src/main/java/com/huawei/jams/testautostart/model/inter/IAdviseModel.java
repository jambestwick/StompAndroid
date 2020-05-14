package com.huawei.jams.testautostart.model.inter;

import com.huawei.jams.testautostart.presenter.inter.StompCallBack;

public interface IAdviseModel<T> {


    /**
     * 查询广告版本
     * @param token 后台返回的token
     * @param currentVer 广告的当前版本
     *
     * ***/
    void queryVersion(String token, String currentVer, StompCallBack<T> callBack);
}
