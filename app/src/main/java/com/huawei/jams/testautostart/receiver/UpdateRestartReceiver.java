package com.huawei.jams.testautostart.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.huawei.jams.testautostart.view.activity.WelcomeActivity;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

public class UpdateRestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")){
            LogUtil.d(UpdateRestartReceiver.class.getName(),"已升级到新版本");
            Intent intent2 = new Intent(context, WelcomeActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);

        }
    }
}
