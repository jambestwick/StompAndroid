package com.yxytech.parkingcloud.baselibrary.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.WindowManager;

public class DensityUtil {


    /**
     * 获得状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            result = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * dip转px
     */
    public static int dip2px(Context context, float dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density);
    }

    /**
     * px转dip
     */
    public static int px2Dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转sp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }




    /**
     * 获取屏幕的宽度（像素）
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;//1080
    }

    /**
     * 获取屏幕的宽度（dp）
     */
    public static int getScreenWidthDp(Context context) {
        float scale = getScreenDensity(context);
        return (int) (context.getResources().getDisplayMetrics().widthPixels / scale);//360
    }

    /**
     * 获取屏幕的高度（像素）
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;//1776
    }

    /**
     * 获取屏幕的高度（像素）
     */
    public static int getScreenHeightDp(Context context) {
        float scale = getScreenDensity(context);
        return (int) (context.getResources().getDisplayMetrics().heightPixels / scale);//592
    }

    /**
     * 屏幕密度比例
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;//3
    }


}