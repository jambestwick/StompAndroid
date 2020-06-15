package com.yxytech.parkingcloud.baselibrary.utils;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/4/13<p>
 * <p>更新时间：2020/4/13<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class ExceptionHelper implements Thread.UncaughtExceptionHandler {
    private final static String TAG = ExceptionHelper.class.getName();
    private static volatile ExceptionHelper INSTANCE;

    private ExceptionHelper() {
    }

    public static ExceptionHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (ExceptionHelper.class) {
                if (INSTANCE == null) {
                    synchronized (ExceptionHelper.class) {
                        INSTANCE = new ExceptionHelper();
                    }
                }
            }
        }
        return INSTANCE;
    }

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * 初始化默认异常捕获
     */
    public void init() {
        // 获取默认异常处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 将当前类设为默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LogUtil.e(TAG, "全局异常捕获:" + "当前线程:" + t + ",异常信息:" + Log.getStackTraceString(e));
        if (handleException(e)) {
            // 已经处理,APP重启
            //restartApp();
        } else {
            // 如果不处理,则调用系统默认处理异常,弹出系统强制关闭的对话框
            if (mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(t, e);
            }
        }
    }

    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }

        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        e.printStackTrace(pw);
        pw.close();
        String result = writer.toString();
        // 打印出错误日志
        LogUtil.e(TAG, Thread.currentThread().getName() + result);
        return true;
    }

    /**
     * 1s后让APP重启
     */
    private void restartApp() {
        Intent intent = BaseApplication.getAppContext().getPackageManager()
                .getLaunchIntentForPackage(BaseApplication.getAppContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(BaseApplication.getAppContext(), 0, intent, 0);
        AlarmManager mgr = (AlarmManager) BaseApplication.getAppContext().getSystemService(Context.ALARM_SERVICE);
        // 1秒钟后重启应用
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
        System.exit(0);
    }
}
