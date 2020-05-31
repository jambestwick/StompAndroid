package com.yxytech.parkingcloud.baselibrary.http.common;

/**
 * Created by zhpan on 2018/2/1.
 */

public class RxRetrofitApp {
    /**
     * 网络请求超时时间毫秒
     */
    private static int DEFAULT_TIMEOUT = 30 * 1000;
    private static String IP;
    private static String HOST;
    private static String API_SERVER_URL;
//    private static String DOWNLOAD_URL;

    //    private static int DEFAULT_TIMEOUT = 6000;
//    private static String IP="202.108.22.59";
//    private static String HOST = "http://gank.io/";
//    private static String API_SERVER_URL = HOST + "api/data/";
//    private static String DOWNLOAD_URL="http://www.oitsme.com/download/oitsme.apk";
    private RxRetrofitApp() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(String API_SERVER_URL) {
        RxRetrofitApp.API_SERVER_URL = API_SERVER_URL;
    }

    public static void init(String HOST, String IP, String API_SERVER_URL, int DEFAULT_TIMEOUT) {
        RxRetrofitApp.HOST = HOST;
        RxRetrofitApp.IP = IP;
        RxRetrofitApp.API_SERVER_URL = API_SERVER_URL;
        RxRetrofitApp.DEFAULT_TIMEOUT = DEFAULT_TIMEOUT;
    }

    public static int getDefaultTimeout() {
        return DEFAULT_TIMEOUT;
    }

    public static String getIP() {
        return IP;
    }

    public static String getHOST() {
        return HOST;
    }

    public static String getApiServerUrl() {
        return API_SERVER_URL;
    }
}
