package com.huawei.jams.testautostart.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huawei.jams.testautostart.BuildConfig;
import com.huawei.jams.testautostart.utils.Constants;
import com.huawei.jams.testautostart.view.activity.MainActivity;
import com.huawei.jams.testautostart.view.activity.WelcomeActivity;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.NetworkUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>文件描述：系统开机自启动<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/4/15<p>
 * <p>更新时间：2020/4/15<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class SystemAutoStartReceiver extends BroadcastReceiver {
    public static final String TAG = SystemAutoStartReceiver.class.getName();

    public SystemAutoStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //监听系统启动的广播接收者
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            LogUtil.d(TAG, Thread.currentThread().getName() + ",接收到系统启动的广播......"+ BuildConfig.VERSION_NAME);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {//等到网路连接上以后再进行启动APP
                @Override
                public void run() {
                    if (NetworkUtils.isConnected()) {
                        Intent i = new Intent(context, WelcomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                        timer.cancel();
                    }
                }
            }, 0, Constants.PATROL_NET_INTERVAL_MILL_SECOND);

        }
    }
}