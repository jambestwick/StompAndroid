package com.huawei.jams.testautostart.view.inter;

public interface IAdviseView {

    void onTopicAdviseSuccess(String url, String newVer);

    void onTopicAdviseFail(String reason);

    void onDownloadAdviseSuccess(String filePath);

    void onDownloadAdviseFail(String reason);

}
