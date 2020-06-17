package com.huawei.jams.testautostart.view.activity;

import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.huawei.jams.testautostart.R;
import com.huawei.jams.testautostart.databinding.ActivityMainBinding;
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
import com.yxytech.parkingcloud.baselibrary.dialog.DialogUtils;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.PackageUtils;
import com.yxytech.parkingcloud.baselibrary.utils.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements IAdviseView, IAppInfoView, IDeviceInfoView, KeyCabinetReceiver.BoxStateListener, DeviceInfoPresenter.TimeOperator {
    private static final String TAG = MainActivity.class.getName();
    private ActivityMainBinding binding;

    /***输入6位开箱码**/
    private String inputCode = "";

    private IDeviceInfoPresenter deviceInfoPresenter;

    private IAppInfoPresenter appInfoPresenter;

    private IAdvisePresenter advisePresenter;
    private Timer patrolTimer = new Timer();//巡检柜门状态任务Timer
    private DeviceInfoPresenter.TimeCountTask timeCountTask;//巡检任务
    private DeviceInfoPresenter.TimeAdviseCountDownTask timeAdviseTask;
    private DialogUtils dialogUtils;//发送成功的等待
    private StompUtil.StompConnectListener stompConnectListener;

    //网络超时8秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViews();
        initNetData();
        initData();
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
    private void initViews() {
        deviceInfoPresenter = new DeviceInfoPresenter(this, this);
        appInfoPresenter = new AppInfoPresenter(this, this);
        advisePresenter = new AdvisePresenter(this, this);
        binding.setClick(v -> {
            if (null != timeAdviseTask) {
                timeAdviseTask.setStartTime(System.currentTimeMillis());
            }
            switch (v.getId()) {
                case R.id.main_video_rl://进入输入6位码界面，30秒未操作返回广告
                    timeAdviseTask = new DeviceInfoPresenter.TimeAdviseCountDownTask(System.currentTimeMillis(), this);
                    patrolTimer.schedule(timeAdviseTask, 0, Constants.DELAY_ADVISE_MILL_SECOND);
                    binding.mainVideoRl.setVisibility(View.GONE);
                    if (binding.mainAdviseVideo.isPlaying()) {
                        binding.mainAdviseVideo.pause();
                    }
                    deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
                case R.id.main_code_delete_tv://删除前一位
                    decreaseInputCode();
                    break;
                case R.id.main_code_ok_tv:
                    SoundPoolUtil.getInstance().play(this, R.raw.msc_input_click);
                    if (inputCode.length() == 6) {
                        //deviceInfoPresenter.openBox(inputCode);
                        String[] box = new String[1];
                        if (inputCode.charAt(5) == '9' || inputCode.charAt(5) == '0') {
                            ToastUtil.showToast(this, "末尾输入有误，请输入1-8之间");
                            break;
                        } else if (inputCode.charAt(5) == '8') {
                            box[0] = "Z99";
                        } else {
                            box[0] = "Z0" + inputCode.charAt(5);
                        }
                        KeyCabinetReceiver.openBatchBox(this, box, this);
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
        Glide.with(this).load(R.mipmap.gif_open_box).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(binding.mainOpenClickIv);

    }

    private void initNetData() {
        StompUtil.getInstance().setContext(this);
        stompConnectListener = enumConnectState -> {
            LogUtil.d(TAG, "stomp Connect response" + enumConnectState);
            switch (enumConnectState) {
                case CLOSE:
                    if (binding.mainDialogAnimIv.getVisibility() != View.VISIBLE) {
                        if (binding.mainAdviseVideo.isPlaying()) {
                            binding.mainAdviseVideo.pause();
                        }
                        startAnim(R.mipmap.bg_hint_net_work_error);
                    }
                    break;
                case CONNECT:
                    if (null != dialogUtils) {
                        dialogUtils.dismissProgress();
                    }
                    if (binding.mainDialogAnimIv.getVisibility() == View.VISIBLE) {
                        closeAnim();
                        if (!binding.mainAdviseVideo.isPlaying()) {
                            binding.mainAdviseVideo.start();
                        }
                    }
                    initTopic();
                    break;
            }
        };
        StompUtil.getInstance().setConnectListener(stompConnectListener);
        initTopic();
    }

    /**
     * 初始化广告播放
     */
    private void initData() {
//        Advise lastAdvise = SQLite.select().from(Advise.class).orderBy(Advise_Table.adv_version, false).limit(1).querySingle();//倒数第一个广告
//        if (lastAdvise != null && StrUtil.isNotBlank(lastAdvise.getFilePath())) {
//            String path = lastAdvise.getFilePath();//广告路径
//            binding.mainVideoRl.setVisibility(View.VISIBLE);
//            binding.mainAdviseVideo.setVideoPath(path);
//            binding.mainAdviseVideo.start();//播放
//            binding.mainAdviseVideo.setOnCompletionListener(mp -> {//循环播放
//                binding.mainAdviseVideo.setVideoPath(path);
//                //或 //mVideoView.setVideoPath(Uri.parse(_filePath));
//                binding.mainAdviseVideo.start();
//            });
//        }
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
        //自动替换安装
        PackageUtils.installApk(this, filePath);
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
        //解压并播放
        binding.mainAdviseVideo.setVideoPath(filePath);
        binding.mainAdviseVideo.start();//播放

    }

    @Override
    public void onDownloadAdviseFail(String reason) {

    }

    @Override
    public void onSendOpenBoxSuccess() {
        //send成功等待返回
        dialogUtils = new DialogUtils();
        dialogUtils.showProgress(this);
    }

    @Override
    public void onSendOpenBoxFail(String reason) {
        startAnim(R.mipmap.bg_hint_net_work_error);
    }

    @Override
    public void onReceiveOpenBoxSuccess(String boxId) {
        timeAdviseTask.cancel();
        if (null != dialogUtils) {
            dialogUtils.dismissProgress();
        }
        KeyCabinetReceiver.openBatchBox(this, new String[]{boxId}, this);
    }

    @Override
    public void onReceiveOpenBoxFail(String reason) {
        //后台返回打开失败
        //timeAdviseTask.cancel();
        if (null != dialogUtils) {
            dialogUtils.dismissProgress();
        }
        ToastUtil.showInCenter(this, this.getString(R.string.back_server_password_invalid_retry));
        deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
    }

    @Override
    public void onSendBoxStateSuccess() {
        //send成功等待返回
        dialogUtils = new DialogUtils();
        dialogUtils.showProgress(this);
    }

    @Override
    public void onSendBoxStateFail(String reason) {
        startAnim(R.mipmap.bg_hint_net_work_error);
        timeAdviseTask = new DeviceInfoPresenter.TimeAdviseCountDownTask(System.currentTimeMillis(), this);
        patrolTimer.schedule(timeAdviseTask, 0, Constants.DELAY_ADVISE_MILL_SECOND);
    }

    @Override
    public void onReceiveBoxStateSuccess(int state) {
        if (null != dialogUtils) {
            dialogUtils.dismissProgress();
        }
    }

    @Override
    public void onReceiveBoxStateFail(String reason) {
        if (null != dialogUtils) {
            dialogUtils.dismissProgress();
        }
        ToastUtil.showInCenter(this, this.getString(R.string.back_server_box_state_invalid));
    }


    /**
     * 操作柜门的回调
     **/
    @Override
    public void onBoxStateBack(KeyCabinetReceiver.EnumActionType enumActionType, String[] boxId, boolean[] isOpen) {
        switch (enumActionType) {
            case OPEN_BATCH:
                if (!isOpen[0]) {//打开失败
                    StompUtil.getInstance().removeConnectListener(stompConnectListener);
                    startAnim(R.mipmap.bg_hint_device_error);
                } else {//打开成功,上传状态
                    startAnim(R.mipmap.bg_hint_open_success);
                    playMusic(R.raw.msc_box_open, DeviceInfo.EnumBoxState.OPEN);
                    //deviceInfoPresenter.uploadBoxState(DeviceInfo.EnumBoxState.OPEN.getKey());
                    timeCountTask = new DeviceInfoPresenter.TimeCountTask(boxId, this);
                    deviceInfoPresenter.patrolBoxState(patrolTimer, timeCountTask);
                }
                break;
            case QUERY_BATCH:
                if (!isOpen[0]) {//查看柜门已关
                    //上报
                    //deviceInfoPresenter.uploadBoxState(DeviceInfo.EnumBoxState.CLOSE.getKey());
                    timeCountTask.cancel();
                    playMusic(R.raw.msc_thank_use, DeviceInfo.EnumBoxState.CLOSE);
                    deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
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
        setAnim(animator);
    }

    /**
     * 关闭动画
     **/
    private void closeAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);//设置属性值
        setAnim(animator);
        //binding.mainDialogAnimIv.setBackgroundResource(0);
        //binding.mainDialogAnimIv.setVisibility(View.GONE);
    }

    /**
     * 播放音乐
     * 如果柜门已关闭，延迟Constants#DELAY_ADVISE_MILL_SECOND 关闭顶部图片，展示并播放广告
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
                        runOnUiThread(() -> {
                            closeAnim();
//                            binding.mainVideoRl.setVisibility(View.VISIBLE);
//                            binding.mainAdviseVideo.start();
                        });
                    }
                }, Constants.DELAY_ADVISE_MILL_SECOND);
            }
        });
    }

    private void setAnim(ValueAnimator animator) {
        animator.setTarget(binding.mainDialogAnimIv);//设置操作对象
        animator.setDuration(Constants.ANIMA_DURATION_MILL_SECOND).start();//动画开始
        animator.addUpdateListener(animation -> {
            binding.mainDialogAnimIv.setScaleY((Float) animation.getAnimatedValue());//设置Y轴上的变化
            binding.mainDialogAnimIv.setScaleX((Float) animation.getAnimatedValue());//设置X轴上的变化
        });
    }

    private void initTopic() {
        appInfoPresenter.topicAppInfo();
        advisePresenter.topicAdviseInfo();
        deviceInfoPresenter.topicOpenBox();
        deviceInfoPresenter.topicUploadBoxState();
    }


    @Override
    public void timeOut() {//超时界面没人操作，则进入广告
        timeAdviseTask.cancel();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeAnim();
                if (null != dialogUtils) {
                    dialogUtils.dismissProgress();
                }
                deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
                binding.mainVideoRl.setVisibility(View.VISIBLE);
                binding.mainAdviseVideo.start();
            }
        });
    }

}
