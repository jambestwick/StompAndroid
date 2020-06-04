package com.huawei.jams.testautostart.view.activity;

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
import com.huawei.jams.testautostart.utils.StompUtil;
import com.huawei.jams.testautostart.view.inter.IDeviceInfoView;
import com.yxytech.parkingcloud.baselibrary.dialog.SweetAlert.SweetAlertDialog;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.*;

import java.util.Objects;


public class WelcomeActivity extends BaseActivity implements IDeviceInfoView, KeyCabinetReceiver.BoxStateListener, StompUtil.StompConnectListener {
    private static final String TAG = WelcomeActivity.class.getName();
    private ActivityWelcomeBinding binding;
    private String hintMessage = "";//提示语
    private String btnMessage = "";//确认按钮
    private String cancelMessage = "";//取消按钮
    private int step = EnumDeviceCheck.STEP_1.key;
    private int queryBoxStateTimes = 1;//查询柜门已关闭次数
    private int openBoxIndex = 0;//逐个打开柜门到第几个
    /***输入6位开箱码**/
    private String inputCode = "";//6未输入码
    private IDeviceInfoPresenter deviceInfoPresenter;
    private EnumDeviceBindState deviceBindState = EnumDeviceBindState.NEW;//设备绑定状态（新/旧）


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        initViews();
        initDevice();
    }

    private void initViews() {
        deviceInfoPresenter = new DeviceInfoPresenter(this, this);
        binding.setClick(v -> {
            switch (v.getId()) {
                case R.id.wel_confirm_btn://点击按钮
                    initDevice();
                    break;
                case R.id.wel_cancel_btn:
                    step = EnumDeviceCheck.STEP_3.key;
                    initDevice();
                    break;
                case R.id.wel_code_delete_tv://删除前一位
                    decreaseInputCode();
                    break;
                case R.id.wel_code_ok_tv:
                    if (inputCode.length() == 6) {
                        deviceInfoPresenter.bindDevice(this, inputCode);
                    } else {
                        //提示码位数不够
                        ToastUtil.showToast(this, this.getString(R.string.six_code_not_enough));
                    }
                    break;
                default:
                    addInputCode(((TextView) v).getText().toString());
                    break;
            }
        });
    }


    private void setData() {
        if (StrUtil.isNotBlank(hintMessage)) {
            binding.welHintTv.setVisibility(View.VISIBLE);
            binding.setHint(hintMessage);
        } else {
            binding.welHintTv.setVisibility(View.GONE);
        }
        if (StrUtil.isNotBlank(btnMessage)) {
            binding.welConfirmBtn.setVisibility(View.VISIBLE);
            binding.setButton(btnMessage);
        } else {
            binding.welConfirmBtn.setVisibility(View.GONE);
        }
        if (StrUtil.isNotBlank(cancelMessage)) {
            binding.welCancelBtn.setVisibility(View.VISIBLE);
            binding.setCancel(cancelMessage);
        } else {
            binding.welCancelBtn.setVisibility(View.GONE);
        }

    }

    private void initDevice() {
        //检查网络
        if (step == EnumDeviceCheck.STEP_1.key) {
            if (!isConnectInternet()) {
                return;
            }
            step = EnumDeviceCheck.STEP_2.key;
            initDevice();
            return;
        }
        if (step == EnumDeviceCheck.STEP_2.key) {
            if (!isConnectServer()) {
                return;
            }
            step = EnumDeviceCheck.STEP_3.key;
            initDevice();
            return;
        }

        if (step == EnumDeviceCheck.STEP_3.key) {
            String account = PreferencesManager.getInstance(this).get(Constants.ACCOUNT);
            String password = PreferencesManager.getInstance(this).get(Constants.PASSWORD);
            if (StrUtil.isNotBlank(account) && StrUtil.isNotBlank(password)) {//不是空说明已经注册过
                StompUtil.getInstance().createStompClient(this, account, password, this);//重绑
                deviceBindState = EnumDeviceBindState.OLD;
                return;
            }
            step = EnumDeviceCheck.STEP_4.key;
            initDevice();
            return;
        }
        if (step == EnumDeviceCheck.STEP_4.key) {
            readBoxAllClose();
        }
        if (step == EnumDeviceCheck.STEP_5.key) {
            judgeBoxAllClose();
        }
        if (step == EnumDeviceCheck.STEP_7.key) {
            StompUtil.getInstance().createStompClient(this
                    , PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.ACCOUNT)
                    , PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.PASSWORD)
                    , this);
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
    public void onOpenBoxSuccess(String boxId) {

    }

    @Override
    public void onOpenBoxFail(String reason) {

    }

    @Override
    public void onUploadBoxStateSuccess() {

    }

    @Override
    public void onUploadBoxStateFail(String reason) {

    }

    @Override
    public void onBindDeviceSuccess(String account, String password) {
        //绑定成功
        turnStep(EnumDeviceCheck.STEP_7, this.getString(R.string.bind_device) + this.getString(R.string.success), null, null);
        StompUtil.getInstance().createStompClient(this, account, password, this);
    }

    @Override
    public void onBindDeviceFail(String reason) {
        //绑定失败,重新输入六位码进行绑定
        new SweetAlertDialog(WelcomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(this.getString(R.string.bind_device))
                .setContentText(this.getString(R.string.bind_device) + this.getString(R.string.fail) + "," + this.getString(R.string.contact_back_office_handle))
                .setConfirmText(this.getString(R.string.ok))
                .showCancelButton(false)
                .setConfirmClickListener(sDialog -> {
                    inputCode = "";
                    deviceInfoPresenter.refreshWelcomeCode2View(binding, inputCode);
                    binding.welSixCodeLl.setVisibility(View.VISIBLE);
                    binding.welKeyboardLl.setVisibility(View.VISIBLE);
                    sDialog.cancel();
                }).show();
    }


    /**
     * 判断网络是否连通
     **/
    private boolean isConnectInternet() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("ping -c 3 " + Constants.ALI_PUBLIC_IP, false);
        if (NetworkUtils.isConnected() && result.result == 0) {
            return true;
        }
        turnStep(EnumDeviceCheck.STEP_1, "未连接网络,请检查后重试", this.getString(R.string.retry), null);
        return false;
    }

    /**
     * 判断后台服务是否联通
     **/
    private boolean isConnectServer() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd("ping -c 3 " + Constants.SERVER_URL, false);
        if (commandResult.result == 0) {//ping后台失败
            //提示框:后台通信失败，请联系后台人员处理(按键重试)点击重试继续判断
            return true;
        }
        turnStep(EnumDeviceCheck.STEP_2, "后台通信失败," + this.getString(R.string.contact_back_office_handle), this.getString(R.string.retry), null);
        return false;
    }

    /***
     *
     * 读取设备柜门是否都关闭
     * */
    private void readBoxAllClose() {
        KeyCabinetReceiver.queryBatchBoxState(this, Constants.BOX_ID_ARRAY, this);
    }

    /**
     * 判断柜门是否全关闭
     **/
    private void judgeBoxAllClose() {
        KeyCabinetReceiver.queryBatchBoxState(this, Constants.BOX_ID_ARRAY, this);
    }

    /**
     * 逐个弹开柜门
     **/
    public void intervalOpenBox() {
        KeyCabinetReceiver.openBatchBox(this, new String[]{Constants.BOX_ID_ARRAY[openBoxIndex]}, this);
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

    @Override
    public void onBoxStateBack(KeyCabinetReceiver.EnumActionType actionType, String[] boxId, boolean[] isOpen) {
        switch (actionType) {
            case QUERY:
                break;
            case OPEN_BATCH:
                if (isOpen[0]) {//弹开
                    if (openBoxIndex == Constants.BOX_ID_ARRAY.length - 1) {//最后一个门，则说明全部OK，进入下一轮校验
                        turnStep(EnumDeviceCheck.STEP_5, "设备自检通过,请关闭所有柜门", this.getString(R.string.next), null);
                        return;
                    } else {
                        openBoxIndex++;
                        intervalOpenBox();
                        return;
                    }
                } else {//没开
                    turnStep(EnumDeviceCheck.STEP_4, this.getString(R.string.device_fault), null, null);
                }
                break;
            case QUERY_BATCH:
                switch (Objects.requireNonNull(EnumDeviceCheck.getEnumByKey(step))) {
                    case STEP_4:
                        if (deviceInfoPresenter.boxListAllClose(isOpen)) {
                            intervalOpenBox();
                        } else {
                            if (queryBoxStateTimes >= 2) {
                                turnStep(EnumDeviceCheck.STEP_4, this.getString(R.string.device_fault), null, null);
                                return;
                            }
                            turnStep(EnumDeviceCheck.STEP_4, "确定柜门是否全部关闭", this.getString(R.string.yes), this.getString(R.string.no));
                            queryBoxStateTimes++;
                        }
                        break;
                    case STEP_5:
                        if (deviceInfoPresenter.boxListAllClose(isOpen)) {
                            turnStep(EnumDeviceCheck.STEP_6, this.getString(R.string.input_six_code_bind_device), null, null);
                            binding.welKeyboardLl.setVisibility(View.VISIBLE);
                            binding.welSixCodeLl.setVisibility(View.VISIBLE);
                        } else {
                            new SweetAlertDialog(WelcomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("柜门尚有未关闭的!")
                                    .setContentText("请关闭所有柜门，再点下一步")
                                    .setConfirmText(this.getString(R.string.next))
                                    .showCancelButton(false)
                                    .setConfirmClickListener(sDialog -> {
                                        sDialog.cancel();
                                        judgeBoxAllClose();
                                    }).show();
                        }
                        break;
                }

                break;
            default:
                break;
        }
    }

    /**
     * 长连接结果
     */
    @Override
    public void onConnectState(StompUtil.EnumConnectState enumConnectState) {
        if (enumConnectState == StompUtil.EnumConnectState.CONNECT) {//连接成功进入Main界面
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        } else {//连接失败，1新设备继续连接，2旧设备重述6位码，重新绑定
            switch (deviceBindState) {
                case NEW:
                    turnStep(EnumDeviceCheck.STEP_7, "与后台连接失败,请联系后重试", this.getString(R.string.retry), null);
                    binding.welKeyboardLl.setVisibility(View.GONE);
                    binding.welSixCodeLl.setVisibility(View.GONE);
                    break;
                case OLD:
                    turnStep(EnumDeviceCheck.STEP_6, "设备账户信息已失效，请重新绑定", null, null);
                    binding.welKeyboardLl.setVisibility(View.VISIBLE);
                    binding.welSixCodeLl.setVisibility(View.VISIBLE);
                    deviceBindState = EnumDeviceBindState.NEW;
                    break;
                default:
                    break;
            }
        }

    }


    enum EnumDeviceCheck {
        STEP_1(1, "网络自检"),
        STEP_2(2, "后台联通自检"),
        STEP_3(3, "设备已绑定过自检"),
        STEP_4(4, "设备柜门全关自检"),
        STEP_5(5, "设备全部弹开"),
        STEP_6(6, "关闭柜门输入6位码绑定"),
        STEP_7(7, "建立STOMP连接");
        private int key;
        private String value;

        EnumDeviceCheck(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public static EnumDeviceCheck getEnumByKey(int key) {
            for (EnumDeviceCheck enumDeviceCheck : EnumDeviceCheck.values()) {
                if (enumDeviceCheck.key == key) {
                    return enumDeviceCheck;
                }
            }

            return null;
        }

        public static EnumDeviceCheck getEnumByValue(String value) {
            if (value == null || value.equals("")) {
                return null;
            }

            for (EnumDeviceCheck enumBoxState : EnumDeviceCheck.values()) {
                if (enumBoxState.value.equals(value)) {
                    return enumBoxState;
                }
            }

            return null;
        }

    }

    enum EnumDeviceBindState {
        NEW, OLD
    }

    private void turnStep(EnumDeviceCheck enumDeviceCheck, String hintMessage, String btnMessage, String cancelMessage) {
        this.step = enumDeviceCheck.key;
        this.hintMessage = hintMessage;
        this.btnMessage = btnMessage;
        this.cancelMessage = cancelMessage;
        this.setData();
    }


}
