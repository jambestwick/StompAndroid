package com.huawei.jams.testautostart.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huawei.jams.testautostart.view.activity.MainActivity;
import com.huawei.jams.testautostart.view.activity.WelcomeActivity;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

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
            LogUtil.d(TAG, Thread.currentThread().getName() + ",接收到系统启动的广播......");
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}