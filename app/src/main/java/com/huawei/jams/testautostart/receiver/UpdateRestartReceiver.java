package com.huawei.jams.testautostart.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.huawei.jams.testautostart.BuildConfig;
import com.huawei.jams.testautostart.presenter.impl.AppInfoPresenter;
import com.huawei.jams.testautostart.presenter.inter.IAppInfoPresenter;
import com.huawei.jams.testautostart.view.activity.WelcomeActivity;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

public class UpdateRestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.PACKAGE_REPLACED".equals(intent.getAction())) {
            LogUtil.d(UpdateRestartReceiver.class.getName(), "已升级到新版本,重新启动app:" + BuildConfig.VERSION_NAME);
            Intent intent2 = new Intent(context, WelcomeActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
            LogUtil.d(UpdateRestartReceiver.class.getName(), "已启动新版本... ...");
            IAppInfoPresenter appInfoPresenter = new AppInfoPresenter(null, null);
            appInfoPresenter.deleteOldApp();

        }
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
            System.out.println("安装了:" + packageName + "包名的程序");
        }
        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString();
            System.out.println("卸载了:" + packageName + "包名的程序");

        }
    }
}
