package com.huawei.jams.testautostart.presenter.impl;

import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.databinding.ActivityWelcomeBinding;
import com.huawei.jams.testautostart.entity.vo.BindDeviceVO;
import com.huawei.jams.testautostart.model.impl.DeviceInfoModel;
import com.huawei.jams.testautostart.model.inter.IDeviceInfoModel;
import com.huawei.jams.testautostart.presenter.inter.HttpCallBack;
import com.huawei.jams.testautostart.presenter.inter.IDeviceCheckPresenter;
import com.huawei.jams.testautostart.view.inter.IDeviceCheckView;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;

public class DeviceCheckPresenter implements IDeviceCheckPresenter {

    private IDeviceInfoModel mDeviceInfoModel;//Model接口
    private IDeviceCheckView deviceCheckView;//View接口

    public DeviceCheckPresenter(BaseActivity activity, IDeviceCheckView deviceCheckView) {
        this.mDeviceInfoModel = new DeviceInfoModel(activity);
        this.deviceCheckView = deviceCheckView;
    }



    /******
     * http请求
     *
     * *****/
    @Override
    public void bindDevice(String sixCode) {
        mDeviceInfoModel.bindDevice(sixCode, (HttpCallBack<BindDeviceVO>) (errorCode, msg, data) -> {
            if (errorCode == EnumResponseCode.SUCCESS.getKey()) {
                deviceCheckView.onBindDeviceSuccess(data.getCabinetNumber(), data.getCabinetPassword());
            } else {
                deviceCheckView.onBindDeviceFail(msg);
            }
        });

    }
    @Override
    public void refreshWelcomeCode2View(ActivityWelcomeBinding binding, String inputCode) {
        binding.welSixCode1Tv.setText("");
        binding.welSixCode2Tv.setText("");
        binding.welSixCode3Tv.setText("");
        binding.welSixCode4Tv.setText("");
        binding.welSixCode5Tv.setText("");
        binding.welSixCode6Tv.setText("");
        for (int i = 0; i < inputCode.length(); i++) {
            switch (i) {
                case 0:
                    binding.welSixCode1Tv.setText("" + inputCode.charAt(i));
                    break;
                case 1:
                    binding.welSixCode2Tv.setText("" + inputCode.charAt(i));
                    break;
                case 2:
                    binding.welSixCode3Tv.setText("" + inputCode.charAt(i));
                    break;
                case 3:
                    binding.welSixCode4Tv.setText("" + inputCode.charAt(i));
                    break;
                case 4:
                    binding.welSixCode5Tv.setText("" + inputCode.charAt(i));
                    break;
                case 5:
                    binding.welSixCode6Tv.setText("" + inputCode.charAt(i));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean boxListAllClose(boolean[] isOpens) {
        for (int i = 0; i < isOpens.length; i++) {
            if (isOpens[i]) {
                return false;
            }
        }
        return true;
    }
}
