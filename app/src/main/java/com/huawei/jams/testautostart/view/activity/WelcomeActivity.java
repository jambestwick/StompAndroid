package com.huawei.jams.testautostart.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.MainActivity;
import com.huawei.jams.testautostart.R;
import com.huawei.jams.testautostart.databinding.ActivityWelcomeBinding;
import com.huawei.jams.testautostart.presenter.impl.DeviceInfoPresenter;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.utils.KeyCabinetReceiver;
import com.huawei.jams.testautostart.view.inter.IMainView;
import com.yxytech.parkingcloud.baselibrary.dialog.SweetAlert.SweetAlertDialog;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.NetworkUtils;
import com.yxytech.parkingcloud.baselibrary.utils.PreferencesManager;
import com.yxytech.parkingcloud.baselibrary.utils.ShellUtils;
import com.yxytech.parkingcloud.baselibrary.utils.StrUtil;

public class WelcomeActivity extends BaseActivity {
    private static final String TAG = WelcomeActivity.class.getName();
    private ActivityWelcomeBinding binding;
    private String hintMessage = "";
    private String btnMessage = "";
    private String cancelMessage = "";
    private int step = 1;
    private int queryBoxStateTimes = 1;
    /***输入6位开箱码**/
    private String inputCode = "";
    private String deviceNo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        initViews();
        initData();
        initDevice(step);
    }

    private void initViews() {
        binding.setClick(v -> {
            switch (v.getId()) {
                case R.id.wel_confirm_btn://点击按钮
                    initDevice(step);
                    break;
                case R.id.wel_cancel_btn:
                    initDevice(4);
                    break;
                default:
                    break;
            }
        });
    }

    private void initData() {
        binding.setHint(hintMessage);
        binding.setButton(btnMessage);
        binding.setCancel(cancelMessage);
    }

    private void initDevice(int step) {
        //检查网络
        if (step == 1) {
            if (!isConnectInternet()) {
                return;
            }
            initDevice(2);
            return;
        }
        if (step == 2) {
            if (!isConnectServer()) {
                return;
            }
            initDevice(3);
            return;
        }

        if (step == 3) {
            deviceNo = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.DEVICE_NO);
            String deviceCode = hasDeviceCode(deviceNo);
            if (!StrUtil.isEmpty(deviceCode)) {//SP有设备号
                stompBindDevice(deviceCode);
                return;
            }
            initDevice(4);
        }
        if (step == 4) {
            readBoxAllClose();
        }
        if (step == 5) {
            judgeBoxAllClose();
        }
        if (step == 6) {
            if (deviceNo == null) {
                deviceNo = inputCode;
            }
            stompBindDevice(deviceNo);

        }


