package com.huawei.jams.testautostart.model.inter;

import com.huawei.jams.testautostart.presenter.inter.HttpDownloadCallBack;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;

public interface IAdviseModel<T> {


    /**
     * 广告版本订阅
     ***/
    void subscribeVersion(StompCallBack<T> callBack);

    void downloadAdvise(String url, HttpDownloadCallBack<T> httpDownloadCallBack);
}
