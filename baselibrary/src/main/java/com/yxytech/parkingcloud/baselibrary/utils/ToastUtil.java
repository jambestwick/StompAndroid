package com.yxytech.parkingcloud.baselibrary.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yxytech.parkingcloud.baselibrary.R;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/09/29
 *     desc  : utils about toast
 * </pre>
 */
public final class ToastUtil {

    private ToastUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 上下文.
     */
    //private static Context mContext = null;
    private static Toast mToast = null;
    /**
     * 显示Toast.
     */
    public static final int SHOW_TOAST = 0;
    public static final int SHOW_TOAST_CENTER = 1;

    /**
     * 主要Handler类，在线程中可用
     * what：0.提示文本信息
     */
    private static Handler baseHandler;

    private static class HandlerOOM extends Handler {
        private WeakReference<Context> weakReference;
        private Context activity;

        HandlerOOM(Context activity) {
            weakReference = new WeakReference<>(activity);
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_TOAST:
                    showToast(activity, msg.getData().getString("TEXT"));
                    break;
                case SHOW_TOAST_CENTER:
                    showInCenter(activity, msg.getData().getString("TEXT"));
                    break;
                default:
                    break;
            }
        }
    }

    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stopToast();
            mToast = null;
        }
    };

    /**
     * 描述：Toast提示文本.
     *
     * @param text 文本
     */
    public static void showToast(Context context, String text) {
        if (!StrUtil.isSpace(text)) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 描述：Toast提示文本.
     *
     * @param resId 文本的资源ID
     */
    public static void showToast(Context context, int resId) {
        Toast.makeText(context, "" + context.getResources().getText(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 描述：在线程中提示文本信息.
     *
     * @param resId 要提示的字符串资源ID，消息what值为0,
     */
    public static void showToastInThread(Context context, int resId) {
        baseHandler =new HandlerOOM(context);
        Message msg = baseHandler.obtainMessage(SHOW_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("TEXT", context.getResources().getString(resId));
        msg.setData(bundle);
        baseHandler.sendMessage(msg);
    }

    /**
     * 描述：在线程中提示文本信息.
     * toast 消息what值为0
     */
    public static void showToastInThread(Context context, String text) {
        baseHandler= new HandlerOOM(context);
        Message msg = baseHandler.obtainMessage(SHOW_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("TEXT", text);
        msg.setData(bundle);
        baseHandler.sendMessage(msg);
    }


    /**
     * 描述：Toast提示文本.
     *
     * @param context
     * @param resources
     */
    public static void showInCenter(Context context, int resources) {
        if (context != null) {
            //String message = String.format(context.getString(resources));
            String message = context.getString(resources);
            showInCenter(context, message);
        }
    }

    /**
     * 描述：Toast提示文本.
     *
     * @param context
     * @param text
     */
    public static void showInCenter(Context context, String text) {
        showInCenter(context, text, 2000);
    }

    /**
     * 描述：Toast提示文本.
     *
     * @param context
     * @param resId
     */
    public static void showInCenterInThread(Context context, int resId) {
        baseHandler= new HandlerOOM(context);
        Message msg = baseHandler.obtainMessage(SHOW_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("TEXT", context.getResources().getString(resId));
        msg.setData(bundle);
        msg.what = SHOW_TOAST_CENTER;
        baseHandler.sendMessage(msg);
    }

    /**
     * 描述：Toast提示文本.
     *
     * @param context
     * @param text
     */
    public static void showInCenterInThread(Context context, String text) {
        baseHandler= new HandlerOOM(context);
        Message msg = baseHandler.obtainMessage(SHOW_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("TEXT", text);
        msg.setData(bundle);
        msg.what = SHOW_TOAST_CENTER;
        baseHandler.sendMessage(msg);
    }

    /**
     * 描述：Toast提示文本.
     *
     * @param context
     * @param resources
     * @param time
     */
    public static void showInCenter(Context context, int resources, int time) {
        if (context != null) {
            String message = context.getString(resources);
            showInCenter(context, message, time);
        }
    }

    public static void showInCenter(Context context, String text, int time) {
        if (context == null) {
            return;
        }
        if (TextUtils.isEmpty(text)) {
            return;
        }

        if (mToast == null) {
            mToast = new Toast(context);
            baseHandler.postDelayed(runnable, time);
        }
        View content = View.inflate(context, R.layout.toast, null);
        TextView tvToast = (TextView) content.findViewById(R.id.tv_toast);
        tvToast.setText(text);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(content);
        mToast.show();

    }

    public static void stopToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