//        if (commandResult.result == -1) {//ping后台失败
//            //提示框:后台通信失败，请联系后台人员处理(按键重试)点击重试继续判断
//        } else {
//            String deviceUuid = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.DEVICE_NO);
//            if (StrUtil.isEmpty(deviceUuid)) {//读不到设备绑定信息（新设备入网自检硬件）
//                //1.提示框：自检柜门,请手动关闭所有柜门(按键确定)
//                //点击确定，1.1读取所有柜门状态
//                // 1.1.1所有关闭-->下一步 2
//                // 1.1.2尚有未关闭的，则提示框：确定柜门是否全部关闭（是，否），
//                // 1.1.2-->是 继续查一遍1.1.2.1 发现柜门全关--->2
//                // 1.1.2.2否则柜门尚有未关闭者，则提示框：设备柜门故障（卡住，设备无法使用）;
//                // 1.1.2-->否-->跳回1步骤
//                //2.发送开门命令，弹开所有柜门，后读取所有柜门状态，
//                //2.1 柜门状态:全开:提示框:设备自检通过，请关闭所有柜门(按钮下一步)
//                //2.1.1 下一步-->判断所有柜门是否已经关闭，是-->3; 否,提示框（非全屏）：请关闭所有柜门，再点下一步(确定)-->2.1。
//                //2.2 柜门状态:存在未弹开柜门,则提示框：设备柜门故障（卡住，设备无法使用）;
//                //3新设备未绑定:工人输入6位数字码，调用设备绑定，
//                //3新设备绑定：结果-->
//                //3.1成功，更新SP设备-->主页
//                //3.2绑定失败,提示框:设备绑定失败,联系后台人员(非全屏)(确定)点击确定自动清空6位号码
//            } else {
//                //发送SP6位数字码，返回-->
//                //1.1成功-->主页
//                //1.2失败-->3
//            }
//        }


    }

    public enum EnumStep {
        STEP_1, STEP_2;

    }

    /**
     * 判断网络是否连通
     **/
    private boolean isConnectInternet() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("ping -c 1 -w 1 www.baidu.com", false);
        if (NetworkUtils.isConnected() && result.result == 0) {
            return true;
        }
        hintMessage = "未连接网络,请检查后重试";
        btnMessage = "重试";
        step = 1;
        return false;
    }

    /**
     * 判断后台服务是否联通
     **/
    private boolean isConnectServer() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd("ping metalcar.cn", false);
        if (commandResult.result == 0) {//ping后台失败
            //提示框:后台通信失败，请联系后台人员处理(按键重试)点击重试继续判断
            return true;
        }
        hintMessage = "后台通信失败，请联系后台人员处理";
        btnMessage = "重试";
        step = 2;
        return false;
    }

    private String hasDeviceCode(String deviceNo) {
        if (!StrUtil.isEmpty(deviceNo)) {
            return deviceNo;
        }
        hintMessage = "自检柜门,请手动关闭所有柜门";
        btnMessage = "确定";
        step = 4;
        return null;
    }

    private void readBoxAllClose() {
        String[] boxIdList = new String[]{"Z01", "Z02", "Z03", "Z04", "Z05", "Z06", "Z07", "Z08"};
        KeyCabinetReceiver.queryBatchBoxState(this, boxIdList, (boxIds, isBatchOpen) -> {
            boolean allClose = true;
            for (int i = 0; i < isBatchOpen.length; i++) {
                if (isBatchOpen[i]) {
                    allClose = false;
                    break;
                }
            }
            if (allClose) {
                //弹开所有柜门
                KeyCabinetReceiver.openBatchBox(this, boxIdList, new KeyCabinetReceiver.OpenBoxListListener() {
                    @Override
                    public void onBoxStateBack(String[] boxIds, boolean[] isBatchOpen) {
                        boolean allOpen = true;
                        for (int i = 0; i < isBatchOpen.length; i++) {
                            if (!isBatchOpen[i]) {
                                allOpen = false;
                                break;
                            }
                        }
                        if (allOpen) {
                            hintMessage = "设备自检通过,请关闭所有柜门";
                            btnMessage = "下一步";
                            //initDevice(5);
                            step = 5;

                        } else {
                            hintMessage = "设备柜门故障（卡住，设备无法使用）";
                            binding.welConfirmBtn.setVisibility(View.GONE);
                            binding.welCancelBtn.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                if (queryBoxStateTimes >= 2) {
                    hintMessage = "设备柜门故障（卡住，设备无法使用）";
                    binding.welConfirmBtn.setVisibility(View.GONE);
                    binding.welCancelBtn.setVisibility(View.GONE);
                    return;

                }
                hintMessage = "确定柜门是否全部关闭";
                btnMessage = "是";
                cancelMessage = "否";
                step = 4;
                queryBoxStateTimes++;
                return;
            }

        });
    }

    private void judgeBoxAllClose() {
        String[] boxIdList = new String[]{"Z01", "Z02", "Z03", "Z04", "Z05", "Z06", "Z07", "Z08"};
        KeyCabinetReceiver.queryBatchBoxState(this, boxIdList, new KeyCabinetReceiver.QueryBatchBoxStateListener() {
            @Override
            public void onBoxStateBack(String[] boxIds, boolean[] isBatchOpen) {
                boolean allClose = true;
                for (int i = 0; i < isBatchOpen.length; i++) {
                    if (isBatchOpen[i]) {
                        allClose = false;
                        break;
                    }
                }
                if (allClose) {
                    step = 6;
                    return;
                } else {
                    new SweetAlertDialog(WelcomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                            //.setTitleText("")
                            .setContentText("请关闭所有柜门，再点下一步")
                            .setConfirmText("下一步")
                            .showCancelButton(false)
                            .setConfirmClickListener(sDialog -> {
                                sDialog.cancel();
                                judgeBoxAllClose();
                            }).show();
                }
            }
        });
    }


    private void stompBindDevice(String sixCode) {
        new DeviceInfoPresenter(new IMainView() {
            @Override
            public void onQueryAppInfoSuccess(String url) {

            }

            @Override
            public void onQueryAppInfoFail(String reason) {

            }

            @Override
            public void onQueryAdviseSuccess(String url) {

            }

            @Override
            public void onQueryAdviseFail(String reason) {

            }

            @Override
            public void onOpenBoxSuccess(String boxId) {

            }

            @Override
            public void onOpenBoxFail(String reason) {

            }

            @Override
            public void onQueryAlarmPropSuccess() {

            }

            @Override
            public void onQueryAlarmPropFail(String reason) {

            }

            @Override
            public void onUploadBoxStateSuccess() {

            }

            @Override
            public void onUploadBoxStateFail(String reason) {

            }

            @Override
            public void onBindDeviceSuccess() {
                //绑定成功
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onBindDeviceFail(String reason) {
                //绑定失败,重新输入六位码进行绑定
                new SweetAlertDialog(WelcomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("设备绑定")
                        .setContentText("设备绑定失败,联系后台人员")
                        .setConfirmText("确定")
                        .showCancelButton(false)
                        .setConfirmClickListener(sDialog -> {
                            sDialog.cancel();
                            inputCode = "";
                            refreshCode2View();
                        }).show();
            }
        }).bindDevice(sixCode);
    }

    private void refreshCode2View() {
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
}