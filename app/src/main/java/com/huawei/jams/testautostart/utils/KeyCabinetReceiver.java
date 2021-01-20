package com.huawei.jams.testautostart.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.huawei.jams.testautostart.view.activity.MainActivity;
import com.yxytech.parkingcloud.baselibrary.dialog.DialogUtils;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

import java.util.Arrays;

public class KeyCabinetReceiver extends BroadcastReceiver {
    private static final String TAG = KeyCabinetReceiver.class.getName();
    private BoxStateListener boxStateListener;//接口回调必须设置静态的，否则onReceive回调接收到的boxStateListener为空
    private DialogUtils dialogUtils;
    private EnumActionType enumActionType;

    public KeyCabinetReceiver(BoxStateListener boxStateListener) {
        this.boxStateListener = boxStateListener;
    }

//    //单例模式
//    public static KeyCabinetReceiver getInstance() {
//        if (instance == null)
//            synchronized (KeyCabinetReceiver.class) {
//                instance = new KeyCabinetReceiver();
//            }
//        return instance;
//    }
//
//    public static KeyCabinetReceiver clearInstance() {
//        if (instance != null) {
//            synchronized (KeyCabinetReceiver.class) {
//                instance = null;
//                boxStateListener = null;
//            }
//        }
//        return instance;
//    }


    /***
     * 参数说明:
     * boxId  String类型  格口编号,一位大写字母+两位数字格式
     * boxId 指盒子的编号主柜 Z开头Z01，Z02......
     *                  副柜 A01，B01... ...
     * ****/
    public void openBatchBox(Activity context, String[] boxIdList) {
        if (dialogUtils == null) {
            LogUtil.d(TAG, Thread.currentThread().getName() + ",context:" + context + ",openBatchBox,需要新建:Dialog");
            dialogUtils = new DialogUtils();
        }
        LogUtil.d(TAG, Thread.currentThread().getName() + ",openBatchBox context:" + context);
        dialogUtils.showProgress(context);
        Intent intent = new Intent("android.intent.action.hal.iocontroller.batchopen");
        intent.putExtra("batchboxid", boxIdList);
        //boxStateListener = listListener;
        enumActionType = EnumActionType.OPEN_BATCH;
        context.sendBroadcast(intent);
        LogUtil.d(TAG, Thread.currentThread().getName() + ",箱门:" + Arrays.toString(boxIdList) + ",打开操作广播发出");

    }

    public void queryBoxState(Activity context, String boxId) {
//        if (dialogUtils == null) {
//            dialogUtils = new DialogUtils();
//        }
//        dialogUtils.showProgress(context);
        Intent intent = new Intent("android.intent.action.hal.iocontroller.query");
        //String boxId = "A01";
        intent.putExtra("boxid", boxId);
        // boxStateListener = listener;
        enumActionType = EnumActionType.QUERY;
        context.sendBroadcast(intent);
        LogUtil.d(TAG, Thread.currentThread().getName() + ",箱门:" + boxId + ",查询操作广播发出");
    }

    public void queryBatchBoxState(Activity context, String[] boxIds) {
//        if (dialogUtils == null) {
//            dialogUtils = new DialogUtils();
//        }
//        dialogUtils.showProgress(context);
        Intent intent = new Intent("android.intent.action.hal.iocontroller.simplebatchquery");
        intent.putExtra("batchboxid", boxIds);
        //boxStateListener = listener;
        enumActionType = EnumActionType.QUERY_BATCH;
        context.sendBroadcast(intent);
        LogUtil.d(TAG, Thread.currentThread().getName() + ",箱门:" + Arrays.toString(boxIds) + ",批量查询操作广播发出");
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (dialogUtils != null) {
            dialogUtils.dismissProgress();
            dialogUtils = null;
        }
        try {
            if ("android.intent.action.hal.iocontroller.querydata".equals(intent.getAction())) {
                String boxId = intent.getExtras().getString("boxid");
                boolean isOpened = intent.getExtras().getBoolean("isopened");
                boolean isStoraged = intent.getExtras().getBoolean("isstoraged");
                LogUtil.d(TAG, Thread.currentThread().getName() + ",箱门:" + boxId + ",查询操作广播返回isOpened:" + isOpened + ",isStoraged:" + isStoraged);
                if (boxStateListener != null)
                    boxStateListener.onBoxStateBack(enumActionType, new String[]{boxId}, new boolean[]{isOpened});
            }
            if ("android.intent.action.hal.iocontroller.batchopen.result".equals(intent.getAction())) {
                String[] batchboxid = intent.getExtras().getStringArray("batchboxid");
                boolean[] opened = intent.getExtras().getBooleanArray("opened");
                LogUtil.d(TAG, Thread.currentThread().getName() + ".箱门:" + Arrays.toString(batchboxid) + ",打开操作广播返回opened:" + Arrays.toString(opened));
                if (boxStateListener != null)
                    boxStateListener.onBoxStateBack(enumActionType, batchboxid, opened);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, Thread.currentThread().getName() + ",onReceive:Intent" + intent + ",操作广播返回异常:" + Log.getStackTraceString(e));
        }
    }

    public interface BoxStateListener {
        void onBoxStateBack(EnumActionType enumActionType, String[] boxId, boolean[] isOpen);
    }

    public enum EnumActionType {
        QUERY, QUERY_BATCH, OPEN_BATCH
    }

}
