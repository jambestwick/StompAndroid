package com.huawei.jams.testautostart.view.inter;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/5/27<p>
 * <p>更新时间：2020/5/27<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public interface IDeviceInfoView {
    void onOpenBoxSuccess(String boxId);

    void onOpenBoxFail(String reason);

    void onUploadBoxStateSuccess();

    void onUploadBoxStateFail(String reason);

    void onBindDeviceSuccess(String account, String password);

    void onBindDeviceFail(String reason);
}
