package com.yxytech.parkingcloud.baselibrary.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeUtil {

    public static final String ACTION_THREE_CLOCK_RESTART = "ACTION_THREE_CLOCK_RESTART";
    public static final String ACTION_TIME_SET = "android.intent.action.TIME_SET";
    private static final String TAG = TimeUtil.class.getName();

    /**
     * 一秒钟
     */
    public static final int ONE_SECOND = 1000;
    /**
     * 一分钟
     */
    public static final int ONE_MINUTE = 60;
    /**
     * 一小时
     */
    public static final int ONE_HOUR = 60 * ONE_MINUTE;
    /**
     * 一天
     */
    public static final int ONE_DAY = 24 * ONE_HOUR;

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 整型日期格式
     */
    public static final String INTEGER_DATE_FORMAT = "yyyyMMdd";
    /**
     * 字符型日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 字符型日期格式
     */
    public static final String DEFAULT_MILL_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss SSS";

    /**
     * 字符型格式
     */
    public static final String DEFAULT_SECOND_FORMAT = "yyyyMMddHHmmss";


    /**
     * 字符型格式
     */
    public static final String DEFAULT_PAYNO_FORMAT = "yyyyMMddHHmmssSSS";

    /**
     * Created by jambestwick@126.com
     * on 2018/3/22
     * 将日期转换成字符串
     *
     * @param date    转换日期
     * @param formart 转换格式
     * @return
     */
    public static String date2Str(Date date, String formart) {
        SimpleDateFormat sdf = new SimpleDateFormat(formart);
        return sdf.format(date);
    }

    /**
     * 把时间戳变yyyy-MM-dd HH:mm:ss格式时间
     *
     * @param time
     * @return
     */
    public String date2Str(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
        return sf.format(d);
    }

    /**
     * 字符串转换成日期 如果转换格式为空，则利用默认格式进行转换操作
     *
     * @param str    字符串
     * @param format 日期格式
     * @return 日期
     */
    public static Date str2Date(String str, String format) {
        if (null == str || "".equals(str)) {
            return new Date();
        }
        // 如果没有指定字符串转换的格式，则用默认格式进行转换
        if (null == format || "".equals(format)) {
            format = DEFAULT_TIME_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(str);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }


    /**
     * 字符串转换成日期 如果转换格式为空，则利用默认格式进行转换操作
     *
     * @param str    字符串
     * @param format 日期格式
     * @return 日期
     */
    public static Date str2Date(String str, String format, Locale locale) {
        if (null == str || "".equals(str)) {
            return new Date();
        }
        // 如果没有指定字符串转换的格式，则用默认格式进行转换
        if (null == format || "".equals(format)) {
            format = DEFAULT_TIME_FORMAT;
        }
        SimpleDateFormat sdf;
        if (null == locale || locale.equals("")) {
            sdf = new SimpleDateFormat(format);
        } else {
            sdf = new SimpleDateFormat(format, locale);
        }

        Date date = null;
        try {
            date = sdf.parse(str);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /***
     * @param date1 开始时间
     * @param date2  结束时间
     *
     * **/
    public static Date compareTwoTimes(Date date1, Date date2) {
        long diff = date2.getTime() - date1.getTime();
        if (diff >= 0) {
            return date2;
        } else {
            return date1;
        }
    }

    public static Date compareTimes(List<Date> dates) {
        if (null == dates || dates.size() <= 0) {
            return null;
        }
        Date temDate = dates.get(0);
        for (Date date : dates) {
            if (date.getTime() < temDate.getTime()) {
                temDate = date;
            }
        }
        return temDate;
    }

    /**
     * 把时间戳变yyyy-MM-dd
     * HH:mm:ss格式时间
     *
     * @param time
     * @return
     */

    public static String long2String(long time, String format) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(d);
    }

    /**
     * 前补零几位
     *
     * @param str     需要补零的参数
     * @param pattern 补零的总位数 （例：pattern :0000, str =12 ,则0012）
     **/
    public static String leadingZero(String str, String pattern) {
        int origin = Integer.parseInt(str);
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(origin);
    }

    /**
     * 前补零几位
     *
     * @param number  需要补零的参数
     * @param pattern 补零的总位数 （例：pattern :0000, str =12 ,则0012）
     **/
    public static String leadingZero(Integer number, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(number);
    }

    public static void start3Clock(Context context) {
        AlarmManager mg = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mFifteenIntent = new Intent(ACTION_THREE_CLOCK_RESTART);
        PendingIntent p = PendingIntent.getBroadcast(context, 0, mFifteenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long systemTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long selectTime = calendar.getTimeInMillis();
        /**如果超过今天的3点，那么定时器就设置为明天3点*/
        if (systemTime > selectTime) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
        String selectStr = sdf.format(new Date(calendar.getTimeInMillis()));
        LogUtil.i(TAG, Thread.currentThread().getName() + ",selectStr 3 clock : " + selectStr);
        long clockTime = SystemClock.elapsedRealtime();
        LogUtil.i(TAG, Thread.currentThread().getName() + ",selectStr 3 clock : " + clockTime + ",set clock" + TimeUtil.long2String(calendar.getTimeInMillis(), TimeUtil.DEFAULT_MILL_TIME_FORMAT) + ",current:" + TimeUtil.long2String(systemTime, TimeUtil.DEFAULT_MILL_TIME_FORMAT));
        /**RTC_SHUTDOWN_WAKEUP 使用标识，系统进入深度休眠还唤醒*/
        mg.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, p);
    }

}
