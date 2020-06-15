package com.yxytech.parkingcloud.baselibrary.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;

import java.io.IOException;
import java.util.List;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/6/14<p>
 * <p>更新时间：2019/6/14<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class LocationUtils {
    private static final String TAG = LocationUtils.class.getName();
    // 纬度
    public static double latitude = 0.0;
    // 经度
    public static double longitude = 0.0;

    //地址
    public static String address = "";

    /**
     * 初始化位置信息
     *
     * @param context
     */
    public static void initLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //下面注释的代码获取的location为null，所以采用Criteria的方式。
        /*List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            Log.d(TAG, "onCreate: gps=" + locationProvider);
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            Log.d(TAG, "onCreate: network=" + locationProvider);
        } else {
            Log.d(TAG, "onCreate: 没有可用的位置提供器");
            Toast.makeText(this,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
            return;
        }
        //获取Location，老是获取为空！所以用locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(locationProvider);
        */
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，如果设置为高精度，依然获取不了location。
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
        //从可用的位置提供器中，匹配以上标准的最佳提供器
        String locationProvider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LogUtil.d(TAG, Thread.currentThread().getName() + "位置: 没有权限 ");
            ToastUtil.showInCenter(context, "请打开位置权限");
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList = null;
        if (location != null) {
            LogUtil.d(TAG, Thread.currentThread().getName() + "当前的位置: location" + location);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            try {
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.e(TAG, Thread.currentThread().getName() + "location Geo error:" + e.toString());
            }
            if (addressList != null && addressList.size() > 0) {
                Address ad = addressList.get(0);
                address = ad.getCountryName() + ad.getAdminArea() + ad.getLocality() + ad.getSubLocality() + ad.getFeatureName();//拿到城市
            }
        }

        LocationListener locationListener = new LocationListener() {
            // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            @Override
            public void onLocationChanged(Location location) {
                LogUtil.d(TAG, Thread.currentThread().getName() + "onLocationChanged: ");
                //如果位置发生变化,重新显示
                latitude = location.getLatitude(); // 经度
                longitude = location.getLongitude(); // 纬度
                List<Address> addressList1 = null;
                try {
                    addressList1 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, Thread.currentThread().getName() + "location Geo error:" + Log.getStackTraceString(e));
                }
                if (addressList1 != null && addressList1.size() > 0) {
                    Address ad = addressList1.get(0);
                    LogUtil.d(TAG, Thread.currentThread().getName() + "current location address:" + ad.toString());
                    address = ad.getCountryName() + ad.getAdminArea() + ad.getLocality() + ad.getSubLocality() + ad.getFeatureName();//拿到城市
                }
            }

            // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                LogUtil.d(TAG, Thread.currentThread().getName() + "onStatusChanged: " + provider + ",status:" + status + ",Bundle:" + extras);

            }

            // Provider被enable时触发此函数，比如GPS被打开
            @Override
            public void onProviderEnabled(String provider) {
                LogUtil.d(TAG, Thread.currentThread().getName() + "onProviderEnabled: " + provider);
            }

            // Provider被disable时触发此函数，比如GPS被关闭
            @Override
            public void onProviderDisabled(String provider) {
                LogUtil.d(TAG, Thread.currentThread().getName() + "onProviderDisabled: " + provider);
            }
        };
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
    }

}
