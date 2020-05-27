package com.huawei.jams.testautostart.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yxytech.parkingcloud.baselibrary.dialog.DialogUtils;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

import java.util.Arrays;

public class KeyCabinetReceiver extends BroadcastReceiver {
    private static final String TAG = KeyCabinetReceiver.class.getName();
    private static BoxStateListener boxStateListener;//接口回调必须设置静态的，否则onReceive回调接收到的boxStateListener为空
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
    public void openBatchBox(Context context, String[] boxIdList, BoxStateListener listListener) {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils();
        }
        dialogUtils.showProgress(context);
        Intent intent = new Intent("android.intent.action.hal.iocontroller.batchopen");
        intent.putExtra("batchboxid", boxIdList);
        context.sendBroadcast(intent);
        LogUtil.d(TAG, "箱门:" + Arrays.toString(boxIdList) + ",打开操作广播发出");
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
        LogUtil.d(TAG, "箱门:" + boxId + ",查询操作广播发出");
        boxStateListener = listener;
        boxStateListener.setType(EnumActionType.QUERY);

    }

    public void queryBatchBoxState(Context context, String[] boxIds, BoxStateListener listener) {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils();
        }
        dialogUtils.showProgress(context);
        Intent intent = new Intent("android.intent.action.hal.iocontroller.simplebatchquery");
        intent.putExtra("batchboxid", boxIds);
        context.sendBroadcast(intent);
        LogUtil.d(TAG, "箱门:" + Arrays.toString(boxIds) + ",查询操作广播发出");
        boxStateListener = listener;
        boxStateListener.setType(EnumActionType.QUERY_BATCH);

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (dialogUtils != null) {
            dialogUtils.dismissProgress();
        }
        try {
            if ("android.intent.action.hal.iocontroller.querydata".equals(intent.getAction())) {
                String boxId = intent.getExtras().getString("boxid");
                boolean isOpened = intent.getExtras().getBoolean("isopened");
                boolean isStoraged = intent.getExtras().getBoolean("isstoraged");
                LogUtil.d(TAG, "箱门:" + boxId + ",查询操作广播返回isOpened:" + isOpened + ",isStoraged:" + isStoraged);
                if (boxStateListener != null)
                    boxStateListener.onBoxStateBack(new String[]{boxId}, new boolean[]{isOpened});
            }
            if ("android.intent.action.hal.iocontroller.batchopen.result".equals(intent.getAction())) {
                String[] batchboxid = intent.getExtras().getStringArray("batchboxid");
                boolean[] opened = intent.getExtras().getBooleanArray("opened");
                LogUtil.d(TAG, "箱门:" + Arrays.toString(batchboxid) + ",操作广播返回opened:" + Arrays.toString(opened));
                if (boxStateListener != null) boxStateListener.onBoxStateBack(batchboxid, opened);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "onReceive:Intent" + intent + ",操作广播返回异常:" + Log.getStackTraceString(e));
        }
    }

    public interface BoxStateListener {
        void setType(EnumActionType enumActionType);//区分当前操作
        void onBoxStateBack(String[] boxId, boolean[] isOpen);
    }

    public enum EnumActionType {
        QUERY, QUERY_BATCH, OPEN_BATCH
    }

}
