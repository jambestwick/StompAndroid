package com.yxytech.parkingcloud.baselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/6/14<p>
 * <p>更新时间：2019/6/14<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class PackageUtils {

    private static final String TAG = PackageUtils.class.getName();
    public static final int APP_INSTALL_AUTO = 0;
    public static final int APP_INSTALL_INTERNAL = 1;
    public static final int APP_INSTALL_EXTERNAL = 2;


    /**
     * 获取版本名称
     *
     * @param context 上下文
     * @return 版本名称
     */
    public static String getVersionName(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 获取版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static int getVersionCode(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;

    }

    /**
     * 获取App的名称
     *
     * @param context 上下文
     * @return 名称
     */
    public static String getAppName(Context context) {
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //获取应用 信息
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //获取albelRes
            int labelRes = applicationInfo.labelRes;
            //返回App的名称
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取当前应用信息类
     *
     * @param context
     * @return PackageInfo
     */
    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据包名获取意图
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 意图
     */
    private static Intent getIntentByPackageName(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    /**
     * 根据包名判断App是否安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 已安装<br>{@code false}: 未安装
     */
    public static boolean isInstallApp(Context context, String packageName) {
        return getIntentByPackageName(context, packageName) != null;
    }


    /**
     * 非Root
     * 安装apk
     *
     * @param mContext    上下文
     * @param apkFilePath apk文件的绝对路径
     */

    public static boolean installApk(Context mContext, String apkFilePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(apkFilePath);
        if (file == null || !file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }
        intent.setDataAndType(Uri.parse("file://" + apkFilePath), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        return true;
    }

    /**
     * Root权限安装
     */
    public static void installSilent(String filePath) {
        installSilent(filePath, "-r " + getInstallLocationParams());
    }

    private static String getInstallLocationParams() {
        int location = getInstallLocation();
        switch (location) {
            case APP_INSTALL_INTERNAL:
                return "-f";
            case APP_INSTALL_EXTERNAL:
                return "-s";
            default:
                break;
        }
        return "";
    }

    public static int getInstallLocation() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm get-install-location",
                false, true);
        if (commandResult.result == 0 && commandResult.successMsg != null && commandResult.successMsg.length() > 0) {
            try {
                int location = Integer.parseInt(commandResult.successMsg.substring(0, 1));
                switch (location) {
                    case APP_INSTALL_INTERNAL:
                        return APP_INSTALL_INTERNAL;
                    case APP_INSTALL_EXTERNAL:
                        return APP_INSTALL_EXTERNAL;
                    default:
                        break;
                }
            } catch (NumberFormatException e) {
                LogUtil.d(TAG, "pm get-install-location error!!!  NumberFormatException :" + Log.getStackTraceString(e));
            }
        }
        return APP_INSTALL_AUTO;
    }


    /**
     * root静默安装
     *
     * @param filePath
     * @param pmParams
     */
    private static void installSilent(String filePath, String pmParams) {
        if (filePath == null || filePath.length() == 0) {
            LogUtil.i(TAG, "installSilent: error path");
            return;
        }

        File file = new File(filePath);
        if (file.length() <= 0 || !file.exists() || !file.isFile()) {
            LogUtil.i(TAG, "installSilent:  error file");
            return;
        }
        //LD_LIBRARY_PATH 指定链接库位置 指定安装命令
        String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install " +
                (pmParams == null ? "" : pmParams) +
                " " +
                filePath.replace(" ", "\\ ");
        //以root模式执行
        ShellUtils.CommandResult result = ShellUtils.execCmd(command, true, true);
        if (result.successMsg != null
                && (result.successMsg.contains("Success") || result.successMsg.contains("success"))) {
            LogUtil.i(TAG, "installSilent: success");
        }
    }

    /**
     * root 静默卸载
     *
     * @param packageName
     * @param isKeepData
     */
    public static void uninstallSilent(String packageName, boolean isKeepData) {
        if (packageName == null) {
            LogUtil.i(TAG, "uninstallSilent: error package");
            return;
        }
        String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall" +
                (isKeepData ? " -k " : " ") +
                packageName.replace(" ", "\\ ");
        ShellUtils.CommandResult result = ShellUtils.execCmd(command, true, true);
        if (result.successMsg != null
                && (result.successMsg.contains("Success") || result.successMsg.contains("success"))) {
            LogUtil.i(TAG, "uninstallSilent: success");
        }
    }


    /**
     * 打开指定包名的App
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 打开成功<br>{@code false}: 打开失败
     */
    public static boolean openAppByPackageName(Context context, String packageName) {
        Intent intent = getIntentByPackageName(context, packageName);
        if (intent != null) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 打开指定包名的App应用信息界面
     *
     * @param context 上下文
     */
    public static void openAppSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    /**
     * 打开定位信息、GPS
     *
     * @param context
     */
    public static void openLocationSetting(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 前往网络设置界面
     *
     * @param context
     */
    public static void openNetworkSetting(Context context) {
        // 跳转到系统的网络设置界面
        Intent intent = null;
        // 先判断当前系统版本
        if (Build.VERSION.SDK_INT > 10) {  // 3.0以上
            intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
        }
        context.startActivity(intent);
    }

    /**
     * 软键盘是否显示
     *
     * @param rootView
     * @return
     */
    public static boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    /**
     * 测量软键盘的底部距离屏幕底部大小
     *
     * @param rootView
     * @return
     */
    public static int getKeyboardToBottomhighet(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff;
    }

    /**
     * 显示和隐藏软键盘 View ： EditText、TextView isShow : true = show , false = hide
     *
     * @param context
     * @param view
     * @param isShow
     */
    public static void popSoftKeyboard(Context context, View view,
                                       boolean isShow) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            view.requestFocus();
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     *
     * @param view
     */
    public static void showSoftKeyboard(View view) {
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }


    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public static void hideSoftKeyboard(View view) {
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * 获取设备唯一标志
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String imei;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//            imei = telephonyManager.getDeviceId();
            //imei = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
            //  System.out.println("============-===========imei:"+imei);
        } catch (Exception e) {
            imei = telephonyManager.getDeviceId();
        }
        if (imei == null || imei.trim().equals("")) {
            imei = telephonyManager.getImei();
        }

        return imei;
//        return "A00000930125AA";
    }
}
