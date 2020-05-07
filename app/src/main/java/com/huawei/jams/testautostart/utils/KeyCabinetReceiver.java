package com.huawei.jams.testautostart.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.ToastUtil;

public class KeyCabinetReceiver extends BroadcastReceiver {
    private static final String TAG = KeyCabinetReceiver.class.getName();

    /***
     * 参数说明:
     * boxId  String类型  格口编号,一位大写字母+两位数字格式
     *
     * ****/
    public static boolean openBox(Context context, String boxId) {
        try {
            Intent intent = new Intent("android.intent.action.hal.iocontroller.open");
            //String boxId = "A01";
            intent.putExtra("boxid", boxId);
            context.sendBroadcast(intent);
            LogUtil.d(TAG, "箱门:" + boxId + ",打开操作完成");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "箱门:" + boxId + ",打开操作异常:" + Log.getStackTraceString(e));
        }
        return false;
    }

    public static void queryBoxState(Context context, String boxId) {
        Intent intent = new Intent("android.intent.action.hal.iocontroller.query");
        //String boxId = "A01";
        intent.putExtra("boxid", boxId);
        LogUtil.d(TAG, "箱门:" + boxId + ",查询操作完成");
        context.sendBroadcast(intent);
        LogUtil.d(TAG, "箱门:" + boxId + ",查询操作完成");
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        ToastUtil.showInCenter(context, intent.toString());
        if (intent.getAction().equals("android.intent.action.hal.iocontroller.querydata")) {
            String boxId = intent.getExtras().getString("boxid");
            LogUtil.d(TAG, "箱门:" + boxId + ",返回查询操作完成");
            boolean isOpened = intent.getExtras().getBoolean("isopened");
            boolean isStoraged = intent.getExtras().getBoolean("isstoraged");
            LogUtil.d(TAG, "箱门:box" + boxId + "box状态" + isOpened + ",isStoraged:" + isStoraged);
            // TODO ...
        }
    }
}
