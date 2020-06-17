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

    public DeviceInfoPresenter(BaseActivity baseActivity, IDeviceInfoView deviceInfoView) {
        this.mDeviceInfoModel = new DeviceInfoModel(baseActivity);
        this.deviceInfoView = deviceInfoView;
    }


    @Override
    public void uploadBoxState(int boxState) {
        mDeviceInfoModel.uploadBoxState(boxState, (errorCode, msg, data) -> {
            if (errorCode == EnumResponseCode.SUCCESS.getKey()) {
                deviceInfoView.onSendBoxStateSuccess();
            } else {
                deviceInfoView.onSendBoxStateFail(data.toString());
            }
        });

    }

    @Override
    public void openBox(String sixCode) {
        mDeviceInfoModel.openBox(sixCode, (StompCallBack<String>) (errorCode, msg, data) -> {
            if (errorCode == EnumResponseCode.SUCCESS.getKey()) {
                deviceInfoView.onSendOpenBoxSuccess();
            } else {
                deviceInfoView.onSendOpenBoxFail(data);
            }
        });
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
                    binding.mainSixCode1Tv.setText(inputCode.charAt(i) + "");
                    break;
                case 1:
                    binding.mainSixCode2Tv.setText(inputCode.charAt(i) + "");
                    break;
                case 2:
                    binding.mainSixCode3Tv.setText(inputCode.charAt(i) + "");
                    break;
                case 3:
                    binding.mainSixCode4Tv.setText(inputCode.charAt(i) + "");
                    break;
                case 4:
                    binding.mainSixCode5Tv.setText(inputCode.charAt(i) + "");
                    break;
                case 5:
                    binding.mainSixCode6Tv.setText(inputCode.charAt(i) + "");
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void topicOpenBox() {
        mDeviceInfoModel.subscribeOpenBox((StompCallBack<String>) (errorCode, msg, boxId) -> {
            if (errorCode == EnumResponseCode.SUCCESS.getKey()) {
                deviceInfoView.onReceiveOpenBoxSuccess(boxId);
            } else {
                deviceInfoView.onReceiveOpenBoxFail(msg);
            }
        });
    }

    @Override
    public void topicUploadBoxState() {
        mDeviceInfoModel.subscribeBoxState((StompCallBack<Integer>) (errorCode, msg, eventCode) -> {
            if (errorCode == EnumResponseCode.SUCCESS.getKey()) {
                deviceInfoView.onReceiveBoxStateSuccess(errorCode);
            } else {
                deviceInfoView.onReceiveBoxStateFail(msg);
            }
        });

    }

    public static class TimeCountTask extends TimerTask {
        private String boxId[];
        private KeyCabinetReceiver.BoxStateListener boxStateListener;

        public TimeCountTask(String[] boxId, KeyCabinetReceiver.BoxStateListener boxStateListener) {
            this.boxId = boxId;
            this.boxStateListener = boxStateListener;
        }

        @Override
        public void run() {
            KeyCabinetReceiver.queryBatchBoxState(BaseApp.getAppContext(), boxId, boxStateListener);
        }
    }

    /**
     *
     **/
    public static class TimeAdviseCountDownTask extends TimerTask {
        private long startTime;
        private TimeOperator timeOperator;

        public TimeAdviseCountDownTask(long startTime, TimeOperator timeOperator) {
            this.startTime = startTime;
            this.timeOperator = timeOperator;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        @Override
        public void run() {//Constants.NOT_CLICK_DELAY_SECOND30秒没操作就回到播放广告
            if (System.currentTimeMillis() - startTime > Constants.NOT_CLICK_DELAY_SECOND) {
                timeOperator.timeOut();
            }
        }
    }

    public interface TimeOperator {
        void timeOut();
    }

    public enum EnumBoxConvert {
        BOX1("1", "Z01"),
        BOX2("2", "Z02"),
        BOX3("3", "Z03"),
        BOX4("4", "Z04"),
        BOX5("5", "Z05"),
        BOX6("6", "Z06"),
        BOX7("7", "Z07"),
        BOX8("8", "Z99");
        private String key;
        private String value;

        EnumBoxConvert(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static EnumBoxConvert getEnumByKey(String key) {
            if (key == null || key.equals("")) {
                return null;
            }

            for (EnumBoxConvert enumResponseCode : EnumBoxConvert.values()) {
                if (enumResponseCode.getKey().equals(key)) {
                    return enumResponseCode;
                }
            }

            return null;
        }

        public static EnumBoxConvert getEnumByValue(String value) {
            if (value == null || value.equals("")) {
                return null;
            }

            for (EnumBoxConvert enumBoxState : EnumBoxConvert.values()) {
                if (enumBoxState.getValue().equals(value)) {
                    return enumBoxState;
                }
            }

            return null;
        }


    }
}


