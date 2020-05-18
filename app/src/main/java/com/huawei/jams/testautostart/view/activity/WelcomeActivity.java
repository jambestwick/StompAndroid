package com.huawei.jams.testautostart.view.activity;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.R;
import com.huawei.jams.testautostart.databinding.ActivityWelcomeBinding;
import com.huawei.jams.testautostart.presenter.impl.DeviceInfoPresenter;
import com.huawei.jams.testautostart.presenter.inter.IDeviceInfoPresenter;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.utils.KeyCabinetReceiver;
import com.huawei.jams.testautostart.view.inter.IMainView;
import com.yxytech.parkingcloud.baselibrary.dialog.SweetAlert.SweetAlertDialog;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.NetworkUtils;
import com.yxytech.parkingcloud.baselibrary.utils.PreferencesManager;
import com.yxytech.parkingcloud.baselibrary.utils.RxPermissionsUtil;
import com.yxytech.parkingcloud.baselibrary.utils.ShellUtils;
import com.yxytech.parkingcloud.baselibrary.utils.StrUtil;

import static com.huawei.jams.testautostart.utils.Constants.BOX_ID_ARRAY;

public class WelcomeActivity extends BaseActivity implements IMainView {
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
    private IDeviceInfoPresenter deviceInfoPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] netPer = new String[]{Manifest.permission.INTERNET};
        RxPermissionsUtil.request(this, netPer);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        initViews();
        initData();
        initDevice();
    }

    private void initViews() {
        deviceInfoPresenter = new DeviceInfoPresenter(this);
        binding.setClick(v -> {
            switch (v.getId()) {
                case R.id.wel_confirm_btn://点击按钮
                    initDevice();
                    break;
                case R.id.wel_cancel_btn:
                    step = 3;
                    initDevice();
                    break;
                case R.id.wel_code_delete_tv://删除前一位
                    decreaseInputCode();
                    break;
                case R.id.wel_code_ok_tv:
                default:
                    addInputCode(((TextView) v).getText().toString());
                    break;
            }
        });
    }

    private void initData() {
        binding.setHint(hintMessage);
        binding.setButton(btnMessage);
        binding.setCancel(cancelMessage);
    }

    private void initDevice() {
        //检查网络
        if (step == 1) {
            if (!isConnectInternet()) {
                return;
            }
            step = 2;
            initDevice();
            return;
        }
        if (step == 2) {
            if (!isConnectServer()) {
                return;
            }
            step = 3;
            initDevice();
            return;
        }

        if (step == 3) {
            deviceNo = PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.DEVICE_NO);
            String deviceCode = hasDeviceCode(deviceNo);
            if (!StrUtil.isEmpty(deviceCode)) {//SP有设备号
                deviceInfoPresenter.bindDevice(deviceCode);
                return;
            }
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
            deviceInfoPresenter.bindDevice(deviceNo);

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
                    inputCode = "";
                    deviceInfoPresenter.refreshWelcomeCode2View(binding, inputCode);
                    sDialog.cancel();
                }).show();
    }


    /**
     * 判断网络是否连通
     **/
    private boolean isConnectInternet() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("ping -c 1 " + Constants.ALI_PUBLIC_IP, false);
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
        //initData();
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
        KeyCabinetReceiver.queryBatchBoxState(this, BOX_ID_ARRAY, (boxIds, isBatchOpen) -> {
            boolean allClose = true;
            for (int i = 0; i < isBatchOpen.length; i++) {
                if (isBatchOpen[i]) {
                    allClose = false;
                    break;
                }
            }
            if (allClose) {
                //弹开所有柜门
                KeyCabinetReceiver.openBatchBox(this, BOX_ID_ARRAY, new KeyCabinetReceiver.OpenBoxListListener() {
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
                            step = 5;
                        } else {
                            hintMessage = "设备柜门故障（卡住，设备无法使用）";
                            binding.welConfirmBtn.setVisibility(View.GONE);
                            binding.welCancelBtn.setVisibility(View.GONE);
                            return;
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
            }

        });
    }

    private void judgeBoxAllClose() {
        KeyCabinetReceiver.queryBatchBoxState(this, BOX_ID_ARRAY, new KeyCabinetReceiver.QueryBatchBoxStateListener() {
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
                    hintMessage = "请输入6位设备码进行绑定";
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

    /**
     * 输入6位密码
     **/
    private void addInputCode(String addCode) {
        if (inputCode.length() < 6) {
            inputCode = inputCode + addCode;
            deviceInfoPresenter.refreshWelcomeCode2View(binding, inputCode);
        }

    }

    /**
     * 删除6位密码一位
     **/
    private void decreaseInputCode() {
        if (inputCode.length() > 0) {
            inputCode = inputCode.substring(0, inputCode.length() - 1);
            deviceInfoPresenter.refreshWelcomeCode2View(binding, inputCode);
        }
    }

    public enum EnumStep {
        STEP_1, STEP_2;

    }

}
