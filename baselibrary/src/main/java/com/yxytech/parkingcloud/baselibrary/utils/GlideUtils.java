package com.yxytech.parkingcloud.baselibrary.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yxytech.parkingcloud.baselibrary.widget.glide.GlideCircleTransform;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/7/16<p>
 * <p>更新时间：2019/7/16<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class GlideUtils {
    public static void loadImage(ImageView imageView, String url, int loadingRes, int errorRes) {

        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(loadingRes) // 加载时的图片
                .error(errorRes) // 错误是的图片
                .fitCenter()
                .into(imageView);
    }

    /**
     * 加载网络圆形图片
     *
     * @param imageView
     * @param url
     * @param loadingRes
     * @param errorRes
     */
    public static void loadCircleImage(ImageView imageView, String url, int loadingRes, int errorRes) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(loadingRes) // 加载时的图片
                .error(errorRes) // 错误是的图片
                .transform(new GlideCircleTransform(imageView.getContext()))
                .into(imageView);
    }

    /**
     * 加载本地资源文件
     *
     * @param imageView
     * @param resourceId
     */
    public static void loadCircleImage(ImageView imageView, int resourceId) {
        Glide.with(imageView.getContext())
                .load(resourceId)
                .transform(new GlideCircleTransform(imageView.getContext()))
                .into(imageView);
    }

}
