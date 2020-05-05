package com.yxytech.parkingcloud.baselibrary.utils;

import android.graphics.Color;
import android.widget.TextView;

import com.yxytech.parkingcloud.baselibrary.R;


/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/6/17<p>
 * <p>更新时间：2019/6/17<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class PlateColorSwitch {
    public static void selectPlateColor(TextView textView, String plateColor) {
        if (StrUtil.isEmpty(plateColor)) {
        } else {
            switch (plateColor) {
                case "1":
                    //
                    textView.setBackgroundResource(R.color.base_title_bg);
                    textView.setTextColor(Color.BLACK);
                    break;
                case "2":
                    textView.setBackgroundResource(R.color.yellow);
                    textView.setTextColor(Color.BLACK);
                    break;
                case "3":
                    textView.setBackgroundResource(R.color.black);
                    textView.setTextColor(Color.WHITE);
                    break;
                case "4":
                    textView.setBackgroundResource(R.color.white);
                    textView.setTextColor(Color.BLACK);
                    break;
                case "5":
                    textView.setBackgroundResource(R.color.green);
                    textView.setTextColor(Color.BLACK);
                    break;
                case "6":
                    textView.setBackgroundResource(R.drawable.bg_yellow_green);
                    textView.setTextColor(Color.BLACK);
                    break;
                default:
                    textView.setBackgroundResource(R.color.transparent_black);
                    textView.setTextColor(Color.TRANSPARENT);
                    break;
            }
        }
    }
}
