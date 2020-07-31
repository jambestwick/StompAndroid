package com.yxytech.parkingcloud.baselibrary.utils;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.yxytech.parkingcloud.baselibrary.BuildConfig;
import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Date;
import java.text.SimpleDateFormat;

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
    private static volatile ExceptionHelper instance;
    private static final String FILE_NAME_SUFFIX = ".trace";
    private Context context;

    private ExceptionHelper() {
    }

    public static ExceptionHelper getInstance() {
        if (instance == null) {
            synchronized (ExceptionHelper.class) {
                if (instance == null) {
                    synchronized (ExceptionHelper.class) {
                        instance = new ExceptionHelper();
                    }
                }
            }
        }
        return instance;
    }

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * 初始化默认异常捕获
     */
    public void init(Context context) {
        this.context = context.getApplicationContext();
        // 获取默认异常处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 将当前类设为默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LogUtil.e(TAG, "全局异常捕获:" + "当前线程:" + t + ",异常信息:" + Log.getStackTraceString(e));
        dumpException2SDCard(e);
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

//        Writer writer = new StringWriter();
//        PrintWriter pw = new PrintWriter(writer);
//        e.printStackTrace(pw);
//        pw.close();
//        String result = writer.toString();
//        // 打印出错误日志
//        LogUtil.e(TAG, Thread.currentThread().getName() + "," + result);
        return true;
    }

    /**
     * 导出异常信息到SD卡
     *
     * @param ex
     */
    private void dumpException2SDCard(Throwable ex) {
        LogUtil.e(TAG, "==============================" + ex.toString());
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        //创建文件夹
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "metalcar" + File.separator + "crash_log/";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //获取当前时间
        long current = System.currentTimeMillis();
        String time = TimeUtil.long2String(current, TimeUtil.DEFAULT_MILL_TIME_FORMAT);
        //以当前时间创建log文件
        File file = new File(dirPath + "crash" + time + FILE_NAME_SUFFIX);
        try {
            //输出流操作
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //导出手机信息和异常信息
            pw.println("发生异常时间:" + time);
            PackageInfo packageInfo = PackageUtils.getPackageInfo(context);
            if (null != packageInfo) {
                pw.println("应用版本:" + packageInfo.versionName);
                pw.println("应用版本号:" + packageInfo.versionCode);
            }
            pw.println("android版本号:" + Build.VERSION.RELEASE);
            pw.println("android版本号API:" + Build.VERSION.SDK_INT);
            pw.println("手机制造商:" + Build.MANUFACTURER);
            pw.println("手机型号:" + Build.MODEL);
            pw.println("手机品牌:" + Build.BRAND);
            pw.println("手机IMEI:" + PackageUtils.getIMEI(context));
            pw.println("Product:" + Build.PRODUCT);
            pw.println("CPU_ABI:" + Build.CPU_ABI);
            pw.println("TAGS:" + Build.TAGS);
            pw.println("VERSION_CODES.BASE:" + Build.VERSION_CODES.BASE);
            pw.println("SDK:" + Build.VERSION.SDK);
            pw.println("DEVICE:" + Build.DEVICE);
            pw.println("DISPLAY:" + Build.DISPLAY);
            pw.println("BOARD:" + Build.BOARD);
            pw.println("FINGERPRINT:" + Build.FINGERPRINT);
            pw.println("ID:" + Build.ID);
            pw.println("USER:" + Build.USER);
            pw.println("HARDWARE:" + Build.HARDWARE);
            ex.printStackTrace(pw);
            //关闭输出流
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
