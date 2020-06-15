package com.yxytech.parkingcloud.baselibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/6/26<p>
 * <p>更新时间：2019/6/26<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class BitmapUtils {
    /**
     * 把base64的String码转换成正常显示的字符串
     */
    public static String base64ToString(String base64) {
        byte[] decode = Base64Util.decode(base64);
        return new String(decode);
    }

    /**
     * 把String的转换成base64码
     */
    public static String stringToBase64(String ss) {
        byte[] bytes = ss.getBytes();
        return Base64Util.encode(bytes);
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    //把图片路径转为bitmap
    public static Bitmap getBitmapForSDCard(String imagePath, Bitmap.Config config) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = config;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        return BitmapFactory.decodeFile(imagePath, opt);
    }

    /**
     * 绘制文字到右下角
     *
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingBottom
     * @param paddingRight
     * @return
     */
    public static Bitmap drawTextToRightBottom(Context context, Bitmap bitmap, String text, String name, String ctx,
                                               String text1, String name1, String ctx1,
                                               int size, int color, int paddingRight, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(DensityUtil.dip2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(bitmap, text, name, ctx, text1, name1, ctx1, paint,
                bitmap.getWidth() - bounds.width() - DensityUtil.dip2px(context, paddingRight),
                bitmap.getHeight() - DensityUtil.dip2px(context, paddingBottom));
    }

    public static Bitmap drawTextToLeftTop(Context context, Bitmap bitmap, Long time, String deviceName, String berthCode, int size, int color, int paddingLeft, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setShadowLayer(5, 1, 1, Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setTextSize(DensityUtil.dip2px(context, size));//设置字符大小
        paint.setTextAlign(Paint.Align.LEFT);//左对齐
        paint.setTypeface(Typeface.DEFAULT_BOLD);//设置字体类型
//        paint.setMaskFilter(new BlurMaskFilter(10f, BlurMaskFilter.Blur.OUTER));

        String timeStr = TimeUtil.long2String(time, TimeUtil.DEFAULT_MILL_TIME_FORMAT);
        String drawText = timeStr + " " + deviceName + " " + berthCode;
        Rect bounds = new Rect();
        paint.getTextBounds(drawText, 0, drawText.length(), bounds);
        return drawTextToBitmap(bitmap, drawText, paint, paddingLeft, paddingTop);

    }

    public static Bitmap drawTextToLeftTop(Context context, Bitmap bitmap, Long time, String deviceName, String berthCode, int size, int color, int paddingLeft, int paddingTop, int line) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setShadowLayer(5, 1, 1, Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setTextSize(DensityUtil.dip2px(context, size));//设置字符大小
        paint.setTextAlign(Paint.Align.LEFT);//左对齐
        paint.setTypeface(Typeface.DEFAULT_BOLD);//设置字体类型
//        paint.setMaskFilter(new BlurMaskFilter(10f, BlurMaskFilter.Blur.OUTER));
        String timeStr = TimeUtil.long2String(time, TimeUtil.DEFAULT_MILL_TIME_FORMAT);
        Rect bounds = new Rect();
        Bitmap resultBitMap = null;
        switch (line) {
            case 1:
                String drawText = timeStr + " " + deviceName + " " + berthCode;
                paint.getTextBounds(drawText, 0, drawText.length(), bounds);
                resultBitMap = drawTextToBitmap(bitmap, drawText, paint, paddingLeft, paddingTop);
                break;
            case 2:
                String drawText1 = timeStr + " " + deviceName;
                paint.getTextBounds(drawText1, 0, drawText1.length(), bounds);
                resultBitMap = drawTextToBitmap(bitmap, drawText1, berthCode, paint, paddingLeft, paddingTop);
                break;
            case 3:
                resultBitMap = drawTextToBitmap(bitmap, timeStr, deviceName, berthCode, paint, paddingLeft, paddingTop);
                break;

            default:

                break;
        }
        return resultBitMap;

    }


    //图片上绘制文字
    private static Bitmap drawTextToBitmap(Bitmap bitmap, String text, String name, String ctx,
                                           String text1, String name1, String ctx1,
                                           Paint paint, int paddingLeft, int paddingTop) {
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, paddingLeft, paddingTop, paint);
        canvas.drawText(name, paddingLeft, paddingTop + 100, paint);
        canvas.drawText(ctx, paddingLeft, paddingTop + 200, paint);

        canvas.drawText(text1, paddingLeft, paddingTop + 300, paint);
        canvas.drawText(name1, paddingLeft, paddingTop + 400, paint);
        canvas.drawText(ctx1, paddingLeft, paddingTop + 500, paint);

        return bitmap;
    }

    //绘制文字
    private static Bitmap drawTextToBitmap(Bitmap bitmap, String text, Paint paint, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;

    }

    private static Bitmap drawTextToBitmap(Bitmap bitmap, String text1, String text2, String text3, Paint paint, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text1, paddingLeft, paddingTop, paint);
        canvas.drawText(text2, paddingLeft, paddingTop + 100, paint);
        canvas.drawText(text3, paddingLeft, paddingTop + 200, paint);
        return bitmap;
    }

    private static Bitmap drawTextToBitmap(Bitmap bitmap, String text1, String text2, Paint paint, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text1, paddingLeft, paddingTop, paint);
        canvas.drawText(text2, paddingLeft, paddingTop + 100, paint);
        return bitmap;
    }


    public static byte[] bitmapToByte(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buf);
        return buf.array();
    }

    public static Bitmap byteToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /***
     * 將Bitmap保存到指定文件
     *
     * ***/
    public static File saveBitmapToPath(Bitmap bitmap) {
        File cameraSavePath = new File(Environment.getExternalStorageDirectory(), File.separator + "YxyTech" + File.separator + "Camera" + File.separator + System.currentTimeMillis() + ".jpg");
        if (!cameraSavePath.getParentFile().exists()) {
            cameraSavePath.getParentFile().mkdir();
        }
        FileOutputStream fos = null;
        try {
            if (!cameraSavePath.exists()) {
                cameraSavePath.createNewFile();
            }
            fos = new FileOutputStream(cameraSavePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(BitmapUtils.class.getName(), Thread.currentThread().getName() + Log.getStackTraceString(e));
            return null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cameraSavePath;

    }
}
