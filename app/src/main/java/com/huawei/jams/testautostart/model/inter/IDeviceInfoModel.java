package com.huawei.jams.testautostart.model.inter;

import com.huawei.jams.testautostart.presenter.inter.HttpCallBack;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.presenter.inter.StompSendBack;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;

import java.util.Date;

public interface IDeviceInfoModel<E> {
    /**
     * 初始化设备，http需要网络交互后台设备绑定
     *
     * @param sixCode 设备sixCode
     **/
    <T> void bindDevice(String sixCode, HttpCallBack<T> callBack);


    /**
     * 上传柜门状态
     *
     * @param boxState 箱子状态
     *                 事件代码 0：开⻔ 1：关⻔
     ***/
    <T> void uploadBoxState(int boxState, StompCallBack<T> callBack);


    /**
     * 打开柜门
     *
     * @param sixCode openBoxCode
     ***/
    <T> void openBox(String sixCode, StompCallBack<T> callBack);

    /**
     * 订阅柜门状态
     */
    <T> void subscribeBoxState(StompCallBack<T> callBack);

    /**
     * 订阅打开柜门
     */
    <T> void subscribeOpenBox(StompCallBack<T> callBack);
    /**
     * 订阅服务端的心跳
     * **/
    <T> void subscribeServerHeartBeat(StompCallBack<T> callBack);


}
