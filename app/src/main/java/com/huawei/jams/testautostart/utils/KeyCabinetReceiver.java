package com.huawei.jams.testautostart.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.yxytech.parkingcloud.baselibrary.dialog.DialogUtils;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;

import java.util.Arrays;

public class KeyCabinetReceiver extends BroadcastReceiver {
    private static final String TAG = KeyCabinetReceiver.class.getName();
    private static BoxStateListener boxStateListener;//接口回调必须设置静态的，否则onReceive回调接收到的boxStateListener为空
    private static DialogUtils dialogUtils;
    private static EnumActionType enumActionType;

    /***
     * 参数说明:
     * boxId  String类型  格口编号,一位大写字母+两位数字格式
     * boxId 指盒子的编号主柜 Z开头Z01，Z02......
     *                  副柜 A01，B01... ...
     * ****/
    public static void openBatchBox(Context context, String[] boxIdList, BoxStateListener listListener) {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils();
        }
        LogUtil.d(TAG, Thread.currentThread().getName() + ",openBatchBox context:" + context);
        dialogUtils.showProgress(context);
        Intent intent = new Intent("android.intent.action.hal.iocontroller.batchopen");
        intent.putExtra("batchboxid", boxIdList);
        context.sendBroadcast(intent);
        LogUtil.d(TAG, Thread.currentThread().getName() + ",箱门:" + Arrays.toString(boxIdList) + ",打开操作广播发出");
        boxStateListener = listListener;
        enumActionType = EnumActionType.OPEN_BATCH;
    }

    public static void queryBoxState(Context context, String boxId, BoxStateListener listener) {
//        if (dialogUtils == null) {
//            dialogUtils = new DialogUtils();
//        }
//        dialogUtils.showProgress(context);
        Intent intent = new Intent("android.intent.action.hal.iocontroller.query");
        //String boxId = "A01";
        intent.putExtra("boxid", boxId);
        context.sendBroadcast(intent);
        LogUtil.d(TAG, Thread.currentThread().getName() + ",箱门:" + boxId + ",查询操作广播发出");
        boxStateListener = listener;
        enumActionType = EnumActionType.QUERY;

    }

    public static void queryBatchBoxState(Context context, String[] boxIds, BoxStateListener listener) {
//        if (dialogUtils == null) {
//            dialogUtils = new DialogUtils();
//        }
//        dialogUtils.showProgress(context);
        Intent intent = new Intent("android.intent.action.hal.iocontroller.simplebatchquery");
        intent.putExtra("batchboxid", boxIds);
        context.sendBroadcast(intent);
        LogUtil.d(TAG, Thread.currentThread().getName() + ",箱门:" + Arrays.toString(boxIds) + ",批量查询操作广播发出");
        boxStateListener = listener;
        enumActionType = EnumActionType.QUERY_BATCH;

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
                LogUtil.d(TAG, Thread.currentThread().getName() + ",箱门:" + boxId + ",查询操作广播返回isOpened:" + isOpened + ",isStoraged:" + isStoraged);
                if (boxStateListener != null)
                    boxStateListener.onBoxStateBack(enumActionType, new String[]{boxId}, new boolean[]{isOpened});
            }
            if ("android.intent.action.hal.iocontroller.batchopen.result".equals(intent.getAction())) {
                String[] batchboxid = intent.getExtras().getStringArray("batchboxid");
                boolean[] opened = intent.getExtras().getBooleanArray("opened");
                LogUtil.d(TAG, Thread.currentThread().getName() + ".箱门:" + Arrays.toString(batchboxid) + ",操作广播返回opened:" + Arrays.toString(opened));
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
