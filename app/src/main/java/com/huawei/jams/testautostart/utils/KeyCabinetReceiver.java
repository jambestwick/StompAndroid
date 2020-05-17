package com.huawei.jams.testautostart.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.yxytech.parkingcloud.baselibrary.utils.LogUtil;
import com.yxytech.parkingcloud.baselibrary.utils.ToastUtil;

public class KeyCabinetReceiver extends BroadcastReceiver {
    private static final String TAG = KeyCabinetReceiver.class.getName();

    private static DataBack callBack;
    private static QueryBoxStateListener queryBoxStateListener;
    private static QueryBatchBoxStateListener queryBatchBoxStateListener;
    private static OpenBoxListListener openBoxListListener;


    /***
     * 参数说明:
     * boxId  String类型  格口编号,一位大写字母+两位数字格式
     * boxId 指盒子的编号主柜 Z开头Z01，Z02......
     *                  副柜 A01，B01... ...
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

    public static void openBatchBox(Context context, String[] boxIdList, OpenBoxListListener listListener) {
        Intent intent = new Intent("android.intent.action.hal.iocontroller.batchopen");
        intent.putExtra("batchboxid", boxIdList);
        context.sendBroadcast(intent);
        openBoxListListener = listListener;
    }

    public static void queryBoxState(Context context, String boxId, QueryBoxStateListener listener) {
        Intent intent = new Intent("android.intent.action.hal.iocontroller.query");
        //String boxId = "A01";
        intent.putExtra("boxid", boxId);
        context.sendBroadcast(intent);
        LogUtil.d(TAG, "箱门:" + boxId + ",查询操作完成");
        queryBoxStateListener = listener;

    }

    public static void queryBatchBoxState(Context context, String[] boxIds, QueryBatchBoxStateListener listener) {
        Intent intent = new Intent("android.intent.action.hal.iocontroller.simplebatchquery");
        //String[] batchBoxId = {"A01","A02","A03","A04","A05"};
        intent.putExtra("batchboxid", boxIds);
        context.sendBroadcast(intent);
        queryBatchBoxStateListener = listener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (callBack != null) {
            callBack.onReceive(intent);
        }
        ToastUtil.showInCenter(context, intent.toString());
        if (intent.getAction().equals("android.intent.action.hal.iocontroller.querydata")) {
            String boxId = intent.getExtras().getString("boxid");
            LogUtil.d(TAG, "箱门:" + boxId + ",返回查询操作完成");
            boolean isOpened = intent.getExtras().getBoolean("isopened");
            boolean isStoraged = intent.getExtras().getBoolean("isstoraged");
            LogUtil.d(TAG, "箱门:box" + boxId + "box状态" + isOpened + ",isStoraged:" + isStoraged);
            if (queryBoxStateListener != null) {
                queryBoxStateListener.onBoxStateBack(boxId, isOpened, isStoraged);
            }
            // TODO ...
        }
        if (intent.getAction().equals("android.intent.action.hal.iocontroller.batchopen.result")) {
            String[] batchboxid = intent.getExtras().getStringArray("batchboxid");
            boolean[] opened = intent.getExtras().getBooleanArray("opened ");
            if (queryBatchBoxStateListener != null) {
                queryBatchBoxStateListener.onBoxStateBack(batchboxid, opened);
            }
            if (openBoxListListener != null) {
                openBoxListListener.onBoxStateBack(batchboxid, opened);
            }

        }
    }

    public interface DataBack {
        void onReceive(Intent intent);
    }

    public interface QueryBoxStateListener {
        void onBoxStateBack(String boxId, boolean isOpen, boolean isStorage);
    }

    public interface QueryBatchBoxStateListener {
        void onBoxStateBack(String[] boxIds, boolean[] isBatchOpen);
    }

    public interface OpenBoxListListener {
        void onBoxStateBack(String[] boxIds, boolean[] isBatchOpen);
    }


}
