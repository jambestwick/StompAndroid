package com.huawei.jams.testautostart.presenter.inter;

public interface IAdvisePresenter {
    /**
     * 订阅广告推送
     */
    void topicAdviseInfo();

    void downloadAdvise(String url, String newVer);

    boolean deleteOldAdvise();
}
