package com.yxytech.parkingcloud.baselibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;

import java.util.Stack;

/**
 * Activity工具类，用于Activity管理和应用程序退出
 * Created by kb.zhang on 2018/4/13.
 */

public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    // 将当前Activity移出栈中
    public void removeActivity(Activity activity) {
        if (activity != null) {
            if (activityStack == null) {
                activityStack = new Stack<Activity>();
            }
            activityStack.remove(activity);
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());

        } catch (Exception e) {
            Log.e(AppManager.class.getName(), Thread.currentThread().getName() + "AppExit:" + Log.getStackTraceString(e));
        }
    }

    /**
     * 得到指定类名的Activity
     */
    public Activity getActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 判断Activity是否Destroy
     *
     * @param mActivity
     * @return true:已销毁
     */
    public static boolean isDestroy(Activity mActivity) {
        if (mActivity == null ||
                mActivity.isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }

    public void restartApp(Context context) {
        Intent i = context.getPackageManager().getLaunchIntentForPackage(BaseApplication.getAppContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

}
