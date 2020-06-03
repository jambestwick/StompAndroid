package com.huawei.jams.testautostart.model.inter;

import com.huawei.jams.testautostart.presenter.inter.HttpDownloadCallBack;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;

public interface IAppInfoModel<T> {


    /**
     * APP版本订阅
     **/
    void subscribeVersion(StompCallBack<T> callBack);

    void downloadApp(String url, HttpDownloadCallBack<T> callBack);
}
