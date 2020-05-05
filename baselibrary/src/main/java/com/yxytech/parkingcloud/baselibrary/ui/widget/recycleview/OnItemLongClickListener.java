package com.yxytech.parkingcloud.baselibrary.ui.widget.recycleview;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Water on 2017/4/20.
 */

public interface OnItemLongClickListener {

    /**
     * 长按事件监听接口
     *
     * @param view            响应事件的View
     * @param adapterPosition item在列表中的位置
     * @return true 响应事件
     * false 不响应事件
     */
    boolean onItemLongClick(@NonNull View view, int adapterPosition);
}
