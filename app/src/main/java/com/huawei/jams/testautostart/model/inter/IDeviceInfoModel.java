package com.huawei.jams.testautostart.model.inter;

import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;

import java.util.Date;

public interface IDeviceInfoModel<T> {
    /**
     * 初始化设备，需要网络交互后台设备绑定
     *
     * @param deviceUuid 设备uuid
     **/
    void bindDevice(BaseActivity baseActivity, LifecycleProvider lifecycleProvider, String deviceUuid, StompCallBack<T> callBack);


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
     * @param deviceUuid 设备编号
     * @param token      令牌
     ***/
    void openBox(String deviceUuid, String sixCode, String token, StompCallBack<T> callBack);

    /**
     * 查询柜门长时间未锁
     * 报警配置
     *
     * @param deviceUuid   设备UUid
     * @param operatorTime 操作时间
     */
    void queryAlarmProperties(String deviceUuid, Date operatorTime, StompCallBack<T> callBack);

}
