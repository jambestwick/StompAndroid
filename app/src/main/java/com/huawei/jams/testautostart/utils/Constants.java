package com.huawei.jams.testautostart.utils;

import android.os.Environment;

import java.io.File;

public class Constants {

    public static final String PATROL_TIME = "patrolTime";
    public static final String PATROL_NUM = "patrolNum";
    public static final String TOKEN = "token";
    public static final String DEVICE_NO = "deviceNo";
    //public static final String[] BOX_ID_ARRAY = new String[]{"Z01", "Z02", "Z03", "Z04", "Z05", "Z06", "Z07", "Z08"};
    public static final String[] BOX_ID_ARRAY = new String[]{"Z01"};
    // ip = "223.5.5.5";// 阿里巴巴公共ip
    public static final String ALI_PUBLIC_IP = "223.5.5.5";
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String SERVER_URL = "www.metalcar.cn";
    public static final long ONE_MILL_SECOND = 1000L;
    public static final long PATROL_INTERVAL_MILL_SECOND = 5 * ONE_MILL_SECOND;//巡检间隔时间
    public static final long DELAY_ADVISE_MILL_SECOND = 2 * ONE_MILL_SECOND;//延时播放广告
    public static final long ANIMA_DURATION_MILL_SECOND =  ONE_MILL_SECOND;//动画播放时间

    public static final String APP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "metalcar" + File.separator + "app";
    public static final String ADVISE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "metalcar" + File.separator + "advise";


}
