package com.huawei.jams.testautostart.presenter.inter;

public interface IAppInfoPresenter {
    /**
     * 订阅app推送
     */
    void topicAppInfo();

    void downloadApp(String url, String newVer);

    boolean deleteOldApp();
}
