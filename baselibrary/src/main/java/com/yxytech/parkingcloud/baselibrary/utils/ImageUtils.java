package com.yxytech.parkingcloud.baselibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import com.yxytech.parkingcloud.baselibrary.R;
import com.yxytech.parkingcloud.baselibrary.ui.BaseApplication;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/7/16<p>
 * <p>更新时间：2019/7/16<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class ImageUtils {
    /**
     * 图片转为上传参数LMultipartBody.Part
     *
     * @param path
     */
    public static MultipartBody.Part fileToMultipartBodyPart(String path) {
        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        return part;
    }

    /**
     * 将String参数转为上传参数RequestBody
     *
     * @param param
     */
    public static RequestBody convertToRequestBody(String param) {
//        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), param);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), param);
        return requestBody;
    }

    /**
     * 加载网络圆形图片
     *
     * @param imageView
     * @param url
     */
    public static void loadCircleImage(ImageView imageView, String url) {
        GlideUtils.loadCircleImage(imageView, url, R.mipmap.ic_head_normal, R.mipmap.ic_head_normal);
    }

    /**
     * 加载网络方形图片
     *
     * @param imageView
     * @param url
     */
    public static void loadImage(ImageView imageView, String url) {
        GlideUtils.loadImage(imageView, url, R.mipmap.ic_head_normal, R.mipmap.ic_head_normal);
    }

    public static File openSysCameraToFile(Fragment fragment, int requestCode) {
        File cameraSavePath = new File(Environment.getExternalStorageDirectory(), File.separator + "YxyTech" + File.separator + "Camera" + File.separator + System.currentTimeMillis() + ".jpg");
        if (!cameraSavePath.getParentFile().exists()) {
            cameraSavePath.getParentFile().mkdirs();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //检查是否有存储权限，以免崩溃
            if (ContextCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ToastUtil.showInCenter(fragment.getActivity(), "请开启存储权限");
                return null;
            }
            if (ContextCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.showInCenter(fragment.getActivity(), "请开启拍照权限");
                return null;
            }

            uri = FileProvider.getUriForFile(fragment.getActivity(), "com.yxytech.parkingcloud.efsspda.fileprovider", cameraSavePath);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(cameraIntent, requestCode);
        return cameraSavePath;
    }

    public static File openSysCameraToFile(Activity activity, int requestCode) {
        File cameraSavePath = new File(Environment.getExternalStorageDirectory(), File.separator + "YxyTech" + File.separator + "Camera" + File.separator + System.currentTimeMillis() + ".jpg");
        if (!cameraSavePath.getParentFile().exists()) {
            cameraSavePath.getParentFile().mkdirs();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //检查是否有存储权限，以免崩溃
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ToastUtil.showInCenter(activity, "请开启存储权限");
                return null;
            }
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.showInCenter(activity, "请开启拍照权限");
                return null;
            }

            uri = FileProvider.getUriForFile(activity, "com.yxytech.parkingcloud.efsspda.fileprovider", cameraSavePath);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(cameraIntent, requestCode);
        return cameraSavePath;
    }

}
