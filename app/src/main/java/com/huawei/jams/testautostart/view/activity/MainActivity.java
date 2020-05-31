package com.huawei.jams.testautostart.view.activity;

import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.huawei.jams.testautostart.R;
import com.huawei.jams.testautostart.databinding.ActivityMainBinding;
import com.huawei.jams.testautostart.entity.Advise;
import com.huawei.jams.testautostart.entity.Advise_Table;
import com.huawei.jams.testautostart.entity.DeviceInfo;
import com.huawei.jams.testautostart.presenter.impl.AdvisePresenter;
import com.huawei.jams.testautostart.presenter.impl.AppInfoPresenter;
import com.huawei.jams.testautostart.presenter.impl.DeviceInfoPresenter;
import com.huawei.jams.testautostart.presenter.inter.IAdvisePresenter;
import com.huawei.jams.testautostart.presenter.inter.IAppInfoPresenter;
import com.huawei.jams.testautostart.presenter.inter.IDeviceInfoPresenter;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.utils.KeyCabinetReceiver;
import com.huawei.jams.testautostart.view.inter.IAdviseView;
import com.huawei.jams.testautostart.view.inter.IAppInfoView;
import com.huawei.jams.testautostart.view.inter.IDeviceInfoView;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.StrUtil;
import com.yxytech.parkingcloud.baselibrary.utils.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements IAdviseView, IAppInfoView, IDeviceInfoView, KeyCabinetReceiver.BoxStateListener {
    private static final String TAG = MainActivity.class.getName();
    private ActivityMainBinding binding;

    /***输入6位开箱码**/
    private String inputCode = "";

    private IDeviceInfoPresenter deviceInfoPresenter;

    private IAppInfoPresenter appInfoPresenter;

    private IAdvisePresenter advisePresenter;
    private Timer patrolTimer = new Timer();//巡检柜门状态任务Timer
    private DeviceInfoPresenter.TimeCountTask timeCountTask;//巡检任务

    //网络超时8秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViews();
        initNetData();
        initData();

    }


    private void initViews() {
        deviceInfoPresenter = new DeviceInfoPresenter(this, this, this);
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
                        deviceInfoPresenter.openBox(inputCode);
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
        binding.mainAdviseVideo.setOnTouchListener((v, event) -> {//点击就暂停并消失
            if (binding.mainAdviseVideo.isPlaying()) {
                binding.mainAdviseVideo.pause();
                binding.mainAdviseVideo.setVisibility(View.GONE);
            }
            return false;
        });
    }

    private void initNetData() {
        appInfoPresenter.topicAppInfo();
        advisePresenter.topicAdviseInfo();
        deviceInfoPresenter.topicOpenBox();
        deviceInfoPresenter.topicUploadBoxState();
    }

    /**
     * 初始化广告播放
     */
    private void initData() {
        Advise lastAdvise = SQLite.select().from(Advise.class).orderBy(Advise_Table.adv_version, false).limit(1).querySingle();//倒数第一个广告
        if (lastAdvise != null && StrUtil.isNotBlank(lastAdvise.getFilePath())) {
            String path = lastAdvise.getFilePath();//广告路径
            binding.mainAdviseVideo.setVisibility(View.VISIBLE);
            binding.mainAdviseVideo.setVideoPath(path);
            binding.mainAdviseVideo.start();//播放
            binding.mainAdviseVideo.setOnCompletionListener(mp -> {//循环播放
                binding.mainAdviseVideo.setVideoPath(path);
                //或 //mVideoView.setVideoPath(Uri.parse(_filePath));
                binding.mainAdviseVideo.start();
            });
        }
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
    }

    @Override
    public void onTopicAppInfoSuccess(String url) {
        //订阅的app推送过来下载app数据库存储，并更新安装

    }

    @Override
    public void onTopicAppInfoFail(String reason) {
    }

    @Override
    public void onTopicAdviseSuccess(String url) {
        //订阅的广告推送过来下载广告,数据库存储，并替换
        //Advise.Builder.anAdvise().advDate(new Date()).build().save
    }

    @Override
    public void onTopicAdviseFail(String reason) {

    }


    @Override
    public void onOpenBoxSuccess(String boxId) {
        KeyCabinetReceiver.openBatchBox(this, new String[]{boxId}, this);
    }

    @Override
    public void onOpenBoxFail(String reason) {//密码错误，请重新输入，确定 提示框（非全屏）
        //后台返回打开失败
        ToastUtil.showInCenter(this, this.getString(R.string.back_server_exception_retry));
        deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
        startAnim(R.mipmap.bg_hint_net_work_error);
    }


    @Override
    public void onUploadBoxStateSuccess() {
    }

    @Override
    public void onUploadBoxStateFail(String reason) {


    }

    @Override
    public void onBindDeviceSuccess(String account, String password) {

    }

    @Override
    public void onBindDeviceFail(String reason) {

    }


    /**
     * 操作柜门的回调
     **/
    @Override
    public void onBoxStateBack(KeyCabinetReceiver.EnumActionType enumActionType, String[] boxId, boolean[] isOpen) {
        switch (enumActionType) {
            case OPEN_BATCH:
                if (!isOpen[0]) {//打开失败
                    startAnim(R.mipmap.bg_hint_device_error);
                } else {//打开成功,上传状态
                    startAnim(R.mipmap.bg_hint_open_success);
                    playMusic(R.raw.msc_box_open, DeviceInfo.EnumBoxState.OPEN);
                    deviceInfoPresenter.uploadBoxState(boxId[0], DeviceInfo.EnumBoxState.OPEN.getKey());
                    timeCountTask = new DeviceInfoPresenter.TimeCountTask(boxId[0], this);
                    deviceInfoPresenter.patrolBoxState(patrolTimer, timeCountTask);
                }
                break;
            case QUERY_BATCH:
                if (!isOpen[0]) {//查看柜门已关
                    //上报
                    deviceInfoPresenter.uploadBoxState(boxId[0], DeviceInfo.EnumBoxState.CLOSE.getKey());
                    timeCountTask.cancel();
                    playMusic(R.raw.msc_thank_use, DeviceInfo.EnumBoxState.CLOSE);
                }

                break;
        }
    }

    /**
     * 播放动画
     */
    private void startAnim(int resId) {
        binding.mainDialogAnimIv.setVisibility(View.VISIBLE);
        binding.mainDialogAnimIv.setBackgroundResource(resId);
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);//设置属性值
        setAnima(animator);
        closeAnim();

    }

    /**
     * 关闭动画
     **/
    private void closeAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);//设置属性值
        setAnima(animator);
        binding.mainDialogAnimIv.setBackgroundResource(0);
        binding.mainDialogAnimIv.setVisibility(View.GONE);
    }

    /**
     * 播放音乐
     */
    private void playMusic(int rawId, DeviceInfo.EnumBoxState enumBoxState) {
        //直接创建，不需要设置setDataSource
        MediaPlayer mMediaPlayer = MediaPlayer.create(this, rawId);
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            if (enumBoxState == DeviceInfo.EnumBoxState.CLOSE) {
                patrolTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        closeAnim();
                        binding.mainAdviseVideo.setVisibility(View.VISIBLE);
                        binding.mainAdviseVideo.start();
                    }
                }, Constants.DELAY_ADVISE_MILL_SECOND);
            }
        });
    }

    private void setAnima(ValueAnimator animator) {
        animator.setTarget(binding.mainDialogAnimIv);//设置操作对象
        animator.setDuration(Constants.ANIMA_DURATION_MILL_SECOND).start();//动画开始
        animator.addUpdateListener(animation -> {
            binding.mainDialogAnimIv.setScaleY((Float) animation.getAnimatedValue());//设置Y轴上的变化
            binding.mainDialogAnimIv.setScaleX((Float) animation.getAnimatedValue());//设置X轴上的变化
        });
    }

}
