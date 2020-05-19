package com.huawei.jams.testautostart.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.MediaController;
import android.widget.TextView;

import com.huawei.jams.testautostart.R;
import com.huawei.jams.testautostart.databinding.ActivityMainBinding;
import com.huawei.jams.testautostart.entity.DeviceInfo;
import com.huawei.jams.testautostart.presenter.impl.AdvisePresenter;
import com.huawei.jams.testautostart.presenter.impl.AppInfoPresenter;
import com.huawei.jams.testautostart.presenter.impl.DeviceInfoPresenter;
import com.huawei.jams.testautostart.presenter.inter.IAdvisePresenter;
import com.huawei.jams.testautostart.presenter.inter.IAppInfoPresenter;
import com.huawei.jams.testautostart.presenter.inter.IDeviceInfoPresenter;
import com.huawei.jams.testautostart.service.StompService;
import com.huawei.jams.testautostart.utils.KeyCabinetReceiver;
import com.huawei.jams.testautostart.view.inter.IMainView;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.ToastUtil;

public class MainActivity extends BaseActivity implements IMainView {
    private static final String TAG = MainActivity.class.getName();
    private ActivityMainBinding binding;

    /***输入6位开箱码**/
    private String inputCode = "";

    private IDeviceInfoPresenter deviceInfoPresenter;

    private IAppInfoPresenter appInfoPresenter;

    private IAdvisePresenter advisePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViews();
        initNetData();
        initData();
    }


    private void initNetData() {
//        deviceInfoPresenter.bindDevice();
//        deviceInfoPresenter.queryAlarmProp();
//        appInfoPresenter.queryAppInfo();
//        advisePresenter.queryAdviseInfo();
    }


    private void initViews() {
        deviceInfoPresenter = new DeviceInfoPresenter(this);
        appInfoPresenter = new AppInfoPresenter(this);
        advisePresenter = new AdvisePresenter(this);
        binding.setClick(v -> {
            switch (v.getId()) {
                case R.id.main_code_delete_tv://删除前一位
                    decreaseInputCode();
                    break;
                case R.id.main_code_ok_tv:

                    //用户在广告界面点击任意键，则退出广告，输入（小程序下发）6位数字码，
                    //输入完成-->
                    //1.发送6位码Stomp-->后台
                    //后台-->结果：
                    //1.1、成功返回，boxId，根据boxid开柜-->查询柜门状态-->开柜结果
                    //1.1.1 成功，同步播放‘开门成功’,和弹出全屏‘动画开柜成功’,发送stomp柜门boxid状态开启，轮巡读取柜门状态
                    //1.1.2 失败，同步播放‘设备故障’,和弹出全屏‘动画设备故障’,
                    //1.2 返回异常或超时，重发送6位码Stomp-->后台 3次，如果有成功，break；，否则，，同步播放‘网络故障’,和弹出全屏‘动画网络故障 界面含有重试按键关闭网络故障界面’,用户点击重试-->清空当前6位码-->1
                    //1.3 密码错误,提示框（非全屏）:'密码错误(确定按钮)' 点击确定关闭窗口 清空当前6位码-->1


                    //轮巡机制查询1.如果boxId关闭，stomp上报状态-->关闭,语音:"感谢你的使用!",关闭"开门成功页面"，清空6位码，弹出播放广告。
                    if (inputCode.length() == 6) {
                        deviceInfoPresenter.openBox(inputCode, 1);
                    } else {
                        //提示码位数不够
                        ToastUtil.showToast(this, "输入的位数不足");
                    }
                    break;
                default:
                    addInputCode(((TextView) v).getText().toString());
                    break;
            }

        });
    }

    private void initData(){
        String path ="";
        binding.mainAdviseVideo.setVideoPath(path);
        MediaController mediaController = new MediaController(this);
        binding.mainAdviseVideo.setMediaController(mediaController);

    }

    /**
     * 输入6位密码
     **/
    private void addInputCode(String addCode) {
        if (inputCode.length() < 6) {
            inputCode = inputCode + addCode;
            deviceInfoPresenter.refreshMainCode2View(binding, inputCode);
        }

    }

    /**
     * 删除6位密码一位
     **/
    private void decreaseInputCode() {
        if (inputCode.length() > 0) {
            inputCode = inputCode.substring(0, inputCode.length() - 1);
            deviceInfoPresenter.refreshMainCode2View(binding, inputCode);
        }
    }

    // 按照下面代码示例修改Activity的onResume方法
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, StompService.class));
    }

    @Override
    public void onQueryAppInfoSuccess(String url) {
        //下载app

    }

    @Override
    public void onQueryAppInfoFail(String reason) {
    }

    @Override
    public void onQueryAdviseSuccess(String url) {
        //下载广告
    }

    @Override
    public void onQueryAdviseFail(String reason) {

    }

    @Override
    public void onOpenBoxSuccess(String boxId) {
        KeyCabinetReceiver.openBatchBox(this, new String[]{boxId}, new KeyCabinetReceiver.OpenBoxListListener() {
            @Override
            public void onBoxStateBack(String[] boxIds, boolean[] isBatchOpen) {
                boolean hasNotOpen = false;
                for (int i = 0; i < isBatchOpen.length; i++) {
                    if (!isBatchOpen[i]) {
                        hasNotOpen = true;
                        break;
                    }
                }
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_scal);
                if (hasNotOpen) {
                    binding.mainDialogAnimIv.setImageResource(R.mipmap.bg_hint_device_error);
                    binding.mainDialogAnimIv.startAnimation(animation);
                } else {
                    binding.mainDialogAnimIv.setImageResource(R.mipmap.bg_hint_open_success);
                    binding.mainDialogAnimIv.startAnimation(animation);
                }

            }
        });
        deviceInfoPresenter.patrolBoxState(boxId, DeviceInfo.EnumBoxState.OPEN.getKey());

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
        binding.mainDialogAnimIv.setVisibility(TextView.GONE);
        //继续播放广告

    }

    @Override
    public void onUploadBoxStateFail(String reason) {

    }

    @Override
    public void onBindDeviceSuccess() {

    }

    @Override
    public void onBindDeviceFail(String reason) {

    }
}
