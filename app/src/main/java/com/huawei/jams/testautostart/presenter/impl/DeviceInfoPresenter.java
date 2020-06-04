package com.huawei.jams.testautostart.presenter.impl;

import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.EnumResponseCode;
import com.huawei.jams.testautostart.databinding.ActivityMainBinding;
import com.huawei.jams.testautostart.databinding.ActivityWelcomeBinding;
import com.huawei.jams.testautostart.entity.vo.BindDeviceVO;
import com.huawei.jams.testautostart.model.impl.DeviceInfoModel;
import com.huawei.jams.testautostart.model.inter.IDeviceInfoModel;
import com.huawei.jams.testautostart.presenter.inter.HttpCallBack;
import com.huawei.jams.testautostart.presenter.inter.IDeviceInfoPresenter;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.utils.KeyCabinetReceiver;
import com.huawei.jams.testautostart.view.inter.IDeviceInfoView;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

public class DeviceInfoPresenter implements IDeviceInfoPresenter {

    private static final String TAG = AppInfoPresenter.class.getName();
    private IDeviceInfoModel mDeviceInfoModel;//Model接口
    private IDeviceInfoView deviceInfoView;//View接口

    public DeviceInfoPresenter(IDeviceInfoView deviceInfoView, BaseActivity activity) {
        this.mDeviceInfoModel = new DeviceInfoModel(activity);
        this.deviceInfoView = deviceInfoView;
    }

    @Override
    public void bindDevice(BaseActivity activity, String sixCode) {
        mDeviceInfoModel.bindDevice(sixCode, (HttpCallBack<BindDeviceVO>) (errorCode, msg, data) -> {
            if (errorCode == EnumResponseCode.SUCCESS.getKey()) {
                deviceInfoView.onBindDeviceSuccess(data.getCabinetNumber(), data.getCabinetPassword());
            } else {
                deviceInfoView.onBindDeviceFail(msg);
            }
        });

    }

    @Override
    public void uploadBoxState(int boxState) {
        mDeviceInfoModel.uploadBoxState(boxState, (errorCode, msg, data) -> {
            switch (errorCode) {
                case 0://EnumResponseCode.SUCCESS.getKey()
                    deviceInfoView.onUploadBoxStateSuccess();
                    break;
                default:
                    deviceInfoView.onUploadBoxStateFail(msg);
                    break;
            }
        });

    }

    @Override
    public void openBox(String sixCode) {
        mDeviceInfoModel.openBox(sixCode, new StompCallBack() {
            @Override
            public void onCallBack(int errorCode, String msg, Object data) {

            }
        });
        deviceInfoView.onOpenBoxFail(sixCode);
//        String deviceUuid = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.DEVICE_NO);
//        String token = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.TOKEN);
//        mDeviceInfoModel.openBox(deviceUuid, password, token, (errorCode, msg, data) -> {
//            switch (errorCode) {
//                case ApiResponse.SUCCESS:
//                    deviceInfoView.onOpenBoxSuccess((String) data);
//                    break;
//                default:
//                    deviceInfoView.onOpenBoxFail(msg);
//                    break;
//            }
//        });


    }

    @Override
    public void patrolBoxState(Timer timer, TimerTask task) {
        timer.schedule(task, 0, Constants.PATROL_INTERVAL_MILL_SECOND);
    }

    @Override
    public void refreshMainCode2View(ActivityMainBinding binding, String inputCode) {
        binding.mainSixCode1Tv.setText("");
        binding.mainSixCode2Tv.setText("");
        binding.mainSixCode3Tv.setText("");
        binding.mainSixCode4Tv.setText("");
        binding.mainSixCode5Tv.setText("");
        binding.mainSixCode6Tv.setText("");
        for (int i = 0; i < inputCode.length(); i++) {
            switch (i) {
                case 0:
                    binding.mainSixCode1Tv.setText("" + inputCode.charAt(i));
                    break;
                case 1:
                    binding.mainSixCode2Tv.setText("" + inputCode.charAt(i));
                    break;
                case 2:
                    binding.mainSixCode3Tv.setText("" + inputCode.charAt(i));
                    break;
                case 3:
                    binding.mainSixCode4Tv.setText("" + inputCode.charAt(i));
                    break;
                case 4:
                    binding.mainSixCode5Tv.setText("" + inputCode.charAt(i));
                    break;
                case 5:
                    binding.mainSixCode6Tv.setText("" + inputCode.charAt(i));
                    break;
                default:
                    break;
            }
        }
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

    @Override
    public void topicOpenBox() {
        mDeviceInfoModel.subscribeOpenBox((StompCallBack<String>) (errorCode, msg, data) -> {
            switch (errorCode) {
                case 0://EnumResponseCode.SUCCESS.getKey()
                    deviceInfoView.onOpenBoxSuccess(data);
                    break;
                default:
                    deviceInfoView.onOpenBoxFail(msg);
                    break;
            }
        });
    }

    @Override
    public void topicUploadBoxState() {
        mDeviceInfoModel.subscribeBoxState(new StompCallBack() {
            @Override
            public void onCallBack(int errorCode, String msg, Object data) {
                switch (errorCode) {
                    case 0://EnumResponseCode.SUCCESS.getKey()
                        deviceInfoView.onUploadBoxStateSuccess();
                        break;
                    default:
                        deviceInfoView.onUploadBoxStateFail(msg);
                        break;
                }
            }
        });

    }

    public static class TimeCountTask extends TimerTask {
        private String boxId;
        private KeyCabinetReceiver.BoxStateListener boxStateListener;

        public TimeCountTask(String boxId, KeyCabinetReceiver.BoxStateListener boxStateListener) {
            this.boxId = boxId;
            this.boxStateListener = boxStateListener;
        }

        @Override
        public void run() {
            KeyCabinetReceiver.queryBoxState(BaseApp.getAppContext(), boxId, boxStateListener);

        }
    }
}


