package com.huawei.jams.testautostart.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huawei.jams.testautostart.BaseApp;
import com.yxytech.parkingcloud.baselibrary.dialog.DialogUtils;
import com.yxytech.parkingcloud.baselibrary.http.common.ProgressUtils;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.ToastUtil;

public class KeyCabinetReceiver extends BroadcastReceiver {
    private static final String TAG = KeyCabinetReceiver.class.getName();
    private BoxStateListener boxStateListener;
    private static DialogUtils dialogUtils;
    private static KeyCabinetReceiver instance;
    private static final Object lock = new Object();

    public static KeyCabinetReceiver getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new KeyCabinetReceiver();
                }
            }
        }
        return instance;
    }


    /***
     * 参数说明:
     * boxId  String类型  格口编号,一位大写字母+两位数字格式
     * boxId 指盒子的编号主柜 Z开头Z01，Z02......
     *                  副柜 A01，B01... ...
     * ****/
//    public static boolean openBox(Context context, String boxId) {
//        try {
//            Intent intent = new Intent("android.intent.action.hal.iocontroller.open");
//            //String boxId = "A01";
//            intent.putExtra("boxid", boxId);
//            context.sendBroadcast(intent);
//            LogUtil.d(TAG, "箱门:" + boxId + ",打开操作完成");
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtil.d(TAG, "箱门:" + boxId + ",打开操作异常:" + Log.getStackTraceString(e));
//        }
//        return false;
//    }
    public void openBatchBox(Context context, String[] boxIdList, BoxStateListener listListener) {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils();
        }
        dialogUtils.showProgress(context);
        Intent intent = new Intent("android.intent.action.hal.iocontroller.batchopen");
        intent.putExtra("batchboxid", boxIdList);
        context.sendBroadcast(intent);
        boxStateListener = listListener;
        boxStateListener.setType(EnumActionType.OPEN_BATCH);
    }

    public void queryBoxState(Context context, String boxId, BoxStateListener listener) {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils();
        }
        dialogUtils.showProgress(context);
        Intent intent = new Intent("android.intent.action.hal.iocontroller.query");
        //String boxId = "A01";
        intent.putExtra("boxid", boxId);
        context.sendBroadcast(intent);
        LogUtil.d(TAG, "箱门:" + boxId + ",查询操作完成");
        boxStateListener = listener;
        boxStateListener.setType(EnumActionType.QUERY);

    }

    public void queryBatchBoxState(Context context, String[] boxIds, BoxStateListener listener) {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils();
        }
        dialogUtils.showProgress(context);
        Intent intent = new Intent("android.intent.action.hal.iocontroller.simplebatchquery");
        //String[] batchBoxId = {"A01","A02","A03","A04","A05"};
        intent.putExtra("batchboxid", boxIds);
        context.sendBroadcast(intent);
        boxStateListener = listener;
        boxStateListener.setType(EnumActionType.QUERY_BATCH);

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (dialogUtils != null) {
            dialogUtils.dismissProgress();
        }
        if (intent.getAction().equals("android.intent.action.hal.iocontroller.querydata")) {
            String boxId = intent.getExtras().getString("boxid");
            LogUtil.d(TAG, "箱门:" + boxId + ",返回查询操作完成");
            boolean isOpened = intent.getExtras().getBoolean("isopened");
            boolean isStoraged = intent.getExtras().getBoolean("isstoraged");
            LogUtil.d(TAG, "箱门:box" + boxId + "box状态" + isOpened + ",isStoraged:" + isStoraged);
            if (boxStateListener != null) {
                boxStateListener.onBoxStateBack(new String[]{boxId}, new boolean[]{isOpened});
            }
        }
        if (intent.getAction().equals("android.intent.action.hal.iocontroller.batchopen.result")) {
            String[] batchboxid = intent.getExtras().getStringArray("batchboxid");
            boolean[] opened = intent.getExtras().getBooleanArray("opened");
            if (boxStateListener != null) {
                LogUtil.d(TAG, "中间层回调:操作完成");
                boxStateListener.onBoxStateBack(batchboxid, opened);
            }
        }
    }

    public interface BoxStateListener {
        void setType(EnumActionType enumActionType);

        void onBoxStateBack(String[] boxId, boolean[] isOpen);
    }

    public enum EnumActionType {
        QUERY, QUERY_BATCH, OPEN_BATCH
    }

}
