package com.huawei.jams.testautostart.view.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.huawei.jams.testautostart.BaseApp;
import com.huawei.jams.testautostart.BuildConfig;
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
import com.huawei.jams.testautostart.utils.BoxUtil;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.utils.KeyCabinetReceiver;
import com.huawei.jams.testautostart.utils.SoundPoolUtil;
import com.huawei.jams.testautostart.utils.StompUtil;
import com.huawei.jams.testautostart.view.inter.IAdviseView;
import com.huawei.jams.testautostart.view.inter.IAppInfoView;
import com.huawei.jams.testautostart.view.inter.IDeviceInfoView;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yxytech.parkingcloud.baselibrary.ui.BaseActivity;
import com.yxytech.parkingcloud.baselibrary.utils.AppManager;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.NetworkUtils;
import com.yxytech.parkingcloud.baselibrary.utils.PackageUtils;
import com.yxytech.parkingcloud.baselibrary.utils.PreferencesManager;
import com.yxytech.parkingcloud.baselibrary.utils.StrUtil;
import com.yxytech.parkingcloud.baselibrary.utils.TimeUtil;
import com.yxytech.parkingcloud.baselibrary.utils.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity implements IAdviseView, IAppInfoView, IDeviceInfoView, KeyCabinetReceiver.BoxStateListener, DeviceInfoPresenter.TimeOperator, DeviceInfoPresenter.AdvicePlayState {
    private static final String TAG = MainActivity.class.getName();
    private ActivityMainBinding binding;

    /***输入6位开箱码**/
    private String inputCode = "";

    public IDeviceInfoPresenter deviceInfoPresenter;

    private IAppInfoPresenter appInfoPresenter;

    private IAdvisePresenter advisePresenter;
    private ScheduledExecutorService patrolTimer = Executors.newScheduledThreadPool(20);//轮巡队列
    private Timer scheduleTimer = new Timer();
    private DeviceInfoPresenter.TimeBoxStateTask timeBoxStateTask;//巡检柜门状态任务
    private DeviceInfoPresenter.TimeArrayBoxStateTask timeArrayBoxStateTask;
    private DeviceInfoPresenter.TimeAdviseCountDownTask timeAdviseTask;//巡检超时未操作
    private DeviceInfoPresenter.TimeAdvisePlayTask timeAdvisePlayTask;//巡检广告播放状态
    private StompUtil.StompConnectListener stompConnectListener;
    private long clickOKMillTime = System.currentTimeMillis();
    private boolean isOpen = false;
    private BroadcastReceiver mThreeClockReceiver;
    private long sendMsgMillTime = System.currentTimeMillis();
    private boolean isReceiver123 = false;
    private boolean isFirstQueryBoxState = true;

    private KeyCabinetReceiver receiver;


    //网络超时8秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        LogUtil.d(TAG, Thread.currentThread().getName() + ",onCreate");
        registerReceiver();
        initViews();
        initNetData();
        initData();
        initReceiver();
        restartApp();
    }

    @Override
    protected void initViews() {
        binding.mainAppVersionTv.setText(BuildConfig.VERSION_NAME);
        binding.mainDeviceNameTv.setText(PreferencesManager.getInstance(BaseApp.getAppContext()).get(Constants.NAME));
        deviceInfoPresenter = new DeviceInfoPresenter(this, this);
        appInfoPresenter = new AppInfoPresenter(this, this);
        advisePresenter = new AdvisePresenter(this, this);
        binding.setClick(v -> {
            switch (v.getId()) {
                case R.id.main_video_rl://进入输入6位码界面，倒计时30秒未操作返回广告
                    deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
                    if (null == timeAdviseTask) {
                        timeAdviseTask = new DeviceInfoPresenter.TimeAdviseCountDownTask(System.currentTimeMillis(), this);
                        scheduleTimer.schedule(timeAdviseTask, Constants.ZERO_SECOND, Constants.DELAY_ADVISE_MILL_SECOND);
                    }
                    hideAdvise();
                case R.id.main_code_delete_tv://删除前一位
                    decreaseInputCode();
                    break;
                case R.id.main_code_ok_tv:
                    SoundPoolUtil.getInstance().play(this, R.raw.msc_input_click);
                    if (inputCode.length() == 6) {
                        //判断5秒内不允许再次点击
                        if (System.currentTimeMillis() - clickOKMillTime < Constants.NOT_CLICK_FIVE_MILL_SECOND) {
                            LogUtil.d(TAG, Thread.currentThread().getName() + ",点击间隔未超过5秒，不予处理");
                        } else {
                            deviceInfoPresenter.openBox(inputCode);
                            //stopPatrolAdvTimeOut();
                        }
                        clickOKMillTime = System.currentTimeMillis();
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
                //LogUtil.d(TAG, Thread.currentThread().getName() + ",点击时间:" + TimeUtil.long2String(System.currentTimeMillis(), TimeUtil.DEFAULT_MILL_TIME_FORMAT) + "点击对象:" + v.toString());
                timeAdviseTask.setStartTime(System.currentTimeMillis());
            }

        });
        clickAble(false);
        Glide.with(this).load(R.mipmap.gif_open_box).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(binding.mainOpenClickIv);//加载gif动画

    }

    private void initNetData() {
        stompConnectListener = enumConnectState -> {
            LogUtil.d(TAG, Thread.currentThread().getName() + ",stomp Connect response:" + enumConnectState);
            switch (enumConnectState) {
                case CLOSE:
                    if (null == binding.mainDialogAnimIv.getTag() || (int) binding.mainDialogAnimIv.getTag() != R.mipmap.bg_hint_net_work_error) {//当前的界面没展示网络异常
                        stopPatrolBoxStateTask();
                        stopPatrolArrayBoxStateTask();
                        stopPatrolAdvTimeOut();
                        hideAdvise();
                        startAnim(R.mipmap.bg_hint_net_work_error);
                        //StompUtil.getInstance().disconnect();
                        DeviceInfoPresenter.firstNetDisconnected = System.currentTimeMillis();
                    }
                    break;
                case CONNECT:
                    DeviceInfoPresenter.firstNetDisconnected = null;
                    isFirstQueryBoxState = true;
                    initTopic();
                    break;
            }

        };
        StompUtil.getInstance().setConnectListener(stompConnectListener);
        initTopic();
        scheduleTimer.scheduleAtFixedRate(DeviceInfoPresenter.TimeConnectTask.getInstance(), Constants.ZERO_SECOND, Constants.PATROL_WORK_NET_INTERVAL_MILL_SECOND);//全局不停止每30s全程巡检网络

    }

    /**
     * 初始化广告播放
     */
    private void initData() {
        Advise lastAdvise = SQLite.select().from(Advise.class).orderBy(Advise_Table.create_time, false).limit(1).querySingle();//倒数第一个广告
        if (lastAdvise != null && StrUtil.isNotBlank(lastAdvise.getFilePath())) {
            String path = lastAdvise.getFilePath();//广告路径
            binding.mainVideoRl.setVisibility(View.GONE);
            binding.mainAdviseVideo.setVideoPath(path);
            binding.mainAdviseVideo.pause();//默认先暂停
            binding.mainAdviseVideo.setOnCompletionListener(mp -> {//循环播放
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
        scheduleTimer.schedule(timeAdvisePlayTask, Constants.ZERO_SECOND, Constants.DELAY_ADVISE_MILL_SECOND);

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
            advisePresenter.deleteOldAdvise();
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
        if (boxId.equals("-1")) {
            //连接成功
            isReceiver123 = true;
            return;
        }
        DeviceInfoPresenter.EnumBoxConvert enumBoxConvert = DeviceInfoPresenter.EnumBoxConvert.getEnumByKey(boxId);
        if (null != enumBoxConvert) {
            stopPatrolAdvTimeOut();
            receiver.openBatchBox(MainActivity.this, new String[]{enumBoxConvert.getValue()});
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

    @Override
    public void onServerReceiveHeart() {
        //心跳接收成功
        //LogUtil.d(TAG, Thread.currentThread().getName() + ",连接心跳时间:" + TimeUtil.long2String(currentMillTime, TimeUtil.DEFAULT_MILL_TIME_FORMAT));
        DeviceInfoPresenter.serverHeartBeatTime = System.currentTimeMillis();

    }


    /**
     * 操作柜门的回调
     **/
    @Override
    public void onBoxStateBack(KeyCabinetReceiver.EnumActionType enumActionType, String[] boxId, boolean[] isOpen) {
        this.isOpen = isOpen[0];
        switch (enumActionType) {
            case OPEN_BATCH:
                if (!isOpen[0]) {//打开柜门失败
                    releaseResource();
                    startAnim(R.mipmap.bg_hint_device_error);
                } else {//打开柜门成功,上传状态
                    deviceInfoPresenter.uploadBoxState(DeviceInfo.EnumBoxState.OPEN.getKey());
                    startAnim(R.mipmap.bg_hint_open_success);
                    playMusic(R.raw.msc_box_open);
                    timeBoxStateTask = new DeviceInfoPresenter.TimeBoxStateTask(MainActivity.this, boxId[0], receiver);
                    scheduleTimer.schedule(timeBoxStateTask, Constants.ZERO_SECOND, Constants.PATROL_INTERVAL_MILL_SECOND);
                }
                deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
                clickAble(false);
                break;
            case QUERY:
                if (!isOpen[0]) {//查看柜门已关
                    //上报
                    deviceInfoPresenter.uploadBoxState(DeviceInfo.EnumBoxState.CLOSE.getKey());
                    stopPatrolBoxStateTask();
                    playMusic(R.raw.msc_thank_use);
                    patrolTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                showAdvise();
                                clickAble(true);
                            });
                        }
                    }, Constants.DELAY_ADVISE_MILL_SECOND, TimeUnit.MILLISECONDS);

                }
                break;
            case QUERY_BATCH:
                int boxOpenIndex = BoxUtil.boxOpenIndex(isOpen);
                binding.mainAllFrame.setVisibility(View.VISIBLE);
                if (boxOpenIndex == -1) {//都关闭
                    stopPatrolArrayBoxStateTask();
                    this.isOpen = false;
                    deviceInfoPresenter.uploadBoxState(DeviceInfo.EnumBoxState.CLOSE.getKey());
                    patrolTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                showAdvise();
                                clickAble(true);
                            });
                        }
                    }, Constants.DELAY_ADVISE_MILL_SECOND, TimeUnit.MILLISECONDS);
                } else {
                    this.isOpen = true;
                    hideAdvise();
                    if (isFirstQueryBoxState) {
                        startAnim(R.mipmap.bg_hint_open_success);
                        playMusic(R.raw.msc_box_open);
                        isFirstQueryBoxState = false;
                    }
                    deviceInfoPresenter.refreshMainCode2View(binding, inputCode = "");
                    clickAble(false);
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
        deviceInfoPresenter.topicServerHeartBeat();
        send123();
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
            LogUtil.d(TAG, Thread.currentThread().getName() + ",静默安装结果:" + installRes);
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
            patrolTimer.shutdownNow();
            patrolTimer = null;
        }
        if (null != scheduleTimer) {
            scheduleTimer.cancel();
            scheduleTimer = null;
        }
        if (null != stompConnectListener) {
            StompUtil.getInstance().removeConnectListener(stompConnectListener);
        }
        StompUtil.getInstance().disconnect();
        if (mThreeClockReceiver != null) {
            unregisterReceiver(mThreeClockReceiver);
        }
        unRegisterReceiver();
    }

    private void stopPatrolAdvTimeOut() {
        if (null != timeAdviseTask) {
            timeAdviseTask.cancel();
            timeAdviseTask = null;
        }
    }

    private void stopPatrolBoxStateTask() {
        if (null != timeBoxStateTask) {
            timeBoxStateTask.cancel();
            timeBoxStateTask = null;
        }
    }

    private void stopPatrolArrayBoxStateTask() {
        if (null != timeArrayBoxStateTask) {
            timeArrayBoxStateTask.cancel();
            timeArrayBoxStateTask = null;
        }
    }

    /**
     * 定时重启广播初始化
     */
    private void initReceiver() {
        mThreeClockReceiver = new ThreeClockReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimeUtil.ACTION_THREE_CLOCK_RESTART);
        filter.addAction(TimeUtil.ACTION_TIME_SET);
        registerReceiver(mThreeClockReceiver, filter);
    }

    //每天3点分钟重启
    private void restartApp() {
        TimeUtil.start3Clock(this);
    }

    class ThreeClockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), TimeUtil.ACTION_THREE_CLOCK_RESTART)) {
                /**重启App*/
                //重新打开app启动页
                restartAppByBoxClosed();
            } else if (TextUtils.equals(intent.getAction(), TimeUtil.ACTION_TIME_SET)) {
                /**修改时间后，重新设置定时器*/
                TimeUtil.start3Clock(context);
            }
        }
    }

    private void restartAppByBoxClosed() {
        patrolTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                while (isOpen) {
                    try {
                        Thread.sleep(Constants.RESTART_UP_MILL_SECOND);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //LogUtil.d(TAG, Thread.currentThread().getName() + ",重启前判断柜门打开/关闭(true打开/false关闭):" + isOpen);
                }
                LogUtil.d(TAG, Thread.currentThread().getName() + ",重启柜门,当前的柜门状态:" + isOpen);
                AppManager.getAppManager().restartApp(MainActivity.this);
                AppManager.getAppManager().AppExit();
            }
        }, Constants.ZERO_SECOND, TimeUnit.MILLISECONDS);
    }

    /**
     * 发送123等待响应
     **/
    public void send123() {
        TimerTask query123ResultTask = new TimerTask() {
            @Override
            public void run() {
                LogUtil.e(TAG, Thread.currentThread().getName() + ",接收到123的回复:" + isReceiver123);
                while (!isReceiver123) {
                    LogUtil.e(TAG, Thread.currentThread().getName() + ",查询,接收到123的回复:" + isReceiver123);
                    try {
                        Thread.sleep(Constants.HALF_ONE_MILL_SECOND);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (System.currentTimeMillis() - sendMsgMillTime > Constants.RESTART_UP_MILL_SECOND) {
                        //直接关闭重启APP
                        LogUtil.e(TAG, Thread.currentThread().getName() + ",超过" + Constants.RESTART_UP_MILL_SECOND + "ms,强制退出");
                        AppManager.getAppManager().restartApp(BaseApp.getAppContext());
                        AppManager.getAppManager().AppExit();
                    }
                }
                LogUtil.e(TAG, Thread.currentThread().getName() + ",接收到123的回复,退出查询123返回任务");
                timeArrayBoxStateTask = new DeviceInfoPresenter.TimeArrayBoxStateTask(MainActivity.this, receiver);
                scheduleTimer.schedule(timeArrayBoxStateTask, Constants.ZERO_SECOND, Constants.PATROL_INTERVAL_MILL_SECOND);
                isFirstQueryBoxState = true;
            }
        };
        deviceInfoPresenter.openBox("123");
        sendMsgMillTime = System.currentTimeMillis();
        patrolTimer.schedule(query123ResultTask, Constants.START_UP_MILL_SECOND, TimeUnit.MILLISECONDS);
    }

    private void clickAble(boolean flag) {
        binding.mainVideoRl.setClickable(flag);
        binding.mainCodeDeleteTv.setClickable(flag);
        binding.mainCodeOkTv.setClickable(flag);
        binding.mainCode0Tv.setClickable(flag);
        binding.mainCode1Tv.setClickable(flag);
        binding.mainCode2Tv.setClickable(flag);
        binding.mainCode3Tv.setClickable(flag);
        binding.mainCode4Tv.setClickable(flag);
        binding.mainCode5Tv.setClickable(flag);
        binding.mainCode6Tv.setClickable(flag);
        binding.mainCode7Tv.setClickable(flag);
        binding.mainCode8Tv.setClickable(flag);
        binding.mainCode9Tv.setClickable(flag);
    }


    private void registerReceiver() {
        receiver = new KeyCabinetReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.hal.iocontroller.querydata");
        intentFilter.addAction("android.intent.action.hal.iocontroller.batchopen.result");
        intentFilter.addAction("android.intent.action.hal.iocontroller.queryAllData");
        intentFilter.addAction("android.intent.action.hal.printer.supportsize.result");
        intentFilter.addAction("android.intent.action.hal.printer.result.haspaper");
        intentFilter.addAction("android.intent.action.hal.printer.result.needmore");
        intentFilter.addAction("android.intent.action.hal.printer.result.status");
        intentFilter.addAction("android.intent.action.hal.printer.error");
        intentFilter.addAction("android.intent.action.hal.barcodescanner.scandata");
        intentFilter.addAction("android.intent.action.hal.barcodescanner.error");
        intentFilter.addAction("android.intent.action.hal.iocontroller.batchopen.result");
        registerReceiver(receiver, intentFilter);
    }

    private void unRegisterReceiver() {
        if (null != receiver) {
            unregisterReceiver(receiver);
        }
    }


}
