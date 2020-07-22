package com.huawei.jams.testautostart.view.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.huawei.jams.testautostart.BaseApp;
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
import com.huawei.jams.testautostart.utils.SoundPoolUtil;
import com.huawei.jams.testautostart.utils.StompUtil;
import com.huawei.jams.testautostart.view.inter.IAdviseView;
import com.huawei.jams.testautostart.view.inter.IAppInfoView;
import com.huawei.jams.testautostart.view.inter.IDeviceInfoView;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yxytech.parkingcloud.baselibrary.dialog.DialogUtils;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.*;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements IAdviseView, IAppInfoView, IDeviceInfoView, KeyCabinetReceiver.BoxStateListener, DeviceInfoPresenter.TimeOperator, DeviceInfoPresenter.AdvicePlayState {
    private static final String TAG = MainActivity.class.getName();
    private ActivityMainBinding binding;

    /***输入6位开箱码**/
    private String inputCode = "";

    private IDeviceInfoPresenter deviceInfoPresenter;

    private IAppInfoPresenter appInfoPresenter;

    private IAdvisePresenter advisePresenter;
    private Timer patrolTimer = new Timer();//轮巡队列
    private DeviceInfoPresenter.TimeBoxStateTask timeBoxStateTask;//巡检柜门状态任务
    private DeviceInfoPresenter.TimeAdviseCountDownTask timeAdviseTask;//巡检超时未操作
    private DeviceInfoPresenter.TimeAdvisePlayTask timeAdvisePlayTask;//巡检广告播放状态
    private StompUtil.StompConnectListener stompConnectListener;

    //网络超时8秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViews();
        initNetData();
        initData();
        patrolTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                deviceInfoPresenter.openBox("123456");
                deviceInfoPresenter.uploadBoxState(DeviceInfo.EnumBoxState.OPEN.getKey());
            }
        }, 0L, 10L);
    }

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

    @Override
    protected void initViews() {
        binding.mainDeviceNameTv.setText(PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.NAME)+"测试多多多多多多多多多多");
        deviceInfoPresenter = new DeviceInfoPresenter(this, this);
        appInfoPresenter = new AppInfoPresenter(this, this);
        advisePresenter = new AdvisePresenter(this, this);
        binding.setClick(v -> {
            switch (v.getId()) {
                case R.id.main_video_rl://进入输入6位码界面，倒计时30秒未操作返回广告
                    deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
                    if (null == timeAdviseTask) {
                        timeAdviseTask = new DeviceInfoPresenter.TimeAdviseCountDownTask(System.currentTimeMillis(), this);
                        patrolTimer.schedule(timeAdviseTask, 0, Constants.DELAY_ADVISE_MILL_SECOND);
                    }
                    hideAdvise();
                case R.id.main_code_delete_tv://删除前一位
                    decreaseInputCode();
                    break;
                case R.id.main_code_ok_tv:
                    SoundPoolUtil.getInstance().play(this, R.raw.msc_input_click);
                    if (inputCode.length() == 6) {
                        deviceInfoPresenter.openBox(inputCode);
                    } else {
                        //提示码位数不够
                        ToastUtil.showInCenter(this, this.getString(R.string.six_code_not_enough));
                    }
                    break;
                default:
                    addInputCode(((TextView) v).getText().toString());
                    break;
            }
            if (null != timeAdviseTask) {
                timeAdviseTask.setStartTime(System.currentTimeMillis());
            }

        });
        Glide.with(this).load(R.mipmap.gif_open_box).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(binding.mainOpenClickIv);//加载gif动画

    }

    private void initNetData() {
        stompConnectListener = enumConnectState -> {
            LogUtil.d(TAG, Thread.currentThread().getName() + ",stomp Connect response:" + enumConnectState);
            switch (enumConnectState) {
                case CLOSE:
                    if (null == binding.mainDialogAnimIv.getTag() || (int) binding.mainDialogAnimIv.getTag() != R.mipmap.bg_hint_net_work_error) {//当前的界面没展示网络异常
                        stopPatrolAdvTimeOut();
                        hideAdvise();
                        startAnim(R.mipmap.bg_hint_net_work_error);
                    }
                    break;
                case CONNECT:
                    initTopic();
                    showAdvise();
                    break;
            }

        };
        StompUtil.setConnectListener(stompConnectListener);
        initTopic();
        patrolTimer.schedule(new DeviceInfoPresenter.TimeConnectTask(), 0, Constants.PATROL_NET_INTERVAL_MILL_SECOND);//全程巡检网络
    }

    /**
     * 初始化广告播放
     */
    private void initData() {
        Advise lastAdvise = SQLite.select().from(Advise.class).orderBy(Advise_Table.adv_version, false).limit(1).querySingle();//倒数第一个广告
        if (lastAdvise != null && StrUtil.isNotBlank(lastAdvise.getFilePath())) {
            String path = lastAdvise.getFilePath();//广告路径
            binding.mainVideoRl.setVisibility(View.VISIBLE);
            binding.mainAdviseVideo.setVideoPath(path);
            binding.mainAdviseVideo.start();//播放
            binding.mainAdviseVideo.setOnCompletionListener(mp -> {//循环播放
                //binding.mainAdviseVideo.setVideoPath(path);
                //或 //mVideoView.setVideoPath(Uri.parse(_filePath));
                binding.mainAdviseVideo.start();
            });
        }
    }

    /**
     * 输入6位密码
     **/
    private void addInputCode(String addCode) {
        SoundPoolUtil.getInstance().play(this, R.raw.msc_input_click);
        if (inputCode.length() < 6) {
            inputCode = inputCode + addCode;
            deviceInfoPresenter.refreshMainCode2View(binding, inputCode);
        }

    }

    /**
     * 删除6位密码一位
     **/
    private void decreaseInputCode() {
        SoundPoolUtil.getInstance().play(this, R.raw.msc_input_click);
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
        releaseResource();
    }

    @Override
    public void onTopicAppInfoSuccess(String url, String newVer) {
        appInfoPresenter.downloadApp(url, newVer);
    }

    @Override
    public void onTopicAppInfoFail(String reason) {
    }

    @Override
    public void onDownloadAppSuccess(String filePath) {
        //自动替换安装 需要判断正在广告状态无人操作时才更新
        timeAdvisePlayTask = new DeviceInfoPresenter.TimeAdvisePlayTask(filePath, MainActivity.this);
        timeAdvisePlayTask.setPlayState(binding.mainAdviseVideo.isPlaying());
        patrolTimer.schedule(timeAdvisePlayTask, 0, Constants.DELAY_ADVISE_MILL_SECOND);

    }

    @Override
    public void onDownloadAppFail(String reason) {

    }

    @Override
    public void onTopicAdviseSuccess(String url, String newVer) {
        advisePresenter.downloadAdvise(url, newVer);
        //订阅的广告推送过来下载广告,数据库存储，并替换
        //Advise.Builder.anAdvise().advDate(new Date()).build().save
    }

    @Override
    public void onTopicAdviseFail(String reason) {

    }

    @Override
    public void onDownloadAdviseSuccess(String filePath) {
        //播放
        if (!TextUtils.isEmpty(filePath)) {
            binding.mainAdviseVideo.setVideoPath(filePath);
            binding.mainAdviseVideo.start();//播放
        }
    }

    @Override
    public void onDownloadAdviseFail(String reason) {

    }

    @Override
    public void onSendOpenBoxSuccess() {
        //send成功等待返回
    }

    @Override
    public void onSendOpenBoxFail(String reason) {
        ToastUtil.showInCenter(this, "打开柜门失败");
        deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
    }

    @Override
    public void onReceiveOpenBoxSuccess(String boxId) {
        DeviceInfoPresenter.EnumBoxConvert enumBoxConvert = DeviceInfoPresenter.EnumBoxConvert.getEnumByKey(boxId);
        if (null != enumBoxConvert) {
            stopPatrolAdvTimeOut();
            KeyCabinetReceiver.getInstance().openBatchBox(MainActivity.this, new String[]{enumBoxConvert.getValue()}, this);
        } else {
            ToastUtil.showInCenter(this, this.getString(R.string.back_server_box_num_error));
            deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
        }

    }

    @Override
    public void onReceiveOpenBoxFail(String reason) {
        //后台返回打开失败
        ToastUtil.showInCenter(this, this.getString(R.string.back_server_password_invalid_retry));
        deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
    }

    @Override
    public void onSendBoxStateSuccess() {
        //send成功等待返回
    }

    @Override
    public void onSendBoxStateFail(String reason) {
    }

    @Override
    public void onReceiveBoxStateSuccess(int state) {
    }

    @Override
    public void onReceiveBoxStateFail(String reason) {
        ToastUtil.showInCenter(this, this.getString(R.string.back_server_box_state_invalid));
    }


    /**
     * 操作柜门的回调
     **/
    @Override
    public void onBoxStateBack(KeyCabinetReceiver.EnumActionType enumActionType, String[] boxId, boolean[] isOpen) {
        switch (enumActionType) {
            case OPEN_BATCH:
                if (!isOpen[0]) {//打开柜门失败
                    releaseResource();
                    startAnim(R.mipmap.bg_hint_device_error);
                } else {//打开柜门成功,上传状态
                    deviceInfoPresenter.uploadBoxState(DeviceInfo.EnumBoxState.OPEN.getKey());
                    startAnim(R.mipmap.bg_hint_open_success);
                    playMusic(R.raw.msc_box_open);
                    if (!NetworkUtils.isConnected()) {
                        startAnim(R.mipmap.bg_hint_net_work_error);
                    }
                    timeBoxStateTask = new DeviceInfoPresenter.TimeBoxStateTask(MainActivity.this, boxId, this);
                    patrolTimer.schedule(timeBoxStateTask, 0, Constants.PATROL_INTERVAL_MILL_SECOND);
                }
                break;
            case QUERY_BATCH:
                if (!isOpen[0]) {//查看柜门已关
                    //上报
                    deviceInfoPresenter.uploadBoxState(DeviceInfo.EnumBoxState.CLOSE.getKey());
                    timeBoxStateTask.cancel();//关闭查询状态的轮巡
                    playMusic(R.raw.msc_thank_use);
                    if (NetworkUtils.isConnected()) {
                        patrolTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(() -> showAdvise());
                            }
                        }, Constants.DELAY_ADVISE_MILL_SECOND);
                    }

                }
                break;
        }
    }

    /**
     * 播放动画
     */
    private void startAnim(int resId) {
        binding.mainDialogAnimIv.setTag(resId);
        binding.mainDialogAnimIv.setVisibility(View.VISIBLE);
        binding.mainDialogAnimIv.setBackgroundResource(resId);
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);//设置属性值
        setAnim(animator, View.VISIBLE);
    }

    /**
     * 关闭动画
     **/
    private void closeAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);//设置属性值
        setAnim(animator, View.GONE);
    }

    /**
     * 播放音乐
     *
     * @param rawId 资源ID
     *              如果柜门已关闭，
     *              延迟Constants#DELAY_ADVISE_MILL_SECOND 关闭顶部图片，展示并播放广告
     */
    private void playMusic(int rawId) {
        //直接创建，不需要设置setDataSource
        SoundPoolUtil.getInstance().play(this, rawId);
    }

    private void setAnim(ValueAnimator animator, int visible) {
        animator.setTarget(binding.mainDialogAnimIv);//设置操作对象
        animator.setDuration(Constants.ANIMA_DURATION_MILL_SECOND).start();//动画开始
        animator.addUpdateListener(animation -> {
            binding.mainDialogAnimIv.setScaleY((Float) animation.getAnimatedValue());//设置Y轴上的变化
            binding.mainDialogAnimIv.setScaleX((Float) animation.getAnimatedValue());//设置X轴上的变化
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (visible == View.GONE) {
                    binding.mainDialogAnimIv.setTag(-1);
                    binding.mainDialogAnimIv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }

    private void initTopic() {
        appInfoPresenter.topicAppInfo();
        advisePresenter.topicAdviseInfo();
        deviceInfoPresenter.topicOpenBox();
        deviceInfoPresenter.topicUploadBoxState();
    }


    @Override
    public void timeOut() {//超时界面没人操作，则回到广告
        stopPatrolAdvTimeOut();
        runOnUiThread(this::showAdvise);
    }

    /**
     * 等到广告出现，再开始更新安装APP
     **/
    @Override
    public void isPlaying(String filePath, boolean isPlaying) {
        if (isPlaying) {
            timeAdvisePlayTask.cancel();
            boolean installRes = PackageUtils.clientInstall(filePath);
            //ShellUtils.CommandResult commandResult = ShellUtils.execCmd("pm install -r" + filePath, true);
            LogUtil.d(TAG, "静默安装结果:" + installRes);
            //LogUtil.d(TAG, "静默安装结果:" + commandResult.toString());
        } else {
            timeAdvisePlayTask.setPlayState(binding.mainAdviseVideo.isPlaying());
        }
    }

    private void showAdvise() {
        closeAnim();
        binding.mainVideoRl.setVisibility(View.VISIBLE);
        binding.mainAdviseVideo.start();

    }

    private void hideAdvise() {
        binding.mainVideoRl.setVisibility(View.GONE);
        binding.mainAdviseVideo.pause();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            releaseResource();
            AppManager.getAppManager().AppExit();
            return true;
        }
        return false;
    }

    private void releaseResource() {
        if (null != patrolTimer) {
            patrolTimer.cancel();
            patrolTimer = null;
        }
        if (null != stompConnectListener) {
            StompUtil.removeConnectListener(stompConnectListener);
        }
        StompUtil.disconnect();
    }

    private void stopPatrolAdvTimeOut() {
        if (null != timeAdviseTask) {
            timeAdviseTask.cancel();
            timeAdviseTask = null;
        }
    }


}
