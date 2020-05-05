package com.yxytech.parkingcloud.baselibrary.ui.widget.recycleview;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Water on 2017/4/20.
 */

public interface OnItemClickListener {
    /**
     * 响应点击事件监听回调
     * @param view            响应事件的View
     * @param adapterPosition item所在的位置
     */
    void onItemClick(@NonNull View view, int adapterPosition);

}
