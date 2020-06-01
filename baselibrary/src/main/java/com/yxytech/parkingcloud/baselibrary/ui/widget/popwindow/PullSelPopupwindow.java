package com.yxytech.parkingcloud.baselibrary.ui.widget.popwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yxytech.parkingcloud.baselibrary.R;
import com.yxytech.parkingcloud.baselibrary.utils.DensityUtil;

import java.util.List;

/**
 * Created by lWX348305 on 2017/5/4.
 * 下拉选择框（运营商、城市等）
 */

public class PullSelPopupwindow {
    private Context mContext;

    public PullSelPopupwindow(Context context) {
        this.mContext = context;
    }

    public ListView setPublicLayoutParamsForListView(final View parent, View view, String tag) {
        ListView listView = (ListView) view.findViewById(R.id.listView);
        int tempWidth = 0;
        int tempGravity = 0;
        tempWidth = parent.getWidth() - 60;
        tempGravity = Gravity.CENTER;
        LinearLayout.LayoutParams params = null;
        if ("EnsureTime".equals(tag)) {
            tempWidth = parent.getWidth();
            params = new LinearLayout.LayoutParams(tempWidth,
                    (int) DensityUtil.dip2px(mContext, 150));
        } else {
            params = new LinearLayout.LayoutParams(tempWidth,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        params.gravity = tempGravity;
        params.setMargins(30, 0, 30, 0);
        listView.setLayoutParams(params);
        return listView;
    }

    public PopWindowListAdapter setListAdapterForListView(ListView listView, List<PopWindowInfo> params, int index) {
        PopWindowListAdapter popWindowListAdapter = new PopWindowListAdapter(mContext);
        popWindowListAdapter.setInfos(params);
        popWindowListAdapter.setSelCityIndex(index);
        listView.setAdapter(popWindowListAdapter);
        return popWindowListAdapter;
    }

    @SuppressWarnings("deprecation")
    public PopupWindow setPublicPopupWindowParams(View parent, View view, ListView listView, BaseAdapter menuAdapter,
                                                  PopupWindow popupWindow) {
        int totalHeight = 0;

        for (int i = 0; i < menuAdapter.getCount(); i++) {

            View listItem = menuAdapter.getView(i, null, listView);

            listItem.measure(0, 0);

            totalHeight += listItem.getMeasuredHeight();

        }
        int height = DensityUtil.getScreenHeight(mContext);// 手机屏幕的高度//- DensityUtil.dip2px(mContext, 60) - parent.getHeight()
        if (totalHeight >= height) {
            totalHeight = (int) (height - DensityUtil.dip2px(mContext, 20));
        } else {
            totalHeight = (int) (totalHeight + DensityUtil.dip2px(mContext, 10));
        }
        if (null != view) {
            popupWindow = new PopupWindow(view, parent.getWidth(), totalHeight);
        }
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(parent, 0, 0);
        return popupWindow;
    }

    @SuppressLint("InflateParams")
    public View setPublicViewParams(final PopupWindow popupTimeWindow) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pop_sel_layout, null);

        view.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InflateParams")
            @Override
            public void onClick(View v) {
                if (popupTimeWindow != null) {
                    popupTimeWindow.dismiss();
                }
            }
        });
        return view;
    }

    public void setDarkBackground(boolean isDark, TextView pwHelpView) {
        // 设置gone是为了提高拖拽性能，且为后期扩展保留了图层
        if (isDark) {
            pwHelpView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_black_50));
            pwHelpView.setVisibility(View.VISIBLE);
        } else {
            pwHelpView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_black));
            pwHelpView.setVisibility(View.GONE);
        }
    }

    public Double[] switchString2DoubleArray(String str, String tag) {
        String[] split = str.split(",");
        Double[] intArr = new Double[split.length];
        boolean isFromAtu = tag.equals("ATU");
        for (int i = 0; i < split.length; i++) {
            if (isFromAtu) {
                intArr[i] = Math.abs(Double.valueOf(split[i]));
            } else {
                intArr[i] = Double.valueOf(split[i]);
            }
        }
        return intArr;
    }

    /**
     * 附加方法
     *
     * @param parent
     * @param view
     * @param listView
     * @param menuAdapter
     * @param popupWindow
     * @return
     */
    public PopupWindow setPublicPopupWindowParams_(View parent, View view, ListView listView, BaseAdapter menuAdapter,
                                                   PopupWindow popupWindow) {
        int totalHeight = 0;


        if (menuAdapter.getCount() <= 4) {
            for (int i = 0; i < menuAdapter.getCount(); i++) {

                View listItem = menuAdapter.getView(i, null, listView);

                listItem.measure(0, 0);

                totalHeight += listItem.getMeasuredHeight();

            }
        } else {
            View listItem = menuAdapter.getView(0, null, listView);
            listItem.measure(0, 0);

            totalHeight = listItem.getMeasuredHeight() * 4;
        }


        if (null != view) {
            popupWindow = new PopupWindow(view, parent.getWidth(), totalHeight);
        }
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(parent, 0, 0);
        return popupWindow;
    }
}
