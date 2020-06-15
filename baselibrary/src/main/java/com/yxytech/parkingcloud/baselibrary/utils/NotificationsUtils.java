package com.yxytech.parkingcloud.baselibrary.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/6/20<p>
 * <p>更新时间：2019/6/20<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class NotificationsUtils {
    private static final String TAG = NotificationsUtils.class.getName();

    /**
     * 通知权限是否开启
     **/
    public static boolean isPermissionOpen(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return NotificationManagerCompat.from(context).getImportance() != NotificationManager.IMPORTANCE_NONE;
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    public static void openPermissionSetting(Context context) {

        try {
            Intent localIntent = new Intent();
            localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //直接跳转到应用通知设置的代码：
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localIntent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                context.startActivity(localIntent);
                return;
            }
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(localIntent);
                return;
            }
            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,

            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                context.startActivity(localIntent);
                return;
            }
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, Thread.currentThread().getName() + "打开权限设置,异常:" + Log.getStackTraceString(e));
        }
    }
}
