package com.huawei.jams.testautostart.view.inter;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/5/27<p>
 * <p>更新时间：2020/5/27<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public interface IAppInfoView {
    void onTopicAppInfoSuccess(String url, String version);

    void onTopicAppInfoFail(String reason);

    void onDownloadAppSuccess(String filePath);

    void onDownloadAppFail(String reason);
}
