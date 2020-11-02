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
import java.io.PrintWriter;

import static android.os.Process.killProcess;
import static android.os.Process.myPid;

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
        if (!file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }
        //安装好后打开新版本
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
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
                LogUtil.d(TAG, Thread.currentThread().getName() + ",pm get-install-location error!!!  NumberFormatException :" + Log.getStackTraceString(e));
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
            LogUtil.i(TAG, Thread.currentThread().getName() + ",installSilent: error path");
            return;
        }

        File file = new File(filePath);
        if (file.length() <= 0 || !file.exists() || !file.isFile()) {
            LogUtil.i(TAG, Thread.currentThread().getName() + ",installSilent:  error file");
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
            LogUtil.i(TAG, Thread.currentThread().getName() + ",installSilent: success");
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
            LogUtil.i(TAG, Thread.currentThread().getName() + ",uninstallSilent: error package");
            return;
        }
        String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall" +
                (isKeepData ? " -k " : " ") +
                packageName.replace(" ", "\\ ");
        ShellUtils.CommandResult result = ShellUtils.execCmd(command, true, true);
        if (result.successMsg != null
                && (result.successMsg.contains("Success") || result.successMsg.contains("success"))) {
            LogUtil.i(TAG, Thread.currentThread().getName() + ",uninstallSilent: success");
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
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 打开apk
     *
     * @param context
     * @param pathName 文件路径
     */
    public static void openFile(Context context, String pathName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(pathName)), "application/vnd.android.package-archive");
        context.startActivity(intent);
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

    /**
     * 静默安装
     */
    public static boolean clientInstall(String apkPath) {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 " + apkPath);
            PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r " + apkPath);
//          PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    private static boolean returnResult(int value) {
        // 代表成功
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }
    }


    /**
     * uninstall package silent by root
     * <ul>
     * <strong>Attentions:</strong>
     * <li>Don't call this on the ui thread, it may costs some times.</li>
     * <li>You should add <strong>android.permission.DELETE_PACKAGES</strong> in manifest, so no need to request root
     * permission, if you are system app.</li>
     * </ul>
     * <p>
     * /**
     * uninstall package silent by root
     * <ul>
     * <strong>Attentions:</strong>
     * <li>Don't call this on the ui thread, it may costs some times.</li>
     * <li>You should add <strong>android.permission.DELETE_PACKAGES</strong> in manifest, so no need to request root
     * permission, if you are system app.</li>
     * </ul>
     *
     * @param context     file path of package
     * @param packageName package name of app
     * @param isKeepData  whether keep the data and cache directories around after package removal
     * @return <ul>
     * <li>{@link #DELETE_SUCCEEDED} means uninstall success</li>
     * <li>{@link #DELETE_FAILED_INTERNAL_ERROR} means internal error</li>
     * <li>{@link #DELETE_FAILED_INVALID_PACKAGE} means package name error</li>
     * <li>{@link #DELETE_FAILED_PERMISSION_DENIED} means permission denied</li>
     */
    public static int uninstallSlient(Context context, String packageName, boolean isKeepData) {
        if (packageName == null || packageName.length() == 0) {
            return DELETE_FAILED_INVALID_PACKAGE;
        }

        /**
         * if context is system app, don't need root permission, but should add <uses-permission
         * android:name="android.permission.DELETE_PACKAGES" /> in mainfest
         **/
        StringBuilder command = new StringBuilder().append("LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm uninstall")
                .append(isKeepData ? " -k " : " ").append(packageName.replace(" ", "\\ "));
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command.toString(), !isSystemApplication(context), true);
        if (commandResult.successMsg != null
                && (commandResult.successMsg.contains("Success") || commandResult.successMsg.contains("success"))) {
            return DELETE_SUCCEEDED;
        }
        Log.e(TAG,
                new StringBuilder().append("uninstallSilent successMsg:").append(commandResult.successMsg)
                        .append(", ErrorMsg:").append(commandResult.errorMsg).toString());
        if (commandResult.errorMsg == null) {
            return DELETE_FAILED_INTERNAL_ERROR;
        }
        if (commandResult.errorMsg.contains("Permission denied")) {
            return DELETE_FAILED_PERMISSION_DENIED;
        }
        return DELETE_FAILED_INTERNAL_ERROR;
    }

    /**
     * whether context is system application
     *
     * @param context
     * @return
     */
    public static boolean isSystemApplication(Context context) {
        if (context == null) {
            return false;
        }

        return isSystemApplication(context, context.getPackageName());
    }

    /**
     * whether packageName is system application
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isSystemApplication(Context context, String packageName) {
        if (context == null) {
            return false;
        }

        return isSystemApplication(context.getPackageManager(), packageName);
    }

    /**
     * whether packageName is system application
     *
     * @param packageManager
     * @param packageName
     * @return <ul>
     * <li>if packageManager is null, return false</li>
     * <li>if package name is null or is empty, return false</li>
     * <li>if package name not exit, return false</li>
     * <li>if package name exit, but not system app, return false</li>
     * <li>else return true</li>
     * </ul>
     */
    public static boolean isSystemApplication(PackageManager packageManager, String packageName) {
        if (packageManager == null || packageName == null || packageName.length() == 0) {
            return false;
        }

        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Installation return code<br/>
     * install success.
     */
    public static final int INSTALL_SUCCEEDED                              = 1;
    /**
     * Installation return code<br/>
     * the package is already installed.
     */
    public static final int INSTALL_FAILED_ALREADY_EXISTS                  = -1;

    /**
     * Installation return code<br/>
     * the package archive file is invalid.
     */
    public static final int INSTALL_FAILED_INVALID_APK                     = -2;

    /**
     * Installation return code<br/>
     * the URI passed in is invalid.
     */
    public static final int INSTALL_FAILED_INVALID_URI                     = -3;

    /**
     * Installation return code<br/>
     * the package manager service found that the device didn't have enough storage space to install the app.
     */
    public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE            = -4;

    /**
     * Installation return code<br/>
     * a package is already installed with the same name.
     */
    public static final int INSTALL_FAILED_DUPLICATE_PACKAGE               = -5;

    /**
     * Installation return code<br/>
     * the requested shared user does not exist.
     */
    public static final int INSTALL_FAILED_NO_SHARED_USER                  = -6;

    /**
     * Installation return code<br/>
     * a previously installed package of the same name has a different signature than the new package (and the old
     * package's data was not removed).
     */
    public static final int INSTALL_FAILED_UPDATE_INCOMPATIBLE             = -7;

    /**
     * Installation return code<br/>
     * the new package is requested a shared user which is already installed on the device and does not have matching
     * signature.
     */
    public static final int INSTALL_FAILED_SHARED_USER_INCOMPATIBLE        = -8;

    /**
     * Installation return code<br/>
     * the new package uses a shared library that is not available.
     */
    public static final int INSTALL_FAILED_MISSING_SHARED_LIBRARY          = -9;

    /**
     * Installation return code<br/>
     * the new package uses a shared library that is not available.
     */
    public static final int INSTALL_FAILED_REPLACE_COULDNT_DELETE          = -10;

    /**
     * Installation return code<br/>
     * the new package failed while optimizing and validating its dex files, either because there was not enough storage
     * or the validation failed.
     */
    public static final int INSTALL_FAILED_DEXOPT                          = -11;

    /**
     * Installation return code<br/>
     * the new package failed because the current SDK version is older than that required by the package.
     */
    public static final int INSTALL_FAILED_OLDER_SDK                       = -12;

    /**
     * Installation return code<br/>
     * the new package failed because it contains a content provider with the same authority as a provider already
     * installed in the system.
     */
    public static final int INSTALL_FAILED_CONFLICTING_PROVIDER            = -13;

    /**
     * Installation return code<br/>
     * the new package failed because the current SDK version is newer than that required by the package.
     */
    public static final int INSTALL_FAILED_NEWER_SDK                       = -14;

    /**
     * Installation return code<br/>
     * the new package failed because it has specified that it is a test-only package and the caller has not supplied
     * the {@link #INSTALL_ALLOW_TEST} flag.
     */
    public static final int INSTALL_FAILED_TEST_ONLY                       = -15;

    /**
     * Installation return code<br/>
     * the package being installed contains native code, but none that is compatible with the the device's CPU_ABI.
     */
    public static final int INSTALL_FAILED_CPU_ABI_INCOMPATIBLE            = -16;

    /**
     * Installation return code<br/>
     * the new package uses a feature that is not available.
     */
    public static final int INSTALL_FAILED_MISSING_FEATURE                 = -17;

    /**
     * Installation return code<br/>
     * a secure container mount point couldn't be accessed on external media.
     */
    public static final int INSTALL_FAILED_CONTAINER_ERROR                 = -18;

    /**
     * Installation return code<br/>
     * the new package couldn't be installed in the specified install location.
     */
    public static final int INSTALL_FAILED_INVALID_INSTALL_LOCATION        = -19;

    /**
     * Installation return code<br/>
     * the new package couldn't be installed in the specified install location because the media is not available.
     */
    public static final int INSTALL_FAILED_MEDIA_UNAVAILABLE               = -20;

    /**
     * Installation return code<br/>
     * the new package couldn't be installed because the verification timed out.
     */
    public static final int INSTALL_FAILED_VERIFICATION_TIMEOUT            = -21;

    /**
     * Installation return code<br/>
     * the new package couldn't be installed because the verification did not succeed.
     */
    public static final int INSTALL_FAILED_VERIFICATION_FAILURE            = -22;

    /**
     * Installation return code<br/>
     * the package changed from what the calling program expected.
     */
    public static final int INSTALL_FAILED_PACKAGE_CHANGED                 = -23;

    /**
     * Installation return code<br/>
     * the new package is assigned a different UID than it previously held.
     */
    public static final int INSTALL_FAILED_UID_CHANGED                     = -24;

    /**
     * Installation return code<br/>
     * if the parser was given a path that is not a file, or does not end with the expected '.apk' extension.
     */
    public static final int INSTALL_PARSE_FAILED_NOT_APK                   = -100;

    /**
     * Installation return code<br/>
     * if the parser was unable to retrieve the AndroidManifest.xml file.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_MANIFEST              = -101;

    /**
     * Installation return code<br/>
     * if the parser encountered an unexpected exception.
     */
    public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION      = -102;

    /**
     * Installation return code<br/>
     * if the parser did not find any certificates in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES           = -103;

    /**
     * Installation return code<br/>
     * if the parser found inconsistent certificates on the files in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;

    /**
     * Installation return code<br/>
     * if the parser encountered a CertificateEncodingException in one of the files in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING      = -105;

    /**
     * Installation return code<br/>
     * if the parser encountered a bad or missing package name in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME          = -106;

    /**
     * Installation return code<br/>
     * if the parser encountered a bad shared user id name in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID        = -107;

    /**
     * Installation return code<br/>
     * if the parser encountered some structural problem in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_MALFORMED        = -108;

    /**
     * Installation return code<br/>
     * if the parser did not find any actionable tags (instrumentation or application) in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_EMPTY            = -109;

    /**
     * Installation return code<br/>
     * if the system failed to install the package because of system issues.
     */
    public static final int INSTALL_FAILED_INTERNAL_ERROR                  = -110;
    /**
     * Installation return code<br/>
     * other reason
     */
    public static final int INSTALL_FAILED_OTHER                           = -1000000;

    /**
     * Uninstall return code<br/>
     * uninstall success.
     */
    public static final int DELETE_SUCCEEDED                               = 1;

    /**
     * Uninstall return code<br/>
     * uninstall fail if the system failed to delete the package for an unspecified reason.
     */
    public static final int DELETE_FAILED_INTERNAL_ERROR                   = -1;

    /**
     * Uninstall return code<br/>
     * uninstall fail if the system failed to delete the package because it is the active DevicePolicy manager.
     */
    public static final int DELETE_FAILED_DEVICE_POLICY_MANAGER            = -2;

    /**
     * Uninstall return code<br/>
     * uninstall fail if pcakge name is invalid
     */
    public static final int DELETE_FAILED_INVALID_PACKAGE                  = -3;

    /**
     * Uninstall return code<br/>
     * uninstall fail if permission denied
     */
    public static final int DELETE_FAILED_PERMISSION_DENIED                = -4;


}
