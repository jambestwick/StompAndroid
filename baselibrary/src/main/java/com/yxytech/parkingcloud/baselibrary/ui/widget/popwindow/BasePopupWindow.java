package com.yxytech.parkingcloud.baselibrary.ui.widget.popwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;

import com.yxytech.parkingcloud.baselibrary.utils.DensityUtil;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/6/3<p>
 * <p>更新时间：2019/6/3<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class BasePopupWindow {
    public static PopupWindow popupWindow;

    /**
     * @param isShowAtLoaction(true表示显示在指定位置，false表示显示在一个参照物view的周围)
     * @param context                                                上下文
     * @param parent                                                 父类或参照物view
     * @param layoutId                                               popWindow的布局id
     * @param width                                                  popWindow的宽
     * @param height                                                 popWindow的高
     * @param xoff                                                   x轴偏移量
     * @param yoff                                                   y轴偏移量
     * @param gravity                                                重心位置
     * @return
     */
    public static View showAtLocation(boolean isShowAtLoaction, Context context, View parent, int layoutId, int width,
                                      int height, int xoff, int yoff, int gravity) {
        // this.popupWindow=popupWindow;
        View contextView = LayoutInflater.from(context).inflate(layoutId, null);
        popupWindow = new PopupWindow(contextView, width, height, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(() -> {

        });
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        if (isShowAtLoaction) {
            popupWindow.showAtLocation(contextView, gravity, xoff, yoff);
        } else {
            popupWindow.showAsDropDown(parent, xoff, yoff);
        }

        return contextView;

    }

    /**
     * @param context
     * @param layoutId 简单的显示在屏幕中间的pop窗口
     * @return
     */
    public static View showSimplePop(Context context, int layoutId) {

        View contextView = showAtLocation(true, context, null, layoutId, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, Gravity.CENTER, 0, 0);

        return contextView;

    }

    public static void dismiss() {
        // TODO Auto-generated method stub
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }


    /**
     * 设置Popup显示位置
     * @param context
     * @param view
     * @param parent
     */
    public static void setPopupShowLocation(Context context, View view, View parent) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        int dis = (int) DensityUtil.dip2px(context, 55);
        if (parent != null) {
            //控件的位置
            int[] position = new int[2];
            parent.getLocationInWindow(position);

            //控件距离屏幕底部的距离
            dis = DensityUtil.getScreenHeight(context) - position[1];
        }
        params.bottomMargin = dis;
    }
}
