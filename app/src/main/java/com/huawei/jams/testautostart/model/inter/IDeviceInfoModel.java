package com.huawei.jams.testautostart.model.inter;

import com.huawei.jams.testautostart.presenter.inter.HttpCallBack;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;

import java.util.Date;

public interface IDeviceInfoModel<T> {
    /**
     * 初始化设备，http需要网络交互后台设备绑定
     *
     * @param deviceUuid 设备uuid
     **/
    void bindDevice(BaseActivity baseActivity, LifecycleProvider lifecycleProvider, String deviceUuid, HttpCallBack<T> callBack);


    /**
     * 上传柜门状态
     *
     * @param deviceUuid 设备uuid
     * @param boxId      箱子编号
     * @param boxState   箱子状态
     ***/
    void uploadBoxState(String deviceUuid, String deviceType, String boxId, String boxState, StompCallBack<T> callBack);


    /**
     * 打开柜门
     *
     *
     * @param sixCode      openBoxCode
     ***/
    void openBox(String sixCode, StompCallBack<T> callBack);

    /**
     * 订阅柜门状态
     */
    void subscribeBoxState(StompCallBack<T> callBack);

    /**
     * 订阅打开柜门
     */
    void subscribeOpenBox(StompCallBack<T> callBack);


}
