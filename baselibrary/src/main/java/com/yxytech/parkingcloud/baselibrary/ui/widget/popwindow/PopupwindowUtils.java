package com.yxytech.parkingcloud.baselibrary.ui.widget.popwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.yxytech.parkingcloud.baselibrary.R;
import com.yxytech.parkingcloud.baselibrary.utils.DensityUtil;

import java.util.List;
import static com.yxytech.parkingcloud.baselibrary.ui.widget.popwindow.BasePopupWindow.popupWindow;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/6/4<p>
 * <p>更新时间：2019/6/4<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class PopupwindowUtils {

    /**
     * @param context                        DOU运营商选择的popupwindow
     * @param parent
     * @param list
     * @param selNetOperaIndex
     * @param popDismissAndItemClickCallBack
     */
    public static void showCarrierMenuPop(final Context context, View parent, List<PopWindowInfo> list, int selNetOperaIndex, final PopDismissAndItemClickCallBack popDismissAndItemClickCallBack) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pop_sel_layout, null);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BasePopupWindow.dismiss();
            }
        });


        ListView listView = (ListView) view.findViewById(R.id.listView);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(parent.getWidth() - 60,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 0, 30, 0);
        listView.setLayoutParams(params);

        PopWindowListAdapter popWindowListAdapter = new PopWindowListAdapter(context);
        popWindowListAdapter.setInfos(list);
        popWindowListAdapter.setSelCityIndex(selNetOperaIndex);
        listView.setAdapter(popWindowListAdapter);
        int totalHeight = 0;

        for (int i = 0; i < popWindowListAdapter.getCount(); i++) {

            View listItem = popWindowListAdapter.getView(i, null, listView);

            listItem.measure(0, 0);

            totalHeight += listItem.getMeasuredHeight();

        }
        popupWindow = new PopupWindow(view, parent.getWidth(),
                totalHeight + (int) DensityUtil.dip2px(context, 10));

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(parent, 0, 0);
        //        setDarkBackground(context,pwHelpView,true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                popDismissAndItemClickCallBack.ToDismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popDismissAndItemClickCallBack.OnItemClick(position);
                BasePopupWindow.dismiss();

            }
        });

    }
    
    public interface PopDismissAndItemClickCallBack {

        void ToDismiss();

        void OnItemClick(int position);
    }

}
