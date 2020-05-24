package com.huawei.jams.testautostart.presenter.impl;

import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.api.ApiResponse;
import com.huawei.jams.testautostart.databinding.ActivityMainBinding;
import com.huawei.jams.testautostart.databinding.ActivityWelcomeBinding;
import com.huawei.jams.testautostart.entity.DeviceInfo;
import com.huawei.jams.testautostart.model.impl.DeviceInfoModel;
import com.huawei.jams.testautostart.model.inter.IDeviceInfoModel;
import com.huawei.jams.testautostart.presenter.inter.IDeviceInfoPresenter;
import com.huawei.jams.testautostart.presenter.inter.StompCallBack;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.utils.KeyCabinetReceiver;
import com.huawei.jams.testautostart.view.inter.IMainView;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.PreferencesManager;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceInfoPresenter implements IDeviceInfoPresenter {

    private static final String TAG = AppInfoPresenter.class.getName();
    private IDeviceInfoModel mDeviceInfoModel;//Model接口
    private IMainView mainView;//View接口

    public DeviceInfoPresenter(IMainView mainView) {
        this.mDeviceInfoModel = new DeviceInfoModel();
        this.mainView = mainView;
    }

    @Override
    public void bindDevice(BaseActivity activity, LifecycleProvider lifecycleProvider, String sixCode) {
        mDeviceInfoModel.bindDevice(activity, lifecycleProvider, sixCode, new StompCallBack() {
            @Override
            public void onCallBack(int errorCode, String msg, Object data) {
                switch (errorCode) {
                    case ApiResponse.SUCCESS:
                        mainView.onBindDeviceSuccess(((Map)data).get(Constants.ACCOUNT).toString(),((Map)data).get(Constants.PASSWORD).toString());
                        break;
                    default:
                        mainView.onBindDeviceFail(msg);
                        break;
                }
            }
        });

    }

    @Override
    public void uploadBoxState(String boxId, String boxState) {
        String deviceUuid = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.DEVICE_NO);
        String token = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.TOKEN);
        mDeviceInfoModel.uploadBoxState(deviceUuid, "deviceType", boxId, boxState, new StompCallBack() {
            @Override
            public void onCallBack(int errorCode, String msg, Object data) {
                switch (errorCode) {
                    case ApiResponse.SUCCESS:
                        mainView.onUploadBoxStateSuccess();
                        break;
                    default:
                        mainView.onUploadBoxStateFail(msg);
                        break;
                }
            }
        });

    }

    @Override
    public void openBox(String password, final int times) {
        if (times > 3) {
            mainView.onOpenBoxFail("网络异常");
            return;
        }
        String deviceUuid = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.DEVICE_NO);
        String token = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.TOKEN);
        mDeviceInfoModel.openBox(deviceUuid, password, token, new StompCallBack() {
            @Override
            public void onCallBack(int errorCode, String msg, Object data) {
                switch (errorCode) {
                    case ApiResponse.SUCCESS:
                        mainView.onOpenBoxSuccess((String) data);
                        break;
                    default:
                        mainView.onOpenBoxFail(msg);
                        openBox(password, times + 1);
                        break;
                }
            }
        });


    }

    @Override
    public void patrolBoxState(String boxId, String boxState) {
        long patrolTime = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.PATROL_TIME, Long.class);
        int patrolNum = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.PATROL_NUM, Integer.class);
        Timer timer = new Timer();
        timer.schedule(new TimeCountTask(boxId, timer, patrolNum), 0, patrolTime);
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

    class TimeCountTask extends TimerTask {
        private String boxId;
        private Timer timer;
        private int exeCount; //此处没有线程安全问题

        public TimeCountTask(String boxId, Timer timer, int exeCount) {
            this.boxId = boxId;
            this.timer = timer;
            this.exeCount = exeCount;
        }

        private int i = 1;

        @Override
        public void run() {
            i++;
            KeyCabinetReceiver.getInstance().queryBoxState(BaseApp.getAppContext(), boxId, new KeyCabinetReceiver.BoxStateListener() {
                @Override
                public void setType(KeyCabinetReceiver.EnumActionType enumActionType) {

                }
                @Override
                public void onBoxStateBack( String[] boxId, boolean[] isOpen) {
                    if (!isOpen[0]) {
                        uploadBoxState(boxId[0], DeviceInfo.EnumBoxState.CLOSE.getKey());
                        timer.cancel();
                        i = 1;
                    } else {
                        if (i > exeCount) {
                            //上报
                            uploadBoxState(boxId[0], DeviceInfo.EnumBoxState.OPEN.getKey());
                            timer.cancel();
                            i = 1;
                        }
                    }

                }
            });

        }
    }
}


