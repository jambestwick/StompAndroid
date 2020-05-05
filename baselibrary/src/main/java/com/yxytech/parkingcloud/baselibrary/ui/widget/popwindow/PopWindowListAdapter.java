package com.yxytech.parkingcloud.baselibrary.ui.widget.popwindow;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yxytech.parkingcloud.baselibrary.R;

import java.util.List;

public class PopWindowListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private String title;

    private List<PopWindowInfo> list;// 有npo权限的数据

    private int selCityIndex = -1;

    public void setInfos(List<PopWindowInfo> list) {
        this.list = list;
    }


    public PopWindowListAdapter(Context context, String title) {
        mContext = context;
        this.title = title;
        mInflater = LayoutInflater.from(mContext);
    }

    public PopWindowListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setSelCityIndex(int selCityIndex) {
        this.selCityIndex = selCityIndex;
    }

    @Override
    public int getCount() {

        if (null != list && list.size() != 0) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.item_sel_list, null);
            // 初始化holder对象
            initHolder(holder, position, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setData(position, convertView);

        return convertView;
    }

    private void initHolder(ViewHolder holder, int position, View convertView) {
        holder.nameView = convertView.findViewById(R.id.text1);
        holder.nodebId = convertView.findViewById(R.id.text2);
        convertView.setTag(holder);
    }

    private void setData(final int position, View convertView) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (null != list && list.size() > 0) {
            holder.nameView.setText(list.get(position).getValue());
            holder.nodebId.setText(list.get(position).getKey());
            if (position == selCityIndex) {
                holder.nameView.setTextColor(mContext.getResources().getColor(R.color.text_atu_lay_blue));
            } else {
                holder.nameView.setTextColor(Color.BLACK);
            }
        }
    }


    public class ViewHolder {
        TextView nodebId;
        TextView nameView;
    }

}
