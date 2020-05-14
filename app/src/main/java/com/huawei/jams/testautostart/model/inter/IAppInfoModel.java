package com.huawei.jams.testautostart.model.inter;

import com.huawei.jams.testautostart.presenter.inter.StompCallBack;

public interface IAppInfoModel<T> {



    /**
     * APP版本查询
     *
     * @param currentVer 当前版本号
     * @param token      后台token
     **/
    void queryVersion(String token, String currentVer, StompCallBack<T> callBack);
}
