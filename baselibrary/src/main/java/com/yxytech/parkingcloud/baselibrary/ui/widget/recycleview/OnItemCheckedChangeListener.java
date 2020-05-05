package com.yxytech.parkingcloud.baselibrary.ui.widget.recycleview;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Water on 2017/4/20.
 */

public interface OnItemCheckedChangeListener {

    /**
     * item 选中状态监听
     * @param view
     * @param isChecked
     * @param adapterPosition
     */
    void onItemCheck(@NonNull View view, boolean isChecked, int adapterPosition);
}
